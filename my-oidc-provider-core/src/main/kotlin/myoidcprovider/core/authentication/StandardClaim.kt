package myoidcprovider.core.authentication

/**
 * スタンダードクレーム。
 */
data class StandardClaim(
    /**
     * name
     */
    val name: String? = null,
    /**
     * given_name
     */
    val givenName: String? = null,
    /**
     * family_name
     */
    val familyName: String? = null,
    /**
     * middle_name
     */
    val middleName: String? = null,
    /**
     * nickname
     */
    val nickname: String? = null,
    /**
     * preferred_username
     */
    val preferredUsername: String? = null,
    /**
     * profile
     */
    val profile: String? = null,
    /**
     * picture
     */
    val picture: String? = null,
    /**
     * website
     */
    val website: String? = null,
    /**
     * email
     */
    val email: String? = null,
    /**
     * email_verified
     */
    val emailVerified: Boolean? = null,
    /**
     * gender
     */
    val gender: String? = null,
    /**
     * birthdate
     */
    val birthdate: String? = null,
    /**
     * zoneinfo
     */
    val zoneInfo: String? = null,
    /**
     * locale
     */
    val locale: String? = null,
    /**
     * phone_number
     */
    val phoneNumber: String? = null,
    /**
     * phone_number_verified
     */
    val phoneNumberVerified: Boolean? = null,
    /**
     * address
     */
    val address: AddressClaim? = null,
    /**
     * updated_at
     */
    val updatedAt: Long? = null,
)
