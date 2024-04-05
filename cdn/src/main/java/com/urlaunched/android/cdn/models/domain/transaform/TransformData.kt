package com.urlaunched.android.cdn.models.domain.transaform

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransformData(
    @SerialName("bucket")
    val bucket: String,
    @SerialName("key")
    val objectKey: String,
    @SerialName("edits")
    val edits: Edits
)