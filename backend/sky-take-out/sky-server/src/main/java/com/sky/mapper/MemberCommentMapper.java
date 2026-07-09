package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.MemberCommentPageQueryDTO;
import com.sky.vo.MemberCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberCommentMapper {

    Page<MemberCommentVO> pageQuery(MemberCommentPageQueryDTO queryDTO);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void deleteById(Long id);
}
