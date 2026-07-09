package com.sky.controller.admin;

import com.sky.dto.MemberCommentPageQueryDTO;
import com.sky.dto.MemberFavoritePageQueryDTO;
import com.sky.dto.MemberPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.MemberService;
import com.sky.vo.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/member")
@Api(tags = "会员管理接口")
@Slf4j
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/page")
    @ApiOperation("会员分页查询")
    public Result<PageResult> page(MemberPageQueryDTO queryDTO) {
        return Result.success(memberService.page(queryDTO));
    }

    @GetMapping("/{id}")
    @ApiOperation("会员详情")
    public Result<MemberVO> getById(@PathVariable Long id) {
        return Result.success(memberService.getById(id));
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用会员")
    public Result<String> status(@PathVariable Integer status, Long id) {
        memberService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/comment/page")
    @ApiOperation("会员评论分页查询")
    public Result<PageResult> commentPage(MemberCommentPageQueryDTO queryDTO) {
        return Result.success(memberService.commentPage(queryDTO));
    }

    @PostMapping("/comment/status/{status}")
    @ApiOperation("显示隐藏会员评论")
    public Result<String> commentStatus(@PathVariable Integer status, Long id) {
        memberService.updateCommentStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/comment")
    @ApiOperation("删除会员评论")
    public Result<String> deleteComment(Long id) {
        memberService.deleteComment(id);
        return Result.success();
    }

    @GetMapping("/favorite/page")
    @ApiOperation("会员收藏分页查询")
    public Result<PageResult> favoritePage(MemberFavoritePageQueryDTO queryDTO) {
        return Result.success(memberService.favoritePage(queryDTO));
    }

    @DeleteMapping("/favorite")
    @ApiOperation("删除会员收藏")
    public Result<String> deleteFavorite(Long id) {
        memberService.deleteFavorite(id);
        return Result.success();
    }
}
