package com.coderpig.wechathelper.ui

import BaseActivity
import com.coderpig.wechathelper.databinding.ActivityUdpBinding
import com.coderpig.wechathelper.ws.WebSocketCallBack
import com.coderpig.wechathelper.ws.WebSocketHandler
import com.ymt.base.IpUtil
import ld

/**
* Copyright (C), 2015-2020, 华苗木云有限公司
* @author : lqj
* Date : 2021/8/30 :14:44
*/
class UdpActivity : BaseActivity<ActivityUdpBinding>() {

    override fun initView() {
        binding.btnConnect.setOnClickListener {
          val serverIp = "192.168.2.196"
          val  serverPort = IpUtil.serverPort
            val url = "ws://$serverIp:$serverPort"
                val ws = WebSocketHandler.getInstance(url)
                 ws?.connect();
                ws?.setSocketIOCallBack(object :WebSocketCallBack{
                    override fun onConnectError(e: Throwable?) {
                        ld("onConnectError")
                    }

                    override fun onMessage(msg: String?) {
                      ld("onMessage=$msg")
                    }

                    override fun onClose() {
                      ld("onClose")
                    }

                    override fun onOpen() {
                      ld("onOpen")
                    }

                })
        }
    }

}
