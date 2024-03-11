package org.apache.spark.rpc.rpctest


import org.apache.spark.internal.Logging
import org.apache.spark.rpc._
import org.apache.spark.{SecurityManager, SparkConf}

import java.util.UUID


class DeployWorker(override val rpcEnv: RpcEnv) extends ThreadSafeRpcEndpoint with Logging {

  // 每个 Worker 生成一个随机 id。
  val id = java.util.UUID.randomUUID().toString


  private def getMasterEndpointRef: RpcEndpointRef = rpcEnv.setupEndpointRef(RpcAddress("127.0.0.1", 9099), DeployMaster.ENDPOINT_NAME)

  override def onStart(): Unit = {
    println("DeployWorker onStart")

    getMasterEndpointRef.send(RegisterWorkerInfo(id, self, 8, 16 * 1024))

  }


  override def receive: PartialFunction[Any, Unit] = {

    case RegisteredWorkerInfo =>

      println(s"id = ${id} 注册成功")

  }

  override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = super.receiveAndReply(context)


  override def onStop(): Unit = super.onStop()
}

object DeployWorker {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    val securityMgr = new SecurityManager(conf)

    val rpcEnv = RpcEnv.create("DeployWorker" + UUID.randomUUID().toString, "127.0.0.1", 9099, conf, securityMgr)

    val worker = new DeployWorker(rpcEnv)
    val workerRef: RpcEndpointRef = rpcEnv.setupEndpoint("Worker", worker)


    rpcEnv.awaitTermination()

  }

}
