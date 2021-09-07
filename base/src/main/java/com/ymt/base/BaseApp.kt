package com.ymt.base

import android.app.Application

/**
 * Copyright (C), 2015-2021, 华苗木云有限公司
 * @author : lqj
 * Date : 2021/8/30 17:11
 */
 object  BaseApp {
  var app: Application? = null
  fun init(context: Application) {
    app =  context
  }

}