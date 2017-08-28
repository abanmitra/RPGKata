package com.rpg.kata

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CharacterBehaviour {

  def damageToCharacters(physicalCharacter: PhysicalCharacter, health: ChangeHealth): Future[PhysicalCharacter] = {
    physicalCharacter processDamageToCharacters(health, physicalCharacter)
  }

  def healToCharacter(physicalCharacter: PhysicalCharacter, health: ChangeHealth): Future[PhysicalCharacter] = {
    physicalCharacter processHealToCharacter(health, physicalCharacter)
  }

}


object CharacterInAction {

  def main(args: Array[String]): Unit = {

    import CharacterBehaviour._

    val char = PhysicalCharacter("PERSON")

    val goodHealth = ChangeHealth("PERSON", 100)
    val badHealth = ChangeHealth("PERSON", 200)

    val change1: Future[PhysicalCharacter] = damageToCharacters(char, badHealth)

    change1.onSuccess({
      case result =>
        println(s"${result.name} :: ${result.health}\n\n")
        val change2 = damageToCharacters(result, badHealth)

        change2.onComplete({
          case result =>
            println("*")
            println(s"${result.get.name} :: ${result.get.health}\n\n")
            val change3 = damageToCharacters(result.get, badHealth)

            change3.onComplete({
              case result =>
                println("*")
                println(s"${result.get.name} :: ${result.get.health}\n\n")
                val change4 = healToCharacter(result.get, goodHealth)

                change4.onComplete({
                  case result =>
                    println("*")
                    println(s"${result.get.name} :: ${result.get.health}\n\n")
                    val change5 = healToCharacter(result.get, goodHealth)

                    change5.onComplete({
                      case result =>

                        val veryBadHealth = ChangeHealth("PERSON", 800)
                        println("*")
                        println(s"${result.get.name} :: ${result.get.health}\n\n")
                        val change6 = damageToCharacters(result.get, veryBadHealth)

                        change6.onComplete({
                          case result =>
                            val recoverHealth = ChangeHealth("PERSON", 1000)
                            println("*")
                            println(s"${result.get.name} :: ${result.get.health}\n\n")
                            healToCharacter(result.get, recoverHealth)
                        })
                    })
                })
            })
        })
    })

    /*val change2 = damageToCharacters(change1, badHealth)
    val change3 = damageToCharacters(change2, badHealth)
    val change4 = healToCharacter(change3, goodHealth)
    val change5 = healToCharacter(change4, goodHealth)

    val veryBadHealth = ChangeHealth("PERSON", 800)
    val change6 = damageToCharacters(change5, veryBadHealth)

    val recoverHealth = ChangeHealth("PERSON", 1000)
    healToCharacter(change6, recoverHealth)*/
  }

}