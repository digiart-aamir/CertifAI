package com.asbots.certifai.data

data class Lesson(
    val id: String,
    val title: String,
    val duration: String,
    val isCompleted: Boolean = false,
    val contentText: String
)

data class Module(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
)

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class Course(
    val id: String,
    val title: String,
    val category: String,
    val stageText: String,
    val level: String,
    val duration: String,
    val rating: Double,
    val progressPercent: Int,
    val description: String,
    val modules: List<Module>,
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val projectStatement: String = ""
)

data class Certificate(
    val id: String,
    val courseTitle: String,
    val learnerName: String,
    val issueDate: String,
    val certificateId: String,
    val category: String
)
