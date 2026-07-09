package com.sky.service;

import com.sky.dto.MemberCommentPageQueryDTO;
import com.sky.dto.MemberFavoritePageQueryDTO;
import com.sky.dto.MemberPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.MemberVO;

public interface MemberService {

    PageResult page(MemberPageQueryDTO queryDTO);

    MemberVO getById(Long id);

    void updateStatus(Long id, Integer status);

    PageResult commentPage(MemberCommentPageQueryDTO queryDTO);

    void updateCommentStatus(Long id, Integer status);

    void deleteComment(Long id);

    PageResult favoritePage(MemberFavoritePageQueryDTO queryDTO);

    void deleteFavorite(Long id);
}
