package com.asbots.certifai.data.models

import com.google.gson.annotations.SerializedName

enum class NodeStatus {
    LOCKED,
    IN_PROGRESS,
    COMPLETED
}

data class RoadmapNode(
    @SerializedName("id") val id: Int,
    @SerializedName("stage_number") val stageNumber: Int,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("status") val status: NodeStatus,
    @SerializedName("as_coins_reward") val asCoinsReward: Int,
    @SerializedName("total_lessons") val totalLessons: Int,
    @SerializedName("completed_lessons") val completedLessons: Int
)

data class CourseModule(
    @SerializedName("id") val id: String,
    @SerializedName("node_id") val nodeId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String,
    @SerializedName("level") val level: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("rating") val rating: Float = 4.9f,
    @SerializedName("reviews_count") val reviewsCount: Int = 1250,
    @SerializedName("as_coins_price") val asCoinsPrice: Int = 0,
    @SerializedName("is_enrolled") var isEnrolled: Boolean = false,
    @SerializedName("is_completed") var isCompleted: Boolean = false,
    @SerializedName("lessons") val lessons: List<LessonItem> = emptyList()
)

data class LessonItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("type") val type: String, // VIDEO, CODE_SANDBOX, QUIZ, PROJECT
    @SerializedName("video_url") val videoUrl: String? = null,
    @SerializedName("code_snippet") val codeSnippet: String? = null,
    @SerializedName("is_completed") var isCompleted: Boolean = false
)

data class QuizQuestion(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("correct_option_index") val correctOptionIndex: Int,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("as_coins_reward") val asCoinsReward: Int = 20
)

data class UserCertificate(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("issue_date") val issueDate: String,
    @SerializedName("verification_url") val verificationUrl: String,
    @SerializedName("badge_icon") val badgeIcon: String,
    @SerializedName("category") val category: String,
    @SerializedName("as_coins_earned") val asCoinsEarned: Int
)

data class RoadmapNodesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("nodes") val nodes: List<RoadmapNode>?
)

data class CourseModulesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("modules") val modules: List<CourseModule>?
)
