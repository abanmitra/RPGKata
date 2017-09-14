package com.rpg.lens

case class DomainLines[SUPER, CHILD](set: (SUPER, CHILD) => SUPER, get: SUPER => CHILD) {

  def apply(sup: SUPER): CHILD = get(sup)

  def transformation(sup: SUPER, func: CHILD => CHILD): SUPER = set(sup, func(get(sup)))

  def =>=[NEWSUPER](otherLens: DomainLines[CHILD, NEWSUPER]): DomainLines[SUPER, NEWSUPER] = andThen(otherLens)

  def andThen[NEWSUPER](otherLens: DomainLines[CHILD, NEWSUPER]): DomainLines[SUPER, NEWSUPER] = otherLens compose this

  def compose[NEWSUPER](otherLens: DomainLines[NEWSUPER, SUPER]): DomainLines[NEWSUPER, CHILD] =
    DomainLines[NEWSUPER, CHILD]((newSuper, child) => otherLens.transformation(newSuper, set(_, child)), newSuper => get(otherLens.get(newSuper)))

}

object SiteInfoService {

  val generalInfoAgainstUser: DomainLines[UserInfo, GeneralInfo] =
    DomainLines[UserInfo, GeneralInfo]((user, genInfo) => user.copy(generalInfo = genInfo), _.generalInfo)

  val siteInfoAgainstGeneralInfo: DomainLines[GeneralInfo, SiteInfo] =
    DomainLines[GeneralInfo, SiteInfo]((genInfo, stInfo) => genInfo.copy(siteInfo = stInfo), _.siteInfo)

  val userRatingAgainstSiteInfo: DomainLines[SiteInfo, Long] =
    DomainLines[SiteInfo, Long]((stInfo, usrRating) => stInfo.copy(userRating = usrRating), _.userRating)

  def updateUserRating(user: UserInfo): UserInfo = {
    (generalInfoAgainstUser =>= siteInfoAgainstGeneralInfo =>= userRatingAgainstSiteInfo) transformation(user, _ + 10)
  }

  def display(user: UserInfo) = {
    println(s"User ID: ${user.id.id}; User Rating: ${user.generalInfo.siteInfo.userRating}")
  }

}

// Write a function that confirms the first address - ????
object BillingInfoService {

  val billingInfoAgainstUser: DomainLines[UserInfo, BillingInfo] =
    DomainLines[UserInfo, BillingInfo] ((user, billingInfo) => user.copy(billingInfo = billingInfo), _.billingInfo)

  val addressAgainstBillingInfo: DomainLines[BillingInfo, List[Address]] =
    DomainLines[BillingInfo, List[Address]] ((billing, adrss) => billing.copy(addresses = adrss), _.addresses)

  def updateAddress(user: UserInfo, updateAddressInfo: Address) = {

    /*********************************************************
      * It return new update address info, not UserInfo.
      *********************************************************/
    (billingInfoAgainstUser =>= addressAgainstBillingInfo) get(user) updated(0,updateAddressInfo)

    /********************************************************
      * Not able to call transformation function -- ?????
      ********************************************************/
    //(billingInfoAgainstUser =>= addressAgainstBillingInfo) transformation (user, user.billingInfo.addresses.updated(0,updateAddressInfo))

  }
}



object ProcessDomain {

  def main(args: Array[String]): Unit = {

    val siteInfo = SiteInfo("someName", "URL", 10)

    val email = Email("aban.m@hcl.com")
    val generalInfo = GeneralInfo(email, true, "password", "12345678", true, siteInfo)

    val city = City("kolkata")
    val country = Country("INDIA")
    val address1 = Address("123", "Street1", city, country, true)
    val address2 = Address("321", "Street123", city, country, true)
    val addresses = List(address1,address2)
    val billingInfo = BillingInfo("bill1",addresses)

    val userId = UserId(100)
    val user = UserInfo(userId,generalInfo,billingInfo)

    import SiteInfoService._

    display(user)

    val newUser = updateUserRating(user)

    println("After Update rating...")
    display(newUser)

    val newAddress = Address("99999", "ROAD - 007", city, country, true)

    // Write a function that confirms the first address - ????
    import BillingInfoService._
    val address: List[Address] = updateAddress(user, newAddress)
  }
}
