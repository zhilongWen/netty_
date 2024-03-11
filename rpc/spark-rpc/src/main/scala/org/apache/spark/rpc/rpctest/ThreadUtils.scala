package org.apache.spark.rpc.rpctest

import com.google.common.util.concurrent.ThreadFactoryBuilder

import java.util.concurrent.{ScheduledExecutorService, ScheduledThreadPoolExecutor, ThreadFactory}

object ThreadUtils {

  def newDaemonSingleThreadScheduledExecutor(threadName: String): ScheduledExecutorService = {
    val threadFactory = createThreadFactory(threadName)
    val executor = new ScheduledThreadPoolExecutor(1, threadFactory)
    // By default, a cancelled task is not automatically removed from the work queue until its delay
    // elapses. We have to enable it manually.
    executor.setRemoveOnCancelPolicy(true)
    executor
  }

  private def createThreadFactory(threadName: String): ThreadFactory = {

    new ThreadFactoryBuilder()
      .setNameFormat(s"${threadName}-%d")
      .setDaemon(true)
      .setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler)
      .build()

  }
}

