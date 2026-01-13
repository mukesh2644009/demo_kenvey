// ShopEase E-Commerce - Admin JavaScript

// Get auth token
function getToken() {
    return localStorage.getItem('token');
}

// Logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

// API Call Helper
async function adminApiCall(endpoint, method = 'GET', data = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    } else {
        window.location.href = '/login';
        return null;
    }
    
    const config = {
        method,
        headers
    };
    
    if (data && method !== 'GET') {
        config.body = JSON.stringify(data);
    }
    
    const response = await fetch(endpoint, config);
    
    if (response.status === 401 || response.status === 403) {
        alert('Session expired or access denied');
        logout();
        return null;
    }
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Request failed');
    }
    
    return response.json();
}

// Format Currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format Date
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Show notification
function showNotification(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Confirm action
function confirmAction(message) {
    return confirm(message);
}

// Initialize DataTable-like features
function initTable(tableId) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    // Add search functionality
    const searchInput = document.querySelector(`[data-table="${tableId}"]`);
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const filter = this.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');
            
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    }
}

// Check admin access on page load
document.addEventListener('DOMContentLoaded', function() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login';
        return;
    }
    
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user.role !== 'ADMIN') {
        alert('Admin access required');
        window.location.href = '/';
        return;
    }
    
    // Highlight current nav item
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-nav li').forEach(li => {
        li.classList.remove('active');
        const link = li.querySelector('a');
        if (link && link.getAttribute('href') === currentPath) {
            li.classList.add('active');
        }
    });
});

