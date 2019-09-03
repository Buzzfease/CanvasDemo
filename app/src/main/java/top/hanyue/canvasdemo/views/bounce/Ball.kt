package top.hanyue.canvasdemo.views.bounce

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log


/**
 * Created by Buzz on 2019/8/9.
 * Email :lmx2060918@126.com
 * 本例只是演示模拟一个小球在重力环境下在屏幕边框内的运动情况，其中只涉及简单的模拟计算，并非完全符合力学
 */
class Ball {
    private var speedX: Double = 0.0            //小球水平方向速度，单位像素/s
    private var speedY: Double = 0.0            //小球垂直方向速度，单位像素/s
    private var x: Float = 0f                   //小球x坐标
    private var y: Float = 0f                   //小球y坐标
    private var ballRadius: Int = 0             //小球半径
    private var ballPaint:Paint? = null         //小球画笔
    private var ballFriction:Double = 0.0       //小球摩擦力加速度



    constructor(x:Float, y:Float, ballRadius:Int){
        this.x = x
        this.y = y
        this.ballRadius = ballRadius
        this.ballFriction = 0.1
        ballPaint = Paint()
        ballPaint?.color = Color.BLACK
        ballPaint?.strokeWidth = 5f
    }

    /**
     * 更新小球位置
     * @param deltaTime 绘制时间
     * @param accelerateX X轴加速度
     * @param accelerateY Y轴加速度
     */
    fun updatePosition(deltaTime: Long, accelerateX: Double, accelerateY: Double) {
        var ax: Double
        var ay: Double
        /**
         * 1.水平方向加速度为正的情况下，如果小球x轴速度也为正(小球水平运动方向与加速度一致)
         *   则水平加速度为水平加速度- 摩擦力加速度
         * 2.如果小球x速度为负，即和其加速度方向相反
         *   则水平加速度为水平加速度+ 摩擦力加速度
         * 3.如果小球静止，则动摩擦力转换为静摩擦力，小球水平加速度小于摩擦力时，水平加速度为0
         * 4.以此类推水平加速度为负的情况和Y轴情况
         */
        if (accelerateX >= 0){
            if (speedX > 0){
                ax = accelerateX - ballFriction
            }else if (speedX < 0){
                ax = accelerateX + ballFriction
            }else{
                ax = accelerateX - ballFriction
                if (ax < 0){
                    ax = 0.0
                }
            }
        }else{
            if (speedX < 0){
                ax = accelerateX + ballFriction
            }else if (speedX > 0){
                ax = accelerateX - ballFriction
            }else{
                ax = accelerateX + ballFriction
                if (ax > 0){
                    ax = 0.0
                }
            }
        }

        if (accelerateY >= 0){
            if (speedY > 0){
                ay = accelerateY - ballFriction
            }else if (speedY < 0){
                ay = accelerateY + ballFriction
            }else{
                ay = accelerateY - ballFriction
                if (ay < 0){
                    ay = 0.0
                }
            }
        }else{
            if (speedY < 0){
                ay = accelerateY + ballFriction
            }else if (speedY > 0){
                ay = accelerateY - ballFriction
            }else{
                ay = accelerateY + ballFriction
                if (ay > 0){
                    ay = 0.0
                }
            }
        }
        //当前速度增加量，BounceSurfaceView.SPEED_COEFFICIENT是该设备 m/s 转 pixel/s的系数
        speedX += ax * deltaTime/1000L * BounceSurfaceView.SPEED_COEFFICIENT
        speedY += ay * deltaTime/1000L * BounceSurfaceView.SPEED_COEFFICIENT
        //边界检测
        when {
            x + (speedX * deltaTime/1000L).toFloat() > BounceSurfaceView.SCREEN_WIDTH_PIXEL - ballRadius -> x = (BounceSurfaceView.SCREEN_WIDTH_PIXEL - ballRadius).toFloat()
            x + (speedX * deltaTime/1000L).toFloat() < ballRadius -> x = ballRadius.toFloat()
            else -> x += (speedX * deltaTime/1000L).toFloat()
        }
        when {
            y + (speedY * deltaTime/1000L).toFloat() > BounceSurfaceView.SCREEN_HEIGHT_PIXEL - ballRadius -> y = (BounceSurfaceView.SCREEN_HEIGHT_PIXEL - ballRadius).toFloat()
            y + (speedY * deltaTime/1000L).toFloat() < ballRadius -> y = ballRadius.toFloat()
            else -> y += (speedY * deltaTime/1000L).toFloat()
        }

        Log.e("Ball","position :" + x + " , " + y)
        Log.e("Ball","speedX :" + speedX)
        Log.e("Ball","speedY :" + speedY)
    }


    /**
     * 设置小球运动方将
     * 模拟每次碰撞消耗1/5能量，当速度小于100像素/s时，将速度设为0，不然小球永远无法停止，会在屏幕边缘抖动
     */
    fun setBallHeader(){
        if (x - ballRadius <= 0){
            //球碰到左边
            Log.e("Ball","碰左边")
            x = ballRadius.toFloat() + 1
            speedX = if (speedX >= -100){
                0.0
            }else{
                Math.abs(speedX - speedX / 5)
            }

        }
        if (x + ballRadius >= BounceSurfaceView.SCREEN_WIDTH_PIXEL){
            //球碰到右边
            Log.e("Ball","碰右边")
            x = BounceSurfaceView.SCREEN_WIDTH_PIXEL.toFloat() - ballRadius.toFloat() - 1
            speedX = if (speedX <= 100){
                0.0
            }else{
                - Math.abs(speedX - speedX / 5)
            }

        }
        if (y - ballRadius <= 0){
            Log.e("Ball","碰上边")
            y = ballRadius.toFloat() + 1
            speedY = if (speedY >= -100){
                0.0
            }else{
                Math.abs(speedY - speedY / 5)
            }
        }
        if (y + ballRadius >= BounceSurfaceView.SCREEN_HEIGHT_PIXEL){
            //球碰到下边
            Log.e("Ball","碰下边")
            y = BounceSurfaceView.SCREEN_HEIGHT_PIXEL.toFloat() - ballRadius.toFloat() - 1
            speedY = if (speedY <= 100){
                0.0
            }else{
                - Math.abs(speedY - speedY / 5)
            }
        }
    }

    /**
     * 绘制小球
     */
    fun draw(canvas: Canvas) {
        canvas.drawCircle(x ,y , ballRadius.toFloat(), ballPaint)
    }
}