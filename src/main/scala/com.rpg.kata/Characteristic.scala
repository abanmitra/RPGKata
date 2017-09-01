package com.rpg.kata

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by aban.m on 8/28/2017.
  */

trait DamageToCharacter[T, P] extends ((T, P) => Future[P])

trait HealToCharacter[T, P] extends ((T, P) => Future[P])

//trait ProfileCharacter[T] extends (T => Future[T])

trait Character

case class ChangeHealth(name: String, effectHealthAmount: Int) extends Character

case class PhysicalCharacter(name: String, health: Int = 1000, level: Int = 1, alive: Boolean = true) extends Character {

  def processDamageToCharacters(changeHealth: ChangeHealth, character: PhysicalCharacter)(implicit damageCharacters: DamageToCharacter[ChangeHealth, PhysicalCharacter]): Future[PhysicalCharacter] = {
    damageCharacters(changeHealth, character)
  }

  def processHealToCharacter(changeHealth: ChangeHealth, character: PhysicalCharacter)(implicit healCharacter: HealToCharacter[ChangeHealth, PhysicalCharacter]): Future[PhysicalCharacter] = {
    healCharacter(changeHealth, character)
  }
}


object Character {

  /*implicit object profileCharacter extends  ProfileCharacter[PhysicalCharacter] {
    override def apply(pChar: PhysicalCharacter): Future[PhysicalCharacter] = {
      printStatusAfterEffect(pChar)
      Future{pChar}
    }
  }*/

  implicit object damageToCharacters extends DamageToCharacter[ChangeHealth, PhysicalCharacter] {
    override def apply(cChar: ChangeHealth, pChar: PhysicalCharacter): Future[PhysicalCharacter] = {

      printStatusBeforeEffect(cChar, pChar)
      println(s"Process -> Damage health...")
      val char: PhysicalCharacter = pChar.name match {
        case cChar.name =>
          pChar.health match {
            case x if (x == 0 || (x > 0 && cChar.effectHealthAmount > pChar.health)) =>
              PhysicalCharacter(cChar.name, 0, pChar.level, false)
            case _ =>
              PhysicalCharacter(cChar.name, pChar.health - cChar.effectHealthAmount, pChar.level, true)
          }
        case _ => pChar
      }
      printStatusAfterEffect(char)
      Future{char}

    }
  }

  implicit object healToCharacter extends HealToCharacter[ChangeHealth, PhysicalCharacter] {
    override def apply(cChar: ChangeHealth, pChar: PhysicalCharacter): Future[PhysicalCharacter] = {

      printStatusBeforeEffect(cChar, pChar)
      println(s"Process -> Heal health...")
      val char = pChar.name match {
        case cChar.name =>
          pChar.alive match {
            case true => {
              if (pChar.health + cChar.effectHealthAmount >= 1000)
                PhysicalCharacter(cChar.name, 1000, pChar.level, true)
              else
                PhysicalCharacter(cChar.name, pChar.health + cChar.effectHealthAmount, pChar.level, true)
            }
            case false => pChar
          }
        case _ => pChar
      }
      printStatusAfterEffect(char)
      Future{char}

    }
  }

  def printStatusBeforeEffect(cChar: ChangeHealth, pChar: PhysicalCharacter) : Unit = {
    println(s"Before effect => Name: ${pChar.name}, Health: ${pChar.health}, Level: ${pChar.level}, Alive: ${pChar.alive}")
    println(s"Change Health => Name: ${cChar.name}, Health: ${cChar.effectHealthAmount}")
    println()
  }

  def printStatusAfterEffect(pChar: PhysicalCharacter) : Unit = {
    println(s"After effect => Name: ${pChar.name}, Health: ${pChar.health}, Level: ${pChar.level}, Alive: ${pChar.alive}")
    println("--------------------------------------------------")
  }

}
