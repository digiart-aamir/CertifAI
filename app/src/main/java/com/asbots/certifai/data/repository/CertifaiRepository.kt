package com.asbots.certifai.data.repository

import com.asbots.certifai.data.models.CourseModule
import com.asbots.certifai.data.models.LessonItem
import com.asbots.certifai.data.models.NodeStatus
import com.asbots.certifai.data.models.QuizQuestion
import com.asbots.certifai.data.models.RoadmapNode
import com.asbots.certifai.data.models.UserCertificate

object CertifaiRepository {

    // 10-Stage ASBots CertifAI Roadmap Nodes
    private val nodesList = mutableListOf(
        RoadmapNode(
            id = 1,
            stageNumber = 1,
            title = "Python for AI & CS Fundamentals",
            subtitle = "Variables, Control Flow & Data Structures",
            description = "Master core Python 3 syntax, OOP, Pyodide in-browser execution, and memory stack layout.",
            category = "Python & CS",
            status = NodeStatus.COMPLETED,
            asCoinsReward = 50,
            totalLessons = 8,
            completedLessons = 8
        ),
        RoadmapNode(
            id = 2,
            stageNumber = 2,
            title = "NumPy, Pandas & Data Wrangling",
            subtitle = "Vectorized Operations & Data Cleaning",
            description = "Process multi-dimensional arrays, perform matrix transformations, and clean datasets efficiently.",
            category = "Data Science",
            status = NodeStatus.COMPLETED,
            asCoinsReward = 75,
            totalLessons = 6,
            completedLessons = 6
        ),
        RoadmapNode(
            id = 3,
            stageNumber = 3,
            title = "Classical Machine Learning",
            subtitle = "Linear Models, Decision Trees & SVMs",
            description = "Implement Scikit-Learn algorithms, perform cross-validation, and tune hyperparameters.",
            category = "Machine Learning",
            status = NodeStatus.IN_PROGRESS,
            asCoinsReward = 100,
            totalLessons = 10,
            completedLessons = 4
        ),
        RoadmapNode(
            id = 4,
            stageNumber = 4,
            title = "Deep Learning & Neural Networks",
            subtitle = "PyTorch, Activation Functions & Backprop",
            description = "Build artificial neural networks (ANNs), optimize loss functions, and prevent overfitting.",
            category = "Deep Learning",
            status = NodeStatus.IN_PROGRESS,
            asCoinsReward = 125,
            totalLessons = 8,
            completedLessons = 2
        ),
        RoadmapNode(
            id = 5,
            stageNumber = 5,
            title = "Computer Vision & OpenCV",
            subtitle = "CNNs, Object Detection & YOLOv8",
            description = "Train Convolutional Neural Networks, perform real-time video inference, and segment images.",
            category = "Computer Vision",
            status = NodeStatus.LOCKED,
            asCoinsReward = 150,
            totalLessons = 8,
            completedLessons = 0
        ),
        RoadmapNode(
            id = 6,
            stageNumber = 6,
            title = "NLP, Transformers & BERT",
            subtitle = "Tokenization, Self-Attention & Embeddings",
            description = "Understand Transformer architectures, attention mechanisms, and HuggingFace pipelines.",
            category = "NLP & LLMs",
            status = NodeStatus.LOCKED,
            asCoinsReward = 175,
            totalLessons = 8,
            completedLessons = 0
        ),
        RoadmapNode(
            id = 7,
            stageNumber = 7,
            title = "RAG Systems & Vector Databases",
            subtitle = "LangChain, LlamaIndex & Qdrant/Pinecone",
            description = "Build Retrieval-Augmented Generation architectures with semantic chunking and vector search.",
            category = "RAG & Agents",
            status = NodeStatus.LOCKED,
            asCoinsReward = 200,
            totalLessons = 6,
            completedLessons = 0
        ),
        RoadmapNode(
            id = 8,
            stageNumber = 8,
            title = "Autonomous AI Agents & Multi-Agent Swarms",
            subtitle = "CrewAI, AutoGen & Function Calling",
            description = "Orchestrate multi-agent systems, enable tool usage, memory retention, and autonomous reasoning.",
            category = "RAG & Agents",
            status = NodeStatus.LOCKED,
            asCoinsReward = 250,
            totalLessons = 6,
            completedLessons = 0
        ),
        RoadmapNode(
            id = 9,
            stageNumber = 9,
            title = "Autonomous Robotics & ROS 2",
            subtitle = "Sensor Fusion, SLAM & Motion Planning",
            description = "Program ROS 2 nodes, simulate lidar sensor fusion, and execute robot navigation paths.",
            category = "Autonomous Robotics",
            status = NodeStatus.LOCKED,
            asCoinsReward = 300,
            totalLessons = 8,
            completedLessons = 0
        ),
        RoadmapNode(
            id = 10,
            stageNumber = 10,
            title = "CertifAI Capstone & Deployments",
            subtitle = "Production MLOps, ONNX Runtime & Edge AI",
            description = "Deploy quantized neural networks on edge devices with live performance benchmarks.",
            category = "CertifAI Capstone",
            status = NodeStatus.LOCKED,
            asCoinsReward = 500,
            totalLessons = 12,
            completedLessons = 0
        )
    )

    // Course Modules List
    private val modulesList = mutableListOf(
        CourseModule(
            id = "course_1",
            nodeId = 1,
            title = "Python 3 Mastery for AI Engineers",
            subtitle = "From Variables to In-Browser Execution",
            description = "Learn production Python programming designed specifically for AI data pipelines and automated agents.",
            category = "Python & CS",
            level = "Beginner",
            duration = "4 Hours",
            imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600",
            rating = 4.9f,
            reviewsCount = 1840,
            asCoinsPrice = 0,
            isEnrolled = true,
            isCompleted = true,
            lessons = listOf(
                LessonItem("l1_1", "1. Python Variables & Memory Layout", "12 min", "VIDEO", isCompleted = true),
                LessonItem("l1_2", "2. Interactive Python Code Sandbox", "20 min", "CODE_SANDBOX", isCompleted = true),
                LessonItem("l1_3", "3. Control Flow & Function Signatures", "15 min", "VIDEO", isCompleted = true),
                LessonItem("l1_4", "4. Python Core Principles Quiz", "10 min", "QUIZ", isCompleted = true)
            )
        ),
        CourseModule(
            id = "course_2",
            nodeId = 3,
            title = "Classical Machine Learning with Scikit-Learn",
            subtitle = "Regression, Classification & Clustering",
            description = "Master supervised and unsupervised learning algorithms with hands-on Python model training.",
            category = "Machine Learning",
            level = "Intermediate",
            duration = "6 Hours",
            imageUrl = "https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?w=600",
            rating = 4.8f,
            reviewsCount = 1420,
            asCoinsPrice = 0,
            isEnrolled = true,
            isCompleted = false,
            lessons = listOf(
                LessonItem("l2_1", "1. Supervised Learning & Cost Functions", "18 min", "VIDEO", isCompleted = true),
                LessonItem("l2_2", "2. Decision Trees & Random Forests", "25 min", "VIDEO", isCompleted = true),
                LessonItem("l2_3", "3. Model Evaluation & ROC Curves", "15 min", "QUIZ", isCompleted = false)
            )
        ),
        CourseModule(
            id = "course_3",
            nodeId = 4,
            title = "Deep Learning & PyTorch Architectures",
            subtitle = "Neural Networks from Scratch",
            description = "Build, train, and deploy PyTorch neural networks with custom loss functions and gradient descent.",
            category = "Deep Learning",
            level = "Intermediate",
            duration = "8 Hours",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600",
            rating = 4.95f,
            reviewsCount = 2100,
            asCoinsPrice = 50,
            isEnrolled = true,
            isCompleted = false,
            lessons = listOf(
                LessonItem("l3_1", "1. PyTorch Tensors & Autograd", "22 min", "VIDEO", isCompleted = true),
                LessonItem("l3_2", "2. Training Loop & Optimizers", "30 min", "CODE_SANDBOX", isCompleted = false),
                LessonItem("l3_3", "3. PyTorch Deep Learning Quiz", "15 min", "QUIZ", isCompleted = false)
            )
        ),
        CourseModule(
            id = "course_4",
            nodeId = 7,
            title = "RAG Systems & Vector DBs (Qdrant & Pinecone)",
            subtitle = "Semantic Search & Production RAG",
            description = "Build context-aware AI applications leveraging vector embeddings, hybrid search, and LangChain.",
            category = "RAG & Agents",
            level = "Advanced",
            duration = "5 Hours",
            imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=600",
            rating = 4.9f,
            reviewsCount = 980,
            asCoinsPrice = 100,
            isEnrolled = false,
            isCompleted = false,
            lessons = listOf(
                LessonItem("l4_1", "1. Text Embeddings & Vector Spaces", "20 min", "VIDEO"),
                LessonItem("l4_2", "2. Qdrant Setup & Ingestion Pipeline", "25 min", "CODE_SANDBOX"),
                LessonItem("l4_3", "3. Production RAG Architecture Quiz", "15 min", "QUIZ")
            )
        ),
        CourseModule(
            id = "course_5",
            nodeId = 9,
            title = "Autonomous Robotics & ROS 2 Navigation",
            subtitle = "SLAM, Sensor Fusion & Path Planning",
            description = "Program autonomous mobile robots using ROS 2 Nodes, Lidar SLAM, and Nav2 motion controllers.",
            category = "Autonomous Robotics",
            level = "Advanced",
            duration = "10 Hours",
            imageUrl = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=600",
            rating = 5.0f,
            reviewsCount = 760,
            asCoinsPrice = 150,
            isEnrolled = false,
            isCompleted = false,
            lessons = listOf(
                LessonItem("l5_1", "1. ROS 2 Nodes, Topics & Services", "30 min", "VIDEO"),
                LessonItem("l5_2", "2. Lidar SLAM & Map Generation", "45 min", "CODE_SANDBOX")
            )
        )
    )

    // Certificates List
    private val certificatesList = mutableListOf(
        UserCertificate(
            id = "cert_1",
            title = "Python for AI & CS Fundamentals",
            issueDate = "Oct 2026",
            verificationUrl = "https://asbots.com/verify/cert_1",
            badgeIcon = "🌱",
            category = "Python & CS",
            asCoinsEarned = 50
        ),
        UserCertificate(
            id = "cert_2",
            title = "NumPy & Data Wrangling Specialist",
            issueDate = "Oct 2026",
            verificationUrl = "https://asbots.com/verify/cert_2",
            badgeIcon = "📊",
            category = "Data Science",
            asCoinsEarned = 75
        )
    )

    // Sample Quiz Questions
    private val sampleQuiz = listOf(
        QuizQuestion(
            id = 1,
            question = "Which activation function is most widely used in hidden layers of deep neural networks to prevent vanishing gradient?",
            options = listOf("Sigmoid", "ReLU (Rectified Linear Unit)", "Tanh", "Linear"),
            correctOptionIndex = 1,
            explanation = "ReLU outputs max(0, x), which computes extremely fast and reduces vanishing gradient during backpropagation.",
            asCoinsReward = 25
        ),
        QuizQuestion(
            id = 2,
            question = "What is the primary role of a Vector Database in a Retrieval-Augmented Generation (RAG) architecture?",
            options = listOf("Store raw video files", "Perform fast nearest-neighbor similarity search on dense embeddings", "Compile C++ code", "Host HTML web pages"),
            correctOptionIndex = 1,
            explanation = "Vector DBs store high-dimensional embeddings and execute similarity searches (e.g. Cosine, Dot Product) in milliseconds.",
            asCoinsReward = 25
        )
    )

    fun getRoadmapNodes(): List<RoadmapNode> = nodesList

    fun getCourseModules(): List<CourseModule> = modulesList

    fun getModuleById(id: String): CourseModule? {
        return modulesList.find { it.id == id } ?: modulesList.firstOrNull()
    }

    fun getCertificates(): List<UserCertificate> = certificatesList

    fun getSampleQuiz(): List<QuizQuestion> = sampleQuiz
}
