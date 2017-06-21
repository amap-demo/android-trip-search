# android-trip-search
出行类app结合了叫车主页和检索的demo

## 前述 ##
- [高德官网申请Key](http://lbs.amap.com/dev/#/).
- 阅读[参考手册](http://a.amap.com/lbs/static/unzip/Android_Map_Doc/index.html).
- 工程基于高德地图Android地图SDK实现

### 配置搭建AndroidSDK工程 ###
- [Android Studio工程搭建方法](http://lbs.amap.com/api/android-sdk/guide/creat-project/android-studio-creat-project/#add-jars).
- [通过maven库引入SDK方法](http://lbsbbs.amap.com/forum.php?mod=viewthread&tid=18786).

### 使用场景 ###
AMapTripModule 提供了叫车主页的组件

### 组件截图 ###
<img src="https://github.com/amap-demo/android-trip-demo/blob/master/trip_host_sc.png" width="50%" />
<img src="https://github.com/amap-demo/android-trip-demo/blob/master/trip_start_call_sc.png" width="50%" />

### gradle中引入AmapPoiSearchModule和AMapTripModule ###
#### settings.gradle 配置 ####
```java
include ':app'

include ':AMapTripModule'
project(":AMapTripModule").projectDir=new File("AMapTripModule路径")

include ':AmapPoiSearchModule'
project(":AmapPoiSearchModule").projectDir = new File("AmapPoiSearchModule路径")
```

#### build.gradle 配置 ####
```java
dependencies {

    .......
    compile project(":AMapTripModule")
    compile project(":AmapPoiSearchModule")
    
    compile 'com.amap.api:3dmap:latest.integration'
    compile 'com.amap.api:search:latest.integration'
    compile 'com.amap.api:location:latest.integration'
    compile 'com.google.code.gson:gson:2.8.0'
    .......
}
```

### 使用方法 ###
此处以MainActivity为例进行介绍：

step1. 在onCreate中初始化Widget和ModuleDelegte<br />
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout contentView = (RelativeLayout)findViewById(R.id.content_view);

        mTripHostDelegate = new TripHostModuleDelegate();
        mTripHostDelegate.bindParentDelegate(mParentTripDelegate);

        contentView.addView(mTripHostDelegate.getWidget(this));
        mTripHostDelegate.onCreate(savedInstanceState);
    }
```

step2. 在TripHostModuleDelegate.IParentDelegate进行回调逻辑<br />
```java
 private IParentDelegate mParentTripDelegate = new IParentDelegate() {
        @Override
        public void onStartCall() {
            showMsg("on start call");
        }
    };
......
```


