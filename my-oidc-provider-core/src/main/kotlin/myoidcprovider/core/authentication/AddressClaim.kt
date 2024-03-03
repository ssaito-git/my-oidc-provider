package myoidcprovider.core.authentication

/**
 * 住所クレーム。
 */
data class AddressClaim(
    /**
     * formatted
     */
    val formatted: String?,
    /**
     * street_address
     */
    val streetAddress: String?,
    /**
     * locality
     */
    val locality: String?,
    /**
     * region
     */
    val region: String?,
    /**
     * postal_code
     */
    val postalCode: String?,
    /**
     * country
     */
    val country: String?,
)
