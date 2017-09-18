package com.rpg.kata

import java.util.Date
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TimeService {
  def apply(): Long
  def currentTime(): Long
}

object TimeService {
  implicit object SystemClockTimeService extends TimeService {
    override def apply(): Long = System.nanoTime()

    override def currentTime: Long = {
      (new Date()).getTime
    }
  }
}

trait DateFormatting {
  def format(timestamp: Long) : String
}

object DateFormatting {
  implicit object DateFormattingService extends DateFormatting {
    override def format(timestamp: Long): String = {
      val format = new java.text.SimpleDateFormat("MM.dd.yyyy HH:mm:ss.SSS")
      format.format(timestamp)
    }
  }
}

object ProfileService {
  def profile[T, R](fn: T => Future[R]) : T => Future[R] = new ProfileService[T, R](fn)
}

class ProfileService[T, R](fn: T => Future[R]) (implicit ts: TimeService, df: DateFormatting)  extends ((T) => Future[R]) {

  override def apply(t: T)  = {
    val startTime: Long = ts()
    val result: Future[R] = fn(t)

    result.onComplete{ result =>
      val timeSpan = ts() - startTime
      println(s"${df.format(ts.currentTime())} [Profile INFO] ::: Started with $t produced $result taking $timeSpan\n")
    }
    result
  }
}

object LogService {
  def logging[T, R](fn: T => Future[R]) : T => Future[R] = new LogService[T, R] (fn)
}

class LogService[T, R](fn: T => Future[R]) (implicit ts: TimeService, df: DateFormatting) extends ((T) => Future[R]) {
  override def apply(t: T) =  {
    t.isInstanceOf[PhysicalCharacter] match {
      case true =>
        val pc = t.asInstanceOf[PhysicalCharacter]
        println(s"${df.format(ts.currentTime())} [Logging INFO] ::: Current status of the processing object is: Name - ${pc.name}, Health - ${pc.health}\n")
      case _ =>
        println(s"${df.format(ts.currentTime())} [Logging INFO] ::: Current status of the processing object is: ${t}\n")
    }
    fn(t)
  }
}

object MetricsService {
  def metrics[T, R](fn: T =>  Future[R]) : T => Future[R] = new MetricsService[T, R] (fn)
}

class MetricsService[T, R](fn: T =>  Future[R]) (implicit ts: TimeService, df: DateFormatting) extends ((T) => Future[R]) {
  override def apply(t: T) = {
    t.isInstanceOf[PhysicalCharacter] match {
      case true =>
        val pc = t.asInstanceOf[PhysicalCharacter]
        println(s"${df.format(ts.currentTime())} [Metrics INFO] ::: HEAL Count: ${pc.noOfHeal}, DAMAGE Count: ${pc.noOfDamage}\n")
      case _ =>
        println(s"${df.format(ts.currentTime())} [Metrics INFO] ::: No Metrics Information available for ${t}\n")
    }
    fn(t)
  }
}