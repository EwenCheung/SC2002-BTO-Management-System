# SC2002-BTO-Management-System

## UI elements
### Main menu
 On construction, MainMenu calls FileIO to load in all the objects (Users, Projects, Applications, Enquiry, Withdrawal, OfficerRegistration). Objects held as lists. 
 Main menu is the first menu that users will see, with 3 options, Login, Register and Exit. Login calls AuthenticationSystem to handle the checks of NRIC and password to get the specific user. AuthenticationSystem will pass the specific user to MainMenu, based on userType, MainMenu will call the relevant sub-Menus. 
 Register calls RegistrationSystem to create a new User object. Users will still have to login after registration.

### Sub Menu
#### Applicant Menu
 Handles all the functions that applicants can access. 
 Uses Applicant~Features interface wrappers to give only specific functionality to manipulate objects. 
 Passes in Project, Application, Enquiry, Withdrawal lists.

#### Officer Menu
 Handles all the functions that officer can access.
 Uses Officer~Features interface wrappers to give only specific functionality to manipulate objects.
 Passes in Project, Application, Enquiry, OfficerRegistration lists.

#### Manager Menu
 Handles all the functions that Manager can access.
 Uses Manager~Features interface wrappers to give only specific functionality to manipulate objects. 
 Passes in all object lists. 

## Database System
### FileIO
 Handles all interactions with the text files that store the object information. 
 
 Load~ methods call ~Factory classes to create all the objects listed in the text files. Used during construction of MainMenu. Recursive calls for multiple objects. 

 Save~ methods call ~Serializer classes to transform objects to string, using global constant delimiter ##. Currently used during system shutdown, may be added to save files at critical junctures. Recursive call for multiple objects.

### ~Factory
 1 Factory for each object type. 
 Creates 1 instance of that object based on the information stored in the respective text files

### ~Serializer
 1 Serializer for each object type.
 Takes 1 instance of an object from the list and creates a string holding all the object information, using delimiter. 