package com.ut.user.assignAuthmgr;

import com.google.common.base.Strings;
import com.ut.user.appmgr.AppService;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.usermgr.MyUserEntity;
import com.ut.user.usermgr.MyUserRelateAppsService;
import com.ut.user.usermgr.MyUserService;
import com.ut.user.utils.SpringUtils;

import java.util.Collection;

public abstract class AbstractDistributeAuthority {

    //分配权限流程
    public boolean distributeAuthorities(String username, String appKey, Collection<String> authorityVo)throws Exception{
        //前置检查
        checkBefore(appKey, username);

        //确认调用者身份：开发者，开发者子账户，普通账户
        boolean hasDevAuth = checkIdentity(appKey, username);

        //若不指定功能，则删除
        if(null == authorityVo || authorityVo.size() == 0) {
            deleteAuth(appKey, username);
            return true;
        }

        //校验是否超出权限
        checkAuthorityBoundary(appKey, hasDevAuth, authorityVo);

        //检查权限是否属于应用
        checkIsAppAuth(appKey, authorityVo);

        //关联应用
        SpringUtils.getBean(MyUserRelateAppsService.class).userBindApp(username, appKey);

        //分配权限
        distribute(username, appKey, authorityVo);
        return true;
    }

    public void deleteAuth(String appKey, String username){
    }

    public void checkBefore(String appKey, String username)throws Exception{
        if(Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(appKey))
            throw new Exception(String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "username, appKey"));
        if(!SpringUtils.getBean(AppService.class).checkAppAlive(appKey))
            throw new Exception("应用已关闭 或 您无权操作该应用！");
        if(null == SpringUtils.getBean(MyUserService.class).getUserByKeyword(username))
            throw new Exception(username + "用户不存在！");
    }

    public boolean checkIdentity(String appKey, String username)throws Exception{
        //是否开发者
        if(SpringUtils.getBean(AppService.class).isAppDeveloper(appKey))
            return true;
        //自身是有拥有授权能力，且该应用是其父账户所属
        if(isChildForDeveloper(appKey))
            return true;

        //非开发者角色，需要检查是否父子账户关系
        checkIsChildUser(username);
        return false;
    }

    public boolean isChildForDeveloper(String appKey){
        return false;
    }

    public void checkIsChildUser(String username) throws Exception {
        MyUserEntity user = SpringUtils.getBean(MyUserService.class).getByUsernameOrLoginName(username);
        if(null == user.getParentUser())
            throw new Exception(String.format(ExceptionContants.NOT_CHILDREN_EXCEPTION, username));
        if(!user.getParentUser().equals( SpringUtils.getBean(MyUserService.class).getLoginUsername()))
            throw new Exception(String.format(ExceptionContants.NOT_CHILDREN_EXCEPTION, username));
    }

    public void checkAuthorityBoundary(String appKey, boolean hasDevAuth, Collection<String> authorityVo)throws Exception{

    }

    public void checkIsAppAuth(String appKey, Collection<String> authorityVo)throws Exception{
        for(String authKey : authorityVo){
            if(!appKey.equals(authKey.split("_")[0]))
                throw new Exception(ExceptionContants.AUTHORITY_EXCEED_EXCEPTION);
        }
    }

    public void distribute(String username, String appKey,  Collection<String> authorityVo){

    }
}
