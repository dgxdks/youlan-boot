package com.youlan.system.service.biz;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.system.entity.Post;
import com.youlan.system.entity.UserPost;
import com.youlan.system.service.PostService;
import com.youlan.system.service.UserPostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PostBizService {
    private final PostService postService;
    private final UserPostService userPostService;

    /**
     * 岗位新增
     */
    public void addPost(Post post) {
        beforeAddOrgUpdatePost(post);
        postService.save(post);
    }

    /**
     * 岗位修改
     */
    public void updatePost(Post post) {
        beforeAddOrgUpdatePost(post);
        postService.updateById(post);
    }


    /**
     * 岗位删除*
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePost(List<Long> ids) {
        for (Long id : ids) {
            boolean postExists = userPostService.exists(Wrappers.<UserPost>lambdaQuery().eq(UserPost::getPostId, id));
            if (postExists) {
                throw new BizRuntimeException("岗位已绑定用户时不能删除");
            }
        }
        postService.removeBatchByIds(ids);
    }

    /**
     * 新增或修改前置操作
     */
    public void beforeAddOrgUpdatePost(Post post) {
        // 岗位名称不能重复
        LambdaQueryWrapper<Post> postNameWrapper = Wrappers.<Post>lambdaQuery()
                .eq(Post::getPostName, post.getPostName())
                .ne(ObjectUtil.isNotNull(post.getId()), Post::getId, post.getId());
        boolean postNameExists = postService.exists(postNameWrapper);
        if (postNameExists) {
            throw new BizRuntimeException("岗位名称已存在");
        }
        // 岗位编码不能重复
        LambdaQueryWrapper<Post> postCodeWrapper = Wrappers.<Post>lambdaQuery()
                .eq(Post::getPostCode, post.getPostCode())
                .ne(ObjectUtil.isNotNull(post.getId()), Post::getId, post.getId());
        boolean postCodeExists = postService.exists(postCodeWrapper);
        if (postCodeExists) {
            throw new BizRuntimeException("岗位编码已存在");
        }
    }
}
