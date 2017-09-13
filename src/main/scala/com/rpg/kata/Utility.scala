package com.rpg.kata

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TimeService {
  def apply(): Long
}

object TimeService {

  implicit object SystemClockTimeService extends TimeService {
    override def apply(): Long = System.nanoTime()
  }

}

object ProfileService {
  def profile[T, R](fn: T => Future[R]) : T => Future[R] = new ProfileService[T, R](fn)
}

class ProfileService[T, R](fn: T => Future[R]) (implicit timeService: TimeService)  extends ((T) => Future[R]) {

  override def apply(t: T)  = {

    val startTime: Long = timeService()
    val result: Future[R] = fn(t)

    result.onComplete{ result =>
      val timeSpan = timeService() - startTime
      println(s"Started with $t produced $result taking $timeSpan")
    }
    result
  }
}
