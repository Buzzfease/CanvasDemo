package top.hanyue.canvasdemo.views.track

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.hanyue.canvasdemo.util.ScreenUtil


/**
 * Created by Buzz on 2019/8/3.
 * Email :lmx2060918@126.com
 * 模拟轨道
 */
class TrackSurfaceView:SurfaceView, SurfaceHolder.Callback, Runnable {

    private var surfaceHolder: SurfaceHolder? = null
    private var canvas: Canvas? = null
    private var mThread:Thread? = null
    @Volatile private var isRun:Boolean = false
    private var drawPosition:Double = 0.0
    private var defaultTextPixel:Float = 0f
    private var defaultLongMarkPixel:Float = 0f
    private var defaultShortMarkPixel:Float = 0f
    private var defaultBufferOffset:Double = 0.0
    private var defaultMarkOffset:Double = 0.0
    private var defaultFrameRate:Double = 0.0
    private var pixelPerFrame:Double = 0.0
    private var linePaint = Paint()
    private var centerMarkerPaint = Paint()
    private var longMarkPaint = Paint()
    private var shortMarkPaint = Paint()
    private var textPaint = Paint()


    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(context, attrs)
    }


    private fun initView(context: Context, attrs: AttributeSet?){
        surfaceHolder = holder
        surfaceHolder?.addCallback(this)
        isFocusable = true

        defaultTextPixel = ScreenUtil.sp2px(context, 10f).toFloat()
        defaultLongMarkPixel = ScreenUtil.dip2px(context, 10f).toFloat()
        defaultShortMarkPixel = ScreenUtil.dip2px(context, 5f).toFloat()
        defaultFrameRate = 35.0
        defaultMarkOffset = 20.0
        defaultBufferOffset = defaultMarkOffset * 5
        pixelPerFrame = defaultMarkOffset * 5 / defaultFrameRate

        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 10f
        centerMarkerPaint.color = Color.RED
        centerMarkerPaint.strokeWidth = 1f
        longMarkPaint.color = Color.BLACK
        longMarkPaint.strokeWidth = 5f
        shortMarkPaint.color = Color.BLACK
        shortMarkPaint.strokeWidth = 3f
        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5f
        textPaint.textSize = defaultTextPixel
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        drawPosition = (width / 2).toDouble()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        draw()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun run() {
        var deltaTime: Long
        var tickTime: Long
        tickTime = System.currentTimeMillis()
        while(isRun){
            drawPosition -= pixelPerFrame
            Log.e("TrackView", "drawPosition = $drawPosition")
            draw()
            deltaTime = System.currentTimeMillis() - tickTime
            if (deltaTime < 1000L / defaultFrameRate) {
                try {
                    Thread.sleep(1000L / defaultFrameRate.toLong() - deltaTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            tickTime = System.currentTimeMillis()
        }
    }

    private fun draw(){
        try {
            canvas = surfaceHolder?.lockCanvas()
            canvas?.drawColor(Color.WHITE)
            //执行具体的绘制操作
            drawTopLine(canvas)
            drawBottomLine(canvas)
            drawCenterMarker(canvas)
            var paintX = drawPosition
            var divideCount = 0
            while (paintX < width + defaultBufferOffset){
                if (paintX > 0 - defaultBufferOffset){
                    if (divideCount % 5 == 0){
                        //长标记
                        drawLongMarkWithText(canvas, paintX.toFloat(), divideCount)
                    }else{
                        //短标记
                        drawShortMark(canvas, paintX.toFloat())
                    }
                }
                divideCount ++
                paintX += defaultMarkOffset
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (canvas != null) {
                surfaceHolder?.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun drawTopLine(canvas: Canvas?){
        canvas?.drawLine(0f,0f, width.toFloat(), 0f, linePaint)
    }

    private fun drawBottomLine(canvas: Canvas?){
        canvas?.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), linePaint)
    }

    private fun drawCenterMarker(canvas: Canvas?){
        canvas?.drawLine((width / 2).toFloat(),0f, (width / 2).toFloat(), height.toFloat(), centerMarkerPaint)
    }

    private fun drawShortMark(canvas: Canvas?, offset:Float){
        canvas?.drawLine(offset, 0f, offset, defaultShortMarkPixel, shortMarkPaint)
        canvas?.drawLine(offset, height.toFloat(), offset, height - defaultShortMarkPixel, shortMarkPaint)
    }

    private fun drawLongMarkWithText(canvas: Canvas?, offset:Float, divideCount:Int){
        canvas?.drawLine(offset, 0f, offset, defaultLongMarkPixel, longMarkPaint)
        canvas?.drawLine(offset, height.toFloat(), offset,  height - defaultLongMarkPixel, longMarkPaint)
        canvas?.drawText(getTimeStrBySecond(divideCount/5), offset, defaultLongMarkPixel + defaultTextPixel, textPaint)
    }

    /**
     * 根据秒数获取时间串
     * @param secondNum (eg: 100s)
     * @return (eg: 00:01:40)
     */
     private fun getTimeStrBySecond(secondNum:Int):String {
        var second = secondNum
        if (second <= 0) {
            return "00:00"
        }

        val minutes = second / 60
        if (minutes > 0) {
            second -= minutes * 60
        }
        return if (minutes >= 10) minutes.toString() else "0$minutes" + ":"+ if (second >= 10) (second).toString() + "" else "0$second"
    }

    fun startMove(){
        isRun = true
        mThread = Thread(this, "surfaceThread")
        mThread?.start()
    }

    fun stopMove(){
        isRun = false
    }

    fun destroyView(){
        isRun = false
        mThread?.interrupt()
    }

}