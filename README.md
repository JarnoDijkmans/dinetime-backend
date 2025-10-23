# 🍽️ DineTime Backend

Individual School Project – Semester 6

DineTime is a smart meal decision-making application designed for groups. It helps users collectively decide what’s for dinner based on personal preferences, diets, and allergies.

This repository contains the backend of the DineTime application, which powers the group management, meal generation, and voting system.

## 🚀 Overview

The DineTime backend handles all the core logic and data management for the app.
### Key features include:

- User & Group Management – Create and manage groups for meal selection.
- Dietary Preferences & Allergies – Store individual user preferences to filter meal options.
- Meal Pool Generation – Automatically generate meal suggestions based on group criteria.
- Voting System – Group members can like or dislike meals in the pool.
- Top 5 Results – At the end of the voting phase, a ranked list of top meals is generated.

## 🧠 How It Works

- The matchmaker (group creator) sets up a group and inputs dietary restrictions, allergies, and preferences.
- The backend uses these filters to generate a custom meal pool.
- Each group member can vote (like/dislike) on each meal.
- When voting ends, the system aggregates votes and returns a Top 5 list of meals for the group.

## 🏗️ Tech Stack
Category,Technology,Rationale
Languages,TypeScript (Node.js) & Java,"TypeScript powers the I/O-heavy services (e.g., API Gateway, Voting) for efficiency, while Java (Spring Boot) handles CPU-intensive core logic (e.g., Meal Pool Generation)."
Frameworks,Express.js & Spring Boot,"Express.js provides a minimal, high-performance API entry point. Spring Boot offers a robust, established environment for building core business microservices."
API Architecture,RESTful APIs,Standard protocol for public communication.
Inter-Service Comms,gRPC (Recommended),"High-performance, language-agnostic protocol for efficient communication between the Java and Node.js microservices."
Database,MySQL,"A reliable relational database used for structured data, including User, Group, and Preference management."
Data Access,TypeORM / JPA (Hibernate),"Utilizes appropriate Object-Relational Mappers (ORMs) for simplified, type-safe data interaction within each language environment."
In-Memory Store,Redis,"Used for fast-access caching, rate limiting, and temporary state management (e.g., real-time voting results)."
Authentication,JWT (JSON Web Tokens),Stateless authentication for securing endpoints and transmitting authenticated user identity across the microservice boundary.
Testing,Jest / JUnit,"Comprehensive testing frameworks used for unit, integration, and end-to-end testing across both the Node.js and Java services."
Architecture,Hexagonal Architecture & Polyglot Microservices,"Design principles promoting decoupled business logic, testability, and technology independence at the service level."
