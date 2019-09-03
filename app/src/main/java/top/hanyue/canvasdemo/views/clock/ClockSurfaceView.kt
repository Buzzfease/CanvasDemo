package top.hanyue.canvasdemo.views.clock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.hanyue.canvasdemo.util.ScreenUtil
import java.util.*

/**
 * Created by Buzz on 2019/8/9.
 * Email :lmx2060918@126.com
 */
class ClockSurfaceView : SurfaceView, SurfaceHolder.Callback, Runnable {
    private var surfaceHolder: SurfaceHolder? = null
    private var canvas: Canvas? = null
    private var mThread:Thread? = null
    @Volatile private var isRun:Boolean = false
    private var defaultFrameRate:Long = 0L
    private var accelerateHeader:Int = 0
    private var accelerateValue:Double = 0.0
    private var circlePaint = Paint()
    private var shortMarkPaint = Paint()
    private var longMarkPaint = Paint()
    private var secondPaint = Paint()
    private var minutePaint = Paint()
    private var hourPaint = Paint()
    private var defaultTextPixel:Float = 0f
    private var defaultLongMarkPixel:Float = 0f
    private var defaultShortMarkPixel:Float = 0f
    private var defaultSecondPointerPixel = 0f
    private var defaultMinutePointerPixel = 0f
    private var defaultHourPointerPixel = 0f
    private var centerX:Float = 0f
    private var centerY:Float = 0f
    private var circleRadius:Float = 0f

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
        defaultFrameRate = 35L

        defaultTextPixel = ScreenUtil.sp2px(context, 10f).toFloat()
        defaultLongMarkPixel = ScreenUtil.dip2px(context, 10f).toFloat()
        defaultShortMarkPixel = ScreenUtil.dip2px(context, 5f).toFloat()

        circlePaint.color = Color.BLACK
        circlePaint.strokeWidth = 10f
        circlePaint.style = Paint.Style.STROKE
        longMarkPaint.color = Color.BLACK
        longMarkPaint.strokeWidth = 5f
        shortMarkPaint.color = Color.BLACK
        shortMarkPaint.strokeWidth = 3f

        hourPaint.color = Color.BLACK
        hourPaint.strokeWidth = 10f
        minutePaint.color = Color.BLACK
        minutePaint.strokeWidth = 5f
        secondPaint.color = Color.RED
        secondPaint.strokeWidth = 3f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        this.centerX = (width / 2).toFloat()
        this.centerY = (height / 2).toFloat()
        this.circleRadius = if (centerX - centerY > 0) centerY - 100 else centerX - 100
        this.defaultSecondPointerPixel = circleRadius
        this.defaultMinutePointerPixel = defaultSecondPointerPixel * 0.75f
        this.defaultHourPointerPixel = defaultMinutePointerPixel* 0.75f
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        //直接开始绘制
        if (mThread == null){
            isRun = true
            mThread = Thread(this, "surfaceThread")
            mThread?.start()
        }
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
            try {
                canvas = surfaceHolder?.lockCanvas()
                if (canvas != null){
                    var clockDivide = 0
                    canvas?.translate(centerX, centerY)
                    canvas?.drawColor(Color.WHITE)
                    canvas?.drawCircle(0f, 0f, circleRadius ,circlePaint)
                    //drawClock
                    val timeArray = getCurrentTime()
                    val secDivide = timeArray[2]
                    val minDivide = timeArray[1]
                    val hourDivide = run {
                        val oriHour = if (timeArray[0] <= 12) timeArray[0] else timeArray[0] - 12
                        (oriHour * 5 + minDivide / 12)
                    }
                    while (clockDivide < 60){
                        if (clockDivide % 5 == 0){
                            canvas?.drawLine(0f, 0 - circleRadius + defaultLongMarkPixel, 0f, - circleRadius, longMarkPaint)
                        }else{
                            canvas?.drawLine(0f, 0  - circleRadius + defaultShortMarkPixel , 0f,  - circleRadius, shortMarkPaint)
                        }

                        if (secDivide == clockDivide) canvas?.drawLine(0f, 50f, 0f, 0 - defaultSecondPointerPixel + 50f, secondPaint)
                        if (minDivide == clockDivide) canvas?.drawLine(0f, 0f, 0f, 0 - defaultMinutePointerPixel, minutePaint)
                        if (hourDivide == clockDivide) canvas?.drawLine(0f, 0f, 0f, 0 - defaultHourPointerPixel, hourPaint)
                        canvas?.rotate(6f)
                        clockDivide ++
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    surfaceHolder?.unlockCanvasAndPost(canvas)
                }
            }
            deltaTime = System.currentTimeMillis() - tickTime
            if (deltaTime < 1000L / defaultFrameRate) {
                try {
                    Thread.sleep(1000L / defaultFrameRate - deltaTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            tickTime = System.currentTimeMillis()
        }
    }


    fun onDeviceStatusChange(accelerateHeader:Int, accelerateValue:Double){
        this.accelerateHeader = accelerateHeader
        this.accelerateValue = accelerateValue
    }

    private fun getCurrentTime():IntArray{
        val now = Calendar.getInstance()
        return intArrayOf(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND))
    }
}