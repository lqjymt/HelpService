package com.ymt.wsserver

import BaseActivity
import android.content.Context
import android.net.InetAddresses
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ymt.base.IpUtil
import com.ymt.wsserver.DeviceSearch.OnSearchListener
import com.ymt.wsserver.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ld
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.ByteString
import java.net.InetAddress

/**
* Copyright (C), 2015-2020, 华苗木云有限公司
* @author : lqj
* Date : 2021/8/30 :17:24
 * web socket 服务端
*/
class MainActivity : BaseActivity<ActivityMainBinding>() {
  var mWebSocket:WebSocket ?= null
  override fun initView() {
    binding.tvIp.text = IpUtil.getIpAddressFromHotspot(this)
    DeviceSearch.search(object :OnSearchListener{
      override fun onSearchStart() {
        TODO("Not yet implemented")
      }

      override fun onSearchedNewOne(device: Device?) {
        TODO("Not yet implemented")
      }

      override fun onSearchFinish() {
        TODO("Not yet implemented")
      }
    })

    val mMockWebServer =  MockWebServer();
    val response =  MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
      override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        //有客户端连接时回调
        ld( "服务器收到客户端连接成功回调：");
        mWebSocket= webSocket
        mWebSocket?.send("i am server")
      }

      override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        ld( "服务器收到消息：");
      }

      override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        ld( "onClosed：");
      }
    })
    mMockWebServer.enqueue(response);
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val dhcpInfo = wifiManager.dhcpInfo
    val address = dhcpInfo.ipAddress
    val byte = byteArrayOfInts((address and 0xFF),(address shr 8 and 0xFF),(address shr 16 and 0xFF), (address shr 24 and 0xFF));
    val iNet =InetAddress.getByAddress(byte)
    Thread {
      mMockWebServer.start(iNet,IpUtil.serverPort)
    }.start()



  }
  fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
}