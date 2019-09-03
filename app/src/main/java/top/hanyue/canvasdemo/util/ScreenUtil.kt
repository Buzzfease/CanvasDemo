package top.hanyue.canvasdemo.util

import android.content.Context

/**
 * Created by Buzz on 2019/7/29.
 * Email :lmx2060918@126.com
 */
object ScreenUtil {

    fun dip2px(context: Context, dpValue :Float):Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}