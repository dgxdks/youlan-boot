package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.controller.base.BaseController;
import com.youlan.system.anno.OperationLog;
import com.youlan.system.constant.OperationLogType;
import com.youlan.system.entity.Post;
import com.youlan.system.service.PostService;
import com.youlan.system.service.biz.PostBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "岗位管理")
@RestController
@RequestMapping("/system/post")
@AllArgsConstructor
public class PostController extends BaseController {
    private final PostService postService;
    private final PostBizService postBizService;

    @SaCheckPermission("system:post:add")
    @Operation(summary = "岗位新增")
    @PostMapping("/addPost")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    public ApiResult addPost(@Validated @RequestBody Post post) {
        postBizService.addPost(post);
        return toSuccess();
    }

    @SaCheckPermission("system:post:update")
    @Operation(summary = "岗位修改")
    @PostMapping("/updatePost")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updatePost(@Validated @RequestBody Post post) {
        if (ObjectUtil.isNull(post.getId())) {
            return toError(ApiResultCode.C0001);
        }
        postBizService.updatePost(post);
        return toSuccess();
    }

    @SaCheckPermission("system:post:remove")
    @Operation(summary = "岗位删除")
    @PostMapping("/removePost")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removePost(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        postBizService.removePost(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:post:load")
    @Operation(summary = "岗位详情")
    @PostMapping("/loadPost")
    public ApiResult loadPost(@RequestParam Long id) {
        return toSuccess(postService.loadOne(id));
    }

    @SaCheckPermission("system:post:list")
    @Operation(summary = "岗位分页")
    @PostMapping("/getPostPageList")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getPostPageList(@RequestBody Post post) {
        return toSuccess(postService.loadPage(post, DBHelper.getQueryWrapper(post)));
    }

    @SaCheckPermission("system:post:list")
    @Operation(summary = "岗位列表")
    @PostMapping("/getPostList")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_LIST)
    public ApiResult getPostList(@RequestBody Post post) {
        return toSuccess(postService.loadMore(DBHelper.getQueryWrapper(post)));
    }

    @SaCheckPermission("system:post:export")
    @Operation(summary = "岗位导出")
    @PostMapping("/exportPostList")
    @OperationLog(name = "岗位", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportPostList(@RequestBody Post dto, HttpServletResponse response) throws IOException {
        List<Post> postList = postService.loadMore(DBHelper.getQueryWrapper(dto));
        toExcel("岗位.xlsx", "岗位", Post.class, postList, response);
    }

}
