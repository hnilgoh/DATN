package com.duantn.dtos;

public class TienDoHocRequest {
    private Integer baiGiangId;
    private Integer khoaHocId;

    public TienDoHocRequest() {
    }

    public TienDoHocRequest(Integer baiGiangId, Integer khoaHocId) {
        this.baiGiangId = baiGiangId;
        this.khoaHocId = khoaHocId;
    }

    public Integer getBaiGiangId() {
        return baiGiangId;
    }

    public void setBaiGiangId(Integer baiGiangId) {
        this.baiGiangId = baiGiangId;
    }

    public Integer getKhoaHocId() {
        return khoaHocId;
    }

    public void setKhoaHocId(Integer khoaHocId) {
        this.khoaHocId = khoaHocId;
    }

    @Override
    public String toString() {
        return "TienDoHocRequest{" +
                "baiGiangId=" + baiGiangId +
                ", khoaHocId=" + khoaHocId +
                '}';
    }
}
