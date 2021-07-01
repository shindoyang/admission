package com.ut.user.controller;

import com.google.common.base.Strings;
import com.ut.user.questionmgr.QuestionVo;
import com.ut.user.questionmgr.ResetQuestionEntity;
import com.ut.user.thirdauth.SocialAccountDTO;
import com.ut.user.thirdauth.SocialAccountProvider;
import com.ut.user.thirdauth.SocialAccountService;
import com.ut.user.usermgr.MyUserExtendEntity;
import com.ut.user.usermgr.MyUserService;
import com.ut.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 14:40 2017-11-16
 */

@RestController
@RequestMapping("/user")
@Api(description="用户信息管理", tags= {"UserController"})
public class UserController {

    @Autowired
    MyUserService myUserService;

    @Autowired
    SocialAccountProvider socialAccountProvider;

    @GetMapping("/curUser")
    @ApiOperation(value = "登录用户信息【公用】")
    public UserVo getLoginUser() {
        return myUserService.getLoginUser();
    }

    @GetMapping("/user")
    @ApiOperation(value = "账户名查找账户(支持username/loginName/mobile)")
    public UserVo getUser(String arg) throws Exception {
        return myUserService.getUser(arg.trim());
    }

    @GetMapping("/userExtend")
    @ApiOperation(value = "获取用户扩展信息(支持username/loginname/mobile)")
    public UserExtendVO getUserExtend(String username) throws Exception {
        return myUserService.getUserExtend(username.trim());
    }

    @PostMapping("/password")
    @ApiOperation(value = "修改用户密码【公用】")
    public boolean changePswd(String oldPwd, String newPwd) throws Exception {
        return myUserService.changePswd(oldPwd.trim(), newPwd.trim());
    }

    @PostMapping("/resetPasswordByQuestion")
    @ApiOperation(value = "重置用户密码-密保方式")
    public boolean resetPasswordByQuestion(String questionId, String answer, String newPwd) throws Exception {
        return myUserService.resetPasswordByQuestion(questionId, answer.trim(), newPwd.trim());
    }

    @PostMapping("/resetPasswordByMobile")
    @ApiOperation(value = "重置用户密码-短信验证码方式")
    public boolean resetPasswordByMobile(@RequestBody PasswordVo passwordVo) throws Exception {
        return myUserService.resetPasswordByMobile(passwordVo.getMessageId().trim(), passwordVo.getSmsCode().trim(), passwordVo.getNewPwd().trim());
    }

    @PostMapping("/nickname")
    @ApiOperation(value = "修改用户昵称【公用】")
    public boolean updateNickname(String nickname) throws Exception {
        return myUserService.changeNickname(nickname);
    }

    @PostMapping("/userExtend")
    @ApiOperation(value = "新增登录用户用户扩展信息【公用】")
    public boolean newUserExtend(@RequestBody MyUserExtendEntity myUserExtendEntity) throws Exception {
        return myUserService.newUserExtend(myUserExtendEntity);
    }

    @GetMapping("/questionList")
    @ApiOperation(value = "获取系统密保问题列表【公用】")
    public List<ResetQuestionEntity> questionList() {
        return myUserService.questionList();
    }

    @GetMapping("/myQuestion")
    @ApiOperation(value = "获取当前用户已设置的密保问题【公用】")
    public List<QuestionVo> myQuestion() throws Exception {
        return myUserService.myQuestion();
    }

    @PostMapping("/questionAnswer")
    @ApiOperation(value = "设置密保问题答案【公用】")
    public boolean questionAnswer(String questionId, String answer) throws Exception {
        return myUserService.questionAnswer(questionId, answer.trim());
    }

    @PostMapping("/checkQuestion")
    @ApiOperation(value = "校验密保问题答案【公用】")
    public boolean checkQuestion(String questionId, String answer) throws Exception {
        return myUserService.checkQuestion(questionId, answer.trim());
    }

    @PostMapping("/resetQuestionAnswer")
    @ApiOperation(value = "修改密保问题【公用】")
    public boolean resetQuestionAnswer(String oldQuestionId, String newQuestionId, String answer) throws Exception {
        return myUserService.resetQuestionAnswer(oldQuestionId, newQuestionId, answer.trim());
    }

    @PutMapping("/bindMobile")
    @ApiOperation(value = "绑定手机号【新接口  用户名密码注册账户使用】")
    public boolean bindMobile(String appPrefix, String messageId, String smsCode, String phoneNo) throws Exception {
        return myUserService.bindMobile(appPrefix.trim(), messageId.trim(), smsCode.trim(), phoneNo.trim());
    }

    @Deprecated
    @PutMapping("/bindPhoneNo")
    @ApiOperation(value = "绑定手机号【餐饮组用于更换手机，待弃用】")
    public boolean bindPhoneNo(String phoneNo) throws Exception {
        return myUserService.bindPhoneNo(phoneNo);
    }

    @PutMapping("/changeMobile")
    @ApiOperation(value = "更换已绑定的手机号")
    public boolean changeMobile(@RequestBody SmsCodeVo smsCodeVo) throws Exception {
        return myUserService.changeMobile(smsCodeVo);
    }

    @PostMapping("/setPassword")
    @ApiOperation(value = "设置密码")
    public boolean setLoginNameAndPassword(String password, String messageId, String smsCode) throws Exception {
        return myUserService.setPassword(password.trim(), messageId.trim(), smsCode.trim());
    }

    @PostMapping("/setLoginName")
    @ApiOperation(value = "设置登录名")
    public void setLoginName(String loginName) throws Exception {
        myUserService.setLoginName(loginName);
    }

    @PostMapping("/batchRegister")
    @ApiOperation(value = "批量注册用户名密码注册")
    public boolean batchRegister(@RequestBody List<BatchUserVo> batchUserVos) throws Exception {
        return myUserService.batchRegister(batchUserVos);
    }

    /**
     * 为支撑单独解绑逻辑，绑定的时候，会关联用户与appId及其社交账户类型的关系。
     * update ： 2021-04-21
     */
    @PutMapping("/bindSocialAccount")
    @ApiOperation(value = "绑定社交账号【绑定类型支持小程序/微信】")
    public void bindSocialAccount(@RequestBody SocialAccountDTO socialAccountDTO) throws Exception {
        SocialAccountService accountService = socialAccountProvider.getAccountService(socialAccountDTO.getSocialAccountType());
        accountService.bindSocialAccount(socialAccountDTO.getSocialAccountKey());
    }

    @DeleteMapping("/unbindSocialAccount")
    @ApiOperation(value = "解绑当前用户的社交账号【解绑类型支持微信/所有小程序】")
    public boolean unbindThirdAccount(String socialAccountType) {
        SocialAccountService accountService = socialAccountProvider.getAccountService(socialAccountType);
        return accountService.unbindThirdAccount();
    }

    @DeleteMapping("/unbindSocialAccountByAppId")
    @ApiOperation(value = "解绑指定的社交账号【解绑类型支持微信/所有小程序】")
    public boolean unbindSocialAccountByAppId(String appId, String socialAccountType)throws Exception {
        if(Strings.isNullOrEmpty(appId) || Strings.isNullOrEmpty(socialAccountType)){
            throw new Exception("appId 和 socialAccountType不能为空");
        }
        SocialAccountService accountService = socialAccountProvider.getAccountService(socialAccountType);
        return accountService.unbindSocialAccountByAppId(appId, socialAccountType);
    }

    @GetMapping("/bindStatus")
    @ApiOperation(value = "获取当前用户所有社交账号的绑定状态")
    public BindStatuVO getBindStatus() throws Exception {
        return myUserService.getBindStatus();
    }

    @PostMapping("/resetPassword")
    @ApiOperation(value = "重置用户密码")
    public String resetPassword(String username) throws Exception {
        return myUserService.resetPassword(username.trim());
    }

    @GetMapping("/getParentUsername")
    @ApiOperation(value = "获取父账户名")
    public String getMyParentUsername() {
        return myUserService.getSelf().getParentUser();
    }

    @PostMapping("/changeUserStatus")
    @ApiOperation(value = "修改用户状态【子账户管理使用】")
    public boolean changeUserStatus(String username, boolean activated) throws Exception {
        return myUserService.changeUserStatus(username, activated);
    }

    //	创建指定账户的子账户
    @PostMapping("/createSpecifiedChild")
    @ApiOperation(value = "创建指定账户的子账户【bin执行专用】", position = 23)
    public String createSpecifiedChild(@RequestBody SpecialChildUser specialChildUser) throws Exception {
        return myUserService.createSpecifiedChild(specialChildUser.getUsername().trim());
    }

    @PostMapping("/creatRandomChild")
    @ApiOperation(value = "创建当前用户的随机子账户【bin执行专用】")
    public String creatRandomChild() throws Exception {
        return myUserService.creatRandomChild();
    }

    @GetMapping("/getToken")
    @ApiOperation(value = "获取指定账户token【针对由“createSpecifiedChild”和“creatRandomChild”接口注册的用户】")
    public String getToken(String username) throws Exception {
        return myUserService.getToken(username);
    }

    @GetMapping("/checkAndRegister")
    @ApiOperation(value = "检查用户是否注册", notes = "第三方系统，若不对接用户中心，使用本接口默认为指定用户注册")
    public String checkAndRegister(@RequestParam(required = true) String username) throws Exception {
        return myUserService.checkAndRegister(username);
    }

}
