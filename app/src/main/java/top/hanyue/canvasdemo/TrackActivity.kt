package top.hanyue.canvasdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_track.*
import top.hanyue.canvasdemo.views.track.TrackSurfaceView

/**
 * Created by Buzz on 2019/7/29.
 * Email :lmx2060918@126.com
 */
class TrackActivity: AppCompatActivity() {
    private var isStarted:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)
        findViewById<TrackSurfaceView>(R.id.trackView)


        tvStart.setOnClickListener {
            if (isStarted){
                tvStart.text = "Start"
                trackView.stopMove()
                isStarted = false
            }else{
                tvStart.text = "Stop"
                trackView.startMove()
                isStarted = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        trackView.destroyView()
    }
}