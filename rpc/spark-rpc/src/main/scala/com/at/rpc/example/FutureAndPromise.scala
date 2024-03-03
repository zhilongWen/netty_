package com.at.rpc.example

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object FutureAndPromise {

  def main(args: Array[String]): Unit = {

    println(System.nanoTime())

//    Await.result(donutStock("vv"),5 seconds)

//    donutStock("dd").onComplete{
//      case Success(res) => println(s"Success ${res}")
//      case Failure(exception) => println(s"fail ${exception}")
//    }

//    val res: Future[Boolean] = donutStock("ppp").flatMap(r => buyDonuts(r))
//    val bool: Boolean = Await.result(res, 5 seconds)
//    println(s"bool = ${bool}")

//    for {
//      s1 <- donutStock("ddd")
//      s2 <- buyDonuts(s1)
//    } yield println(s"res s1 = ${s1} s2 = ${s2}")
//    Thread.sleep(3000)

    val eventualInts = List(
      donutStock("s1"),
      donutStock("s2"),
      donutStock("s3")
    )
//    val res: Future[List[Int]] = Future.sequence(eventualInts)
//    res.onComplete {
//      case Failure(exception) => println(s"exception = ${exception}")
//      case Success(value) => println(s"res = ${value}")
//    }
//    val ints: List[Int] = Await.result(res, 5 seconds)
//    println(ints.mkString(","))

    val futureTraverseResult = Future.traverse(eventualInts) { futureSomeQty =>
      val qty: Future[Int] = futureSomeQty
      qty.map(someQty => someQty + 1)
    }
    futureTraverseResult.onComplete {
      case Success(results) => println(s"Results $results")
      case Failure(e) => println(s"Error processing future operations, error = ${e.getMessage}")
    }
    Thread.sleep(1000)

  }






  def buyDonuts(param:Int):Future[Boolean] = Future{
    println(s"buyDonuts ${param}")
    true
  }

  def donutStock(dount :String): Future[Int] = Future {
    println("checking donut stock")
//    Thread.sleep(7000)
//    10 / 0
    10
  }


}
