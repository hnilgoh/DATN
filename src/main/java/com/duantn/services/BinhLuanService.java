package com.duantn.services;

import java.util.List;
import java.util.Optional;

import com.duantn.entities.BinhLuan;

import jakarta.transaction.Transactional;

public interface BinhLuanService {

    // Lấy tất cả bình luận chính (parent IS NULL) của một bài giảng
    public List<BinhLuan> getCommentsByBaiGiangId(Integer baiGiangId);

    // Lấy tất cả bình luận con (replies) của một bình luận cha
    public List<BinhLuan> getRepliesByParentCommentId(Integer parentId);

    // Đăng bình luận mới
    @Transactional
    public BinhLuan saveComment(Integer baiGiangId, String noiDung, Integer taikhoanId);

    // Trả lời bình luận
    @Transactional
    public BinhLuan replyToComment(Integer baiGiangId, Integer parentCommentId, String noiDung, Integer taikhoanId);

    // Xóa bình luận
    @Transactional
    public boolean deleteComment(Integer binhLuanId, Integer currentUserId);

    public Optional<BinhLuan> getCommentById(Integer commentId);

    public List<BinhLuan> getAllCommentsByBaiGiangId(Integer baiGiangId);

}