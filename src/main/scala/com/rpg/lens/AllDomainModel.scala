package com.rpg.lens

case class UserInfo(
                     id: UserId,
                     generalInfo: GeneralInfo,
                     billingInfo: BillingInfo
                   )

case class UserId(id: Long) extends AnyVal

case class GeneralInfo(
                        email: Email,
                        isEmailConfirmed: Boolean = false,
                        password: String,
                        phone: String,
                        isPhoneConfirmed: Boolean = false,
                        siteInfo: SiteInfo
                      )

case class Email(email: String) extends AnyVal
case  class SiteInfo(
                      siteAliasName: String,
                      avatarURL: String,
                      userRating: Long
                    )

case class BillingInfo(
                        name: String,
                        addresses: List[Address]
                      )

case class Address(
                    houseNo: String,
                    street: String,
                    city: City,
                    country: Country,
                    isAddressConfirmed: Boolean = false
                  )
case class City(name: String) extends AnyVal
case class Country(name: String) extends AnyVal
