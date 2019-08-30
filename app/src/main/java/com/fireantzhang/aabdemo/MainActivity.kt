package com.fireantzhang.aabdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isTestVersion = BuildConfig.IS_TEST_VERSION

        val content = "展示测试入口状态：${isTestVersion}"
        id_tv_test_status.text = content

        id_btn_change_env.setOnClickListener {

            Snackbar.make(id_btn_change_env, "设置 API 环境", Snackbar.LENGTH_LONG)
                .show()
        }

        id_btn_change_env.visibility = if (isTestVersion) View.VISIBLE else View.GONE
    }
}
