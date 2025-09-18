document.addEventListener("DOMContentLoaded", function() {
    // Hàm mở modal thông báo
    function showNotification(message) {
        document.getElementById("notificationMessage").innerText = message;
        new bootstrap.Modal(document.getElementById("notificationModal")).show();
    }

    function renderComment(container, data) {
        const noiDung = (data.noiDung || "").trim();
        const div = document.createElement("div");
        div.className = "border rounded p-3 mb-2 bg-white"; // ✅ giống Thymeleaf
        div.dataset.commentId = data.binhluanId;

        div.innerHTML = `
        <div class="d-flex">
            <!-- Avatar -->
            <div class="flex-shrink-0 me-3">
                <img src="${data.taikhoanAvatar || '/images/default-avatar.png'}"
                     alt="avatar" class="rounded-circle" width="48" height="48"
                     style="object-fit: cover;">
            </div>

            <!-- Nội dung -->
            <div class="flex-grow-1">
                <div class="d-flex align-items-center mb-1">
                    <strong>${data.taikhoanName}</strong>
                    ${data.isAuthor ? '<span class="badge bg-primary ms-2">Tác giả</span>' : ''}
                    <small class="text-muted ms-2">${data.ngayBinhLuan}</small>
                </div>

                <p class="mb-1 fs-6 text-break"
                   style="white-space: pre-line; word-wrap: break-word; overflow-wrap: break-word;">${noiDung}</p>

                <div class="small text-muted">
                    <a href="javascript:void(0)" 
                       class="me-3 reply-btn text-decoration-none" 
                       data-comment-id="${data.binhluanId}">Trả lời</a>
                    <a href="javascript:void(0)" 
                       class="deleteCommentBtn text-danger text-decoration-none" 
                       data-comment-id="${data.binhluanId}">Xóa</a>
                </div>

                <div id="reply-container-${data.binhluanId}" class="ms-4 mt-2"></div>
            </div>
        </div>`;
        container.appendChild(div);
    }

    const mainForm = document.getElementById("form-binhluan");
    let deleteCommentId = null;

    // Submit bình luận chính
    if (mainForm) {
        mainForm.addEventListener("submit", function(e) {
            e.preventDefault();
            const baiGiangId = mainForm.dataset.baigiangId;
            const noiDung = mainForm.querySelector("textarea[name='noiDung']").value.trim(); // ✅ trim
            if (!noiDung) return showNotification("Vui lòng nhập nội dung!");

            const formData = new FormData();
            formData.append("noiDung", noiDung);

            fetch(`/bai-giang/${baiGiangId}/binh-luan/add`, { method: "POST", body: formData })
                .then(res => res.json())
                .then(data => {
                    renderComment(document.getElementById("comments-container"), data);
                    mainForm.reset();
                }).catch(err => console.error(err));
        });
    }

    document.addEventListener("click", function(e) {
        const target = e.target.closest(".reply-btn, .cancel-reply, .deleteCommentBtn");
        if (!target) return;

        // Reply
        if (target.classList.contains("reply-btn")) {
            const parentId = target.dataset.commentId;
            const container = document.getElementById("reply-container-" + parentId);
            if (!container.querySelector(".reply-form")) {
                const form = document.createElement("form");
                form.className = "reply-form mt-2";
                form.dataset.parentId = parentId;
                form.innerHTML = `
                    <div class="d-flex align-items-start bg-light p-2 rounded-3 shadow-sm" style="gap: 8px;">
                       <textarea name="noiDung" rows="1" required placeholder="Viết trả lời..."
                           class="form-control form-control-sm reply-textarea flex-grow-1"
                           style="resize: none; overflow: hidden; min-height: 38px; max-height: 120px;
                                  border-radius: 20px; background-color: #f0f2f5; padding: 8px 12px; font-size: 14px;"></textarea>
                        <div class="d-flex align-items-center" style="gap:6px;">
                            <button type="submit" class="btn btn-sm text-primary fw-semibold px-2">Gửi</button>
                            <button type="button" class="btn btn-sm text-secondary cancel-reply px-2">Hủy</button>
                        </div>
                    </div>`;
                container.appendChild(form);
            }
        }

        // Cancel reply
        if (target.classList.contains("cancel-reply")) {
            target.closest(".reply-form").remove();
        }

        // Delete comment (hiện modal xác nhận)
        if (target.classList.contains("deleteCommentBtn")) {
            deleteCommentId = target.dataset.commentId;
            new bootstrap.Modal(document.getElementById("confirmDeleteModal")).show();
        }
    });

    // Xác nhận xóa
    document.getElementById("confirmDeleteBtn").addEventListener("click", function() {
        if (!deleteCommentId) return;
        fetch(`/bai-giang/${mainForm.dataset.baigiangId}/binh-luan/delete/${deleteCommentId}`, { method: "DELETE" })
            .then(res => {
                if (res.ok) return res.text();
                else throw new Error("Xóa thất bại");
            }).then(msg => {
                const div = document.querySelector(`[data-comment-id='${deleteCommentId}']`);
                if (div) div.remove();
                bootstrap.Modal.getInstance(document.getElementById("confirmDeleteModal")).hide();
                showNotification("Đã xóa bình luận thành công!");
            }).catch(err => console.error(err));
    });

    // Auto resize textarea
    document.addEventListener("input", function(e) {
        if (e.target.matches(".reply-textarea")) {
            e.target.style.height = "auto";
            e.target.style.height = (e.target.scrollHeight) + "px";
        }
    });

    // Submit reply
    document.addEventListener("submit", function(e) {
        const form = e.target.closest(".reply-form");
        if (!form) return;
        e.preventDefault();

        const parentId = form.dataset.parentId;
        const baiGiangId = mainForm.dataset.baigiangId;
        const noiDung = form.querySelector("textarea[name='noiDung']").value.trim(); // ✅ trim
        if (!noiDung) return showNotification("Vui lòng nhập nội dung!");

        const formData = new FormData();
        formData.append("noiDung", noiDung);
        formData.append("parentId", parentId);

        fetch(`/bai-giang/${baiGiangId}/binh-luan/reply`, { method: "POST", body: formData })
            .then(res => res.json())
            .then(data => {
                const parentContainer = document.getElementById("reply-container-" + parentId);
                if (parentContainer) renderComment(parentContainer, data);
                form.remove();
            }).catch(err => console.error(err));
    });

});