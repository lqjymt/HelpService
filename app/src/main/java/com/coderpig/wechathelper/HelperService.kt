package com.coderpig.wechathelper

import android.accessibilityservice.AccessibilityService

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.Builder
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ld
import le
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
* Copyright (C), 2015-2020, 华苗木云有限公司
* @author : lqj
* Date : 2021/9/1 :19:24
 * 无障碍服务类
*/
class HelperService : AccessibilityService() {

    private val TAG = "HelperService"
    override fun onInterrupt() {
       ld("HelperService onInterrupt")
    }

    /**
     * 自动刷视频
     */
    var rushJob:Job?=null
    @RequiresApi(VERSION_CODES.N)  fun go() {
        if(rushJob==null){
            rushJob = CoroutineScope(Dispatchers.Default).launch {
                var i = 0;
                Log.d(TAG, "ready go")
                while (i < 100000000000000) {
                    val t = (6000..18000).random().toLong()
                    Log.d(TAG, "ready go delay t=${t}，i=${i}")
                    delay(t)
                    swipeUp()
                    i++
                };
            }
        }
    };
    /**
     * 取消点赞
     */
    var cancelPraiseJob:Job?=null
    @RequiresApi(VERSION_CODES.N)  fun cancelPraise() {
        if(cancelPraiseJob == null){
            cancelPraiseJob = CoroutineScope(Dispatchers.Default).launch {
                var i = 0;
                while (i < 3000) {
                    val t = (1000..2000).random().toLong()
                    ld( "ready go t=${t},i=${i}")
                    delay(t)
                    //进入详情
                    click("com.ss.android.ugc.aweme.lite:id/adt")
                    delay(1000)
                    cPraise()
                    delay(1000)
                    performBackClick()
                    i++
                };
            }
        }

    };
    /**
     * 下滑
     */
    @RequiresApi(VERSION_CODES.N) fun swipeDown() {
        val displayMetrics = resources.displayMetrics
        val centerY = displayMetrics.heightPixels / 2
        val centerX = displayMetrics.widthPixels / 2
        val x = centerX.toFloat()
        val y = centerY.toFloat()
        val x2 = centerX.toFloat()
        val y2 = centerY*1.8F
        swipe(x,y,x2,y2)
    }

    /**
     * 上滑
     */
    @RequiresApi(VERSION_CODES.N)
    fun swipeUp() {
        val displayMetrics = resources.displayMetrics
        val centerY = displayMetrics.heightPixels / 2
        val centerX = displayMetrics.widthPixels / 2
        val x = centerX.toFloat()
        val y = centerY.toFloat()
        val x2 = centerX.toFloat()
        val y2 = centerY*0.1F
        swipe(x,y,x2,y2)
    }

    /**
     * 找到id
     */
    @RequiresApi(VERSION_CODES.N)  fun findId() {
        runBlocking {
            var i = 0;
            while (i < 100) {
                delay(500)
                val root  = rootInActiveWindow
                val praise =  root.findAccessibilityNodeInfosByText("218")?.elementAtOrNull(0)
                ld("praise=${praise}")
                i++
            };
        }
    };
    /**
     * 通过id 点赞
     */
   private fun praise() {
        val root  = rootInActiveWindow
        val praise =  root.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme.lite:id/ai6")?.elementAtOrNull(0)
        praise?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        ld("praise")
    }
    /**
     * 点赞 /取消点赞
     */
    @RequiresApi(VERSION_CODES.N) fun cPraise() {
        clickSome(943F, 1036.0F)
    }
    private fun click(id:String) {
        val root  = rootInActiveWindow
        val praise =  root.findAccessibilityNodeInfosByViewId(id)?.elementAtOrNull(0)
        praise?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        ld("click id=${id}")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        ld("onAccessibilityEvent")
        val eventType = event.eventType
        val classNameChr = event.className
        val className = classNameChr.toString()
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            when(Constant.operateType){
                1->{
                    go()
                }
                2->{
                    cancelPraise()
                }
                3->{
                    findId()
                }
            }

        }
        when(Constant.remoteOperate){
            "up"->{
                Constant.remoteOperate= ""
            }
            "down"->{
                Constant.remoteOperate= ""
            }
        }

        when (eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED->{
                le("View被点击了:${event}")
                le("windowid:${event.windowId}")
                le("text:${event.text}")
            }

        }
    }

    //遍历控件的方法
    @RequiresApi(VERSION_CODES.KITKAT)
    fun recycle(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            Log.i(TAG, "child widget----------------------------" + info.className.toString())
            Log.i(TAG, "showDialog:" + info.canOpenPopup())
            Log.i(TAG, "Text：" + info.text)
            Log.i(TAG, "windowId:" + info.windowId)
            Log.i(TAG, "desc:" + info.contentDescription)
        } else {
            (0 until info.childCount)
                    .filter { info.getChild(it) != null }
                    .forEach { recycle(info.getChild(it)) }
        }
    }
    /**
     * 滑动
     */
    @RequiresApi(VERSION_CODES.N)
    fun swipe(x:Float,y:Float,x2:Float,y2:Float) {
        val gestureBuilder = Builder()
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x2, y2)
        gestureBuilder.addStroke(StrokeDescription(path, 100, 300))
        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                Log.d(TAG, "dispatchGesture complete")
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.d(TAG, "dispatchGesture onCancelled")
                super.onCancelled(gestureDescription)
            }
        }, null)
    }
    /**
     * 点击屏幕某个地方
     */
    @RequiresApi(VERSION_CODES.N)
    fun clickSome(x: Float, y: Float): Unit {
        val gestureBuilder = Builder()
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x, y)
        gestureBuilder.addStroke(StrokeDescription(path, 1, 1))
        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                le("dispatchGesture complete:x=${x},y=${y}")
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                le("dispatchGesture onCancelled:x=${x},y=${y}")
                super.onCancelled(gestureDescription)
            }
        }, null)
        le("clickSome:x=${x},y=${y}")
    }
    /**
     * 点击返回
     */
    private fun performBackClick() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        Log.e(TAG, "点击返回")
    }

}