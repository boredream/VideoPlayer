### 介绍
播放器写法参考系统VideoView，系统的写法比较权威很有参考价值
在VideoView基础上添加了常见的手势滑动、锁屏功能等

将系统控件中的播放器部分抽离出来，封装成了一个单独的类BDVideoPlayer，可以自行替换成vitamio、ijk等其他播放器
播放器的控制面板、错误提示、手势滑动时显示的样式都单独封装成View，可按需自己修改

播放器功能封装成了model，代码独立，引用修改都比较方便
Demo中的VideoDetailActivity页面演示了如何使用本播放器

### Github地址
https://github.com/boredream/VideoPlayer
欢迎start和follow~

---

### 基础功能

* 播放视频
  * 支持本地视频、网络视频
* 播放、暂停
  * 暂停时，面板的显示不会有延迟消失效果，恢复播放时才有
* SeekTo进度拖动
* 操作面板显示、延迟消失
  * 点击视频画面会显示、隐藏操作面板
  * 显示后不操作会3秒后自动消失
* 播放器回调
  * 将系统MediaPlayer所有回调进行统一封装，回调方法名和参数保持一致，减少学习成本
  * 新增播放器当前状态回调，具体状态参考系统MediaPlayer生命周期图
  * 新增loading状态回调，规则为"系统info回调的加载中"、"Preparing状态"时显示；"系统info回调的加载结束 "、"IDEL状态"、"ERROR状态"、"PREPARED状态"时隐藏

### 进阶功能
* 横竖屏切换
  * 竖屏时，操作面板底部有全屏按钮，点击切换到横屏全屏状态
  * 横屏时，隐藏全屏按钮，点击返回会变回竖屏状态
  * 切换全屏时，隐藏statusbar；竖屏时恢复原有状态
  * 竖屏时，顶部返回按钮会一直显示；横屏时顶部返回按钮随控制面板的显示状态
* 手势操作
  * 屏幕左侧上下滑动调节亮度
  * 屏幕右侧上下滑动调节音量
  * 屏幕底测左右滑动调节进度
* 锁屏
  * 竖屏不提供锁屏按钮，横屏全屏时显示
  * 锁屏时隐藏控制面板除锁屏按钮外其他所有控件
  * 锁屏时，返回键不做任何处理
  * 锁屏时，屏蔽手势处理
  * 锁屏按钮属于控制面板一部分，所以显示、隐藏、延迟自动消失逻辑随面板

* error提示和重试功能
  * 错误时会在视频上方添加一层包含错误信息的蒙版，还有一个重试按钮。有多个错误类型，如下
  * 视频数据错误。包含视频地址、标题等的javabean为空时出现。点击重试重新请求业务代码
  * 视频资源播放错误。视频地址未加载成功。点击重试进行视频restart操作
  * 非wifi网络错误。手机流量下尝试播放视频，提示失败。点击按钮允许流量继续播放
  * 无网络错误。手机未联网时提示错误。点击重试时根据当前网络状态进行错误提示或重新加载视频

* 网络处理
  * 切换到手机流量或无网络时，作为上述error情况中最后两条一样逻辑处理
  * 手机流量的播放，在一次播放过程中，如果允许过一次则后续不再提示错误
  * BDVideoView中会注册一个网络变化监听广播，在网络变更时进行对应处理

---

### 截图

![竖屏状态](http://upload-images.jianshu.io/upload_images/1513977-dc45729d8d5640e4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/400)


![播放中切换到无网络时提示错误](http://upload-images.jianshu.io/upload_images/1513977-d00db0bb8b6a5282.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/400)

![横屏状态](http://upload-images.jianshu.io/upload_images/1513977-74bd348592098f27.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

![手势滑动控制进度](http://upload-images.jianshu.io/upload_images/1513977-5eee01365c860eda.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

![锁屏和loading](http://upload-images.jianshu.io/upload_images/1513977-316be245ec6c3286.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)
