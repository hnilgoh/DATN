let ketQuaId = null;

function dinhDangNgayGio(date) {
    const pad = (n) => n.toString().padStart(2, '0');
    const gio = pad(date.getHours());
    const phut = pad(date.getMinutes());
    const ngay = pad(date.getDate());
    const thang = pad(date.getMonth() + 1);
    const nam = date.getFullYear();
    return `${gio}:${phut} - ${ngay}/${thang}/${nam}`;
}
document.addEventListener("DOMContentLoaded", () => {
    const btnBatDau = document.getElementById("batDauBtn");
    const noiDung = document.getElementById("noiDungBaiTracNghiem");
    const btnNext = document.getElementById("btnTiepTuc");
    const btnPrev = document.getElementById("btnCauTruoc");
    const ketQuaContainer = document.getElementById("ketQuaContainer");
    const cauHoiContainer = document.getElementById("cauHoiContainer");
    const cauHoiList = Array.from(document.querySelectorAll(".cau-hoi"));

    const btnLamLai = document.getElementById("btnLamLai");
    const btnChiTiet = document.getElementById("btnChiTiet");

    let idx = 0;
    let thoiGianBatDau = null;

    if (btnBatDau && noiDung) {
        btnBatDau.addEventListener("click", () => {
            btnBatDau.classList.add("d-none");
            noiDung.style.display = "block";
            thoiGianBatDau = new Date();
        });
    }

    function isAllAnswered() {
        return cauHoiList.every(cauHoi => {
            const radio = cauHoi.querySelector("input[type='radio']");
            const name = radio ? radio.name : null;
            return name && document.querySelector(`input[name='${name}']:checked`);
        });
    }

    function updateView() {
        cauHoiList.forEach((c, i) => c.classList.toggle("d-none", i !== idx));
        btnPrev.classList.toggle("d-none", idx === 0);

        if (idx === cauHoiList.length - 1) {
            btnNext.innerText = "Xem kết quả";
            btnNext.disabled = !isAllAnswered();
        } else {
            btnNext.innerText = "Câu tiếp theo";
            btnNext.disabled = false;
        }
    }

    if (btnNext && btnPrev && cauHoiList.length) {
        btnNext.addEventListener("click", () => {
            if (idx < cauHoiList.length - 1) {
                idx++;
                updateView();
            } else {
                let dung = 0;
                cauHoiList.forEach(cauHoi => {
                    const dapAnDung = cauHoi.querySelector("input[type='radio'][data-dung='true']");
                    const daChon = cauHoi.querySelector("input[type='radio']:checked");
                    if (dapAnDung && daChon && dapAnDung.value === daChon.value) dung++;
                });

                const tongCau = cauHoiList.length;
                const tongDiem = parseFloat(((dung / tongCau) * 10).toFixed(2));
                const diemMoiCau = 10 / tongCau;
                const thoiGianHoanThanh = new Date();


                document.getElementById("diemSo").innerText = tongDiem;
                document.getElementById("soDung").innerText = dung;
                document.getElementById("tongCau").innerText = tongCau;

                if (thoiGianBatDau) {
                    document.getElementById("thoiGianBatDau").innerText = dinhDangNgayGio(thoiGianBatDau);
                }
                document.getElementById("thoiGianHoanThanh").innerText = dinhDangNgayGio(thoiGianHoanThanh);

                cauHoiContainer.classList.add("d-none");
                btnNext.classList.add("d-none");
                btnPrev.classList.add("d-none");
                ketQuaContainer.classList.remove("d-none");
                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });

                const baiTracNghiemInput = document.getElementById("baiTracNghiemId");
                const baiTracNghiemId = baiTracNghiemInput ? parseInt(baiTracNghiemInput.value) : null;

                const taiKhoanInput = document.getElementById("taiKhoanId");
                const taiKhoanId = taiKhoanInput ? parseInt(taiKhoanInput.value) : null;

                const chiTietCauHoi = cauHoiList.map(cauHoi => {
                    const cauHoiId = parseInt(cauHoi.getAttribute("data-cauhoi-id"));


                    const daChon = cauHoi.querySelector("input[type='radio']:checked");
                    const dapAnChonId = daChon ? parseInt(daChon.value) : null;
                    const dapAnDung = daChon ? daChon.getAttribute("data-dung") === "true" : false;

                    return {
                        cauHoiId: cauHoiId,
                        dapAnId: dapAnChonId,
                        dapAnChon: dapAnChonId,
                        dungHaySai: dapAnDung,
                        diem: dapAnDung ? parseFloat(diemMoiCau.toFixed(2)) : 0
                    };
                }).filter(ct => ct.cauHoiId !== null && ct.dapAnId !== null);

                console.log({
                    baiTracNghiemId,
                    taiKhoanId,
                    soCauDung: dung,
                    tongDiem: tongDiem,
                    tongCauHoi: tongCau,
                    thoiGianBatDau: thoiGianBatDau.toISOString(),
                    thoiGianKetThuc: thoiGianHoanThanh.toISOString(),
                    chiTietList: chiTietCauHoi
                });

                fetch('/ket-qua/luu', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            baiTracNghiemId,
                            taiKhoanId,
                            soCauDung: dung,
                            tongDiem: tongDiem,
                            tongCauHoi: tongCau,
                            thoiGianBatDau: thoiGianBatDau.toISOString(),
                            thoiGianKetThuc: thoiGianHoanThanh.toISOString(),
                            chiTietList: chiTietCauHoi
                        })
                    })
                    .then(res => res.json())
                    .then(data => {
                        console.log("✅ Đã lưu kết quả:", data);
                        ketQuaId = data.ketQuaId || data;

                        const baiGiangIdEl = document.getElementById("baiGiangId");
                        const khoaHocIdEl = document.getElementById("khoaHocId");
                        const baiGiangId = baiGiangIdEl ? baiGiangIdEl.value : null;
                        const khoaHocId = khoaHocIdEl ? khoaHocIdEl.value : null;

                        if (baiGiangId && khoaHocId) {
                            capNhatTienDo(baiGiangId, khoaHocId);
                        } else {
                            console.warn("⚠️ Không tìm thấy baiGiangId hoặc khoaHocId để cập nhật tiến độ.");
                        }
                    })
                    .catch(err => {
                        console.error("❌ Lỗi khi lưu kết quả:", err);
                    });
            }
        });

        btnPrev.addEventListener("click", () => {
            if (idx > 0) {
                idx--;
                updateView();
            }
        });

        document.querySelectorAll("input[type='radio']").forEach(input => {
            input.addEventListener("change", () => {
                if (idx === cauHoiList.length - 1) {
                    btnNext.disabled = !isAllAnswered();
                }
            });
        });

        updateView();
    }

    if (btnLamLai) {
        btnLamLai.addEventListener("click", () => {
            idx = 0;
            thoiGianBatDau = new Date();

            document.querySelectorAll("input[type='radio']:checked").forEach(radio => {
                radio.checked = false;
            });

            ketQuaContainer.classList.add("d-none");
            cauHoiContainer.classList.remove("d-none");
            btnNext.classList.remove("d-none");
            btnPrev.classList.add("d-none");
            btnNext.innerText = "Câu tiếp theo";
            btnNext.disabled = false;

            document.getElementById("thoiGianBatDau").innerText = "--:--";
            document.getElementById("thoiGianHoanThanh").innerText = "--:--";
            document.getElementById("diemSo").innerText = "0";
            document.getElementById("soDung").innerText = "0";
            document.getElementById("tongCau").innerText = "0";

            updateView();

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    }



    if (btnChiTiet) {
        btnChiTiet.addEventListener("click", () => {
            if (!ketQuaId) {
                alert("❌ Không tìm thấy kết quả. Vui lòng làm bài lại.");
                return;
            }

            fetch(`/ket-qua/chi-tiet/${ketQuaId}`)
                .then(res => res.json())
                .then(data => {
                    const modalContent = document.getElementById("chiTietNoiDung");
                    if (!modalContent) return;

                    let html = `<div class="list-group">`;

                    data.forEach((item, index) => {
                        html += `
                        <div class="list-group-item shadow-sm rounded mb-3">
                            <h6 class="mb-2">
                                📝 <strong>Câu ${index + 1}:</strong> ${item.noiDungCauHoi}
                            </h6>
                            <p class="mb-1">
                                ✅ <strong>Đáp án đúng:</strong> 
                                <span class="text-success">${item.noiDungDapAnDung}</span><br/>
                                🔘 <strong>Bạn chọn:</strong> 
                                <span class="${item.dungHaySai ? 'text-success' : 'text-danger'}">
                                    ${item.noiDungDapAnChon || 'Không chọn'}
                                </span>
                            </p>
                            <p class="mb-1">
                                ${item.dungHaySai 
                                    ? '<span class="badge bg-success"><i class="fas fa-check-circle me-1"></i> Đúng</span>' 
                                    : '<span class="badge bg-danger"><i class="fas fa-times-circle me-1"></i> Sai</span>'}
                                <span class="ms-2">🎯 Điểm: <strong>${item.diem}</strong></span>
                            </p>
                            <p class="mb-0 mt-2">
                                <i class="fas fa-lightbulb text-warning me-1"></i>
                                <strong>Giải thích:</strong>
                                <span>${item.giaiThich || 'Chưa có giải thích.'}</span>
                            </p>
                        </div>
                    `;
                    });

                    html += `</div>`;
                    modalContent.innerHTML = html;

                    const modal = new bootstrap.Modal(document.getElementById('chiTietModal'));
                    modal.show();
                })
                .catch(err => {
                    console.error("❌ Lỗi khi lấy chi tiết kết quả:", err);
                    alert("❌ Có lỗi xảy ra khi xem kết quả chi tiết.");
                });
        });
    }

    (function kiemTraKetQuaDaLam() {
        const baiTracNghiemInput = document.getElementById("baiTracNghiemId");
        const baiTracNghiemId = baiTracNghiemInput ? parseInt(baiTracNghiemInput.value) : null;

        const taiKhoanInput = document.getElementById("taiKhoanId");
        const taiKhoanId = taiKhoanInput ? parseInt(taiKhoanInput.value) : null;


        if (!taiKhoanId || !baiTracNghiemId) return;

        fetch(`/ket-qua/kiemtra?taiKhoanId=${taiKhoanId}&baiTracNghiemId=${baiTracNghiemId}`)
            .then(res => res.json())
            .then(data => {
                if (data.daLam) {
                    ketQuaId = data.ketQuaId;
                    document.getElementById("batDauBtn").classList.add("d-none");
                    document.getElementById("noiDungBaiTracNghiem").style.display = "block";
                    document.getElementById("cauHoiContainer").classList.add("d-none");
                    btnNext.classList.add("d-none");
                    btnPrev.classList.add("d-none");
                    ketQuaContainer.classList.remove("d-none");

                    document.getElementById("diemSo").innerText = data.tongDiem;
                    document.getElementById("soDung").innerText = data.soCauDung;
                    document.getElementById("tongCau").innerText = data.tongCauHoi;
                    document.getElementById("thoiGianBatDau").innerText = data.thoiGianBatDau;
                    document.getElementById("thoiGianHoanThanh").innerText = data.thoiGianKetThuc;
                }
            })
            .catch(err => {
                console.error("❌ Lỗi kiểm tra đã làm:", err);
            });
    })();
});


function capNhatTienDo(baiGiangId, khoaHocId) {
    fetch(`/khoa-hoc/api/tien-do-hoc/cap-nhat?baiGiangId=${baiGiangId}&khoaHocId=${khoaHocId}`)
        .then(response => response.json())
        .then(data => {
            const tienDo = data.phanTram;
            const tienDoElement = document.getElementById("tienDoHoc");
            if (tienDoElement) {
                tienDoElement.style.width = tienDo + "%";
                tienDoElement.innerText = tienDo + "%";
            }
            console.log("📈 Tiến độ đã cập nhật:", tienDo + "%");

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
        .catch(error => {
            console.error("❌ Lỗi khi cập nhật tiến độ học:", error);
        });
}