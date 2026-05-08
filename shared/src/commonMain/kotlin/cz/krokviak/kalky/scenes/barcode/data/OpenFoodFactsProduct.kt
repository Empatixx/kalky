package cz.krokviak.kalky.scenes.barcode.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val code: String? = null,
    val product: OpenFoodFactsProduct? = null,
    val status: Int? = null
)

@Serializable
data class OpenFoodFactsSearchResponse(
    val count: Int? = null,
    val hits: List<OpenFoodFactsProduct> = emptyList()
)

@Serializable
data class OpenFoodFactsProduct(
    @SerialName("product_name") val productName: String? = null,
    val nutriments: OpenFoodFactsNutriments? = null,
    @SerialName("image_front_url") val imageFrontUrl: String? = null,
    @SerialName("product_quantity") val productQuantity: String? = null,
    @SerialName("serving_size") val servingSize: String? = null
)

@Serializable
data class OpenFoodFactsNutriments(
    @SerialName("energy-kcal_100g") val energyKcal100g: Double? = null,
    @SerialName("proteins_100g") val proteins100g: Double? = null,
    @SerialName("fat_100g") val fat100g: Double? = null,
    @SerialName("carbohydrates_100g") val carbohydrates100g: Double? = null
)
