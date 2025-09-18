const chuongContainer = document.getElementById("chuong-container");

function themChuong() {
    const chuongTemplate = document.getElementById("chuong-template").content.cloneNode(true);
    const chuongEl = chuongTemplate.querySelector(".chuong-item");

    const chuongItems = chuongContainer.querySelectorAll(".chuong-item");

    for (let chuongEl of chuongItems) {
        const baiGiangs = chuongEl.querySelectorAll(".baigiang-item");
        for (let bai of baiGiangs) {
            const loai = bai.querySelector(".loai-bai-giang-input");
            if (!loai || !loai.value) {
                const toast = new bootstrap.Toast(document.getElementById("toastCanhBaoLoaiBaiGiangChuong"));
                toast.show();
                return;
            }
        }
    }

    const chuongIndex = chuongContainer.querySelectorAll(".chuong-item").length;
    chuongEl.querySelector(".chuong-title").innerText = "Phần " + (chuongIndex + 1);

    chuongEl.querySelectorAll("*").forEach(el => {
        if (el.hasAttribute("name")) {
            el.setAttribute("name", el.getAttribute("name").replace(/__index__/g, chuongIndex));
        }
        if (el.hasAttribute("id")) {
            el.setAttribute("id", el.getAttribute("id").replace(/__index__/g, chuongIndex));
        }
        if (el.hasAttribute("for")) {
            el.setAttribute("for", el.getAttribute("for").replace(/__index__/g, chuongIndex));
        }
    });

    chuongEl.querySelectorAll(".btn-toggle-mota").forEach(btn => {
        btn.onclick = function() {
            toggleMoTa(this);
        };
    });

    chuongContainer.appendChild(chuongEl);

    if (coDuLieu) {
        const btnThemBai = chuongEl.querySelector("button[onclick*='themBaiGiang']");
        if (btnThemBai) themBaiGiang(btnThemBai);
    }

    const tooltipTriggerList = [].slice.call(chuongEl.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function themBaiGiang(btn) {
    const chuongEl = btn.closest(".chuong-item");
    const baiGiangContainer = chuongEl.querySelector(".bai-giang-container");

    const chuongItems = document.querySelectorAll(".chuong-item");

    for (let chuongEl of chuongItems) {
        const baiGiangs = chuongEl.querySelectorAll(".baigiang-item");
        for (let bai of baiGiangs) {
            const loai = bai.querySelector(".loai-bai-giang-input");
            if (!loai || !loai.value) {
                const toast = new bootstrap.Toast(document.getElementById("toastCanhBaoLoaiBaiGiang"));
                toast.show();
                return;
            }
        }
    }

    const cIndex = Array.from(chuongContainer.children).indexOf(chuongEl);
    const baigiangIndex = baiGiangContainer.children.length;

    const baigiangTemplate = document.getElementById("baigiang-template").content.cloneNode(true);
    const baigiangHTML = baigiangTemplate.firstElementChild.outerHTML
        .replace(/__cIndex__/g, cIndex)
        .replace(/__bIndex__/g, baigiangIndex);

    const wrapper = document.createElement("div");
    wrapper.innerHTML = baigiangHTML;
    const baigiangEl = wrapper.firstElementChild;
    baigiangEl.querySelector(".baigiang-title").innerText = "Bài giảng " + (baigiangIndex + 1);

    baiGiangContainer.appendChild(baigiangEl);

    capNhatTenLoaiBaiGiang();

    baigiangEl.querySelectorAll(".btn-chon-loai").forEach(button => {
        button.addEventListener("click", function() {
            const selectedValue = this.getAttribute("data-value");
            const baigiangItem = this.closest(".baigiang-item");
            const hiddenInput = baigiangItem.querySelector(".loai-bai-giang-input");
            const allButtons = baigiangItem.querySelectorAll(".btn-chon-loai");
            const videoFields = baigiangItem.querySelector(".video-fields");
            const baivietFields = baigiangItem.querySelector(".baiviet-fields");
            const tracnghiemFields = baigiangItem.querySelector(".tracnghiem-fields");

            const isAlreadyActive = this.classList.contains("active");

            allButtons.forEach(btn => btn.classList.remove("active"));

            if (videoFields) {
                videoFields.style.display = "none";
                videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = true);
            }
            if (baivietFields) {
                baivietFields.style.display = "none";
                baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = true);
            }
            if (tracnghiemFields) {
                tracnghiemFields.style.display = "none";
                tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = true);
            }

            if (isAlreadyActive) {
                if (hiddenInput) hiddenInput.value = "";
            } else {
                if (hiddenInput) hiddenInput.value = selectedValue;
                this.classList.add("active");

                if (selectedValue === "VIDEO" && videoFields) {
                    videoFields.style.display = "block";
                    videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = false);
                } else if (selectedValue === "TAILIEU" && baivietFields) {
                    baivietFields.style.display = "block";
                    baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = false);
                } else if (selectedValue === "TRACNGHIEM" && tracnghiemFields) {
                    tracnghiemFields.style.display = "block";
                    tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = false);
                }
            }
        });
    });

    const tooltipTriggerList = [].slice.call(baigiangEl.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function capNhatTenLoaiBaiGiang() {
    document.querySelectorAll(".baigiang-item").forEach(baigiangEl => {
        const loaiInput = baigiangEl.querySelector(".loai-bai-giang-input");
        const loaiDaChon = loaiInput ? loaiInput.value : "";

        const videoFields = baigiangEl.querySelector(".video-fields");
        const baivietFields = baigiangEl.querySelector(".baiviet-fields");
        const tracnghiemFields = baigiangEl.querySelector(".tracnghiem-fields");

        if (videoFields) {
            videoFields.style.display = "none";
            videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = true);
        }
        if (baivietFields) {
            baivietFields.style.display = "none";
            baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = true);
        }
        if (tracnghiemFields) {
            tracnghiemFields.style.display = "none";
            tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = true);
        }

        baigiangEl.querySelectorAll(".btn-chon-loai").forEach(btn => btn.classList.remove("active"));

        if (loaiDaChon === "VIDEO" && videoFields) {
            videoFields.style.display = "block";
            videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = false);
        } else if (loaiDaChon === "TAILIEU" && baivietFields) {
            baivietFields.style.display = "block";
            baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = false);
        } else if (loaiDaChon === "TRACNGHIEM" && tracnghiemFields) {
            tracnghiemFields.style.display = "block";
            tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = false);
        }

        const nutLoai = baigiangEl.querySelector(`.btn-chon-loai[data-value="${loaiDaChon}"]`);
        if (nutLoai) nutLoai.classList.add("active");
    });
}



function xoaChuong(btn) {
    btn.closest(".chuong-item").remove();
}

function xoaBaiGiang(btn) {
    const chuongEl = btn.closest(".chuong-item");
    const baiGiangContainer = chuongEl.querySelector(".bai-giang-container");
    const baigiangItem = btn.closest(".baigiang-item");

    baigiangItem.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
        const instance = bootstrap.Tooltip.getInstance(el);
        if (instance) instance.dispose();
    });

    baigiangItem.remove();

    const baiGiangs = baiGiangContainer.querySelectorAll(".baigiang-item");

    baiGiangs.forEach((el, bIndex) => {
        const title = el.querySelector(".baigiang-title");
        if (title) title.innerText = "Bài giảng " + (bIndex + 1);

        el.querySelectorAll("*").forEach(child => {
            ["name", "id", "for"].forEach(attr => {
                if (child.hasAttribute(attr)) {
                    child.setAttribute(attr, child.getAttribute(attr)
                        .replace(/baiGiangs\[\d+\]/g, `baiGiangs[${bIndex}]`)
                        .replace(/__bIndex__/g, bIndex)
                    );
                }
            });
        });
    });
    capNhatTenLoaiBaiGiang();
}



function toggleMoTa(btn) {
    const tooltipInstance = bootstrap.Tooltip.getInstance(btn);
    if (tooltipInstance) tooltipInstance.dispose();

    const cardBody = btn.closest(".card").querySelector(".card-body");
    const moTaEl = cardBody.querySelector(".mo-ta-chuong");

    if (moTaEl) {
        moTaEl.style.display = (moTaEl.style.display === "none" || moTaEl.style.display === "") ? "block" : "none";
    }

    new bootstrap.Tooltip(btn, {
        trigger: "hover"
    });
}




document.addEventListener("DOMContentLoaded", function() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    if (document.querySelectorAll(".chuong-item").length === 0) {
        themChuong();
    }

    capNhatTenLoaiBaiGiang();

    document.querySelectorAll(".btn-toggle-mota").forEach(btn => {
        btn.onclick = function() {
            toggleMoTa(this);
        };
    });
});

document.addEventListener("click", function(e) {
    if (e.target.matches(".btn-chon-loai")) {
        e.preventDefault();

        const button = e.target;
        const selectedValue = button.getAttribute("data-value");
        const baigiangItem = button.closest(".baigiang-item");

        const hiddenInput = baigiangItem.querySelector(".loai-bai-giang-input");
        const allButtons = baigiangItem.querySelectorAll(".btn-chon-loai");
        const videoFields = baigiangItem.querySelector(".video-fields");
        const baivietFields = baigiangItem.querySelector(".baiviet-fields");
        const tracnghiemFields = baigiangItem.querySelector(".tracnghiem-fields");

        const isAlreadyActive = button.classList.contains("active");

        allButtons.forEach(btn => btn.classList.remove("active"));

        if (videoFields) {
            videoFields.style.display = "none";
            videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = true);
        }
        if (baivietFields) {
            baivietFields.style.display = "none";
            baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = true);
        }
        if (tracnghiemFields) {
            tracnghiemFields.style.display = "none";
            tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = true);
        }

        if (isAlreadyActive) {
            if (hiddenInput) hiddenInput.value = "";
        } else {

            if (hiddenInput) hiddenInput.value = selectedValue;
            button.classList.add("active");

            if (selectedValue === "VIDEO" && videoFields) {
                videoFields.style.display = "block";
                videoFields.querySelectorAll("input, textarea").forEach(el => el.disabled = false);
            } else if (selectedValue === "TAILIEU" && baivietFields) {
                baivietFields.style.display = "block";
                baivietFields.querySelectorAll("textarea").forEach(el => el.disabled = false);
            } else if (selectedValue === "TRACNGHIEM" && tracnghiemFields) {
                tracnghiemFields.style.display = "block";
                tracnghiemFields.querySelectorAll("input, textarea, button").forEach(el => el.disabled = false);
            }
        }
    }
});


function moModalXoaChuong(chuongId) {
    document.getElementById("inputChuongIdXoa").value = chuongId;
    var modal = new bootstrap.Modal(document.getElementById("modalXoaChuong"));
    modal.show();
}

function moModalXoaBaiGiang(baiGiangId) {
    document.getElementById("inputBaiGiangIdXoa").value = baiGiangId;
    const modal = new bootstrap.Modal(document.getElementById("modalXoaBaiGiang"));
    modal.show();
}


document.addEventListener("DOMContentLoaded", function() {
    restoreChuongCollapseState();

    document.querySelectorAll('.btn-toggle-chuong').forEach(function(btn) {
        btn.addEventListener('click', function() {
            toggleChuongNoiDung(btn);
        });
    });
});

function toggleChuongNoiDung(button) {
    const chuongItem = button.closest('.chuong-item');
    const cardBody = chuongItem.querySelector('.card-body');
    const icon = button.querySelector('i');
    const chuongId = chuongItem.dataset.chuongId;

    const collapsedChuongs = JSON.parse(localStorage.getItem('collapsedChuongs')) || [];

    if (cardBody.style.display === "none") {
        cardBody.style.display = "block";
        icon.classList.remove('bi-chevron-right');
        icon.classList.add('bi-chevron-down');

        const index = collapsedChuongs.indexOf(chuongId);
        if (index !== -1) collapsedChuongs.splice(index, 1);
    } else {
        cardBody.style.display = "none";
        icon.classList.remove('bi-chevron-down');
        icon.classList.add('bi-chevron-right');

        if (!collapsedChuongs.includes(chuongId)) {
            collapsedChuongs.push(chuongId);
        }
    }

    localStorage.setItem('collapsedChuongs', JSON.stringify(collapsedChuongs));
}

function restoreChuongCollapseState() {
    const collapsedChuongs = JSON.parse(localStorage.getItem('collapsedChuongs')) || [];

    collapsedChuongs.forEach(function(chuongId) {
        const chuongItem = document.querySelector(`.chuong-item[data-chuong-id="${chuongId}"]`);
        if (chuongItem) {
            const cardBody = chuongItem.querySelector('.card-body');
            const button = chuongItem.querySelector('.btn-toggle-chuong');
            const icon = button.querySelector('i');

            if (cardBody) {
                cardBody.style.display = "none";
                icon.classList.remove('bi-chevron-down');
                icon.classList.add('bi-chevron-right');
            }
        }
    });
}