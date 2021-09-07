package com.coderpig.wechathelper

import BaseActivity
import android.content.Intent
import android.provider.Settings
import android.view.MotionEvent
import com.coderpig.wechathelper.databinding.ActivityControlBinding
import com.coderpig.wechathelper.ui.UdpActivity
import ld
import java.sql.Blob

/**
 * 描述：辅助服务控制页
 *
 * @author CoderPig on 2018/04/12 10:50.
 */
class ControlActivity : BaseActivity<ActivityControlBinding>() {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x;
        val y = event?.y;
        binding.touchX.text = "x=${x}"
        binding.touchY.text = "y=${y}"
        ld("onTouchEvent  initView x=${x}")
        ld("onTouchEvent  initView y=${y}")
        return super.onTouchEvent(event)
    }

    override fun initView() {
        val pm = resources.getString(R.string.pm)
        ld("ControlActivity  initView pm=${pm}")
        ld("ControlActivity  initView widthPixels=${resources.displayMetrics.widthPixels}")
        ld("ControlActivity  initView heightPixels=${resources.displayMetrics.heightPixels}")
        ld("ControlActivity  cacheDir.absolutePath=${cacheDir.absolutePath}")
        ld("ControlActivity  externalCacheDir.absolutePath=${externalCacheDir?.absolutePath}")

        //启动应用
        binding.btnOpenWechat.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage(pm)
            startActivity(intent)
        }
        //启动ws 连接
        binding.openUdp.setOnClickListener {
            val intent = Intent(this,UdpActivity::class.java)
            startActivity(intent)
        }
        //打开设置
        binding.btnOpenAccessbility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        binding.btnWrite.setOnClickListener {
            Constant.operateType = 1
        }

        binding.btnReset.setOnClickListener {
            Constant.operateType = 2
        }


    }


}
