package com.ut.user;

import com.ut.user.appmgr.AppService;
import com.ut.user.authritymgr.AuthorityGroupRelateAuthoritiesService;
import com.ut.user.authritymgr.AuthorityGroupService;
import com.ut.user.authritymgr.AuthorityService;
import com.ut.user.constants.UserConstants;
import com.ut.user.usermgr.MyUserRelateAppsService;
import com.ut.user.usermgr.MyUserRelateAuthorityGroupsService;
import com.ut.user.usermgr.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 平台权限数据初始化
 */
@Service
public class SystemAuthorityInit {
    @Autowired
    MyUserService myUserService;
    @Autowired
    AppService appService;
    @Autowired
    AuthorityService authorityService;
    @Autowired
    AuthorityGroupService authorityGroupService;
    @Autowired
    MyUserRelateAppsService myUserRelateAppsService;
    @Autowired
    AuthorityGroupRelateAuthoritiesService relateService;
    @Autowired
    private MyUserRelateAuthorityGroupsService myUserRelateAuthorityGroupsService;

    @PostConstruct
    public void init() {

        myUserService.initUser("platform_admin", "Admin12#$", null,null, "platform_admin@xxx.com");//platform_admin用户
        myUserService.initUser("ios_review", null , "ios上架审查账户", UserConstants.REVIEW_USER_MOBILE, null);//ios上架审查账户
        myUserService.initUser("scheduler_developer", "Ut123456", "定时任务调度开发者", null, null);//ios上架审查账户


        //新建基础平台应用
        appService.initApp("platform", "platform_admin", "基础平台", "权限控制中心");
        //新建角色：平台管理员、开发者
        authorityGroupService.initAuthGroup("platform", "platform_admin", "平台管理员", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_developer", "开发者", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_developer_distributeAuthority", "具备开发者分配权限能力", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_accout_admin", "账户管理(餐饮超管专用)", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_utmodel_admin", "优模型管理员", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_utmodel_developer", "优模型开发员", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_driver_developer", "流程定制bin文件执行开发者", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_tokenConvert_developer", "token转换(仅开放开发者使用)", null, null);
        authorityGroupService.initAuthGroup("platform", "platform_getToken_developer", "子账户获取token(仅开放开发者使用)", null, null);

        /**
         * 平台菜单管理
         */
        //标题栏
        authorityService.initAuthority("platform", "platform_management", "平台管理", "");
        //菜单栏及功能
        authorityService.initAuthority("platform", "platform_app", "平台-应用列表", "");
        authorityService.initAuthority("platform", "platform_authority", "平台-功能管理", "");
        authorityService.initAuthority("platform", "platform_authorityGroup", "平台-角色管理", "");
        authorityService.initAuthority("platform", "platform_menu", "平台-菜单管理", "");
        authorityService.initAuthority("platform", "platform_user", "平台-授权管理", "");
        authorityService.initAuthority("platform", "platform_resetPwd", "平台-重置密码", "");
        authorityService.initAuthority("platform", "platform_pagePlatformApps", "分页平台应用列表", "");
        authorityService.initAuthority("platform", "platform_listPlatformApps", "平行平台应用列表", "");

        /**
         * 应用管理
         */
        //标题栏
        authorityService.initAuthority("platform", "platform_app_management", "应用管理", "");

        /*
         * 菜单栏及功能:应用管理/功能管理/角色管理/菜单管理/权限分配/额外授权
         */

        //应用管理
        authorityService.initAuthority("platform", "platform_app_appList", "应用列表", "");
        authorityService.initAuthority("platform", "platform_app_getApp", "获取应用", "");
        authorityService.initAuthority("platform", "platform_app_allApps", "获取全部应用", "");
        authorityService.initAuthority("platform", "platform_app_addApp", "新增应用", "");
        authorityService.initAuthority("platform", "platform_app_updateApp", "编辑应用", "");
        authorityService.initAuthority("platform", "platform_app_changeAppStatus", "修改应用状态", "");


        //功能管理
        authorityService.initAuthority("platform", "platform_app_authorityManagement", "功能管理", "");
        authorityService.initAuthority("platform", "platform_app_allAuthorities", "功能列表", "");
        authorityService.initAuthority("platform", "platform_app_addAuthority", "新增功能", "");
        authorityService.initAuthority("platform", "platform_app_deleteAuthority", "删除功能", "");

        //角色管理
        authorityService.initAuthority("platform", "platform_app_authorityGroupManagement", "角色管理", "");
        authorityService.initAuthority("platform", "platform_app_allAuthorityGroups", "角色列表", "");
        authorityService.initAuthority("platform", "platform_app_addAuthorityGroup", "新增角色", "");
        authorityService.initAuthority("platform", "platform_app_updateAuthorityGroup", "修改角色", "");
        authorityService.initAuthority("platform", "platform_app_deleteAuthorityGroup", "删除角色", "");
        authorityService.initAuthority("platform", "platform_app_bindAuth2AuthGroup", "角色功能绑定", "");

        //菜单管理
        authorityService.initAuthority("platform", "platform_app_menuManagement", "菜单管理", "");
        authorityService.initAuthority("platform", "platform_app_addMenu", "新增菜单", "");
        authorityService.initAuthority("platform", "platform_app_updateMenu", "修改菜单", "");
        authorityService.initAuthority("platform", "platform_app_getMenu", "查询菜单", "");
        authorityService.initAuthority("platform", "platform_app_allMenus", "查询菜单树", "");
        authorityService.initAuthority("platform", "platform_app_deleteMenu", "删除菜单", "");
        authorityService.initAuthority("platform", "platform_app_bindAuth2Menu", "菜单绑定功能", "");
        authorityService.initAuthority("platform", "platform_app_bindAuthGroup2Menu", "菜单绑定角色", "");

        //权限分配
        authorityService.initAuthority("platform", "platform_app_empowerManagement", "授权管理", "");
        authorityService.initAuthority("platform", "platform_app_createChildAccount", "创建子账户", "");
        authorityService.initAuthority("platform", "platform_app_bindAuth2User", "分配功能", "");
        authorityService.initAuthority("platform", "platform_app_bindAuthGroup2User", "分配角色", "");
        authorityService.initAuthority("platform", "platform_app_pageAllAuthorities", "获取指定应用已授权的用户列表", "");

        //子账户管理
        authorityService.initAuthority("platform", "platform_app_childAccountManagement", "子账户管理", "");
        authorityService.initAuthority("platform", "platform_app_childUsers", "子账户分配", "");


        //用户管理
        authorityService.initAuthority("platform","platform_user_batchRegister","批量注册","");
        authorityService.initAuthority("platform","platform_account_systemKey","账号体系管理","");


        //物模型
        authorityService.initAuthority("platform","platform_utmodel_servicemanager","优模型服务管理","");
        authorityService.initAuthority("platform","platform_utmodel_profilemanager","优模型档案管理","");
        authorityService.initAuthority("platform","platform_utmodel_eventmanager","优模型事件管理","");
        authorityService.initAuthority("platform","platform_utmodel_propertymanager","优模型属性管理","");

        //流程定制bin文件执行开发人员
        authorityService.initAuthority("platform", "platform_user_creatRandomChild", "创建当前账户的随机子账户", "");
        authorityService.initAuthority("platform", "platform_user_getToken", "子账户获取token", "");
        authorityService.initAuthority("platform", "platform_user_createSpecifiedChild", "创建指定账户的随机子账户", "");


        //物联token转换-开发人员权限
        authorityService.initAuthority("platform", "platform_user_checkAndRegister", "token转换", "暂时只对物联开发者账户使用");

        //=================================================== 角色权限分配===================================================
        myUserRelateAppsService.initUserRelateApp("platform", "platform_admin");
        myUserRelateAuthorityGroupsService.init("platform","platform_admin", "platform_admin");
        myUserRelateAuthorityGroupsService.init("platform","platform_admin", "platform_developer");
        myUserRelateAuthorityGroupsService.init("platform", "platform_admin", "platform_accout_admin");
        myUserRelateAuthorityGroupsService.init("platform", "platform_admin", "platform_utmodel_admin");
        myUserRelateAuthorityGroupsService.init("platform", "platform_admin", "platform_utmodel_developer");
        myUserRelateAuthorityGroupsService.init("platform", "platform_admin", "platform_driver_developer");

        //定时任务服务开发者权限分配
        myUserRelateAuthorityGroupsService.init("platform", "scheduler_developer", "platform_getToken_developer");


        //platform_admin角色授权-平台权限
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_management");//平台管理
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_app");//应用列表
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_authority");//功能管理
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_authorityGroup");//角色管理
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_menu");//菜单管理
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_user");//授权管理
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_resetPwd");//重置密码
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_pagePlatformApps");//分页平台应用列表
        relateService.initAuthGroupRealteAuth("platform", "platform_admin", "platform_listPlatformApps");//平行平台应用列表


        //developer角色授权
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_management");//应用管理

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_appList");//应用列表
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_getApp");//获取应用
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_allApps");//获取全部应用
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_addApp");//新增应用
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_updateApp");//编辑应用
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_changeAppStatus");//修改应用状态

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_authorityManagement");//功能管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_allAuthorities");//功能列表
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_addAuthority");//新增功能
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_deleteAuthority");//删除功能

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_authorityGroupManagement");//角色管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_allAuthorityGroups");//角色列表
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_addAuthorityGroup");//新增角色
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_updateAuthorityGroup");//修改角色
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_deleteAuthorityGroup");//删除角色
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_bindAuth2AuthGroup");//角色功能绑定

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_menuManagement");//菜单管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_addMenu");//新增菜单
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_updateMenu");//修改菜单
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_getMenu");//查询菜单
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_allMenus");//查询菜单树
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_deleteMenu");//删除菜单
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_bindAuth2Menu");//菜单绑定功能
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_bindAuthGroup2Menu");//菜单绑定角色

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_empowerManagement");//授权管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_createChildAccount");//创建子账户
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_bindAuth2User");//分配功能
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_bindAuthGroup2User");//分配角色
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_pageAllAuthorities");//获取指定应用已授权的用户列表

        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_childAccountManagement");//子账户管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer", "platform_app_childUsers");//子账户分配

        relateService.initAuthGroupRealteAuth("platform","platform_developer","platform_user_batchRegister");

        //具备开发者分配权限能力
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_bindAuth2User");//分配功能
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_bindAuthGroup2User");//分配角色
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_management");//应用管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_empowerManagement");//授权管理
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_allApps");//获取全部应用
        relateService.initAuthGroupRealteAuth("platform", "platform_developer_distributeAuthority", "platform_app_pageAllAuthorities");//获取指定应用已授权的用户列表


        //账户管理(餐饮超管专用)
        relateService.initAuthGroupRealteAuth("platform", "platform_accout_admin", "platform_account_systemKey");

        //优模型管理员
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_admin", "platform_utmodel_servicemanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_admin", "platform_utmodel_profilemanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_admin", "platform_utmodel_eventmanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_admin", "platform_utmodel_propertymanager");

//      //优模型开发者
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_developer", "platform_utmodel_servicemanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_developer", "platform_utmodel_profilemanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_developer", "platform_utmodel_eventmanager");
        relateService.initAuthGroupRealteAuth("platform", "platform_utmodel_developer", "platform_utmodel_propertymanager");
        //流程定制Bin文件执行
        relateService.initAuthGroupRealteAuth("platform", "platform_driver_developer", "platform_user_creatRandomChild");
        relateService.initAuthGroupRealteAuth("platform", "platform_driver_developer", "platform_user_getToken");
        relateService.initAuthGroupRealteAuth("platform", "platform_driver_developer", "platform_user_createSpecifiedChild");


        //token转换
        relateService.initAuthGroupRealteAuth("platform", "platform_tokenConvert_developer", "platform_user_checkAndRegister");
        relateService.initAuthGroupRealteAuth("platform", "platform_getToken_developer", "platform_user_getToken");

    }
}
