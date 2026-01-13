// ShopEase E-Commerce - Main JavaScript

// API Base URL
const API_BASE = '';

// Get auth token
function getToken() {
    return localStorage.getItem('token');
}

// Check if user is logged in
function isLoggedIn() {
    return !!getToken();
}

// Get current user
function getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

// Logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('discountCode');
    window.location.href = '/login';
}

// API Call Helper
async function apiCall(endpoint, method = 'GET', data = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
        method,
        headers
    };
    
    if (data && method !== 'GET') {
        config.body = JSON.stringify(data);
    }
    
    const response = await fetch(`${API_BASE}${endpoint}`, config);
    
    if (response.status === 401) {
        logout();
        return null;
    }
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Request failed');
    }
    
    return response.json();
}

// Add to Cart
async function addToCart(productId, quantity = 1) {
    if (!isLoggedIn()) {
        showToast('Please login to add items to cart', 'warning');
        setTimeout(() => window.location.href = '/login', 1500);
        return;
    }
    
    try {
        await apiCall(`/api/cart/add?productId=${productId}&quantity=${quantity}`, 'POST');
        showToast('Added to cart!', 'success');
        updateCartCount();
    } catch (error) {
        showToast(error.message || 'Failed to add to cart', 'error');
    }
}

// Update Cart Count
async function updateCartCount() {
    const badge = document.getElementById('cartCount');
    if (!badge) return;
    
    if (!isLoggedIn()) {
        badge.textContent = '0';
        return;
    }
    
    try {
        const data = await apiCall('/api/cart/total');
        badge.textContent = data.itemCount || 0;
    } catch (error) {
        badge.textContent = '0';
    }
}

// Show Toast Notification
function showToast(message, type = 'info') {
    const container = document.querySelector('.toast-container') || createToastContainer();
    
    const toast = document.createElement('div');
    toast.className = `toast ${type} show`;
    toast.innerHTML = `
        <div class="toast-body">
            <i class="bi ${getToastIcon(type)} me-2"></i>
            ${message}
        </div>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

function getToastIcon(type) {
    switch (type) {
        case 'success': return 'bi-check-circle-fill';
        case 'error': return 'bi-x-circle-fill';
        case 'warning': return 'bi-exclamation-triangle-fill';
        default: return 'bi-info-circle-fill';
    }
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

// Debounce Function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updateCartCount();
    
    // Update navbar based on auth status
    const user = getCurrentUser();
    if (user) {
        // User is logged in - UI already handles this with Thymeleaf
    }
});

// Handle mobile navbar toggle
document.querySelectorAll('.navbar-toggler').forEach(toggler => {
    toggler.addEventListener('click', function() {
        const target = document.querySelector(this.dataset.bsTarget);
        if (target) {
            target.classList.toggle('show');
        }
    });
});

// Smooth scroll for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            e.preventDefault();
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Lazy load images
if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.classList.remove('lazy');
                observer.unobserve(img);
            }
        });
    });
    
    document.querySelectorAll('img.lazy').forEach(img => {
        imageObserver.observe(img);
    });
}

