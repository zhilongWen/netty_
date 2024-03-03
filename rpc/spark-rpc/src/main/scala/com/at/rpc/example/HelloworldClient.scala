package com.at.rpc.example

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.at.rpc.spark.RpcConf
import com.at.rpc.spark.rpc.netty.NettyRpcEnvFactory
import com.at.rpc.spark.rpc.{RpcAddress, RpcEndpointRef, RpcEnv, RpcEnvClientConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

/**
  * Created by xu.zhang on 8/12/17.
  */
object HelloworldClient {

  def main(args: Array[String]): Unit = {
    asyncCall()
//    syncCall()
  }

  def asyncCall() = {
    val rpcConf = new RpcConf()
    rpcConf.set("spark.rpc.askTimeout","100")
    val config = RpcEnvClientConfig(rpcConf, "hello-client")
    val rpcEnv: RpcEnv = NettyRpcEnvFactory.create(config)
    val endPointRef: RpcEndpointRef = rpcEnv.setupEndpointRef(RpcAddress("localhost", 52345), "hello-service")
    val future: Future[String] = endPointRef.ask[String](SayHi("neo"))
//    val future: Future[String] = endPointRef.ask()[String](SayHi("neo") )
    future.onComplete {
      case scala.util.Success(value) => println(s"Got the result = $value")
      case scala.util.Failure(e) => println(s"Got error: $e")
    }
    Await.result(future, 30 seconds)
  }

  def syncCall() = {
    val rpcConf = new RpcConf()
    val config = RpcEnvClientConfig(rpcConf, "hello-client")
    val rpcEnv: RpcEnv = NettyRpcEnvFactory.create(config)
    val endPointRef: RpcEndpointRef = rpcEnv.setupEndpointRef(RpcAddress("localhost", 52345), "hello-service")
    val result = endPointRef.askWithRetry[String](SayBye("neo"))
    println(result)
  }
}
