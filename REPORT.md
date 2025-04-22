# BTO Management System - Project Report

## Declaration of Original Work

We hereby declare that the attached group assignment has been researched, undertaken, completed, and submitted as a collective effort by the group members listed below.

We have honored the principles of academic integrity and have upheld Student Code of Academic Conduct in the completion of this work.

We understand that if plagiarism is found in the assignment, then lower marks or no marks will be awarded for the assessed work. In addition, disciplinary actions may be taken.

| Name | Course | Lab Group | Signature / Date |
|------|--------|-----------|------------------|
| Ewen Cheung Yi Wen | | | |
| Jerick Ho Cheng Hien | | | |
| Chan Yun Han | | | |
| Makhija Eshaa Jiten | | | |
| Jusvin Adrian Tan | | | |

## Design Considerations

### Approach Taken

Our team approached the BTO Management System project with a focus on creating a modular, maintainable, and extensible application that adheres to Object-Oriented Design principles. We employed a layered architecture with clear separation of concerns:

1. **User Interface Layer**: Implemented through menu classes that handle user interactions
2. **Feature Access Layer**: Interface-based design allowing role-specific access to features
3. **Business Logic Layer**: Handlers implementing the feature interfaces with business logic
4. **Data Access Layer**: File I/O operations for persistent storage

This architecture allows for components to be developed and tested independently, making the system more maintainable and enabling parallel development by team members.

### OO Principles Used

#### 1. Encapsulation
We encapsulated the internal state of our classes by making attributes private and providing controlled access through getters and setters:

```java
public abstract class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private UserType userType;
    private String password;
    
    // Getter example
    public String getNric() {
        return nric;
    }
    
    // Setter example
    public void setPassword(String password) {
        this.password = password;
    }
}
```

This prevents direct manipulation of critical data like passwords and ensures all changes go through validation.

#### 2. Inheritance
We designed a clear inheritance hierarchy for user types:

```java
public abstract class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private UserType userType;
    private String password;
    // Other common fields and methods...
}

public class Applicant extends User {
    // Constructor reuses parent class constructor
    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, UserType.APPLICANT, password);
    }
    // Applicant-specific methods...
}

public class HDBOfficer extends User {
    // Constructor reuses parent class constructor
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, UserType.OFFICER, password);
    }
    // Officer-specific methods...
}
```

This approach allowed us to reuse code for common functionality while specializing behavior for each user type.

#### 3. Polymorphism
We used polymorphism extensively through interfaces and method overriding:

```java
// Interface defining project features for applicants
public interface ApplicantProjectFeatures {
    List<Project> getVisibleProjects();
    List<Project> getVisibleProjects(String applicantNric, List<String> appliedProjectNames);
}

// Interface defining project features for officers
public interface OfficerProjectFeatures {
    List<Project> getProjectsForOfficer(String officerNric);
    void decreaseAvailableUnits(String projectName, String unitType, int count);
}

// Handler implementing both interfaces
public class ProjectHandler implements ApplicantProjectFeatures, OfficerProjectFeatures {
    @Override
    public List<Project> getVisibleProjects() {
        // Implementation for getting visible projects
    }
    
    @Override
    public List<Project> getVisibleProjects(String applicantNric, List<String> appliedProjectNames) {
        // Implementation for getting projects visible to a specific applicant
    }
    
    @Override
    public List<Project> getProjectsForOfficer(String officerNric) {
        // Implementation for getting projects assigned to an officer
    }
    
    @Override
    public void decreaseAvailableUnits(String projectName, String unitType, int count) {
        // Implementation for decreasing available units
    }
}
```

This approach enables runtime binding of methods based on the specific interface being used.

#### 4. Abstraction
We used abstraction to hide implementation details behind interfaces and abstract classes. For example, our menus depend on interfaces rather than concrete implementations:

```java
public class OfficerMenu {
    private Scanner scanner;
    private HDBOfficer officer;
    
    // Interfaces for officer-specific features
    private OfficerProjectFeatures projectFacade;
    private OfficerApplicationFeatures appFacade;
    private OfficerEnquiryFeatures enquiryFacade;
    private OfficerRegistrationApplicantFeatures regFacade;
    
    public OfficerMenu(HDBOfficer officer,
                       OfficerProjectFeatures projectFacade,
                       OfficerApplicationFeatures appFacade,
                       OfficerEnquiryFeatures enquiryFacade,
                       OfficerRegistrationApplicantFeatures regFacade) {
        this.scanner = new Scanner(System.in);
        this.officer = officer;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.regFacade = regFacade;
    }
    
    // Menu methods only interact with the interfaces, not concrete implementations
}
```

This allows the system to work with high-level concepts without needing to know implementation details.

### SOLID Principles Implementation

#### 1. Single Responsibility Principle (SRP)
Each class in our system has a single responsibility:
- `ProjectHandler` manages projects
- `ApplicationHandler` processes applications
- `EnquiryHandler` handles enquiries
- Menu classes focus solely on user interaction

Example from `ApplicationHandler.java`:
```java
public class ApplicationHandler implements ManagerApplicationFeatures, OfficerApplicationFeatures, ApplicantApplicationFeatures {
    
    private List<Application> applications;
    
    // Officer methods for managing applications
    @Override
    public void processApplication(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalArgumentException("Only applications with 'Successful' status can be processed to 'Booked'.");
        }
        app.setStatus(ApplicationStatus.BOOKED);
        saveChanges(); // Ensure changes are saved to CSV
    }
    
    @Override
    public String generateReceipt(String applicationId) {
        Application app = findApplicationById(applicationId);
        if (app == null) {
            throw new IllegalArgumentException("Application not found: " + applicationId);
        }
        if (app.getStatus() != ApplicationStatus.BOOKED) {
            throw new IllegalArgumentException("Receipt can only be generated for applications with 'Booked' status.");
        }
        return app.generateReceipt();
    }
}
```

#### 2. Open/Closed Principle (OCP)
Our design is open for extension but closed for modification. For example, adding new application statuses only requires adding new enum values without changing the core application logic:

```java
public enum ApplicationStatus {
    PENDING("Pending"),
    UNDER_REVIEW("Under Review"),
    PENDING_DOCUMENTS("Pending Documents"),
    BACKGROUND_CHECK("Background Check"),
    WITHDRAW_REQUESTED("Withdraw Requested"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    BOOKED("Booked"),
    WITHDRAWN("Unsuccessful");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
```

Implementation example from `OfficerMenu.java`:
```java
private void processApplicationStatus(Application application) {
    printHeader("PROCESS APPLICATION STATUS");
    System.out.println("Current Status: " + application.getStatus());
    
    if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
        // If application is successful but doesn't have a unit assigned, offer to assign a unit
        if (application.getAssignedUnit() == null || application.getAssignedUnit().isEmpty()) {
            printMessage("This application has been marked as successful by a manager and is ready for unit assignment.");
            // ...implementation for processing SUCCESSFUL applications
        }
    } else if (application.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
        printMessage("This application is unsuccessful and cannot be processed further.");
        // ...
    } else if (application.getStatus() == ApplicationStatus.BOOKED) {
        printMessage("This application has already been processed and a unit has been assigned.");
        // ...
    } else if (application.getStatus() == ApplicationStatus.PENDING) {
        printMessage("Officers cannot process pending applications. Only managers can mark applications as successful or unsuccessful.");
        // ...
    }
}
```

#### 3. Liskov Substitution Principle (LSP)
Child classes can be used wherever their parent classes are expected. For example, all User types are handled correctly in the `handleUserSession` method:

```java
private void handleUserSession(User user) {
    boolean keepSessionActive = true;
    boolean officerMode = false; // Track if we're currently in officer mode
    boolean skipModeSelection = false; // Flag to skip mode selection when switching
    
    while (keepSessionActive) {
        if (user.getUserType() == UserType.APPLICANT) {
            // Regular applicant - just display the menu and then logout
            new ApplicantMenu((Applicant) user, projectHandler, applicationHandler, enquiryHandler, withdrawalHandler).display();
            keepSessionActive = false; // Regular applicants don't have mode switching
        } else if (user.getUserType() == UserType.MANAGER) {
            // Manager - just display the menu and then logout
            new ManagerMenu((ProjectManager) user, projectHandler, applicationHandler, enquiryHandler, registrationHandler, withdrawalHandler).display();
            keepSessionActive = false; // Managers don't have mode switching
        } else if (user.getUserType() == UserType.OFFICER) {
            HDBOfficer officer = (HDBOfficer) user;
            // Special handling for officers who can switch between roles
            // ...
        }
    }
}
```

#### 4. Interface Segregation Principle (ISP)
We created multiple focused interfaces instead of one large interface:

```java
public interface OfficerApplicationFeatures {
    /**
     * Retrieves all applications for a specific project.
     */
    List<Application> getApplicationsForProject(String projectName);
    
    /**
     * Processes an application, setting its status.
     */
    void processApplication(String applicationId);
    
    /**
     * Generates a booking receipt for an approved application.
     */
    String generateReceipt(String applicationId);
    
    /**
     * Retrieves a specific application by its ID.
     */
    Application getApplication(String applicationId);
    
    /**
     * Updates an existing application with new data.
     */
    void updateApplication(Application application);
}
```

Each user type has its own tailored interface for accessing only the features it needs:
- `ApplicantProjectFeatures` has methods for viewing projects relevant to applicants
- `OfficerProjectFeatures` focuses on officer-specific project operations
- `ManagerProjectFeatures` includes methods for project creation and management

#### 5. Dependency Inversion Principle (DIP)
High-level modules depend on abstractions rather than concrete implementations:

```java
public class OfficerMenu {
    private OfficerProjectFeatures projectFacade;
    private OfficerApplicationFeatures appFacade;
    private OfficerEnquiryFeatures enquiryFacade;
    private OfficerRegistrationApplicantFeatures regFacade;
    
    public OfficerMenu(HDBOfficer officer,
                      OfficerProjectFeatures projectFacade,
                      OfficerApplicationFeatures appFacade,
                      OfficerEnquiryFeatures enquiryFacade,
                      OfficerRegistrationApplicantFeatures regFacade) {
        this.scanner = new Scanner(System.in);
        this.officer = officer;
        this.projectFacade = projectFacade;
        this.appFacade = appFacade;
        this.enquiryFacade = enquiryFacade;
        this.regFacade = regFacade;
    }
    
    private void registerForProject() {
        // Get projects using the interface method
        List<Project> availableProjects;
        if (projectFacade instanceof ProjectHandler) {
            availableProjects = ((ProjectHandler) projectFacade).getProjectsWithOpenSlots();
        } else {
            // Fallback to interface method if different implementation
            availableProjects = projectFacade.getProjectsForOfficer(officer.getNric());
        }
        
        // ... rest of method
    }
}
```

This ensures that the menu class is not tightly coupled to specific implementations of the feature interfaces.

### Assumptions Made

1. **Data Persistence**: We assumed text-based file storage would be sufficient for data persistence, with no need for database systems.
2. **User Authentication**: We assumed a simple username/password system would be adequate for demonstration purposes.
3. **User Interface**: We assumed a command-line interface would be sufficient as specified, without graphic user interface requirements.
4. **Default Values**: We assumed some default values for pricing and unit configurations to simplify implementation.

## Detailed UML Class Diagram

[INSERT CLASS DIAGRAM IMAGE HERE]

The UML Class Diagram represents the relationships between the major components of our system:

### Class Relationships
- **User Hierarchy**: Abstract User class extended by Applicant, HDBOfficer, and ProjectManager
- **Model Classes**: Project, Application, Enquiry, OfficerRegistration, WithdrawalRequest
- **Handler Classes**: ProjectHandler, ApplicationHandler, EnquiryHandler, etc.
- **Feature Interfaces**: ApplicantProjectFeatures, OfficerProjectFeatures, ManagerProjectFeatures, etc.
- **Menu Classes**: MainMenu, ApplicantMenu, OfficerMenu, ManagerMenu

### Key Design Patterns
- **Facade Pattern**: Handler classes provide simplified interfaces to complex subsystems
- **Factory Pattern**: Factory classes for creating complex objects
- **Serializer Pattern**: Serializer classes handle conversion between objects and storage format

## Detailed UML Sequence Diagram

[INSERT SEQUENCE DIAGRAM IMAGE HERE]

The UML Sequence Diagram illustrates the flow of the HDB Officer's role in applying for a BTO and registering to handle a project:

1. **User Authentication**: Officer logs in through the authentication system
2. **Menu Navigation**: Officer selects to register for a project
3. **Project Selection**: System displays available projects and officer selects one
4. **Registration Submission**: Officer confirms registration
5. **Registration Processing**: System creates and stores registration record
6. **Status Notification**: System confirms registration submission to officer

## Additional Features Implemented

Beyond the core requirements specified in the assignment, we implemented several additional features to enhance the system's functionality:

1. **Password Change Functionality**: All user types can change their passwords through a secure interface
2. **Advanced Filtering Options**: Projects can be filtered by multiple criteria including neighborhood, flat type, and price range
3. **Detailed Application History**: System maintains a detailed history of application status changes
4. **Receipt Generation and Management**: Officers can generate and save booking receipts for successful applications
5. **User-Friendly CLI Interface**: Enhanced console display with formatted tables and visual separators
6. **Input Validation**: Comprehensive validation for all user inputs to prevent invalid data
7. **Custom File Export Formats**: Reports can be exported in various text formats for external use

## Reflection

### Difficulties Encountered

1. **Complex User Role Requirements**: Managing the overlapping capabilities of different user types required careful design consideration. We overcame this by using interface segregation and proper inheritance hierarchies.

2. **Date-Based Logic**: Implementing date logic for project application periods and registration overlap detection was challenging. We created utility classes to centralize date manipulation and validation.

3. **Data Persistence**: Maintaining data integrity across multiple CSV files without a proper database was difficult. We developed a robust file I/O system with error handling and data validation.

4. **Booking Logic**: Implementing the complex booking process with multiple status changes and validations was challenging. We used the state pattern and careful workflow design to manage transitions.

### Knowledge Learned

1. **Practical OO Design**: We gained hands-on experience applying OO principles to a real-world problem, seeing how proper design leads to maintainable code.

2. **Interface-Based Programming**: We learned the value of programming to interfaces rather than implementations for creating flexible, extensible systems.

3. **File I/O Management**: We developed skills in efficient file operations for data persistence without relying on database systems.

4. **Collaborative Development**: We learned effective practices for parallel development through clear interfaces and responsibility separation.

### Further Improvement Suggestions

1. **Database Integration**: The system could benefit from proper database implementation for improved data integrity and query capabilities.

2. **GUI Implementation**: A graphical user interface would enhance user experience and make the system more accessible.

3. **Authentication Enhancement**: Implementing proper hashing for passwords and more sophisticated authentication mechanisms would improve security.

4. **Notification System**: Adding email or SMS notifications for application status changes would enhance the user experience.

5. **Reporting and Analytics**: More sophisticated reporting capabilities with visual analytics would provide better insights into application patterns.

## GitHub Repository

[https://github.com/EwenCheung/SC2002-BTO-Management-System](https://github.com/EwenCheung/SC2002-BTO-Management-System)