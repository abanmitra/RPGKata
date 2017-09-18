package com.rpg.kata

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by aban.m on 8/28/2017.
  */

trait MyArrow {

  implicit class FutureArrow[T, T1](fn1: T => Future[T1]) {
    def ~>[T2](fn2: T1 => Future[T2]): (T) => Future[T2] = {
      t: T => fn1(t).flatMap(fn2)
    }
  }

}

object MyArrow extends MyArrow

object CharacterBehaviour {

  def damage(health: ChangeHealth)(physicalCharacter: PhysicalCharacter): Future[PhysicalCharacter] = {
    physicalCharacter processDamageToCharacters(health, physicalCharacter)
  }

  def heal(health: ChangeHealth)(physicalCharacter: PhysicalCharacter): Future[PhysicalCharacter] = {
    physicalCharacter processHealToCharacter(health, physicalCharacter)
  }

}

object CharacterInAction {

  def main(args: Array[String]): Unit = {

    import CharacterBehaviour._
    import MyArrow._
    import ProfileService._
    import LogService._
    import MetricsService._

    val char = PhysicalCharacter("PERSON")

    val goodHealth100 = ChangeHealth("PERSON", 100)
    val badHealth100 = ChangeHealth("PERSON", 100)
    val badHealth800 = ChangeHealth("PERSON", 800)

    val damageWith100: (PhysicalCharacter) => Future[PhysicalCharacter] = damage(badHealth100) _

    val healWith100 = heal(goodHealth100) _

    val massiveDamage = damage(badHealth800) _

    val doubleDamageWith100 = damage(badHealth100) _ ~> damage(badHealth100) _

    val doubleHealWith100 = heal(goodHealth100) _ ~> heal(goodHealth100) _

    val damageAndThenHeal100 = damage(badHealth100) _ ~> heal(goodHealth100) _

    val healAndThenDamage100: (PhysicalCharacter) => Future[PhysicalCharacter] = heal(goodHealth100) _ ~> damage(badHealth100) _

    // This is not the proper way to implement. Herw we use ~> concept
    val tmp: Future[PhysicalCharacter] = damageWith100(char).flatMap(damageWith100)

    val charEffect =
      metrics(logging(profile(doubleDamageWith100))) ~>
        metrics(logging(profile(damageWith100))) ~>
        metrics(logging(profile(damageAndThenHeal100))) ~>
        metrics(logging(profile(healAndThenDamage100))) ~>
        metrics(logging(profile(doubleHealWith100))) ~>
        metrics(logging(profile(massiveDamage))) ~>
        metrics(logging(profile(healWith100))) ~>
        metrics(logging(profile(massiveDamage))) ~>
        metrics(logging(profile(healWith100)))

    // val charEffect = doubleDamageWith100 ~> damageWith100 ~> damageAndThenHeal100 ~> healAndThenDamage100


    val duration = Duration(500, "millis")
    val finalChar: PhysicalCharacter = Await.result(charEffect(char), duration)

    println("\nFinal Status....")
    Character.printStatusAfterEffect(finalChar)
  }

}