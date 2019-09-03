package top.hanyue.canvasdemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_clock.*

/**
 * Created by Buzz on 2019/8/9.
 * Email :lmx2060918@126.com
 */
class ClockActivity:AppCompatActivity() {
    private var manager: SensorManager? = null
    private var accelerateHeader:Int = 0
    private var accelerateValue:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        manager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        manager?.registerListener(sensorListener, manager?.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL)
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (Sensor.TYPE_GRAVITY != event.sensor.type) {
                return
            }
            val values = event.values
            val x = values[0].toDouble()
            val y = values[1].toDouble()
            val z = values[2].toDouble()
            val ax = -x
            val ay = -y
            val az = -z

            val angle = Math.toDegrees(Math.atan(Math.abs(ax)/Math.abs(ay)))

            if (ax >= 0 && ay >= 0){
                //第一象限
                accelerateHeader = angle.toInt()
            }else if(ax >= 0 && ay <= 0){
                //第四象限
                accelerateHeader = 180 - angle.toInt()
            }else if (ax <= 0 && ay <= 0){
                //第三象限
                accelerateHeader = 180 + angle.toInt()
            }else if (ax <= 0 && ay >= 0){
                //第二象限
                accelerateHeader = 360 - angle.toInt()
            }
            accelerateValue = Math.sqrt(Math.abs(ax * ax) + Math.abs(ay * ay))
            Log.e("BounceView","accelerateHeader: " + accelerateHeader)
            Log.e("BounceView","accelerateValue: " + accelerateValue)

            indicatorView.onDeviceStatusChange(accelerateHeader, accelerateValue)
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterListener(sensorListener)
    }
}