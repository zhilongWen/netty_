package org.apache.spark.rpc.rpctest

import org.apache.spark.rpc.RpcEndpointRef

// worker 注册时发送给服务器的信息。
case class RegisterWorkerInfo(id: String,worker: RpcEndpointRef, cpu: Int, ram: Int)

// 这个结构体用于 master 将每个注册的 worker 信息保存到 hashMap 当中。
// 为什么 master 不直接存放 RegisterWorkerInfo 呢？
// 在之后，WorkerInfo 会进行拓展。（比如增加 worker 上一次的心跳时间）
class WorkerInfo(val id: String, val cpu: Int, val ram: Int) {

  //拓展：需要记录每个 worker 上次发送心跳消息的信息。
  var lastHeartBeat : Long = System.currentTimeMillis()

}

// 当注册成功时，返回这个单例对象。
// 和 case class 的区别是：case object 不具备 apply, unapply 方法。
case object RegisteredWorkerInfo

// Akka 上下文通过计时器提醒 worker 发送心跳信息时，发送此单例对象。
case object SendHeartBeat

// worker 收到 SendHeartBeat 时，向 master 发送对应的心跳信息，并标注自己的 id 。
case class HeartBeat(id: String)

// master 在启动时会向自己发送此单例对象，来触发定期检查机制。
case object StartTimeOutWorker

// AKka 上下文通过计时器提醒 master 删除过期的 workers 时，发送此单例对象。
case object RemoveTimeOutWorker