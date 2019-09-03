package top.hanyue.canvasdemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bounce.*
import top.hanyue.canvasdemo.views.bounce.BounceSurfaceView


/**
 * Created by Buzz on 2019/8/8.
 * Email :lmx2060918@126.com
 */
class BounceActivity:AppCompatActivity() {
    private var manager:SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dpi = resources.displayMetrics.densityDpi.toDouble()
        BounceSurfaceView.SCREEN_DENSITY = dpi
        setContentView(R.layout.activity_bounce)
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

            bounceView.onDeviceStatusChange(-x, y)//x取负数是为了适配Canvas坐标，便于计算
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.unregisterListener(sensorListener)
        bounceView.destroyView()
    }
}