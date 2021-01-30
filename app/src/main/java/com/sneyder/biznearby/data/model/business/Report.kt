package com.sneyder.biznearby.data.model.business

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReviewReportRequest(
    @SerializedName("analysis") val analysis: String
)

data class Report(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String?,
    @SerializedName("businessId") val businessId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("createdAt") val createdAt: Date?,
    @SerializedName("reviewedAt") val reviewedAt: Date?
)

data class ReportRequest(
    @SerializedName("id") val id: String,
    @SerializedName("businessId") val businessId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)
