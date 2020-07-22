package com.fireantzhang.aabdemo

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompatApplication

/**
 * Created by Fireant on 2020-02-04.
 *
 */
class MyApp : SplitCompatApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}