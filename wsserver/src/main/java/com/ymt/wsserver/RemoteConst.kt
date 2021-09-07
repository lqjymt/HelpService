package com.ymt.wsserver

object RemoteConst {
  /**
   * 用于设备搜索的端口
   */
  const val DEVICE_SEARCH_PORT = 8100

  /**
   * 设备搜索次数
   */
  const val SEARCH_DEVICE_TIMES = 3

  /**
   * 搜索的最大设备数量
   */
  const val SEARCH_DEVICE_MAX = 250

  /**
   * 接收超时时间
   */
  const val RECEIVE_TIME_OUT = 1000

  /**
   * udp数据包前缀
   */
  const val PACKET_PREFIX = '$'.toInt()

  /**
   * udp数据包类型：搜索类型
   */
  const val PACKET_TYPE_SEARCH_DEVICE_REQ = 0x10

  /**
   * udp数据包类型：搜索应答类型
   */
  const val PACKET_TYPE_SEARCH_DEVICE_RSP = 0x11
}