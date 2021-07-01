package com.ut.user.controller;


import com.ut.user.appmgr.AppEntity;
import com.ut.user.appmgr.AppService;
import com.ut.user.support.UploadService;
import com.ut.user.vo.AppVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app")
@Api(description="应用管理", tags= {"AppController"})
public class AppController {
    @Autowired
    AppService appService;
    @Autowired
    UploadService uploadService;

    @PostMapping("/app")
    @ApiOperation(value="新建应用")
    public boolean createApp(@RequestBody AppVo appVo)throws Exception{
        return appService.createApp(appVo);
    }

    @GetMapping("/app")
    @ApiOperation(value="根据appKey获取应用信息")
    public AppEntity getAppInfo(String appKey)throws Exception{
        return appService.getAppInfo(appKey.trim());
    }

    @GetMapping("/apps")
    @ApiOperation(value="获取应用列表(分页-应用列表使用)")
    public Page<AppEntity> pageGetApps(Pageable pageable){
        return appService.pageGetApps(pageable);
    }

    @GetMapping("/allApps")
    @ApiOperation(value="获取已激活应用列表(平行-授权页使用)")
    public List<AppEntity> getAllApps(){
        return appService.getAllApps();
    }

    @PutMapping("/app")
    @ApiOperation(value="修改应用信息")
    public boolean updateApp(@RequestBody AppVo appVo)throws Exception{
        return appService.updateApp(appVo);
    }

    @PutMapping("/appStatus")
    @ApiOperation(value="修改应用状态")
    public boolean updateAppStatus(String appKey, boolean status)throws Exception{
        return appService.updateAppStatus(appKey.trim(), status);
    }

    @GetMapping("/pagePlatformApps")
    @ApiOperation(value="平台应用列表(分页-平台菜单使用)")
    public Page<AppEntity> pagePlatformApps(Pageable pageable){
        return appService.pagePlatformApps(pageable);
    }

    @GetMapping("/listPlatformApps")
    @ApiOperation(value="平台应用列表(平行-平台菜单使用)")
    public List<AppEntity> listPlatformApps(){
        return appService.listPlatformApps();
    }

    /**
     * 七牛云接口feign调用--屏蔽
     */
    /*@PostMapping(value = "/uploadFile", consumes = MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestBody MultipartFile file, String appKey) throws Exception {
        uploadService.uploadFile(file, appKey);
    }*/

}
