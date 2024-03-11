package org.apache.spark.rpc.rpctest

import com.at.rpc.spark_heartbeat._
import org.apache.spark.internal.Logging
import org.apache.spark.rpc.{RpcCallContext, RpcEndpointRef, RpcEnv, ThreadSafeRpcEndpoint}
import org.apache.spark.{SecurityManager, SparkConf}

import java.util.concurrent.{ScheduledExecutorService, TimeUnit}
import scala.collection.mutable
import scala.language.postfixOps

class DeployMaster(override val rpcEnv: RpcEnv) extends ThreadSafeRpcEndpoint with Logging {

  //定义一个管理 worker 信息的 hashMap ， 这个 hashMap 必须是可变的。
  private val workers: mutable.Map[String, WorkerInfo] = mutable.Map[String, WorkerInfo]()

  private val executorService: ScheduledExecutorService = ThreadUtils.newDaemonSingleThreadScheduledExecutor("check-works-heart-beat")

  override def onStart(): Unit = {

    println("DeployMaster onStart")

    self.send(StartTimeOutWorker)
  }

  override def receive: PartialFunction[Any, Unit] = {

    case RegisterWorkerInfo(id, workerRef, cpu, ram) =>
      //接受到客户端的注册信息
      if (!workers.contains(id)) {
        //提取该 worker 的基本信息。
        val workerInfo = new WorkerInfo(id, cpu, ram)

        workers += (id -> workerInfo)

        //一切操作完成时，直接返回该单例对象（伴生类）
        workerRef.send(RegisteredWorkerInfo)
      }

    case HeartBeat(id) =>
    //      // 更新指定 id worker 的信息。
    //      // 1.取出消息
    //      val info: WorkerInfo = workers(id)
    //
    //      // 2.更新时间
    //      info.lastHeartBeat = System.currentTimeMillis()
    //
    //      workers += id -> info
    //
    //      println(s"Worker id : $id 的信息更新了！")

    case StartTimeOutWorker =>
      println("准备定期检测 worker 心跳：")


      executorService.scheduleAtFixedRate(new Runnable() {
        override def run(): Unit = self.send(RemoveTimeOutWorker)
      }, 0, 2000, TimeUnit.MILLISECONDS)

    //      import context.dispatcher
    //
    //      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)

    case RemoveTimeOutWorker =>

      val now: Long = System.currentTimeMillis()

      // 检查哪些 worker 心跳超时了，从 hashMap 当中删除。
      // 利用 Scala 的函数式编程来解决问题。
      // 1. 筛选出超时的 workers
      // 2. 将这些 workers 从 hashMap当中移除。
      //
      // 判断超时的逻辑：
      // (now - specified_id_worker's lastHeartBeat) > threshold 。
      // 由于网络传输存在一些少数的延迟(一般集群都在局域网内)，这个 threshold 我们选取 6 秒。

      workers.values.filter(worker => now - worker.lastHeartBeat > 6000)
        .foreach(worker => workers.remove(worker.id))

      println(s"now = ${now} , 当前有" + workers.size + "个 workers 存活。")

  }

  override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = super.receiveAndReply(context)

  override def onStop(): Unit = super.onStop()

}

object DeployMaster {

  val ENDPOINT_NAME = "Master"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    val securityMgr = new SecurityManager(conf)

    val rpcEnv: RpcEnv = RpcEnv.create("DeployMaster", "127.0.0.1", 9099, conf, securityMgr)

    val endpoint = new DeployMaster(rpcEnv)
    val masterRef: RpcEndpointRef = rpcEnv.setupEndpoint(ENDPOINT_NAME, endpoint)


    rpcEnv.awaitTermination()
  }

}