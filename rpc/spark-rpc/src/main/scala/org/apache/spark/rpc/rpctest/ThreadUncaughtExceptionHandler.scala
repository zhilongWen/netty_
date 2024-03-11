package org.apache.spark.rpc.rpctest

import java.lang.Thread.UncaughtExceptionHandler

class ThreadUncaughtExceptionHandler extends UncaughtExceptionHandler {
  //
  override def uncaughtException(t: Thread, e: Throwable): Unit = {

  }

}
