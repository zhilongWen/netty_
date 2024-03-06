package com.at.rpc.spark_heartbeat

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class SparkWorker(masterHost: String, masterPort: Int) extends Actor {

  // 实际上就是 ActorRef 的另一种叫法。
  var masterProxy: ActorSelection = _

  // 每个 Worker 生成一个随机 id。
  val id = java.util.UUID.randomUUID().toString

  // 初始化 Master 的 Proxy。
  override def preStart(): Unit = {
    masterProxy = context.actorSelection(
      s"akka.tcp://master@$masterHost:$masterPort/user/SparkMaster-01")
  }

  override def receive: Receive = {
    case "start" =>
      println(" Worker 服务器启动成功!")
      masterProxy ! RegisterWorkerInfo(id, 8, 16 * 1024)
    case RegisteredWorkerInfo =>

      println(s" Worker : $id 注册成功了!")

      //注册成功之后，定义一个计时器，每隔一段时间发送消息。
      import context.dispatcher

      /*
      1. initialDelay:初始延迟。设定当 worker 收到注册成功的消息之后，立刻发送一次心跳检测。
      2. internalDelay:时间间隔。设定每 3000 毫秒发送一次心跳消息。
      3. actorRef:本机的 actor 系统将向哪个 actor 发送消息。
      4. message:发送消息的内容。

      其逻辑是：本机的 Akka 系统通过计时器"提醒"此 worker 发送心跳消息，
      然后 worker 收到此"提醒"之后，再向 master 发送真正的心跳消息。
       */
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)

    case SendHeartBeat =>

      println(s" Worker : $id 发送了心跳信息。")
      masterProxy ! HeartBeat(id)

  }




}

object SparkWorker {

  def main(args: Array[String]): Unit = {

    //1.绑定本机地址和启动的端口号
    val workerHost = "127.0.0.1" //绑定为本机地址
    val workerPort = 10001 //绑定本机启动的端口号

    //1.1 配置远端地址和启动的端口号
    val masterHost = "127.0.0.1"
    val masterPort = 9999



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
              hostname="$workerHost"
              port=$workerPort
            }
          }
        }
     """)


    val workerSystem = ActorSystem("worker", config = config)

    val sparkWorkerActorRef: ActorRef = workerSystem.actorOf(Props(new SparkWorker(masterHost, masterPort)), "SparkWorker")

    sparkWorkerActorRef ! "start"

  }
}