package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.MemberCommentPageQueryDTO;
import com.sky.dto.MemberFavoritePageQueryDTO;
import com.sky.dto.MemberPageQueryDTO;
import com.sky.mapper.MemberCommentMapper;
import com.sky.mapper.MemberFavoriteMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.MemberService;
import com.sky.vo.MemberCommentVO;
import com.sky.vo.MemberFavoriteVO;
import com.sky.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MemberCommentMapper memberCommentMapper;

    @Autowired
    private MemberFavoriteMapper memberFavoriteMapper;

    @Override
    public PageResult page(MemberPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<MemberVO> page = userMapper.pageQuery(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public MemberVO getById(Long id) {
        return userMapper.getMemberById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    @Override
    public PageResult commentPage(MemberCommentPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<MemberCommentVO> page = memberCommentMapper.pageQuery(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateCommentStatus(Long id, Integer status) {
        memberCommentMapper.updateStatus(id, status);
    }

    @Override
    public void deleteComment(Long id) {
        memberCommentMapper.deleteById(id);
    }

    @Override
    public PageResult favoritePage(MemberFavoritePageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<MemberFavoriteVO> page = memberFavoriteMapper.pageQuery(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteFavorite(Long id) {
        memberFavoriteMapper.deleteById(id);
    }
}
