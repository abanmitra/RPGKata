package com.rpg.kata

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TimeService {
  def apply: Long
}

object TimeService extends TimeService {
  override def apply: Long = System.currentTimeMillis()
}

class ProfileService[T, R](fn: T => Future[R]) extends ((T) => Future[R]) {
  override def apply(t: T) = {
    val startTime: Long = System.nanoTime()//timeService()
    val result: Future[R] = fn(t)

    result.onComplete{ result =>
      val timeSpan = System.nanoTime() - startTime//timeService() - startTime
      println(s"Started with $t produced $result taking $timeSpan")
    }
    result
  }
}

object ProfileService {
  def profile[T, R](fn: T => Future[R]) : T => Future[R] = new ProfileService[T, R](fn)
}
