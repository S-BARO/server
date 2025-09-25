package com.dh.baro.look.application.dto

data class AiFittingInfo(
    val sourceImageUrl: String,
    val clothingImageUrl: String,
    val generatedImageData: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AiFittingInfo

        if (sourceImageUrl != other.sourceImageUrl) return false
        if (clothingImageUrl != other.clothingImageUrl) return false
        if (!generatedImageData.contentEquals(other.generatedImageData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sourceImageUrl.hashCode()
        result = 31 * result + clothingImageUrl.hashCode()
        result = 31 * result + generatedImageData.contentHashCode()
        return result
    }
}
