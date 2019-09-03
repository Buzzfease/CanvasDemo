package top.hanyue.canvasdemo.views.bounce

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.hanyue.canvasdemo.util.ScreenUtil


/**
 * Created by Buzz on 2019/8/8.
 * Email :lmx2060918@126.com
 * 模拟小球在屏幕上滚动
 */
class BounceSurfaceView:SurfaceView, SurfaceHolder.Callback, Runnable {
    private var surfaceHolder: SurfaceHolder? = null
    private var canvas: Canvas? = null
    private var mThread:Thread? = null
    @Volatile private var isRun:Boolean = false
    private var ball:Ball? = null
    private var accelerateX:Double = 0.0
    private var accelerateY:Double = 0.0

    companion object{
        var SCREEN_WIDTH_PIXEL:Double = 0.0         //屏幕宽度，非全屏状态下取View宽度
        var SCREEN_HEIGHT_PIXEL:Double = 0.0        //屏幕高度，非全屏状态下取View高度
        var SCREEN_DENSITY:Double = 0.0             //屏幕密度，这个是用来计算屏幕真实尺寸
        var SPEED_COEFFICIENT:Double = 0.0          //加速度系数，将重力传感器的 M/S(例如9.8m/s) 转换成 像素/S,这个值和屏幕密度相关
        var FRAME_RATE = 0                          //帧率
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(context, attrs)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        SCREEN_WIDTH_PIXEL = width.toDouble()
        SCREEN_HEIGHT_PIXEL = height.toDouble()
    }

    private fun initView(context: Context, attrs: AttributeSet?){
        surfaceHolder = holder
        surfaceHolder?.addCallback(this)
        isFocusable = true
        FRAME_RATE = 90
        SPEED_COEFFICIENT = 100 / 2.54 * SCREEN_DENSITY / 2
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        //直接开始绘制
        if (mThread == null){
            ball = Ball((SCREEN_WIDTH_PIXEL / 2).toFloat(), (SCREEN_HEIGHT_PIXEL / 2).toFloat(), ScreenUtil.dip2px(context, 30f))
            isRun = true
            mThread = Thread(this, "surfaceThread")
            mThread?.start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    /**
     * 这段代码体现了游戏设计的模式，在一个循环中(gameLoop)，根据帧率取更新逻辑中所有的“精灵”(本例中为Ball)
     * 精灵的所有属性和状态都在精灵内部定义、记录以及计算，gameLoop一般只监听输入
     */
    override fun run() {
        var deltaTime: Long = 0
        var tickTime: Long = 0
        tickTime = System.currentTimeMillis()

        while(isRun){
            try {
                canvas = surfaceHolder?.lockCanvas()
                //更新小球方向
                ball?.setBallHeader()
                //更新小球位置
                ball?.updatePosition(deltaTime, accelerateX, accelerateY)
                //画小球
                if (canvas != null){
                    canvas?.drawColor(Color.WHITE)
                    ball?.draw(canvas!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    surfaceHolder?.unlockCanvasAndPost(canvas)
                }
            }
            deltaTime = System.currentTimeMillis() - tickTime
            if (deltaTime < 1000L / FRAME_RATE) {
                try {
                    Thread.sleep(1000L / FRAME_RATE - deltaTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            tickTime = System.currentTimeMillis()
        }
    }

    /**
     * 接收重力传感器的值
     * @param accelerateX X轴加速度
     * @param accelerateY Y轴加速度
     */
    fun onDeviceStatusChange(accelerateX:Double, accelerateY:Double){
        this.accelerateX = accelerateX
        this.accelerateY = accelerateY
    }

    fun destroyView(){
        isRun = false
        mThread?.interrupt()
    }


}