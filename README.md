# TapSDK_DEMO
代码主要演示如何快速接入TapSDK

## 1. 登录TapTap开发者中心
请登录 [TapTap开发者中心](#) 来创建应用或注册为开发者。

## 2. 下载 TapTap 应用
[点击下载](#) TapTap 应用

## 3. 环境配置
- 最低支持Android level 15+。

## 4. 工程导入
手动添加
1. 将[下载](#)的SDK包，导入到 '/project/app/libs/' 目录下  
2. 打开您工程的 '/project/app/build.gradle' 文件，添加gradle配置如下 
 
```java  
repositories{  
    flatDir {  
        dirs 'libs'  
    }  
}  
dependencies {  
...  
    implementation (name:'TapSDK_0.0.5', ext:'aar')  
    implementation (name:'TDSCommon_1.1.1', ext:'aar') 
}  
```  

## 5. 初始化
TapSDK初始化  
**API**    

**示例代码**  

```java
TdsConfig tdsConfig = new TdsConfig.Builder()
                .appContext(MainActivity.this)
                .clientId(getResources().getString(R.string.tap_client_id))//开发者中心获取到的client Id
                .build();
TdsInitializer.init(tdsConfig);  
```

## 6. 注册登录回调
监听登录的结果  
**API**  `setLoginResultCallback()`

**示例代码**

```java
TapLoginHelper.getInstance().addLoginResultCallback(new TapLoginHelper.TapLoginResultCallback() {
    @Override
    public void onLoginSuccess(AccessToken accessToken) {
        Log.e(Tag, "onLoginSuccess");
    
    }
    
    @Override
    public void onLoginCancel() {
    
    }
    
    @Override
    public void onLoginError(Throwable throwable) {
        Log.e(Tag, "onLoginError: " + throwable.getMessage());
    }
});
```

## 7. 登录
TapTap登录，当没有安装TapTap app时，会打开内置webview进行TapTap验证登录  
**API**  `startTapLogin()`

**示例代码**  
可以用下面代码直接登录： 
 

```java
TapLoginHelper.getInstance().startTapLogin(MainActivity.this,TapTapSdk.SCOPE_PUIBLIC_PROFILE);
```

## 8. 检查登录状态

可以先校验该用户是否登录过，对未登录的用户调用login()  

方法一、通过 TapLoginHelper.getCurrentAccessToken)() 和 TapLoginHelper.getCurrentProfile() 方法分别获取登录状态和用户信息  

```java  
//未登录用户会返回null
if (TapLoginHelper.getInstance().getCurrentAccessToken() == null) {
    TapLoginHelper.getInstance().startTapLogin(MainActivity.this,TapTapSdk.SCOPE_PUIBLIC_PROFILE);
} else {
   startGame();
}
```

方法二、通过 TapLoginHelper.fetchProfileForCurrentAccessToken() 获取实时更新的用户信息    

```java  
//未登录用户会回调onError，已经登录用户实时回调onSuccess
TapLoginHelper.fetchProfileForCurrentAccessToken(new Api.ApiCallback<Profile>() {
    @Override
    public void onSuccess(Profile data) {
        Log.e(Tag, "checkLogin-onSuccess");
        startGame();
    }

    @Override
    public void onError(Throwable error) {
        Log.e(Tag, "checkLogin-onError");
        TapLoginHelper.getInstance().startTapLogin(MainActivity.this, TapTapSdk.SCOPE_PUIBLIC_PROFILE);
    }
});
```

## 9. 登出
调用`TapLoginHelper.logout()` 实现登出功能。


