package com.duantn.dtos;

import java.util.ArrayList;
import java.util.List;

import com.duantn.entities.Chuong;

import jakarta.validation.Valid;

public class ChuongFormWrapper {
    @Valid
    private List<Chuong> chuongs = new ArrayList<>();

    public List<Chuong> getChuongs() {
        return chuongs;
    }

    public void setChuongs(List<Chuong> chuongs) {
        this.chuongs = chuongs;
    }
}
