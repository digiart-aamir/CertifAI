package com.asbots.certifai.data

object SampleData {

    val courses = listOf(
        Course(
            id = "course_1",
            title = "Python for AI & Automation",
            category = "Python",
            stageText = "Stage 2 of 10",
            level = "Beginner",
            duration = "12 Hours",
            rating = 4.9,
            progressPercent = 65,
            description = "Master Python fundamentals, modules, functions, API calls, and automated scripting designed specifically for AI workflows.",
            modules = listOf(
                Module(
                    id = "m1",
                    title = "Module 1: Python Basics & Data Structures",
                    lessons = listOf(
                        Lesson("l1", "Variables, Data Types & Control Flow", "20 mins", true, "Python is the primary programming language for Artificial Intelligence..."),
                        Lesson("l2", "Lists, Dictionaries & Sets", "25 mins", true, "Data structures allow storing and manipulating data collections efficiently...")
                    )
                ),
                Module(
                    id = "m2",
                    title = "Module 2: Functions, OOP & APIs",
                    lessons = listOf(
                        Lesson("l3", "Functions & Lambda Expressions", "30 mins", true, "Functions are reusable blocks of code that perform specific tasks..."),
                        Lesson("l4", "Working with REST APIs & Requests", "35 mins", false, "AI applications communicate heavily via REST APIs using JSON payloads...")
                    )
                )
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    id = "q1",
                    questionText = "Which HTTP method is commonly used to send prompt inputs to AI REST APIs?",
                    options = listOf("GET", "POST", "DELETE", "PUT"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    id = "q2",
                    questionText = "In Python, which data structure stores key-value pairs?",
                    options = listOf("List", "Tuple", "Dictionary", "Set"),
                    correctAnswerIndex = 2
                )
            ),
            projectStatement = "Build an automated Python script that fetches real-time news data via API and generates summary reports using an LLM client."
        ),
        Course(
            id = "course_2",
            title = "RAG & Vector Databases Masterclass",
            category = "Generative AI",
            stageText = "Stage 8 of 10",
            level = "Advanced",
            duration = "16 Hours",
            rating = 4.95,
            progressPercent = 30,
            description = "Learn Retrieval-Augmented Generation (RAG) architecture, document chunking, semantic vector embeddings, Pinecone/ChromaDB, and hybrid search.",
            modules = listOf(
                Module(
                    id = "m1_rag",
                    title = "Module 1: Embeddings & Vector Stores",
                    lessons = listOf(
                        Lesson("l1_rag", "Text Embeddings & Cosine Similarity", "40 mins", true, "Vector embeddings convert unstructured text into numerical vector spaces..."),
                        Lesson("l2_rag", "Chunking Strategies & Token Limits", "45 mins", false, "Optimizing chunk size directly improves context retrieval precision...")
                    )
                )
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    id = "q1_rag",
                    questionText = "What metric measures the angle between two embedding vectors to check semantic similarity?",
                    options = listOf("Euclidean Distance", "Cosine Similarity", "Manhattan Distance", "Hamming Distance"),
                    correctAnswerIndex = 1
                )
            ),
            projectStatement = "Create an intelligent PDF Q&A Knowledge Bot using LangChain, OpenAI embeddings, and ChromaDB vector store."
        ),
        Course(
            id = "course_3",
            title = "Autonomous AI Agents & LangChain",
            category = "Agents",
            stageText = "Stage 9 of 10",
            level = "Advanced",
            duration = "18 Hours",
            rating = 4.88,
            progressPercent = 10,
            description = "Build self-reasoning AI agents using ReAct framework, tool calling, memory persistence, and multi-agent orchestration frameworks.",
            modules = listOf(
                Module(
                    id = "m1_agents",
                    title = "Module 1: Agentic Patterns & Tool Calling",
                    lessons = listOf(
                        Lesson("l1_ag", "ReAct Framework: Reason & Act", "50 mins", false, "ReAct combines reasoning traces with task-specific action execution...")
                    )
                )
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    id = "q1_ag",
                    questionText = "What enables an AI agent to execute external actions like web search or code execution?",
                    options = listOf("Prompt Engineering", "Tool Calling / Function Calling", "Quantization", "Fine-Tuning"),
                    correctAnswerIndex = 1
                )
            ),
            projectStatement = "Develop an autonomous web researcher agent that searches Google, synthesizes findings, and writes a Markdown report."
        ),
        Course(
            id = "course_4",
            title = "Machine Learning Foundations",
            category = "Machine Learning",
            stageText = "Stage 4 of 10",
            level = "Intermediate",
            duration = "14 Hours",
            rating = 4.85,
            progressPercent = 85,
            description = "Understand supervised and unsupervised learning algorithms, Scikit-Learn pipelines, model evaluation metrics, and hyperparameter tuning.",
            modules = listOf(
                Module(
                    id = "m1_ml",
                    title = "Module 1: Regression & Classification",
                    lessons = listOf(
                        Lesson("l1_ml", "Linear & Logistic Regression", "35 mins", true, "Regression predicts continuous values while classification predicts discrete classes...")
                    )
                )
            ),
            quizQuestions = emptyList(),
            projectStatement = "Build a customer churn prediction pipeline using Logistic Regression and Random Forest classifiers."
        )
    )

    val certificates = listOf(
        Certificate(
            id = "cert_1",
            courseTitle = "AI & Digital Foundations",
            learnerName = "Muhammad Ali",
            issueDate = "October 24, 2026",
            certificateId = "CERT-2026-AI-88392",
            category = "Foundations"
        ),
        Certificate(
            id = "cert_2",
            courseTitle = "Python Fundamentals for AI",
            learnerName = "Muhammad Ali",
            issueDate = "November 02, 2026",
            certificateId = "CERT-2026-PY-10928",
            category = "Programming"
        )
    )
}
