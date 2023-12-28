package com.youlan.system.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.system.entity.Dept;
import com.youlan.system.entity.Org;
import com.youlan.system.entity.User;
import com.youlan.system.entity.dto.DeptDTO;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.DeptVO;
import com.youlan.system.enums.OrgType;
import com.youlan.system.service.DeptService;
import com.youlan.system.service.OrgService;
import com.youlan.system.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DeptBizService {
    private final OrgService orgService;
    private final DeptService deptService;
    private final UserService userService;

    /**
     * 部门新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDept(DeptDTO dto) {
        dto.setOrgType(OrgType.DEPT.getCode());
        Org org = orgService.addOrg(dto);
        Dept dept = BeanUtil.copyProperties(dto, Dept.class);
        dept.setOrgId(org.getOrgId());
        deptService.save(dept);
    }

    /**
     * 部门修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDept(DeptDTO dto) {
        dto.setOrgType(OrgType.DEPT.getCode());
        orgService.updateOrg(dto);
        deptService.updateById(BeanUtil.copyProperties(dto, Dept.class));
    }

    /**
     * 部门删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDept(List<Long> ids) {
        for (Long id : ids) {
            if (ObjectUtil.isNull(id)) {
                continue;
            }
            Dept dept = deptService.loadDeptNotNull(id);
            //包含子机构则不允许删除;
            boolean childExists = orgService.exists(Wrappers.<Org>lambdaQuery().eq(Org::getParentOrgId, dept.getOrgId()));
            if (childExists) {
                throw new BizRuntimeException("存在下级机构时不能删除");
            }
            //包含关联用户则不允许删除
            boolean userExists = userService.exists(Wrappers.<User>lambdaQuery().eq(User::getOrgId, dept.getOrgId()));
            if (userExists) {
                throw new BizRuntimeException("机构已绑定用户时不能删除");
            }
            orgService.removeById(dept.getOrgId());
            deptService.removeById(id);
        }
    }

    /**
     * 部门详情
     */
    public DeptVO loadDept(Long id) {
        DeptVO dept = deptService.loadOne(id, DeptVO.class);
        orgService.loadOne(dept.getOrgId(), dept);
        return dept;
    }

    /**
     * 部门分页
     */
    public IPage<DeptVO> getDeptPageList(OrgPageDTO dto) {
        return deptService.getBaseMapper().getDeptList(DBHelper.getPage(dto), dto);
    }

    /**
     * 部门树列表
     */
    public List<DeptVO> getDeptTreeList(OrgPageDTO dto) {
        List<DeptVO> deptList = deptService.getBaseMapper().getDeptList(dto);
        return ListHelper.getTreeList(deptList, DeptVO::getChildren, DeptVO::getOrgId, DeptVO::getParentOrgId, DeptVO::getOrgSort);
    }
}
