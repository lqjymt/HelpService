package com.coderpig.wechathelper.ws



/**
 * Copyright (C), 2015-2021, 华苗木云有限公司
 *
 * @author : lqj
 * Date : 2021/8/30 16:34
 */
interface WebSocketCallBack {
  fun onConnectError(e: Throwable?)
  fun onMessage(msg: String?)
  fun onClose()
  fun onOpen()
}