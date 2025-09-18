document.addEventListener("DOMContentLoaded", function() {
    setTimeout(function() {
        var baiGiangInput = document.getElementById("baiGiangId");
        var khoaHocInput = document.getElementById("khoaHocId");

        var baiGiangId = baiGiangInput ? baiGiangInput.value : null;
        var khoaHocId = khoaHocInput ? khoaHocInput.value : null;

        if (baiGiangId && khoaHocId) {
            fetch('/khoa-hoc/api/tien-do-hoc/cap-nhat', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        baiGiangId: baiGiangId,
                        khoaHocId: khoaHocId
                    })
                })
                .then(res => res.json())
                .then(data => {
                    console.log("✅ Đã cập nhật tiến độ học:", data);

                    return fetch(`/khoa-hoc/api/tien-do-hoc/phan-tram?khoaHocId=${khoaHocId}`);
                })
                .then(res => res.json())
                .then(data => {
                    const phanTram = data.phanTram;
                    console.log("📊 Phần trăm tiến độ mới:", phanTram + "%");

                    const progressBar = document.querySelector(".progress-bar");
                    if (progressBar) {
                        progressBar.style.width = phanTram + "%";
                        progressBar.innerText = phanTram + "%";
                    }

                    const percentText = document.querySelector(".fw-bold.text-success");
                    if (percentText) {
                        percentText.innerText = phanTram + "%";
                    }

                    const iconEl = document.getElementById(`check-baiGiang-${baiGiangId}`);
                    if (iconEl) {
                        iconEl.classList.remove("text-secondary");
                        iconEl.classList.add("text-success");
                        iconEl.innerHTML = '<i class="fas fa-check-circle"></i>';
                    }
                })
                .catch(err => {
                    console.error("❌ Lỗi khi cập nhật hoặc lấy tiến độ:", err);
                });
        } else {
            console.warn("⚠️ Thiếu thông tin bài giảng hoặc khóa học!");
        }
    }, 30000);
});