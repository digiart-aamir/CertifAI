package com.asbots.certifai.data.models

data class MindmapNode(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val durationText: String,
    val isFree: Boolean = false,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val iconType: String = "code"
)
