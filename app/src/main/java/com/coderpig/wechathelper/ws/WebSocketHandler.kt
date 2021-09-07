package com.coderpig.wechathelper.ws

import android.util.Log
import okhttp3.WebSocketListener
import com.coderpig.wechathelper.ws.ConnectStatus.Canceled
import com.coderpig.wechathelper.ws.ConnectStatus.Closed
import com.coderpig.wechathelper.ws.ConnectStatus.Closing
import com.coderpig.wechathelper.ws.ConnectStatus.Connecting
import com.coderpig.wechathelper.ws.ConnectStatus.Open
import okhttp3.OkHttpClient
import okio.ByteString
import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

/**
 * Copyright (C), 2015-2021, 华苗木云有限公司
 *
 * @author : lqj
 * Date : 2021/8/30 16:29
 */
class WebSocketHandler private constructor(private val wsUrl: String) : WebSocketListener() {
  private var webSocket: WebSocket? = null
  var status: ConnectStatus? = null
    private set
  private val client: OkHttpClient = Builder()
    //pingInterval就是设置心跳包发送的间隔时间，，OkHttp就会自动帮我们发送心跳包事件，也就是ping包。当间隔时间到了
    // ，没有收到pong包的话，监听事件中的onFailure方法就会被调用，此时我们就可以进行断线重连。
    .pingInterval(10, TimeUnit.SECONDS)
    .build()

  public fun connect() {
    //构造request对象
    val request: Request =Request.Builder()
      .url(wsUrl)
      .build()
    webSocket = client.newWebSocket(request, this)
    status = Connecting
  }

  public fun reConnect() {
    if (webSocket != null) {
      webSocket = client.newWebSocket(webSocket!!.request(), this)
    }
  }

  public fun send(text: String) {
    if (webSocket != null) {
      log("send： $text")
      webSocket!!.send(text)
    }
  }

  public fun cancel() {
    if (webSocket != null) {
      webSocket!!.cancel()
    }
  }

  public fun close() {
    if (webSocket != null) {
      webSocket!!.close(1000, null)
    }
  }

  override fun onOpen(webSocket: WebSocket, response: Response) {
    super.onOpen(webSocket, response)
    log("onOpen")
    status = Open
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack!!.onOpen()
    }
  }

  private fun log(s: String) {
    Log.d("ws", s)
  }

  override fun onMessage(webSocket: WebSocket, text: String) {
    super.onMessage(webSocket, text)
    log("onMessage: $text")
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack!!.onMessage(text)
    }
  }

  override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
    super.onMessage(webSocket, bytes)
  }

  override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
    super.onClosing(webSocket, code, reason)
    status = Closing
    log("onClosing")
  }

  override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
    super.onClosed(webSocket, code, reason)
    log("onClosed")
    status = Closed
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack!!.onClose()
    }
  }

  override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
    super.onFailure(webSocket, t, response)
    log("onFailure: $t")
    t.printStackTrace()
    status = Canceled
    if (mSocketIOCallBack != null) {
      mSocketIOCallBack!!.onConnectError(t)
    }
  }

  private var mSocketIOCallBack: WebSocketCallBack? = null
  public fun setSocketIOCallBack(callBack: WebSocketCallBack?) {
    mSocketIOCallBack = callBack
  }

  public fun removeSocketIOCallBack() {
    mSocketIOCallBack = null
  }

  companion object {
    private const val TAG = "WebSocketHandler "
    private var INST: WebSocketHandler? = null
    fun getInstance(url: String): WebSocketHandler? {
      if (INST == null) {
        synchronized(WebSocketHandler::class.java) { INST = WebSocketHandler(url) }
      }
      return INST
    }
  }
}