package com.duantn.controllers.controllerHocVien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.configs.PdfChungChiUtil;
import com.duantn.entities.ChungChi;
import com.duantn.services.ChungChiService;

@RestController
public class ChungChiController {

    @Autowired
    private ChungChiService chungChiService;

    @GetMapping("/chung-chi/{dangHocId}")
    public ResponseEntity<byte[]> xemChungChi(@PathVariable("dangHocId") Integer dangHocId) {
        ChungChi cc = chungChiService.layTheoDangHocId(dangHocId); // hoặc logic tương đương
        byte[] pdfData = PdfChungChiUtil.taoChungChiPDF(cc);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=chung-chi.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

}
