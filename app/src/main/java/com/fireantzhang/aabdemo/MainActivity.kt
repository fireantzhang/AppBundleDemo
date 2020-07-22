package com.fireantzhang.aabdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var manager: SplitInstallManager

    @SuppressLint("SwitchIntDef")
    private var listener = SplitInstallStateUpdatedListener { state ->

        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                //  In order to see this, the application has to be uploaded to the Play Store.
//                displayLoadingState(state, "Downloading $names")
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                /*
                  This may occur when attempting to download a sufficiently large module.

                  In order to see this, the application has to be uploaded to the Play Store.
                  Then features can be requested until the confirmation path is triggered.
                 */
                startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
            }
            SplitInstallSessionStatus.INSTALLED -> {
                goToGameActivity()
//                onSuccessfulLoad(names, launch = !multiInstall)
            }

            SplitInstallSessionStatus.INSTALLING -> {
//                displayLoadingState(state, "Installing $names")
            }
            SplitInstallSessionStatus.FAILED -> {
                toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = SplitInstallManagerFactory.create(this)

        val isTestVersion = BuildConfig.IS_TEST_VERSION

        val content = "展示测试入口状态：${isTestVersion}"
        id_tv_test_status.text = content

        id_btn_change_env.setOnClickListener {

            Snackbar.make(id_btn_change_env, "设置 API 环境", Snackbar.LENGTH_LONG)
                .show()
        }

        id_btn_get_modules.setOnClickListener {

            showInstalledModules()
        }

        id_btn_get_files.setOnClickListener {

            showAppFilesContent()
        }

        id_btn_start_game.setOnClickListener {

            loadAndLaunchGameModule()
        }

        id_btn_change_env.visibility = if (isTestVersion) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        manager.registerListener(listener)
    }

    override fun onPause() {
        manager.unregisterListener(listener)
        super.onPause()
    }

    private fun showAppFilesContent() {

        val filesDir: File? = applicationContext.filesDir

        var fileStb = StringBuilder()

        fileStb.append("文件夹：").append(filesDir!!.absolutePath).append("(内容)").append("\n")

        for (file in filesDir.listFiles()) {

            fileStb.append(file.name)
                .append("[")
                .append(if (file.isDirectory) "dir" else "file")
                .append("]")
                .append("\n")

            if (file.isDirectory) {

                file.listFiles().forEach {

                    fileStb.append("---")
                        .append(it.name)
                        .append("[")
                        .append(if (file.isDirectory) "dir" else "file")
                        .append("]")
                        .append("\n")

                    if (it.isDirectory) {

                        val subDirStr = getSubDirFiles(it)

                        fileStb.append("------").append(subDirStr)
                    }
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("App File 下的内容")
            .setMessage(fileStb.toString())
            .show()
    }

    private fun getSubDirFiles(subDirFile: File?): String {

        if (!subDirFile!!.isDirectory) {
            return ""
        }

        var fileStb = StringBuilder()

        for (listFile in subDirFile!!.listFiles()) {
            if (listFile.isDirectory) {
                val subStr = getSubDirFiles(listFile)

                fileStb.append(listFile.name).append(": ").append(subStr).append("\n")
            } else {

                fileStb.append(listFile.name).append("\n")
            }
        }

        return fileStb.toString()
    }

    private fun showInstalledModules() {

        var installModStr = StringBuilder()

        manager.installedModules.forEach {

            installModStr.append(it).append(", ")
        }

        Snackbar.make(id_btn_get_modules, installModStr.toString(), Snackbar.LENGTH_SHORT).show()
    }

    private fun loadAndLaunchGameModule() {

        var gameModule = getString(R.string.title_game)

        if (manager.installedModules.contains(gameModule)) {

            goToGameActivity()

        } else {

            val tipsStr = "游戏模块暂时未下载，请求下载"
            Snackbar.make(id_btn_change_env, tipsStr, Snackbar.LENGTH_SHORT)
                .show()

            // We just added the following lines
            var request = SplitInstallRequest.newBuilder()
                .addModule(gameModule)
                .build()

            progress.visibility = View.VISIBLE

            manager.startInstall(request)
                .addOnCompleteListener {
                    progress.visibility = View.GONE
                }
                .addOnSuccessListener {

                    Snackbar.make(id_btn_change_env, "游戏模块下载成功", Snackbar.LENGTH_SHORT)
                }
                .addOnFailureListener {

                    Snackbar.make(id_btn_change_env, "游戏模块下载失败", Snackbar.LENGTH_SHORT)
                }

        }
    }

    private fun toastAndLog(text: String) {
        Snackbar.make(id_btn_change_env, text, Snackbar.LENGTH_SHORT)
        Log.d("MainActivity", text)
    }

    private fun goToGameActivity() {

        // 由于无法直接访问到动态模块的类，所以需要采用隐式启动
        val intent = Intent()
        intent.setClassName(this, "com.fireantzhang.game.GameFeatureActivity")
        startActivity(intent)
    }
}
