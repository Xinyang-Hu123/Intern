package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.MemberFavoritePageQueryDTO;
import com.sky.vo.MemberFavoriteVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberFavoriteMapper {

    Page<MemberFavoriteVO> pageQuery(MemberFavoritePageQueryDTO queryDTO);

    void deleteById(Long id);
}
