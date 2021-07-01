package com.ut.user.assignAuthmgr;

import com.google.common.base.Strings;
import com.ut.user.appmgr.AppDao;
import com.ut.user.authritymgr.AuthorityGroupService;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.constants.UserConstants;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserRelateAuthorityGroups;
import com.ut.user.usermgr.MyUserRelateAuthorityGroupsService;
import com.ut.user.usermgr.MyUserService;
import com.ut.user.utils.SpringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistributeAuthorityGroupImpl extends AbstractDistributeAuthority {

    @Override
    public void deleteAuth(String appKey, String username){
        SpringUtils.getBean(MyUserRelateAuthorityGroupsService.class).deleteAuthGroupRelateByUsername(appKey, username);
    }

    @Override
    public void checkAuthorityBoundary(String appKey, boolean hasDevAuth, Collection<String> authorityVo)throws Exception{
        Set<String> allMyAuths = new HashSet<>();
        if(hasDevAuth) {
            allMyAuths = SpringUtils.getBean(AuthorityGroupService.class).getAppAuthGroups(appKey);
        }else{
            allMyAuths = SpringUtils.getBean(MyUserService.class).getLoginUserAuthorities();
        }
        if(!allMyAuths.containsAll(authorityVo))
            throw new Exception(ExceptionContants.AUTHORITYGROUP_EXCEED_EXCEPTION);
    }

    @Override
    public boolean isChildForDeveloper(String appKey){
        MyUserService service = SpringUtils.getBean(MyUserService.class);
        MyUserEntity user = service.getSelf();
        if(Strings.isNullOrEmpty(user.getParentUser()))
            return false;

        //当前账户是否拥有分配权限的能力
        Set<String> myAuthorities = service.getLoginUserAuthorities();
        if(!myAuthorities.contains(UserConstants.BIND_AUTHGROUP_TO_USER))
            return false;
        if(null != SpringUtils.getBean(AppDao.class).findByAppKeyAndDeveloperAndStatus(appKey, user.getParentUser(), true))
            return true;
        return false;
    }

    /**
     * 给指定账户分配--角色(增/改)
     *
     * 逻辑：
     * authGroupIn 与 authGroupDb 对比：
     * 1）交集不做修改
     * 2）authGroupIn 与 authGroupDb 的差集 新增
     * 3）authGroupDb 与 authGroupIn 的差集 删除
     */
    @Override
    public void distribute(String username, String appKey,  Collection<String> authorityVo){
        MyUserRelateAuthorityGroupsService myUserRelateAuthorityGroupsService = SpringUtils.getBean(MyUserRelateAuthorityGroupsService.class);
        //用户角色绑定
        Set<String> authGroupIn = (Set)authorityVo;

        Set<String> result =  new HashSet<>();
        List<MyUserRelateAuthorityGroups> userAuths = myUserRelateAuthorityGroupsService.getUserRelateAuthGroupsByApp(username, appKey);
        Set<String> authGroupDb = new HashSet<>();
        userAuths.forEach((MyUserRelateAuthorityGroups userRelateAuthGroup) -> authGroupDb.add(userRelateAuthGroup.getAuthorityGroupKey()));

        result.clear();
        result.addAll(authGroupIn);
        result.removeAll(authGroupDb);
        result.forEach(authGroupAdd -> myUserRelateAuthorityGroupsService.addAuthGroupRelate(new MyUserRelateAuthorityGroups(username, authGroupAdd, appKey)));

        result.clear();
        result.addAll(authGroupDb);
        result.removeAll(authGroupIn);
        result.forEach(authGroupDel -> myUserRelateAuthorityGroupsService.deleteAuthGroupRelate(username, authGroupDel, appKey));
    }
}
