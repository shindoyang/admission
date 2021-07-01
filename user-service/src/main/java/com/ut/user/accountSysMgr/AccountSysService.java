package com.ut.user.accountSysMgr;

import com.google.common.base.Strings;
import com.ut.user.appmgr.AppDao;
import com.ut.user.appmgr.AppService;
import com.ut.user.authritymgr.AuthorityGroupDao;
import com.ut.user.authritymgr.AuthorityGroupEntity;
import com.ut.user.constants.ExceptionContants;
import com.ut.user.constants.UserConstants;
import com.ut.user.exception.UtException;
import com.ut.user.exception.UtExceptionEnum;
import com.ut.user.usermgr.*;
import com.ut.user.vo.AccountAuthorityGroupVO;
import com.ut.user.vo.AccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ut.user.constants.UserConstants.*;

/**
 * @author chenglin
 * @creat 2019/9/16
 */
@Service
public class AccountSysService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MyUserDao myUserDao;
    @Autowired
    MyUserExtendDao myUserExtendDao;
    @Autowired
    MyUserService myUserService;
    @Autowired
    MyUserRelateAppsDao myUserRelateAppsDao;
    @Autowired
    MyUserRelateAuthorityGroupsDao myUserRelateAuthorityGroupsDao;
    @Autowired
    MyUserRelateAuthorityGroupsService myUserRelateAuthorityGroupsService;
    @Autowired
    AppDao appDao;
    @Autowired
    AuthorityGroupDao authorityGroupDao;
    @Autowired
    private AppService appService;

    @Transactional
    @PreAuthorize("hasAuthority('platform_account_systemKey')")
    public void creatAccount(AccountVO accountVO) throws Exception {
        if (Strings.isNullOrEmpty(accountVO.getUsername()) || Strings.isNullOrEmpty(accountVO.getPassword()))
            throw new UtException(UtExceptionEnum.PARAM_NOT_NULL, "用户名或密码不能为空！");
        String username = accountVO.getUsername().trim();
        MyUserEntity myUserEntity = myUserDao.findByUsername(username);
        if (null != myUserEntity)
            throw new UtException(UtExceptionEnum.USERNAME_ALEADY_EXIST, username + ExceptionContants.USERNAME_ALEADY_EXIST);
        if (!username.matches(REGEX_USERNAME))
            throw new UtException(UtExceptionEnum.USERNAME_ERR_MSG);
        if (!accountVO.getPassword().matches(REGEX_PWD))
            throw new UtException(UtExceptionEnum.PWD_ERR_MSG);
        boolean setMobile = false;
        if (!Strings.isNullOrEmpty(accountVO.getMobile())) {
            if (!accountVO.getMobile().matches(REGEX_MOBILE))
                throw new UtException(UtExceptionEnum.MOBILE_ERR_MSG);
            if (null != myUserService.getUserByMobile(accountVO.getMobile()))
                throw new UtException(UtExceptionEnum.MOBILE_USED_EXCEPTION, String.format(ExceptionContants.MOBILE_USED_EXCEPTION, accountVO.getMobile()));
            setMobile = true;
        }

        //新增用户
        myUserEntity = new MyUserEntity();
        myUserEntity.setUsername(username);
        myUserEntity.setLoginName(username);
        myUserEntity.setAccountSystemKey(UserConstants.DEFAULT_SYSTEM_KEY);
        myUserEntity.setPassword(passwordEncoder.encode(accountVO.getPassword().trim()));
        if (!Strings.isNullOrEmpty(accountVO.getNickname()))
            myUserEntity.setNickname(accountVO.getNickname().trim());
        if (setMobile)
            myUserEntity.setMobile(accountVO.getMobile().trim());
        if (!Strings.isNullOrEmpty(accountVO.getActivated()))
            myUserEntity.setActivated("1".equalsIgnoreCase(accountVO.getActivated()) ? true : false);

        if (null != accountVO.getAccountAuthorityGroupVO()) {
            //分配角色
            distributeAuthGroup(accountVO.getAccountAuthorityGroupVO(), username);
        }

        myUserDao.save(myUserEntity);

        //新增扩展信息
        MyUserExtendEntity myUserExtendEntity = new MyUserExtendEntity();
        myUserExtendEntity.setUsername(username);
        if (!Strings.isNullOrEmpty(accountVO.getLogo()))
            myUserExtendEntity.setLogo(accountVO.getLogo().trim());
        myUserExtendDao.save(myUserExtendEntity);
    }

    /**
     * 分配角色
     */
    private void distributeAuthGroup(AccountAuthorityGroupVO accountAuthorityGroupVO, String username) throws Exception {
        if (null == accountAuthorityGroupVO.getAppKey() || null == accountAuthorityGroupVO.getAuthorityGroupKey())
            throw new UtException(UtExceptionEnum.PARAM_NOT_NULL, String.format(ExceptionContants.PARAM_NOTNULL_EXCEPTION, "appKey 和 authorityGroupKey"));
        String appKey = accountAuthorityGroupVO.getAppKey().trim();
        String authGroupkey = accountAuthorityGroupVO.getAuthorityGroupKey().trim();
        if (null == appDao.findByAppKey(appKey))
            throw new UtException(UtExceptionEnum.APP_NOT_EXIXT);
        if (null == authorityGroupDao.findByAppKeyAndAuthorityGroupKey(appKey, authGroupkey))
            throw new UtException(UtExceptionEnum.AUTHORITYGROUP_NOT_BELONGTO_APP, authGroupkey + "角色不属于" + appKey + "应用！");
        MyUserRelateApps myUserRelateApps = myUserRelateAppsDao.findByUsernameAndAppKey(username, accountAuthorityGroupVO.getAppKey());
        if (null == myUserRelateApps) {
            myUserRelateApps = new MyUserRelateApps();
            myUserRelateApps.setUsername(username);
            myUserRelateApps.setAppKey(appKey);
            myUserRelateAppsDao.save(myUserRelateApps);
        }
        distributeAuthGroup(username, accountAuthorityGroupVO.getAppKey(), accountAuthorityGroupVO.getAuthorityGroupKey());
    }

    private void distributeAuthGroup(String username, String appKey, String authGroupKey) {
        //用户角色绑定
        Set<String> authGroupIn = new HashSet<>();
        authGroupIn.add(authGroupKey);

        Set<String> result = new HashSet<>();
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

    /**
     * 修改指定用户信息
     */
    @PreAuthorize("hasAuthority('platform_account_systemKey')")
    @Transactional
    public void updateAccountInfo(AccountVO accountVO) throws Exception {
        if (Strings.isNullOrEmpty(accountVO.getUsername()))
            throw new UtException(UtExceptionEnum.USERNAME_NOT_NULL);
        String username = accountVO.getUsername().trim();
        MyUserEntity user = myUserService.getUserByUsername(username);
        if (user == null)
            throw new UtException(UtExceptionEnum.USER_NOT_EXIXT);
        if (!Strings.isNullOrEmpty(accountVO.getPassword()))
            user.setPassword(passwordEncoder.encode(accountVO.getPassword().trim()));

        if (!Strings.isNullOrEmpty(accountVO.getMobile())) {
            if (!accountVO.getMobile().matches(REGEX_MOBILE))
                throw new UtException(UtExceptionEnum.MOBILE_ERR_MSG);

            //未设置过手机号，直接保存
            if (Strings.isNullOrEmpty(user.getMobile())) {
                MyUserEntity userByMobile = myUserService.getUserByMobile(accountVO.getMobile());
                if (null != userByMobile)
                    throw new UtException(UtExceptionEnum.MOBILE_USED_EXCEPTION, String.format(ExceptionContants.MOBILE_USED_EXCEPTION, accountVO.getMobile()));

                user.setMobile(accountVO.getMobile().trim());
            } else {
                //如果换了新号码，要校验是否占用
                if (!user.getMobile().equals(accountVO.getMobile().trim())) {
                    MyUserEntity userByMobile = myUserService.getUserByMobile(accountVO.getMobile());
                    if (null != userByMobile)
                        throw new UtException(UtExceptionEnum.MOBILE_USED_EXCEPTION, String.format(ExceptionContants.MOBILE_USED_EXCEPTION, accountVO.getMobile()));

                    user.setMobile(accountVO.getMobile().trim());
                }
            }

        }

        if (!Strings.isNullOrEmpty(accountVO.getNickname()))
            user.setNickname(accountVO.getNickname().trim());
        if (!Strings.isNullOrEmpty(accountVO.getLogo())) {
            MyUserExtendEntity userExtendEntity = myUserExtendDao.findByUsername(username);
            if (null == userExtendEntity) {
                userExtendEntity = new MyUserExtendEntity();
                userExtendEntity.setUsername(username);
            }
            userExtendEntity.setLogo(accountVO.getLogo().trim());
            myUserExtendDao.save(userExtendEntity);
        }
        if (!Strings.isNullOrEmpty(accountVO.getActivated()))
            user.setActivated("1".equalsIgnoreCase(accountVO.getActivated().trim()) ? true : false);

        myUserDao.save(user);

        if (null != accountVO.getAccountAuthorityGroupVO()) {
            //分配角色
            distributeAuthGroup(accountVO.getAccountAuthorityGroupVO(), username);
        }
    }

    @PreAuthorize("hasAuthority('platform_account_systemKey')")
    public List<AccountVO> listUserInfo(String appKey, List<String> usernameList) throws Exception {
        if (null == usernameList || 0 == usernameList.size())
            throw new UtException(UtExceptionEnum.PARAM_NOT_NULL, "请指定要查询的用户！");
        List<AccountVO> result = new ArrayList<>();
        for (String username : usernameList) {
            AccountVO accountVO = new AccountVO();
            MyUserEntity user = myUserService.getUserByUsername(username);
            if (null == user)
                continue;
            BeanUtils.copyProperties(user, accountVO);
            accountVO.setPassword(null);
            accountVO.setActivated(user.isActivated() ? "1" : "0");

            //找头像
            MyUserExtendEntity userExtend = myUserExtendDao.findByUsername(username);
            if (null != userExtend){
                if(null != userExtend.getLogo()){
                    accountVO.setLogo(userExtend.getLogo());
                }
                if(null != userExtend.getSex()){
                    accountVO.setSex(userExtend.getSex());
                }
            }

            //找角色
            if (!Strings.isNullOrEmpty(appKey)) {
                if (!appService.isAppDeveloper(appKey))
                    throw new UtException(UtExceptionEnum.NO_DATA_AUTHORITY, "你无权查看" + appKey + "应用所属数据");

                List<MyUserRelateAuthorityGroups> userAuths = myUserRelateAuthorityGroupsService.getUserRelateAuthGroupsByApp(username, appKey);
                if (userAuths.size() > 0) {
                    AuthorityGroupEntity authGroup = authorityGroupDao.findByAppKeyAndAuthorityGroupKey(appKey, userAuths.get(0).getAuthorityGroupKey());
                    if (null != authGroup) {
                        AccountAuthorityGroupVO accountAuthorityGroupVO = new AccountAuthorityGroupVO();
                        accountAuthorityGroupVO.setAppKey(appKey);
                        accountAuthorityGroupVO.setAuthorityGroupKey(authGroup.getAuthorityGroupKey());
                        accountAuthorityGroupVO.setAuthorityGroupName(authGroup.getAuthorityGroupName());
                        accountVO.setAccountAuthorityGroupVO(accountAuthorityGroupVO);
                    }

                }
            }
            result.add(accountVO);
        }
        return result;
    }

    @PreAuthorize("hasAuthority('platform_account_systemKey')")
    public Page<MyUserRelateAuthorityGroups> queryUserByAuthGroup(String appKey, String authGroupKey, Pageable pageable) throws Exception {
        if (Strings.isNullOrEmpty(appKey) || Strings.isNullOrEmpty(authGroupKey))
            throw new UtException(UtExceptionEnum.PARAM_NOT_NULL, "应用Key或角色Key不能为空！");
        if (!appService.isAppDeveloper(appKey))
            throw new UtException(UtExceptionEnum.NO_DATA_AUTHORITY, "你无权查看" + appKey + "应用所属数据");
        if (null == authorityGroupDao.findByAppKeyAndAuthorityGroupKey(appKey, authGroupKey))
            throw new UtException(UtExceptionEnum.AUTHORITYGROUP_NOT_CREATE, appKey + "应用未创建" + authGroupKey + "角色！");
        return myUserRelateAuthorityGroupsDao.findByAppKeyAndAuthorityGroupKey(appKey, authGroupKey, pageable);
    }
}
