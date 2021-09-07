package com.coderpig.wechathelper.ws


/**
 * Copyright (C), 2015-2021, 华苗木云有限公司
 *
 * @author : lqj
 * Date : 2021/8/30 16:32
 */
enum class ConnectStatus {
  // the initial state of each web socket.
  Connecting,  // the web socket has been accepted by the remote peer
  Open,  // one of the peers on the web socket has initiated a graceful shutdown
  Closing,  //  the web socket has transmitted all of its messages and has received all messages from the peer
  Closed,  // the web socket connection failed
  Canceled
}