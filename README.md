#ImageWatcher
图片查看器，为各位追求用户体验的daLao提供更优质的服务
它能够

*点击图片时以一种无缝顺畅的动画切换到图片查看的界面，同样以一种无缝顺畅的动画退出图片查看界面
*支持多图查看，快速翻页，双击放大，单击退出，双手缩放旋转图片
*下拽退出查看图片的操作，以及效果是本View的最大卖点(仿微信)

![image](https://github.com/byc4426/ImageWatcher/blob/master/previews/111.gif)
![image](https://github.com/byc4426/ImageWatcher/blob/master/previews/222.gif)
![image](https://github.com/byc4426/ImageWatcher/blob/master/previews/333.gif)
![image](https://github.com/byc4426/ImageWatcher/blob/master/previews/444.gif)



## 实现步骤

在module的gradle
```
    implementation 'com.byc:ImageWatcher:1.1.0'
```

#### 方法一
* 在Activity对应布局文件
```
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- some layout here -->

    <byc.imagewatcher.ImageWatcher
        android:id="@+id/v_image_watcher"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</FrameLayout>
```

* 然后在Activity onCreate里面简单的初始化一下

```
        // 一般来讲， ImageWatcher 需要占据全屏的位置
        vImageWatcher = (ImageWatcher) findViewById(R.id.v_image_watcher);
        // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
        vImageWatcher.setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0);
        // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
        vImageWatcher.setErrorImageRes(R.mipmap.error_picture);
        // 长按图片的回调，你可以显示一个框继续提供一些复制，发送等功能
        vImageWatcher.setOnPictureLongPressListener(this);
        vImageWatcher.setLoader(new GlideSimpleLoader());
        vImageWatcher.setOnStateChangedListener(new ImageWatcher.OnStateChangedListener() {
            @Override
            public void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag) {
                Log.e("IW", "onStateChangeUpdate [" + position + "][" + uri + "][" + animatedValue + "][" + actionTag + "]");
            }

            @Override
            public void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag) {
                if (actionTag == ImageWatcher.STATE_ENTER_DISPLAYING) {
                    Toast.makeText(getApplicationContext(), "点击了图片 [" + position + "]" + uri + "", Toast.LENGTH_SHORT).show();
                } else if (actionTag == ImageWatcher.STATE_EXIT_HIDING) {
                    Toast.makeText(getApplicationContext(), "退出了查看大图", Toast.LENGTH_SHORT).show();
                }
            }
        });
```

#### 新的初始化方式二
```
        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.mipmap.error_picture) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
                .setOnPictureLongPressListener(this)//长安监听，并不一定要调用这个API
                .setOnStateChangedListener(new ImageWatcher.OnStateChangedListener() {
                    @Override
                    public void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag) {
                        Log.e("IW", "onStateChangeUpdate [" + position + "][" + uri + "][" + animatedValue + "][" + actionTag + "]");
                    }

                    @Override
                    public void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag) {
                        if (actionTag == ImageWatcher.STATE_ENTER_DISPLAYING) {
                            Toast.makeText(getApplicationContext(), "点击了图片 [" + position + "]" + uri + "", Toast.LENGTH_SHORT).show();
                        } else if (actionTag == ImageWatcher.STATE_EXIT_HIDING) {
                            Toast.makeText(getApplicationContext(), "退出了查看大图", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setIndexProvider(new CustomDotIndexProvider())//自定义页码指示器（默认数字），并不一定要调用这个API
                .setLoadingUIProvider(new CustomLoadingUIProvider()); // 自定义LoadingUI，并不一定要调用这个API

```

由于一般图片查看会占据全屏
持有activity引用后 调用`activity.getWindow().getDecorView()`拿到根FrameLayout
即可动态插入ImageWatcher -> 使用
非入侵式 不再需要在布局文件中加入&lt;ImageWatcher&gt;标签 减少布局嵌套


这个时候你的所有准备工作已经完成
```
/**
 * @param i              被点击的ImageView
 * @param imageGroupList 被点击的ImageView的所在列表，加载图片时会提前展示列表中已经下载完成的thumb图片
 * @param urlList        被加载的图片url列表，数量必须大于等于 imageGroupList.size。 且顺序应当和imageGroupList保持一致
 */
 public void show(ImageView i, SparseArray<ImageView> imageGroupList, final List<Uri> urlList) { ... }
```

最后只要调用 `vImageWatcher.show()` 方法就可以了
```
    //记得重写返回键哦
    @Override
    public void onBackPressed() {
//        //方式一
//        if (!vImageWatcher.handleBackPressed()) {
//            super.onBackPressed();
//        }
        //方式二
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
    }
```

```
初始化API简介
name	                 description
setLoader	             图片地址加载的实现者
setTranslucentStatus	 当没有使用透明状态栏，传入状态栏的高度
setErrorImageRes	     图片加载失败时显示的样子
setOnPictureLongPressListener	长按回调
setIndexProvider	     自定义页码UI
setLoadingUIProvider	 自定义加载UI
setOnStateChangedListener	开始显示和退出显示时的回调
```


## 写在最后
----------------------------------------------------------------------------------------
