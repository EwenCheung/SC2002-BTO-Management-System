# BTO Management System

## Project Overview
The BTO (Build-To-Order) Management System is a comprehensive application designed to streamline the management of HDB BTO flat applications in Singapore. It provides a unified platform for applicants, HDB officers, and project managers to interact with the BTO application process, from project creation to unit allocation.

This system was developed following object-oriented design principles (ModelOOApp approach) to ensure modularity, maintainability, and extensibility.

## System Requirements
- Java Development Kit (JDK) 11 or higher
- Minimum 4GB RAM
- 50MB of free disk space
- Any operating system that supports Java (Windows, macOS, Linux)

## Installation & Setup
1. Clone the repository from GitHub:
   ```
   git clone https://github.com/yourusername/SC2002-BTO-Management-System.git
   ```
2. Navigate to the project directory:
   ```
   cd SC2002-BTO-Management-System
   ```
3. Compile the project:
   ```
   javac -d bin src/**/*.java
   ```
4. Run the application:
   ```
   java -cp bin Main
   ```

## Project Structure
The project follows a modular architecture with clear separation of concerns:

- **src/**: Contains all source code
  - **access/**: Feature interfaces and handlers for different user types
    - **application/**: Handles BTO applications
    - **enquiry/**: Manages enquiry submissions and responses
    - **officerregistration/**: Handles officer registration to projects
    - **project/**: Manages BTO projects
    - **withdrawal/**: Processes withdrawal requests
  - **auth/**: Authentication and registration functionality
  - **io/**: Data persistence and file operations
  - **menu/**: User interfaces for different user types
  - **models/**: Domain objects representing core entities
  - **users/**: User types and related functionality
  - **utils/**: Utility classes for common operations

- **Datasets/**: Contains text files for data persistence

## Features & Functionality

### Applicant Requirements Assessment

| Requirement | Status | Notes |
|-------------|--------|-------|
| View projects open to user group (Single/Married) | ✅ | Filters projects appropriately based on marital status |
| Apply for a project (cannot apply for multiple) | ✅ | System checks for existing applications before allowing new ones |
| Singles (35+) can only apply for 2-Room flats | ✅ | Age and marital status validation implemented |
| Married (21+) can apply for any flat type | ✅ | Properly allows married applicants to choose between flat types |
| View applied project (even after visibility off) | ✅ | Application tracking system works regardless of project visibility |
| View application status (Pending/Successful/Unsuccessful/Booked) | ✅ | Status tracking fully implemented |
| Book flat through HDB Officer if application successful | ✅ | Officer-applicant interaction for booking implemented |
| Request withdrawal before/after flat booking | ✅ | Withdrawal system implemented with approval workflow |
| Submit enquiries about projects | ✅ | Enquiry submission function implemented |
| View, edit, and delete enquiries | ✅ | Full CRUD operations for enquiries implemented |

**Extra Features for Applicants:**
- Password change functionality
- Project filtering by neighborhood, flat type, and price range
- Detailed view of application status including history

### HDB Officer Requirements Assessment

| Requirement | Status | Notes |
|-------------|--------|-------|
| Possess all applicant capabilities | ✅ | Officer menu allows switching to applicant mode |
| Register to join a project (with eligibility criteria) | ✅ | Registration system with validation implemented |
| Cannot apply for projects they're handling | ✅ | Cross-check validation implemented |
| View registration status | ✅ | Status tracking for officer registrations |
| Subject to manager approval | ✅ | Approval workflow implemented |
| View project details regardless of visibility | ✅ | Access control implemented correctly |
| Cannot edit project details | ✅ | Read-only access to project details |
| View and reply to project enquiries | ✅ | Full enquiry management system |
| Update remaining flat count | ✅ | Inventory management system implemented |
| Retrieve applicant's BTO application | ✅ | Search functionality by NRIC |
| Update application status from "successful" to "booked" | ✅ | Status update functionality |
| Update applicant's profile with flat type | ✅ | Profile updating capability |
| Generate receipt with booking details | ✅ | Receipt generation implemented |

**Extra Features for Officers:**
- Filtering enquiries by status (pending/responded)
- Generate unit numbers automatically for assignments
- View detailed unit availability before processing applications

### HDB Manager Requirements Assessment

| Requirement | Status | Notes |
|-------------|--------|-------|
| Create, edit, delete BTO project listings | ✅ | Full CRUD operations for projects |
| Define complete project details | ✅ | All required fields implemented |
| Handle one project within an application period | ✅ | Validation for manager assignments |
| Toggle project visibility | ✅ | Visibility control implemented |
| View all projects regardless of visibility | ✅ | Access control properly implemented |
| Filter and view own created projects | ✅ | Filtering capability implemented |
| View pending/approved officer registrations | ✅ | Registration management system |
| Approve/reject officer registrations | ✅ | Approval workflow implemented |
| Approve/reject BTO applications | ✅ | Application approval system |
| Approve/reject withdrawal requests | ✅ | Withdrawal approval system |
| Generate filterable reports | ✅ | Reporting capability with filters |
| Cannot apply for BTO as applicant | ✅ | Role-based restrictions implemented |
| View enquiries of all projects | ✅ | Cross-project access for enquiries |
| View and reply to project enquiries | ✅ | Enquiry management system |

**Extra Features for Managers:**
- Advanced filtering options for reports
- Project analytics dashboard
- Historical tracking of application status changes

### Overall System Features

| Requirement | Status | Notes |
|-------------|--------|-------|
| User filtering of projects | ✅ | Multiple filter options implemented |
| Default alphabetical ordering | ✅ | Default sorting implemented |
| Persistent user filter settings | ✅ | Settings maintained between menu pages |

### Venn Diagram Overlap Analysis

The implementation correctly handles the overlapping responsibilities between user types:

1. **HDB Manager and HDB Officer overlap**:
   - View and reply to enquiries ✅
   - View project details regardless of visibility ✅

2. **Applicant capabilities inside HDB Officer**:
   - View list of projects open to their user group ✅
   - Apply for a project ✅
   - View applied projects including application status ✅
   - Request application withdrawal ✅
   - Create, view, and delete enquiries ✅

3. **HDB Officer unique capabilities**:
   - Register to join a project team ✅
   - Flat selection work (update applicant profiles with flat type) ✅
   - Update number of remaining flats ✅
   - Generate flat selection reports ✅

4. **HDB Manager unique capabilities**:
   - Create, edit, delete project listings ✅
   - Toggle project visibility ✅
   - View all projects with filtering ✅
   - Approve/reject HDB Officer registrations ✅
   - Approve/reject applications and withdrawals ✅
   - Generate filterable reports ✅

### Extra Features Not Required in Original Specification

1. Password change functionality for all user types
2. Advanced filtering options beyond the basic requirements
3. Automatic unit number generation
4. Detailed application tracking history
5. User-friendly UI with formatted tables and confirmation prompts
6. Receipt generation and management
7. Persistent data storage and retrieval

## Implemented Functions & OOP Concepts

### Core User Management System
1. **User Authentication**
   - Login with NRIC and password with role-specific validation
   - Password encryption and security measures
   - Session management for different user types

2. **User Registration**
   - New applicant registration with validation for required fields
   - Officer registration to projects with approval workflow
   - Multi-step registration process with data validation

3. **Password Management**
   - Change password functionality for all user types *(Extra Function)*
   - Password strength validation *(Extra Function)*
   - Limited login attempts security feature *(Extra Function)*

### Object-Oriented Design Implementation

### Core OOP Concepts Application

#### 1. Encapsulation
- **Data Protection**
  - Private attributes with controlled access via getters/setters
  - Example: `User` class encapsulates NRIC and password with validation
  - Sensitive data like passwords are protected from direct access

- **Information Hiding**
  - Implementation details hidden behind interfaces
  - Each handler class encapsulates data access logic
  - Database operations hidden from UI components

#### 2. Inheritance
- **User Type Hierarchy**
  - Abstract `User` class with common attributes and behaviors
  - Specialized user types (`Applicant`, `HDBOfficer`, `ProjectManager`) extend base class
  - Method overriding for role-specific behaviors

- **Feature Extension**
  - Common functionality implemented once in parent classes
  - Specialized behaviors added in child classes
  - Reuse of code across the hierarchy

#### 3. Polymorphism
- **Interface-Based Design**
  - Role-specific interfaces (e.g., `ApplicantProjectFeatures`, `ManagerProjectFeatures`)
  - Same method names with different implementations based on user role
  - Runtime binding of appropriate implementations

- **Method Overriding**
  - Child classes override parent methods to provide specialized behavior
  - Example: Different processing logic for applications based on user role
  - Dynamic behavior based on object type

#### 4. Abstraction
- **Feature Interfaces**
  - Interfaces define capabilities without implementation details
  - Abstract classes establish common structure and behavior
  - Clear separation between what a component does and how it does it

- **Model Abstractions**
  - Domain objects represent real-world entities (Project, Application, etc.)
  - Complex business rules encapsulated in model classes
  - Consistent representation throughout the system

### Design Patterns Implementation

#### 1. Facade Pattern
- **Handler Classes**
  - Handler classes provide simplified interfaces to complex subsystems
  - Example: `ApplicationHandler` hides complexities of application processing
  - Reduces coupling between UI and data access layers

#### 2. Factory Pattern
- **Object Creation**
  - Factory classes for creating complex objects
  - `ApplicationFactory`, `UserFactory`, etc. centralize object creation
  - Consistent object initialization with proper validation

#### 3. Serializer Pattern
- **Data Persistence**
  - Serializer classes handle conversion between objects and storage format
  - Separation of persistence concerns from domain logic
  - Consistent data format for storage and retrieval

#### 4. Singleton Pattern (modified)
- **Centralized Services**
  - `FileIO` provides centralized data access
  - Consistent file operations throughout the application
  - Prevents duplication of critical services

#### 5. MVC-inspired Architecture
- **Separation of Concerns**
  - Models: Domain objects with business logic
  - Views: Menu classes for user interaction
  - Controllers: Handler classes that process user actions

### SOLID Principles Implementation

#### 1. Single Responsibility Principle (SRP)
- Each class has one primary responsibility
- Menu classes handle UI only, handlers process logic, models store data
- Clear separation of duties across all components

#### 2. Open/Closed Principle (OCP)
- System designed to be extended without modifying existing code
- New features can be added through new classes implementing existing interfaces
- Enums for status types allow for extension without changing core logic

#### 3. Liskov Substitution Principle (LSP)
- Child classes can be used wherever their parent classes are expected
- User type hierarchy maintains consistent behavior patterns
- Proper inheritance relationships maintain expected behaviors

#### 4. Interface Segregation Principle (ISP)
- Role-specific interfaces prevent dependency on unused methods
- Different feature interfaces for different user types
- Each class implements only the interfaces it needs

#### 5. Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions, not concrete implementations
- Menu classes depend on feature interfaces, not concrete handlers
- Loose coupling through dependency injection

## User Guide

### General Usage
1. Launch the application by running `Main.java`
2. The main menu will display with options to:
   - Login to an existing account
   - Register as a new applicant
   - Exit the system

### For Applicants

#### Registration
1. Select "Register as Applicant" from the main menu
2. Enter your personal details as prompted:
   - Full name
   - NRIC
   - Age
   - Marital status
   - Password

#### Applying for a BTO
1. Login as an applicant
2. Select "Browse Projects" to view available BTO projects
3. Choose a project to view details
4. Select "Apply for this project"
5. Follow the prompts to submit your application

#### Submitting Enquiries
1. Login as an applicant
2. Select "Submit Enquiry"
3. Choose the relevant project
4. Enter your enquiry text
5. Submit the enquiry

### For HDB Officers

#### Registering for a Project
1. Login as an HDB officer
2. Select "Register for Project"
3. Choose an available project
4. Submit your registration request

#### Processing Applications
1. Login as an HDB officer
2. Select "Process Applications"
3. Choose the project you're registered to
4. View and process pending applications

### For Project Managers

#### Creating a New Project
1. Login as a project manager
2. Select "Create New Project"
3. Enter project details:
   - Name
   - Neighborhood
   - Unit types and quantities
   - Price points
   - Application period dates

#### Generating Reports
1. Login as a project manager
2. Select "Generate Reports"
3. Choose the type of report to generate
4. View the report or save it to a file

## Development Team
- Ewen Cheung Yi Wen
- Jerick Ho Cheng Hien
- Chan Yun Han
- Makhija Eshaa Jiten
- Jusvin Adrian Tan 

## License
This project is licensed under the MIT License - see the LICENSE file for details.

---

© 2025 BTO Management System Team. All rights reserved.