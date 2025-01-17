package com.urlaunched.android.cdn.models.presentation.image.transform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Edits(
    @SerialName("resize")
    val resize: Resize,
    @SerialName("grayscale")
    val isGrayscale: Boolean = false
)