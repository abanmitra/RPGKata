package com.rpg.kata

import com.rpg.kata

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by aban.m on 8/28/2017.
  */

trait MyArrow {

  implicit class FutureArrow[T, T1](fn1: T => Future[T1]) {
    def ~>[T2](fn2 : T1 => Future[T2]): (T) => Future[T2] = {
      t: T => fn1(t).flatMap(fn2)
    }
  }
}

object MyArrow extends MyArrow

//trait Profiling[T] extends ((T) => Future[T])

object CharacterBehaviour {

  def damage(health: ChangeHealth)(physicalCharacter: PhysicalCharacter): Future[PhysicalCharacter] = {
    physicalCharacter processDamageToCharacters(health, physicalCharacter)
  }

  def heal(health: ChangeHealth)(physicalCharacter: PhysicalCharacter): Future[PhysicalCharacter] = {
    physicalCharacter processHealToCharacter(health, physicalCharacter)
  }

  /*implicit object profileCharacters extends Profiling[PhysicalCharacter] {
    override def apply(pc: PhysicalCharacter): Future[PhysicalCharacter] = {
      println(s"\n\n Profile Health :: ${pc.health}   \n\n")
      Future(pc)
    }
  }*/
}

/*class Profile(delegate: (PhysicalCharacter) => Future[PhysicalCharacter]) extends ((PhysicalCharacter) => Future[PhysicalCharacter]) {
  override def apply(pc: PhysicalCharacter): Future[PhysicalCharacter] = {
    println(s"\n\n ${pc.health}   \n\n")
    delegate(pc)
  }
}*/

object CharacterInAction {

  def main(args: Array[String]): Unit = {

    import CharacterBehaviour._
    import MyArrow._
    import ProfileService._

    val char = PhysicalCharacter("PERSON")

    val goodHealth100 = ChangeHealth("PERSON", 100)
    val badHealth100 = ChangeHealth("PERSON", 100)
    val badHealth800 = ChangeHealth("PERSON", 800)

    //val profile = implicitly[Profiling[PhysicalCharacter]]

    val damageWith100 = damage(badHealth100) _

    val healWith100 = heal(goodHealth100) _

    val masiveDamage = damage(badHealth800) _

    val doubleDamageWith100 = damage(badHealth100) _  ~> damage(badHealth100) _

    val doubleHealWith100 = heal(goodHealth100) _  ~>  heal(goodHealth100) _

    val damageAndThenHeal100 = damage(badHealth100) _  ~>  heal(goodHealth100) _

    val healAndThenDamage100 = heal(goodHealth100) _ ~> damage(badHealth100) _

    //val charEffect = new Profile(doubleDamageWith100) ~> damageWith100 ~> new Profile(doubleHealWith100) ~> masiveDamage ~> healWith100 ~> masiveDamage ~> healWith100
    //val charEffect = profile ~> doubleDamageWith100 ~> damageWith100 ~> profile ~> doubleHealWith100 ~> masiveDamage ~> healWith100 ~> profile ~> masiveDamage ~> healWith100
    val charEffect = profile(doubleDamageWith100) ~> damageWith100 ~> profile(doubleHealWith100) ~> masiveDamage ~> healWith100 ~> profile(masiveDamage) ~> healWith100

    val duration = Duration(20, "millis")
    val finalChar: PhysicalCharacter = Await.result(charEffect(char),duration)

    println("\nFinal Status....")
    Character.printStatusAfterEffect(finalChar)
  }

}