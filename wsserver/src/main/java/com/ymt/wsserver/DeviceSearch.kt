package com.ymt.wsserver

import android.os.Handler
import android.os.Looper
import com.ymt.wsserver.DeviceSearch.OnSearchListener
import com.ymt.wsserver.DeviceSearch
import com.ymt.wsserver.DeviceSearch.SearchRunnable
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.util.HashMap
import java.util.concurrent.Executors

/**
 * Copyright (C), 2015-2021, 华苗木云有限公司
 *
 * @author : lqj
 * Date : 2021/9/1 20:06
 */
internal object DeviceSearch {
  private val executorService = Executors.newSingleThreadExecutor()
  private val uiHandler: Handler = Handler(Looper.getMainLooper())
  /**
   * 开始搜索
   * @param onSearchListener
   */
  fun search(onSearchListener: OnSearchListener?) {
    executorService.execute(SearchRunnable(onSearchListener))
  }

  interface OnSearchListener {
    fun onSearchStart()
    fun onSearchedNewOne(device: Device?)
    fun onSearchFinish()
  }

  private class SearchRunnable(var searchListener: OnSearchListener?) : Runnable {
    override fun run() {
      try {
        if (searchListener != null) {
          uiHandler.post(Runnable { searchListener!!.onSearchStart() })
        }
        val socket = DatagramSocket()
        //设置接收等待时长
        socket.soTimeout = RemoteConst.RECEIVE_TIME_OUT
        val sendData = ByteArray(1024)
        val receData = ByteArray(1024)
        val recePack = DatagramPacket(receData, receData.size)
        //使用广播形式（目标地址设为255.255.255.255）的udp数据包
        val sendPacket = DatagramPacket(
          sendData,
          sendData.size,
          InetAddress.getByName("255.255.255.255"),
          RemoteConst.DEVICE_SEARCH_PORT
        )
        //用于存放已经应答的设备
        val devices: HashMap<String, Device?> = HashMap<String, Device?>()
        //搜索指定次数
        for (i in 0 until RemoteConst.SEARCH_DEVICE_TIMES) {
          sendPacket.data = packSearchData(i + 1)
          //发送udp数据包
          socket.send(sendPacket)
          try {
            //限定搜索设备的最大数量
            var rspCount: Int = RemoteConst.SEARCH_DEVICE_MAX
            while (rspCount > 0) {
              socket.receive(recePack)
              val device: Device? = parseRespData(recePack)
              val ip = device?.ip
              if (devices[ip] == null) {
                //保存新应答的设备
//                devices[ip] = device
                if (searchListener != null) {
                  uiHandler.post(Runnable { searchListener!!.onSearchedNewOne(device) })
                }
              }
              rspCount--
            }
          } catch (e: SocketTimeoutException) {
            e.printStackTrace()
          }
        }
        socket.close()
        if (searchListener != null) {
          uiHandler.post(Runnable { searchListener!!.onSearchFinish() })
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }

    /**
     * 校验和解析应答的数据包
     * @param pack udp数据包
     * @return
     */
    private fun parseRespData(pack: DatagramPacket): Device? {
      if (pack.length < 2) {
        return null
      }
      val data = pack.data
      var offset = pack.offset
      //检验数据包格式是否符合要求
      if (data[offset++].toInt() != RemoteConst.PACKET_PREFIX || data[offset++].toInt() != RemoteConst.PACKET_TYPE_SEARCH_DEVICE_RSP) {
        return null
      }
      val length = data[offset++].toInt()
      val uuid = String(data, offset, length)
      return Device(pack.address.hostAddress, pack.port, uuid)
    }

    /**
     * 生成搜索数据包
     * 格式：$(1) + packType(1) + sendSeq(4) + dataLen(1) + [data]
     * packType - 报文类型
     * sendSeq - 发送序列
     * dataLen - 数据长度
     * data - 数据内容
     * @param seq
     * @return
     */
    private fun packSearchData(seq: Int): ByteArray {
      val data = ByteArray(6)
      var offset = 0
      data[offset++] = RemoteConst.PACKET_PREFIX.toByte()
      data[offset++] = RemoteConst.PACKET_TYPE_SEARCH_DEVICE_REQ.toByte()
      data[offset++] = seq.toByte()
      data[offset++] = (seq shr 8).toByte()
      data[offset++] = (seq shr 16).toByte()
      data[offset++] = (seq shr 24).toByte()
      return data
    }
  }
}