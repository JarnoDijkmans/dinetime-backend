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
- Languages:	TypeScript (Node.js) / Java
- Frameworks:	Express.js / Spring Boot
- API Architecture:	RESTful APIs
- Database:	MySQL
- In-Memory Storage: Redis
- Authentication:	JWT (JSON Web Tokens)
- Testing	Jest / JUnit
- Architecture	Hexagonal Architecture / Microservices
