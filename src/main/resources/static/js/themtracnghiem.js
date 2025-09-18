const mainEl = document.getElementById('main');
const BI_ID = mainEl.dataset.baiGiangId;
const CSRF_TOKEN = mainEl.dataset.csrfToken || '';

function luuCauHoi(idx) {
    const ten = document.getElementById('tenCauHoi_' + idx).value.trim();
    const id = document.getElementById('cauHoiId_' + idx).value;

    console.log("Bai giang ID:", BI_ID);

    fetch('/giangvien/cau-hoi/luu-nhanh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(CSRF_TOKEN && { 'X-CSRF-TOKEN': CSRF_TOKEN })
            },
            body: JSON.stringify({
                cauHoiId: id || null,
                tenCauHoi: ten,
                baiGiangId: BI_ID
            })
        })
        .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
        .then(d => {
            document.getElementById('cauHoiId_' + idx).value = d.cauHoiId;
            alert('✅ Đã lưu câu hỏi ' + (idx + 1));
        })
        .catch(err => alert('❌ ' + err));
}