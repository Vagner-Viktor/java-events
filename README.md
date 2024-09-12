# Events!

**Events** is a platform for organizing and discovering events, allowing users to share activities (from exhibitions and concerts to movie outings) and gather a group to participate. Roles for public, private, and administrative APIs are implemented.

**Main Components of the Project:**  
1. **Events:** 
   * Users can create events with various details such as title, description, date and time, category, and location.
   * Events can be paid or free and have participant limits.
2. **Compilations:**
   * Compilations are groups of events that can be combined by specific themes or other criteria. 
   * Compilations can be pinned for priority display on the platform.
3. **Users:**
   * The system supports user registration and management, allowing users to create and manage their events and participate in events created by others.
4. **Categories:**
   * Events can be organized by categories to facilitate their search and filtering.
5. **Locations:**
   * Each event is associated with a specific location, making it easier for users to find events in their area.
6. **Statistics:**
   * The system provides statistical data on event attendance, helping organizers better understand audience interests and needs.

**The program consists of two services:**  
1. Main service - will contain everything necessary for the product to function.
2. Statistics service - will store the number of views and allow various queries for analyzing the application's performance.

**Technologies and Tools:**  
* **Java:** The main programming language used in the project.  
* **Spring Boot:** Used to create RESTful APIs.  
* **Hibernate:** An ORM (Object-Relational Mapping) framework for interacting with the database.  
* **PostgreSQL:** The DBMS used for storing project data.  