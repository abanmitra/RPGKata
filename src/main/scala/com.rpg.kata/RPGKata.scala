package com.rpg.kata

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.duration._

/**
  * Created by aban.m on 8/28/2017.
  */

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

    val char = PhysicalCharacter("PERSON")

    val goodHealth100 = ChangeHealth("PERSON", 100)
    val badHealth100 = ChangeHealth("PERSON", 100)
    val badHealth800 = ChangeHealth("PERSON", 800)

    val applyTooLossDamage =
      damage(badHealth100) _  ~> damage(badHealth100) _  ~>  damage(badHealth100) _ ~> heal(goodHealth100) _ ~> heal(goodHealth100) _ ~> damage(badHealth800) _ ~> heal(goodHealth100) _ ~> damage(badHealth800) _ ~> heal(goodHealth100) _

    val duration = Duration(5, "millis")
    val finalChar: PhysicalCharacter = Await.result(applyTooLossDamage(char),duration)

    println("\nFinal Status....")
    Character.printStatusAfterEffect(finalChar)
  }

}