package com.ymt.base

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.lang.IllegalArgumentException
import java.lang.reflect.InvocationTargetException
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/9/27.
 */
object IpUtil {
  val serverPort = 6666
  /**
   * 获取开启便携热点后自身热点IP地址
   * @param context
   * @return
   */
  fun getHotspotLocalIpAddress(context: Context): String? {
    val wifimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val dhcpInfo = wifimanager.dhcpInfo
    if (dhcpInfo != null) {
      val address = dhcpInfo.serverAddress
      return ((address and 0xFF)
        .toString() + "." + (address shr 8 and 0xFF)
          + "." + (address shr 16 and 0xFF)
          + "." + (address shr 24 and 0xFF))
    }
    return null
  }

  /**
   * 获取连接WiFi后的IP地址
   * @return
   */
  fun getIpAddressFromHotspot(context: Context): String? {
    val wifimanager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val dhcpInfo = wifimanager.dhcpInfo
    if (dhcpInfo != null) {
      val address = dhcpInfo.ipAddress
      return ((address and 0xFF)
        .toString() + "." + (address shr 8 and 0xFF)
          + "." + (address shr 16 and 0xFF)
          + "." + (address shr 24 and 0xFF))
    }
    return null
  }

  fun isWifiApEnabled(context: Context): Boolean {
    try {
      val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
      val method = manager.javaClass.getMethod("isWifiApEnabled")
      return method.invoke(manager) as Boolean
    } catch (e: NoSuchMethodException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    }
    return false
  }// skip ipv6

  /**
   * 获取ip地址
   * @return
   */
  val hostIP: String?
    get() {
      var hostIp: String? = null
      try {
        val nis: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
        var ia: InetAddress? = null
        while (nis.hasMoreElements()) {
          val ni = nis.nextElement() as NetworkInterface
          val ias = ni.inetAddresses
          while (ias.hasMoreElements()) {
            ia = ias.nextElement()
            if (ia is Inet6Address) {
              continue  // skip ipv6
            }
            val ip = ia.hostAddress
            if ("127.0.0.1" != ip) {
              hostIp = ia.hostAddress
              break
            }
          }
        }
      } catch (e: SocketException) {
        Log.i("yao", "SocketException")
        e.printStackTrace()
      }
      return hostIp
    }
}