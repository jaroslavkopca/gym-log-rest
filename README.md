# Gym Workout Log REST API

## Overview
The Gym Workout Log is a REST API designed to facilitate the management of gym memberships, payments, statistics, and workout records. This project extends to include functionality for reserving time slots for gym rooms, enhancing the overall experience for gym members.

## Collaboration
This project is a collaboration between **Adam Zelfel** and **Jaroslav Kopca**.

## Functionalities

### 1. User Identification
- **Active Members:** Access all system functions, including membership tracking, payment processing, statistics, and workout recording. They can share their workout plans with others and execute them in real time.
- **Potential Members:** Limited access to the system for membership purchase.
- **Receptionist:** Access to verify membership validity and provide information.

### 2. System Features
- **Membership Tracking:** Records the validity and expiration of memberships.
- **Payments:** Members can make payments for their memberships.
- **Statistics:** Generates statistics on member attendance.
- **Workout Records:** Members can log individual exercises, repetitions, etc.
- **Sharing Workouts:** Members can share their workout plans with other users.
- **Real-Time Workout Execution:** Members can select and execute workouts in real time.
- **Room Reservation:** Allows reservation of time slots in rooms with specific capacities.

### 3. System Limitations
- **Potential Members:** Restricted access to system functionalities.
- **Receptionist:** Limited access to sensitive information, only for membership verification.

## API Endpoints

### 1. **GroupWorkoutController**
- **POST /rest/groupworkout**
  - **Functionality:** Create a new group workout.
  - **Request Body:** JSON object containing workout details (name, time from, time to, capacity, room ID).
  - **Authorization Required:** ROLE_MEMBER_WITH_MEMBERSHIP.

### 2. **MemberController**
- **POST /rest/member/register**
  - **Functionality:** Register a new client as a member.
  - **Authorization Required:** ROLE_CLIENT.

- **POST /rest/member/createClient**
  - **Functionality:** Create a new client.
  - **Request Body:** JSON object with client details.
  - **Authorization Required:** None for client creation.

- **POST /rest/member/deleteClient**
  - **Functionality:** Delete an existing client by ID.
  - **Query Parameter:** clientId.
  - **Authorization Required:** ROLE_ADMIN.

- **POST /rest/member/deactivateMembership**
  - **Functionality:** Deactivate a member's membership by client ID.
  - **Query Parameter:** clientId.
  - **Authorization Required:** ROLE_ADMIN.

- **POST /rest/member/payMembership**
  - **Functionality:** Process membership payment and activation.
  - **Authorization Required:** ROLE_CLIENT, ROLE_MEMBER.

- **GET /rest/member/current**
  - **Functionality:** Retrieve the current authenticated client's details.
  - **Authorization Required:** ROLE_CLIENT, ROLE_MEMBER, ROLE_MEMBER_WITH_MEMBERSHIP, ROLE_ADMIN.

- **GET /rest/member/{id}/workoutrecords**
  - **Functionality:** Retrieve workout records for a specific member.
  - **Path Variable:** id (member ID).
  - **Authorization Required:** ROLE_MEMBER_WITH_MEMBERSHIP.

- **PUT /rest/member/{id}/workoutrecords/{idRecord}**
  - **Functionality:** Update a specific workout record.
  - **Path Variables:** id (member ID), idRecord (workout record ID).
  - **Authorization Required:** ROLE_MEMBER_WITH_MEMBERSHIP.

- **DELETE /rest/member/{id}/workoutrecords/{idRecord}**
  - **Functionality:** Remove a workout record for a specific member.
  - **Path Variables:** id (member ID), idRecord (workout record ID).
  - **Authorization Required:** ROLE_MEMBER_WITH_MEMBERSHIP.

- **GET /rest/member/statistics**
  - **Functionality:** Get aggregated data for all members.
  - **Authorization Required:** ROLE_ADMIN.

## Conclusion
This REST API is designed to provide a comprehensive solution for managing gym-related functionalities efficiently, making it easier for members and administrative staff to track memberships, workouts, and payments.
