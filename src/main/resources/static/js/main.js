document.addEventListener("DOMContentLoaded", function() {

    // ================== SHARE FORM ==================
    function bindShareForms() {
        document.querySelectorAll("form.ajax-form").forEach(function(form) {
            if (!form.dataset.bound) {
                form.addEventListener("submit", function(e) {
                    e.preventDefault();

                    var formData = new FormData(form);
                    var action = form.getAttribute("action");
                    var method = form.getAttribute("method") || "post";

                    var submitBtn = form.querySelector("button[type='submit']");
                    var originalText = submitBtn ? submitBtn.innerHTML : "";

                    if (submitBtn) {
                        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>Đang gửi...';
                        submitBtn.disabled = true;
                    }

                    fetch(action, {
                            method: method.toUpperCase(),
                            body: formData
                        })
                        .then(function(response) {
                            return response.text().then(function(text) {
                                if (!response.ok) {
                                    if (response.status === 401) {
                                        throw new Error("Vui lòng đăng nhập để chia sẻ.");
                                    }
                                    throw new Error(text || "Lỗi khi gửi yêu cầu");
                                }
                                return text;
                            });
                        })
                        .then(function(data) {
                            var messageDiv = form.querySelector(".ajax-message");
                            if (!messageDiv && form.nextElementSibling && form.nextElementSibling.classList.contains("ajax-message")) {
                                messageDiv = form.nextElementSibling;
                            }

                            if (messageDiv) {
                                messageDiv.innerHTML = '<div class="alert alert-success mt-2">' + data + '</div>';
                            } else {
                                alert(data);
                            }

                            if (submitBtn) submitBtn.innerHTML = 'Đã gửi ✅';

                            var modalElement = form.closest(".modal");
                            if (modalElement) {
                                var modalInstance = bootstrap.Modal.getInstance(modalElement);
                                if (modalInstance) {
                                    setTimeout(function() { modalInstance.hide(); }, 2000);
                                }
                            }

                            setTimeout(function() {
                                form.reset();
                                if (submitBtn) {
                                    submitBtn.innerHTML = originalText;
                                    submitBtn.disabled = false;
                                }
                            }, 3000);
                        })
                        .catch(function(error) {
                            var messageDiv = form.querySelector(".ajax-message");
                            if (!messageDiv && form.nextElementSibling && form.nextElementSibling.classList.contains("ajax-message")) {
                                messageDiv = form.nextElementSibling;
                            }

                            if (messageDiv) {
                                if (error.message.indexOf("Vui lòng đăng nhập để chia sẻ.") !== -1) {
                                    messageDiv.innerHTML =
                                        '<div class="alert alert-warning mt-2 d-flex align-items-center">' +
                                        '<i class="fas fa-sign-in-alt me-2"></i>' +
                                        '<span>' + error.message + '</span>' +
                                        '<a href="/auth/dangnhap" class="ms-2 text-primary text-decoration-underline" style="cursor: pointer; font-weight: bold;">Đăng nhập</a>' +
                                        '</div>';

                                    setTimeout(function() {
                                        messageDiv.innerHTML = "";
                                        var modalElement = form.closest(".modal");
                                        if (modalElement) {
                                            var modalInstance = bootstrap.Modal.getInstance(modalElement);
                                            if (modalInstance) modalInstance.hide();
                                        }
                                    }, 3000);
                                } else {
                                    messageDiv.innerHTML = '<div class="alert alert-danger mt-2">Lỗi: ' + error.message + '</div>';
                                }
                            } else {
                                alert("Lỗi: " + error.message);
                            }

                            if (submitBtn) {
                                submitBtn.innerHTML = originalText;
                                submitBtn.disabled = false;
                            }
                        });
                });
                form.dataset.bound = "true";
            }
        });
    }

    // ================== LIKE BUTTON ==================
    function likeCourse(id) {
        fetch('/khoaHoc/' + id + '/like', { method: 'POST' })
            .then(function(response) {
                if (response.status === 401 || response.status === 403) {
                    fetch("/auth/save-redirect-url", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: "redirect=" + encodeURIComponent(window.location.pathname)
                    });

                    var loginModal = new bootstrap.Modal(document.getElementById('loginRequiredModal'));
                    loginModal.show();
                    return null;
                }
                if (!response.ok) throw new Error('Lỗi khi gửi yêu cầu');
                return response.json();
            })
            .then(function(data) {
                if (!data) return;

                document.querySelectorAll('.like-btn[data-id="' + id + '"]').forEach(function(btn) {
                    var countSpan = btn.querySelector('span');
                    if (countSpan) countSpan.innerText = data.newLikeCount;

                    if (data.isLiked) {
                        btn.classList.add('btn-danger');
                        btn.classList.remove('btn-outline-danger');
                    } else {
                        btn.classList.remove('btn-danger');
                        btn.classList.add('btn-outline-danger');

                        var isYeuThichTab = window.location.href.indexOf("tab=yeu-thich") !== -1;
                        if (isYeuThichTab) {
                            var cardCol = btn.closest('.col-12') || btn.closest('.col-sm-6') || btn.closest('.col-lg-3');
                            if (cardCol) cardCol.remove();
                        }
                    }
                });
            })
            .catch(function(err) {
                console.error(err);
            });
    }

    function bindLikeButtons() {
        document.querySelectorAll(".like-btn").forEach(function(btn) {
            if (!btn.dataset.bound) {
                btn.addEventListener("click", function(e) {
                    e.stopPropagation();
                    var courseId = btn.getAttribute("data-id");
                    likeCourse(courseId);
                });
                btn.dataset.bound = "true";
            }
        });
    }

    // ================== TABS ==================
    function bindTabs() {
        var tabs = document.querySelectorAll('.tab-link');
        var contents = document.querySelectorAll('.tab-content');

        if (tabs.length > 0) {
            tabs[0].classList.add('active');
            contents.forEach(function(content) {
                content.style.display = content.getAttribute('data-dmid') === tabs[0].getAttribute('data-dmid') ? 'block' : 'none';
            });
        }

        tabs.forEach(function(tab) {
            tab.addEventListener('click', function() {
                var id = tab.getAttribute('data-dmid');

                tabs.forEach(function(t) { t.classList.remove('active'); });
                tab.classList.add('active');

                contents.forEach(function(content) {
                    content.style.display = content.getAttribute('data-dmid') === id ? 'block' : 'none';
                });
            });
        });
    }

    // ================== INIT ==================
    bindShareForms();
    bindLikeButtons();
    bindTabs();

    window.afterAjaxLoad = window.afterAjaxLoad || function() {};
    const oldAfterAjaxLoad = window.afterAjaxLoad;
    // Để gọi lại khi load Ajax phân trang
    window.afterAjaxLoad = function() {
        oldAfterAjaxLoad();
        bindShareForms();
        bindLikeButtons();
    };

});