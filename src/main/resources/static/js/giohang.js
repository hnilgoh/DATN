document.addEventListener('DOMContentLoaded', function() {
    const cartKey = 'cart';

    function showToast(message, isSuccess = true) {
        const toastEl = document.getElementById('toastMessage');
        const toastBody = document.getElementById('toastBody');

        toastBody.textContent = message;
        toastEl.classList.remove('bg-success', 'bg-danger');
        toastEl.classList.add(isSuccess ? 'bg-success' : 'bg-danger');

        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    function getCart() {
        return JSON.parse(localStorage.getItem(cartKey)) || [];
    }

    function saveCart(cart) {
        localStorage.setItem(cartKey, JSON.stringify(cart));
    }

    function addToCart(item) {
        const isLoggedIn = /*[[${#authorization.expression('isAuthenticated()')}]]*/ false;

        if (isLoggedIn) {
            fetch('/gio-hang/them', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(item)
            }).then(res => {
                if (res.ok) {
                    showToast("Đã thêm vào giỏ hàng (tài khoản)!");
                } else {
                    showToast("Thêm giỏ hàng thất bại.", false);
                }
            }).catch(() => showToast("Lỗi khi thêm giỏ hàng.", false));
        } else {
            const cart = getCart();
            if (!cart.find(i => i.khoaHocId === item.khoaHocId)) {
                cart.push(item);
                saveCart(cart);
                showToast("Đã thêm vào giỏ hàng!");
            } else {
                showToast("Khóa học đã có trong giỏ hàng.", false);
            }
        }
    }

    // Bind sự kiện cho nút thêm giỏ hàng, tránh bind trùng
    function bindAddToCartButtons() {
        document.querySelectorAll('.add-to-cart-btn').forEach(btn => {
            if (!btn.dataset.bound) {
                btn.addEventListener('click', event => {
                    event.stopPropagation();
                    const item = {
                        khoaHocId: parseInt(btn.dataset.id),
                        tenKhoaHoc: btn.dataset.name,
                        gia: parseFloat(btn.dataset.gia),
                        giaGoc: parseFloat(btn.dataset.giagoc),
                        anhBia: btn.dataset.anh,
                        giangVien: btn.dataset.giangvien
                    };
                    addToCart(item);
                });
                btn.dataset.bound = "true";
            }
        });
    }

    // Gọi bind lần đầu khi trang load
    bindAddToCartButtons();

    // Đồng bộ giỏ hàng nếu đăng nhập
    const isLoggedIn = /*[[${#authorization.expression('isAuthenticated()')}]]*/ false;
    const hasCart = localStorage.getItem('cart');
    const isSynced = localStorage.getItem('cartSynced');
    if (isLoggedIn && hasCart && !isSynced) {
        const cart = JSON.parse(hasCart);
        fetch('/gio-hang/sync', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(cart)
        }).then(res => {
            if (res.ok) {
                localStorage.removeItem('cart');
                localStorage.setItem('cartSynced', 'true');
                location.reload();
            }
        }).catch(() => {
            // Có thể thêm xử lý lỗi ở đây nếu cần
        });
    }

    window.afterAjaxLoad = window.afterAjaxLoad || function() {};
    const oldAfterAjaxLoad = window.afterAjaxLoad;

    // Hàm gọi lại sau khi load ajax phân trang
    window.afterAjaxLoad = function() {
        oldAfterAjaxLoad();
        bindAddToCartButtons();
        // Nếu có bind sự kiện khác, thêm ở đây
    };
});