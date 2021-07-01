package com.ut.user.assignAuthmgr;

import com.google.common.base.Strings;
import com.ut.user.appmgr.AppDao;
import com.ut.user.authritymgr.AuthorityService;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.constants.UserConstants;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserRelateAuthorities;
import com.ut.user.usermgr.MyUserRelateAuthoritiesService;
import com.ut.user.usermgr.MyUserService;
import com.ut.user.utils.SpringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistributeAuthorityImpl extends AbstractDistributeAuthority {

    @Override
    public void deleteAuth(String appKey, String username){
        SpringUtils.getBean(MyUserRelateAuthoritiesService.class).deleteAuthRelateByUser(appKey, username);
    }

    @Override
    public void checkAuthorityBoundary(String appKey, boolean hasDevAuth, Collection<String> authorityVo)throws Exception{
        Set<String> allMyAuths = new HashSet<>();
        if(hasDevAuth) {
            allMyAuths = SpringUtils.getBean(AuthorityService.class).getAppAuths(appKey);
        }else{
            allMyAuths = SpringUtils.getBean(MyUserService.class).getLoginUserAuthorities();
        }
        if(!allMyAuths.containsAll(authorityVo))
            throw new Exception(ExceptionContants.AUTHORITY_EXCEED_EXCEPTION);
    }

    @Override
    public boolean isChildForDeveloper(String appKey){
        MyUserService service = SpringUtils.getBean(MyUserService.class);
        MyUserEntity user = service.getSelf();
        if(Strings.isNullOrEmpty(user.getParentUser()))
            return false;

        //当前账户是否拥有分配权限的能力
        Set<String> myAuthorities = service.getLoginUserAuthorities();
        if(!myAuthorities.contains(UserConstants.BIND_AUTH_TO_USER))
            return false;
        if(null != SpringUtils.getBean(AppDao.class).findByAppKeyAndDeveloperAndStatus(appKey, user.getParentUser(), true))
            return true;
        return false;
    }

    /**
     * 给指定账户分配--功能(增/改)
     *
     * 逻辑：
     * authIn 与 authDb 对比：
     * 1）交集不做修改
     * 2）authIn 与 authDb 的差集 新增
     * 3）authDb 与 authIn 的差集 删除
     */
    @Override
    public void distribute(String username, String appKey,  Collection<String> authorityVo){
        MyUserRelateAuthoritiesService myUserRelateAuthoritiesService = SpringUtils.getBean(MyUserRelateAuthoritiesService.class);
        //功能授权
        Set<String> authIn = (Set)authorityVo;

        Set<String> result =  new HashSet<>();
        List<MyUserRelateAuthorities> userAuths = myUserRelateAuthoritiesService.getUserRelateAuthsByApp(username, appKey);
        Set<String> authDb = new HashSet<>();
        userAuths.forEach((MyUserRelateAuthorities userRelateAuth) -> authDb.add(userRelateAuth.getAuthorityKey()));

        result.clear();
        result.addAll(authIn);
        result.removeAll(authDb);
        result.forEach(authAdd -> myUserRelateAuthoritiesService.addAuthRelate(new MyUserRelateAuthorities(username, authAdd, appKey)));

        result.clear();
        result.addAll(authDb);
        result.removeAll(authIn);
        result.forEach(authDel -> myUserRelateAuthoritiesService.deleteAuthRelate(username, authDel, appKey));
    }
}
