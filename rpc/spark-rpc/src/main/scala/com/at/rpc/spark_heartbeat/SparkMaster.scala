package com.at.rpc.spark_heartbeat

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._

class SparkMaster extends Actor {


  //定义一个管理 worker 信息的 hashMap ， 这个 hashMap 必须是可变的。
  val workers : mutable.Map[String, WorkerInfo] = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" =>
      println(" Master 服务器启动成功！")

      // 自启动 workers 的定时检查机制。
      self ! StartTimeOutWorker

    case RegisterWorkerInfo(id, cpu, ram) =>
      //接受到客户端的注册信息
      if (!workers.contains(id)) {
        //提取该 worker 的基本信息。
        val workerInfo = new WorkerInfo(id, cpu, ram)

        workers += (id -> workerInfo)

        //一切操作完成时，直接返回该单例对象（伴生类）
        sender() ! RegisteredWorkerInfo
      }

    case HeartBeat(id) =>
      // 更新指定 id worker 的信息。
      // 1.取出消息
      val info: WorkerInfo = workers(id)

      // 2.更新时间
      info.lastHeartBeat = System.currentTimeMillis()

      workers += id -> info

      println(s"Worker id : $id 的信息更新了！")

    case StartTimeOutWorker =>
      println("准备定期检测 worker 心跳：")
      import context.dispatcher

      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)

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

      println("当前有" + workers.size + "个 workers 存活。")

  }
}

object SparkMaster {
  def main(args: Array[String]): Unit = {

    // https://juejin.cn/post/6990289650485215239：

    //1.绑定本机地址和启动的端口号
    val host = "127.0.0.1" //绑定为本机地址
    val port = 9999 //绑定本机启动的端口号

    //2.绑定配置文件
    val config = ConfigFactory.parseString(
      s"""
        akka{
          actor{
            provider = "akka.remote.RemoteActorRefProvider"
          }

          remote{
            enabled-transports=["akka.remote.netty.tcp"]
            netty.tcp{
              hostname="$host"
              port=$port
            }
          }
        }
     """)

    val sparkMasterSystem = ActorSystem("master", config = config)

    val sparkMasterRef: ActorRef = sparkMasterSystem.actorOf(Props[SparkMaster], "SparkMaster-01")

    sparkMasterRef ! "start"

  }
}