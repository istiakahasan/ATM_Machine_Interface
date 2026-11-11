/**
 * ==============================================================================
 * ATM Machine Simulation - Client Application Script
 * Vanilla JavaScript (ES6+), Single-Page Architecture, Async/Await Fetch API
 * ==============================================================================
 */

// ==============================================================================
// 1. CONSTANTS & CONFIGURATION
// ==============================================================================
const API_BASE_URL = 'http://localhost:8080';

// ==============================================================================
// 2. APPLICATION STATE
// ==============================================================================
const state = {
  currentUser: null,
  currentBalance: 0.0,
  isLoading: false,
  activeScreenId: 'screen-login'
};

// ==============================================================================
// 3. DOM ELEMENT REFERENCES
// ==============================================================================
const elements = {
  // Screens
  screens: document.querySelectorAll('.screen'),
  screenLogin: document.getElementById('screen-login'),
  screenMenu: document.getElementById('screen-menu'),
  screenWithdraw: document.getElementById('screen-withdraw'),
  screenDeposit: document.getElementById('screen-deposit'),
  screenTransfer: document.getElementById('screen-transfer'),
  screenHistory: document.getElementById('screen-history'),

  // Toast Container
  toastContainer: document.getElementById('toast-container'),

  // Login Screen
  formLogin: document.getElementById('form-login'),
  loginUserId: document.getElementById('login-user-id'),
  loginPin: document.getElementById('login-pin'),
  btnLoginSubmit: document.getElementById('btn-login-submit'),
  loginError: document.getElementById('login-error'),

  // Menu Screen
  menuUserId: document.getElementById('menu-user-id'),
  menuBalanceValue: document.getElementById('menu-balance-value'),
  menuMessage: document.getElementById('menu-message'),
  btnNavWithdraw: document.getElementById('btn-nav-withdraw'),
  btnNavDeposit: document.getElementById('btn-nav-deposit'),
  btnNavTransfer: document.getElementById('btn-nav-transfer'),
  btnNavHistory: document.getElementById('btn-nav-history'),
  btnLogout: document.getElementById('btn-logout'),

  // Withdraw Screen
  formWithdraw: document.getElementById('form-withdraw'),
  withdrawAmount: document.getElementById('withdraw-amount'),
  withdrawCurrentBalance: document.getElementById('withdraw-current-balance'),
  withdrawError: document.getElementById('withdraw-error'),
  btnWithdrawSubmit: document.getElementById('btn-withdraw-submit'),
  quickAmountBtns: document.querySelectorAll('.btn-quick-amount'),

  // Deposit Screen
  formDeposit: document.getElementById('form-deposit'),
  depositAmount: document.getElementById('deposit-amount'),
  depositCurrentBalance: document.getElementById('deposit-current-balance'),
  depositError: document.getElementById('deposit-error'),
  btnDepositSubmit: document.getElementById('btn-deposit-submit'),

  // Transfer Screen
  formTransfer: document.getElementById('form-transfer'),
  transferRecipient: document.getElementById('transfer-recipient'),
  transferAmount: document.getElementById('transfer-amount'),
  transferCurrentBalance: document.getElementById('transfer-current-balance'),
  transferError: document.getElementById('transfer-error'),
  transferSelfWarning: document.getElementById('transfer-self-warning'),
  btnTransferSubmit: document.getElementById('btn-transfer-submit'),

  // History Screen
  historyCurrentBalance: document.getElementById('history-current-balance'),
  historyLoading: document.getElementById('history-loading'),
  historyEmpty: document.getElementById('history-empty'),
  historyList: document.getElementById('history-list'),

  // Generic Back Buttons
  backButtons: document.querySelectorAll('.btn-back')
};

// ==============================================================================
// 4. UTILITY FUNCTIONS
// ==============================================================================

/**
 * Format a numeric amount into USD currency string.
 * @param {number} amount
 * @returns {string} e.g. "$1,050.00"
 */
function formatMoney(amount) {
  const num = Number(amount) || 0;
  return '$' + num.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
}

/**
 * Display an inline message inside a target element.
 * @param {HTMLElement} el - Element to render message inside
 * @param {string} text - Message text
 * @param {boolean} isError - True for error style, false for success
 */
function setMsg(el, text, isError = true) {
  if (!el) return;
  if (!text) {
    el.textContent = '';
    el.classList.add('hidden');
    el.classList.remove('error', 'success');
    return;
  }
  el.textContent = text;
  el.classList.remove('hidden');
  if (isError) {
    el.classList.add('error');
    el.classList.remove('success');
  } else {
    el.classList.add('success');
    el.classList.remove('error');
  }
}

/**
 * Create and show a temporary toast notification at top-right.
 * @param {string} message - Notification text
 * @param {'success'|'error'|'info'|'warning'} type
 * @param {number} duration - Auto-dismiss timeout in ms
 */
function toast(message, type = 'info', duration = 3000) {
  if (!elements.toastContainer) return;

  const toastEl = document.createElement('div');
  toastEl.className = `toast toast-${type}`;
  toastEl.setAttribute('role', type === 'error' ? 'alert' : 'status');

  const iconMap = {
    success: '✅',
    error: '❌',
    info: 'ℹ️',
    warning: '⚠️'
  };

  toastEl.innerHTML = `
    <span class="toast-icon" aria-hidden="true">${iconMap[type] || 'ℹ️'}</span>
    <span class="toast-content">${escapeHtml(message)}</span>
  `;

  elements.toastContainer.appendChild(toastEl);

  // Auto-dismiss
  setTimeout(() => {
    toastEl.classList.add('toast-exit');
    toastEl.addEventListener('animationend', () => {
      toastEl.remove();
    });
  }, duration);
}

/**
 * Basic HTML sanitizer for injected dynamic text.
 * @param {string} str
 * @returns {string}
 */
function escapeHtml(str) {
  if (typeof str !== 'string') return String(str || '');
  return str.replace(/[&<>'"]/g, tag => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    "'": '&#39;',
    '"': '&quot;'
  }[tag] || tag));
}

/**
 * Switch active visible screen and auto-focus first input.
 * @param {string} screenId - Target screen ID
 */
function showScreen(screenId) {
  elements.screens.forEach(screen => {
    if (screen.id === screenId) {
      screen.classList.add('active');
    } else {
      screen.classList.remove('active');
    }
  });

  state.activeScreenId = screenId;

  // Clear messages across sub-screens on transition
  setMsg(elements.withdrawError, '');
  setMsg(elements.depositError, '');
  setMsg(elements.transferError, '');
  setMsg(elements.loginError, '');
  if (elements.transferSelfWarning) {
    elements.transferSelfWarning.classList.add('hidden');
  }

  // Update balance displays whenever switching screens
  syncBalanceDisplay();

  // Auto-focus primary input on the new screen
  setTimeout(() => {
    const activeScreen = document.getElementById(screenId);
    if (!activeScreen) return;
    const firstInput = activeScreen.querySelector('input:not([type="hidden"]), button.action-tile');
    if (firstInput) {
      firstInput.focus();
    }
  }, 100);
}

/**
 * Synchronize state balance across all screen pill badges.
 */
function syncBalanceDisplay() {
  const formatted = formatMoney(state.currentBalance);
  if (elements.menuBalanceValue) elements.menuBalanceValue.textContent = formatted;
  if (elements.withdrawCurrentBalance) elements.withdrawCurrentBalance.textContent = formatted;
  if (elements.depositCurrentBalance) elements.depositCurrentBalance.textContent = formatted;
  if (elements.transferCurrentBalance) elements.transferCurrentBalance.textContent = formatted;
  if (elements.historyCurrentBalance) elements.historyCurrentBalance.textContent = formatted;
  if (elements.menuUserId) elements.menuUserId.textContent = state.currentUser || 'User';
}

/**
 * Toggle loading state and spinner for a button.
 * @param {HTMLButtonElement} button
 * @param {boolean} isLoading
 * @param {string} [loadingText]
 */
function setButtonLoading(button, isLoading, loadingText = '') {
  if (!button) return;
  button.disabled = isLoading;
  const spinner = button.querySelector('.spinner');
  const textEl = button.querySelector('.btn-text');

  if (spinner) {
    if (isLoading) {
      spinner.classList.remove('hidden');
    } else {
      spinner.classList.add('hidden');
    }
  }

  if (textEl && loadingText) {
    textEl.dataset.original = textEl.dataset.original || textEl.textContent;
    textEl.textContent = isLoading ? loadingText : textEl.dataset.original;
  }
}

// ==============================================================================
// 5. API CLIENT FUNCTIONS (async/await)
// ==============================================================================

/**
 * Authenticate user with userId and 4-digit PIN.
 * POST /api/login
 */
async function apiLogin(userId, pin) {
  const response = await fetch(`${API_BASE_URL}/api/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, pin })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || 'Login failed. Please check your credentials.');
  }
  return data;
}

/**
 * Logout user session.
 * POST /api/logout
 */
async function apiLogout(userId) {
  try {
    await fetch(`${API_BASE_URL}/api/logout`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId })
    });
  } catch (err) {
    // Non-blocking logout network exception
    console.warn('Logout API notification warning:', err);
  }
}

/**
 * Fetch latest balance for user.
 * GET /api/balance/:id
 */
async function apiGetBalance(userId) {
  const response = await fetch(`${API_BASE_URL}/api/balance/${encodeURIComponent(userId)}`, {
    method: 'GET',
    headers: { 'Accept': 'application/json' }
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.message || 'Failed to fetch current balance.');
  }
  return data;
}

/**
 * Withdraw amount from account.
 * POST /api/withdraw
 */
async function apiWithdraw(userId, amount) {
  const response = await fetch(`${API_BASE_URL}/api/withdraw`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, amount: Number(amount) })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.ok === false) {
    const errorMsg = data.message || 'Withdrawal failed.';
    throw new Error(errorMsg);
  }
  return data;
}

/**
 * Deposit amount into account.
 * POST /api/deposit
 */
async function apiDeposit(userId, amount) {
  const response = await fetch(`${API_BASE_URL}/api/deposit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, amount: Number(amount) })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.ok === false) {
    const errorMsg = data.message || 'Deposit failed.';
    throw new Error(errorMsg);
  }
  return data;
}

/**
 * Transfer amount from current user to recipient.
 * POST /api/transfer
 */
async function apiTransfer(fromUser, toUser, amount) {
  const response = await fetch(`${API_BASE_URL}/api/transfer`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      fromUser,
      toUser,
      amount: Number(amount)
    })
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.ok === false) {
    const errorMsg = data.message || 'Transfer failed.';
    throw new Error(errorMsg);
  }
  return data;
}

/**
 * Fetch transaction history for user.
 * GET /api/history/:id
 */
async function apiGetHistory(userId) {
  const response = await fetch(`${API_BASE_URL}/api/history/${encodeURIComponent(userId)}`, {
    method: 'GET',
    headers: { 'Accept': 'application/json' }
  });

  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || 'Failed to load transaction history.');
  }
  return await response.json();
}

// ==============================================================================
// 6. ACTION HANDLERS & FLOW CONTROLLERS
// ==============================================================================

/**
 * Handle Login Form Submission
 */
async function handleLogin(e) {
  e.preventDefault();
  setMsg(elements.loginError, '');

  const userId = elements.loginUserId.value.trim();
  const pin = elements.loginPin.value.trim();

  // Client-side validations
  if (!userId) {
    setMsg(elements.loginError, 'Please enter your User ID.', true);
    elements.loginUserId.focus();
    return;
  }

  if (!/^\d{4}$/.test(pin)) {
    setMsg(elements.loginError, 'PIN must be exactly 4 digits.', true);
    elements.loginPin.focus();
    return;
  }

  setButtonLoading(elements.btnLoginSubmit, true, 'Signing In...');

  try {
    const result = await apiLogin(userId, pin);
    state.currentUser = userId;
    state.currentBalance = typeof result.balance === 'number' ? result.balance : parseFloat(result.balance || 0);

    syncBalanceDisplay();
    toast(`Welcome, ${userId}!`, 'success');
    showScreen('screen-menu');

    // Clear sensitive PIN from login input
    elements.loginPin.value = '';
  } catch (err) {
    if (err.message && err.message.toLowerCase().includes('failed to fetch')) {
      setMsg(elements.loginError, 'Server unreachable', true);
      toast('Server unreachable', 'error');
    } else {
      setMsg(elements.loginError, err.message || 'Login failed.', true);
      toast(err.message || 'Login failed.', 'error');
    }
  } finally {
    setButtonLoading(elements.btnLoginSubmit, false);
  }
}

/**
 * Handle Logout
 */
async function handleLogout() {
  setButtonLoading(elements.btnLogout, true);
  const user = state.currentUser;

  try {
    if (user) {
      await apiLogout(user);
    }
  } finally {
    // Clear user state & sensitive fields
    state.currentUser = null;
    state.currentBalance = 0.0;
    elements.loginPin.value = '';
    elements.loginUserId.value = '';
    setMsg(elements.loginError, '');

    setButtonLoading(elements.btnLogout, false);
    toast('Logged out successfully.', 'info');
    showScreen('screen-login');
  }
}

/**
 * Refresh Balance from backend
 */
async function refreshBalance() {
  if (!state.currentUser) return;
  try {
    const res = await apiGetBalance(state.currentUser);
    if (typeof res.balance === 'number') {
      state.currentBalance = res.balance;
      syncBalanceDisplay();
    }
  } catch (err) {
    console.warn('Could not sync latest balance from server:', err);
  }
}

/**
 * Handle Withdrawal Submission
 */
async function handleWithdraw(e) {
  e.preventDefault();
  setMsg(elements.withdrawError, '');

  const amountStr = elements.withdrawAmount.value.trim();
  const amount = parseFloat(amountStr);

  if (isNaN(amount) || amount <= 0) {
    setMsg(elements.withdrawError, 'Amount must be greater than 0.', true);
    elements.withdrawAmount.focus();
    return;
  }

  // Quick client balance check
  if (amount > state.currentBalance) {
    setMsg(elements.withdrawError, 'Insufficient Funds', true);
    toast('Insufficient Funds', 'error');
    return;
  }

  setButtonLoading(elements.btnWithdrawSubmit, true, 'Processing...');

  try {
    const res = await apiWithdraw(state.currentUser, amount);
    toast(res.message || `Withdrew ${formatMoney(amount)} successfully!`, 'success');

    // Update balance
    await refreshBalance();
    elements.withdrawAmount.value = '';
    showScreen('screen-menu');
  } catch (err) {
    const rawMsg = err.message || '';
    if (rawMsg.toLowerCase().includes('failed to fetch')) {
      setMsg(elements.withdrawError, 'Server unreachable', true);
      toast('Server unreachable', 'error');
    } else if (rawMsg.toLowerCase().includes('insufficient funds')) {
      // Must match EXACT case requirement
      setMsg(elements.withdrawError, 'Insufficient Funds', true);
      toast('Insufficient Funds', 'error');
    } else {
      setMsg(elements.withdrawError, rawMsg, true);
      toast(rawMsg, 'error');
    }
  } finally {
    setButtonLoading(elements.btnWithdrawSubmit, false);
  }
}

/**
 * Handle Deposit Submission
 */
async function handleDeposit(e) {
  e.preventDefault();
  setMsg(elements.depositError, '');

  const amountStr = elements.depositAmount.value.trim();
  const amount = parseFloat(amountStr);

  if (isNaN(amount) || amount <= 0) {
    setMsg(elements.depositError, 'Amount must be greater than 0.', true);
    elements.depositAmount.focus();
    return;
  }

  setButtonLoading(elements.btnDepositSubmit, true, 'Processing...');

  try {
    const res = await apiDeposit(state.currentUser, amount);
    toast(res.message || `Deposited ${formatMoney(amount)} successfully!`, 'success');

    // Update balance
    await refreshBalance();
    elements.depositAmount.value = '';
    showScreen('screen-menu');
  } catch (err) {
    const rawMsg = err.message || '';
    if (rawMsg.toLowerCase().includes('failed to fetch')) {
      setMsg(elements.depositError, 'Server unreachable', true);
      toast('Server unreachable', 'error');
    } else {
      setMsg(elements.depositError, rawMsg, true);
      toast(rawMsg, 'error');
    }
  } finally {
    setButtonLoading(elements.btnDepositSubmit, false);
  }
}

/**
 * Handle Transfer Submission
 */
async function handleTransfer(e) {
  e.preventDefault();
  setMsg(elements.transferError, '');

  const recipient = elements.transferRecipient.value.trim();
  const amountStr = elements.transferAmount.value.trim();
  const amount = parseFloat(amountStr);

  if (!recipient) {
    setMsg(elements.transferError, 'Recipient User ID is required.', true);
    elements.transferRecipient.focus();
    return;
  }

  if (recipient.toLowerCase() === (state.currentUser || '').toLowerCase()) {
    setMsg(elements.transferError, 'Cannot transfer funds to yourself.', true);
    elements.transferRecipient.focus();
    return;
  }

  if (isNaN(amount) || amount <= 0) {
    setMsg(elements.transferError, 'Amount must be greater than 0.', true);
    elements.transferAmount.focus();
    return;
  }

  if (amount > state.currentBalance) {
    setMsg(elements.transferError, 'Insufficient Funds', true);
    toast('Insufficient Funds', 'error');
    return;
  }

  setButtonLoading(elements.btnTransferSubmit, true, 'Transferring...');

  try {
    const res = await apiTransfer(state.currentUser, recipient, amount);
    toast(res.message || `Transferred ${formatMoney(amount)} to ${recipient}!`, 'success');

    // Update balance
    await refreshBalance();
    elements.transferRecipient.value = '';
    elements.transferAmount.value = '';
    showScreen('screen-menu');
  } catch (err) {
    const rawMsg = err.message || '';
    if (rawMsg.toLowerCase().includes('failed to fetch')) {
      setMsg(elements.transferError, 'Server unreachable', true);
      toast('Server unreachable', 'error');
    } else if (rawMsg.toLowerCase().includes('insufficient funds')) {
      setMsg(elements.transferError, 'Insufficient Funds', true);
      toast('Insufficient Funds', 'error');
    } else {
      setMsg(elements.transferError, rawMsg, true);
      toast(rawMsg, 'error');
    }
  } finally {
    setButtonLoading(elements.btnTransferSubmit, false);
  }
}

/**
 * Load and display Transaction History
 */
async function loadHistoryScreen() {
  showScreen('screen-history');
  elements.historyList.innerHTML = '';
  elements.historyEmpty.classList.add('hidden');
  elements.historyLoading.classList.remove('hidden');

  try {
    const historyData = await apiGetHistory(state.currentUser);
    elements.historyLoading.classList.add('hidden');

    if (!Array.isArray(historyData) || historyData.length === 0) {
      elements.historyEmpty.classList.remove('hidden');
      return;
    }

    renderHistoryItems(historyData);
  } catch (err) {
    elements.historyLoading.classList.add('hidden');
    if (err.message && err.message.toLowerCase().includes('failed to fetch')) {
      toast('Server unreachable', 'error');
    } else {
      toast(err.message || 'Failed to load transaction history.', 'error');
    }
    elements.historyEmpty.classList.remove('hidden');
  }
}

/**
 * Render items in history screen list
 * @param {Array} items
 */
function renderHistoryItems(items) {
  elements.historyList.innerHTML = '';

  items.forEach(item => {
    const li = document.createElement('li');
    li.className = 'history-item';

    const typeStr = (item.type || '').toUpperCase();
    const isDeposit = typeStr.includes('DEPOSIT') || typeStr.includes('CREDIT') || typeStr.includes('TRANSFER_IN');
    
    let icon = '💳';
    let badgeClass = 'badge-withdraw';
    let sign = '-';
    let amountClass = 'amount-negative';

    if (isDeposit) {
      icon = '💰';
      badgeClass = 'badge-deposit';
      sign = '+';
      amountClass = 'amount-positive';
    } else if (typeStr.includes('TRANSFER')) {
      icon = '🔁';
      badgeClass = 'badge-transfer';
    }

    const typeLabel = formatTransactionType(typeStr);
    const formattedAmount = `${sign}${formatMoney(item.amount)}`;
    const details = item.details || item.description || 'Standard transaction';
    const timestamp = item.timestamp || 'Just now';

    li.innerHTML = `
      <div class="history-left">
        <div class="history-badge ${badgeClass}" aria-hidden="true">${icon}</div>
        <div class="history-info">
          <span class="history-type">${escapeHtml(typeLabel)}</span>
          <span class="history-meta">${escapeHtml(details)} • ${escapeHtml(timestamp)}</span>
        </div>
      </div>
      <div class="history-right">
        <span class="history-amount ${amountClass}">${escapeHtml(formattedAmount)}</span>
      </div>
    `;

    elements.historyList.appendChild(li);
  });
}

/**
 * Make transaction type string readable.
 * @param {string} rawType
 * @returns {string}
 */
function formatTransactionType(rawType) {
  switch (rawType) {
    case 'DEPOSIT':
      return 'Deposit';
    case 'WITHDRAWAL':
    case 'WITHDRAW':
      return 'Withdrawal';
    case 'TRANSFER':
    case 'TRANSFER_OUT':
      return 'Transfer Out';
    case 'TRANSFER_IN':
      return 'Transfer In';
    default:
      return rawType.charAt(0) + rawType.slice(1).toLowerCase();
  }
}

// ==============================================================================
// 7. EVENT LISTENERS & INITIALIZATION
// ==============================================================================

document.addEventListener('DOMContentLoaded', () => {
  // 1. Login Form Submit
  if (elements.formLogin) {
    elements.formLogin.addEventListener('submit', handleLogin);
  }

  // 2. Logout Button Click
  if (elements.btnLogout) {
    elements.btnLogout.addEventListener('click', handleLogout);
  }

  // 3. Main Menu Navigation Tile Clicks
  if (elements.btnNavWithdraw) {
    elements.btnNavWithdraw.addEventListener('click', () => showScreen('screen-withdraw'));
  }
  if (elements.btnNavDeposit) {
    elements.btnNavDeposit.addEventListener('click', () => showScreen('screen-deposit'));
  }
  if (elements.btnNavTransfer) {
    elements.btnNavTransfer.addEventListener('click', () => showScreen('screen-transfer'));
  }
  if (elements.btnNavHistory) {
    elements.btnNavHistory.addEventListener('click', loadHistoryScreen);
  }

  // 4. Quick Amount Preset Buttons in Withdraw Screen
  elements.quickAmountBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const amt = btn.getAttribute('data-amount');
      if (amt && elements.withdrawAmount) {
        elements.withdrawAmount.value = amt;
        elements.withdrawAmount.focus();
      }
    });
  });

  // 5. Withdrawal Form Submit
  if (elements.formWithdraw) {
    elements.formWithdraw.addEventListener('submit', handleWithdraw);
  }

  // 6. Deposit Form Submit
  if (elements.formDeposit) {
    elements.formDeposit.addEventListener('submit', handleDeposit);
  }

  // 7. Transfer Form Submit & Realtime Self-Transfer Warning
  if (elements.formTransfer) {
    elements.formTransfer.addEventListener('submit', handleTransfer);
  }
  if (elements.transferRecipient) {
    elements.transferRecipient.addEventListener('input', () => {
      const val = elements.transferRecipient.value.trim().toLowerCase();
      if (state.currentUser && val === state.currentUser.toLowerCase()) {
        elements.transferSelfWarning.classList.remove('hidden');
      } else {
        elements.transferSelfWarning.classList.add('hidden');
      }
    });
  }

  // 8. Back Buttons across all sub-screens
  elements.backButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      showScreen('screen-menu');
    });
  });

  // 9. Initial Screen Setup
  showScreen('screen-login');
});
