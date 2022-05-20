package coder.siy.test

import android.app.Application


/**
 *
 * @author  Siy
 * @since  2022/5/20
 */
class App : Application() {

    companion object {
        var INSTANCE: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}