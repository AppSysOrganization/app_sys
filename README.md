# 📅 Appointment Scheduling System

A full-featured Appointment Scheduling System built with Java 8+, Maven, JUnit 5, JaCoCo, and Mockito. The system supports multiple user roles (Admin, Supplier, Customer), appointment booking and management, real-time notifications, and a manual text-based search ChatBot, all wrapped in a Swing-based GUI.

---

## 👥 Team Members

| Name   | Role      |
|--------|-----------|
| Shahd  | Developer |
| Rahaf  | Developer |
| Elham  | Developer |

---

## 🛠️ Technologies Used

| Tool / Library | Purpose                            |
|----------------|------------------------------------|
| Java 8+        | Core programming language          |
| Maven          | Build tool & dependency management |
| JUnit 5        | Unit testing framework             |
| JaCoCo         | Code coverage reporting            |
| Mockito        | Mocking framework for tests        |
| Gson           | JSON serialization / persistence   |
| Java Swing     | Desktop GUI                        |
| Eclipse IDE    | Development environment            |

---

## 📁 Project Structure

```
app_sys/
├── src/
│   ├── main/java/com/appointmentsystem/
│   │   ├── ai/
│   │   │   ├── ChatBot.java               # Main chatbot interface for product search
│   │   │   ├── ChatResponse.java          # Holds chatbot response (text + products)
│   │   │   ├── DocumentChunker.java       # Cleans text and removes stop words
│   │   │   ├── ProductRepository.java     # Loads products from JSON
│   │   │   ├── ProductSearchEngine.java   # TF-IDF + Cosine Similarity search
│   │   │   └── TextEmbedder.java          # Converts text to TF-IDF numeric vectors
│   │   │
│   │   ├── model/
│   │   │   ├── Admin.java                 # Admin user (extends User)
│   │   │   ├── Appointment.java           # Core appointment entity
│   │   │   ├── AppointmentStatus.java     # Enum: PENDING, CONFIRMED, CANCELLED, etc.
│   │   │   ├── AppointmentType.java       # Enum: URGENT, FOLLOW_UP, ASSESSMENT, etc.
│   │   │   ├── Customer.java              # Customer user (extends User)
│   │   │   ├── Product.java               # Product/service offered by supplier
│   │   │   ├── Supplier.java              # Supplier user (extends User)
│   │   │   └── User.java                  # Abstract base class for all users
│   │   │
│   │   ├── observer/
│   │   │   ├── Observer.java              # Interface: notify(user, message)
│   │   │   ├── Subject.java               # Interface: register/remove/notifyObservers
│   │   │   ├── NotificationManager.java   # Manages and triggers all observers
│   │   │   └── EmailService.java          # Concrete observer: sends email notifications
│   │   │
│   │   ├── persistence/
│   │   │   └── StorageManager.java        # Save/load data to/from JSON files
│   │   │
│   │   ├── repository/
│   │   │   ├── Repository.java            # Generic CRUD interface
│   │   │   ├── InMemoryAppointmentRepository.java
│   │   │   └── InMemoryUserRepository.java
│   │   │
│   │   ├── service/
│   │   │   ├── AppointmentService.java    # Booking, cancellation, validation logic
│   │   │   └── AuthService.java           # Login / logout logic
│   │   │
│   │   ├── strategy/
│   │   │   ├── BookingRuleStrategy.java   # Interface for validation rules
│   │   │   ├── CapacityRule.java          # Enforces max participant limit
│   │   │   ├── DurationRule.java          # Enforces max appointment duration
│   │   │   └── TypeSpecificRule.java      # Applies rules based on appointment type
│   │   │
│   │   └── ui/
│   │       ├── MainApp.java               # Application entry point
│   │       ├── LoginFrame.java            # Login screen
│   │       ├── AdminDashboard.java        # Admin management panel
│   │       ├── CustomerDashboard.java     # Customer booking panel + ChatBot
│   │       ├── SupplierDashboard.java     # Supplier product & slot management
│   │       └── TableUtils.java            # Shared table rendering utilities
│   │
│   └── test/java/com/appointmentsystem/
│       ├── ai/                            # ChatBot, DocumentChunker, ProductSearchEngine, TextEmbedder tests
│       ├── model/                         # Appointment, Admin, Customer, User, Supplier, Product tests
│       ├── observer/                      # EmailService, NotificationManager tests
│       ├── repository/                    # InMemoryAppointmentRepository, InMemoryUserRepository tests
│       ├── service/                       # AppointmentService, AuthService tests
│       └── strategy/                      # CapacityRule, DurationRule, TypeSpecificRule tests
│
├── app_data/
│   ├── appointments.json
│   ├── users.json
│   └── products.json
└── pom.xml
```

---

## ✅ Implemented Features (User Stories)

### Sprint 1 – Authentication
- **US1.1** Admin login with credential validation
- **US1.2** Admin logout with session termination
- **US1.3** View available appointment slots (only non-full slots shown)

### Sprint 2 – Booking & Business Rules
- **US2.1** Book an appointment (saved with CONFIRMED status)
- **US2.2** Enforce visit duration rule (max duration per type)
- **US2.3** Enforce participant limit per appointment

### Sprint 3 – Notifications
- **US3.1** Appointment reminders via Observer Pattern (Email notifications, mockable in tests)

### Sprint 4 – Management
- **US4.1** Users can modify or cancel upcoming appointments; slot becomes available again
- **US4.2** Admins can manage and override reservations

### Sprint 5 – Appointment Types & Polymorphism
- **US5.1** Multiple appointment types supported:
  `URGENT`, `FOLLOW_UP`, `ASSESSMENT`, `VIRTUAL`, `IN_PERSON`, `INDIVIDUAL`, `GROUP`
- **US5.2** Type-specific rules applied automatically via `TypeSpecificRule`:

| Type       | Rule Applied                  |
|------------|-------------------------------|
| URGENT     | Max duration: 30 minutes      |
| FOLLOW_UP  | 1 participant, max 30 minutes |
| ASSESSMENT | 1 participant, min 30 minutes |
| VIRTUAL    | Max duration: 60 minutes      |
| IN_PERSON  | Max duration: 120 minutes     |
| INDIVIDUAL | Exactly 1 participant         |
| GROUP      | More than 1 participant       |

### Bonus – Manual Search ChatBot 🤖
A text-based product search assistant built from scratch **without any external AI APIs**. It uses a manual NLP pipeline:

```
User Query
    ↓
DocumentChunker    →  Clean text & remove stop words
    ↓
TextEmbedder       →  Convert text to TF-IDF numeric vectors
    ↓
Cosine Similarity  →  Compare query vector against each product
    ↓
ProductSearchEngine →  Rank and return best matches
    ↓
ChatBot            →  Format and display results to user
```

This approach uses real supplier product data (from `products.json`) and works entirely offline.

---

## 🏗️ Architecture

**Style:** Layered (N-Tier) Architecture

```
┌─────────────────────────────┐
│     Presentation Layer      │  ← Swing UI (LoginFrame, Dashboards)
├─────────────────────────────┤
│   Application/Service Layer │  ← AppointmentService, AuthService
├─────────────────────────────┤
│       Domain Layer          │  ← Appointment, User, Admin, Customer, Supplier, Product
├─────────────────────────────┤
│      Persistence Layer      │  ← StorageManager (JSON), InMemoryRepositories
└─────────────────────────────┘
```

---

## 🎨 Design Patterns

### Strategy Pattern
Used for booking rule enforcement. Each rule is a separate class implementing `BookingRuleStrategy`, making it easy to add new rules without modifying existing logic.

```java
interface BookingRuleStrategy {
    boolean isValid(Appointment appointment);
}
// Implementations: CapacityRule, DurationRule, TypeSpecificRule
```

### Observer Pattern
Used for the notification system. Multiple observers (e.g., EmailService) can subscribe to appointment events.

```java
interface Observer {
    void notify(User user, String message);
}
// NotificationManager manages all registered observers
```

---

## 🧪 Testing

The project includes **19 test classes** covering all major components.

### Run all tests:
```bash
mvn test
```

### Generate JaCoCo coverage report:
```bash
mvn verify
```
Then open: `target/site/jacoco/index.html`

### Test coverage includes:
- Appointment booking and cancellation logic
- Authentication (login / logout)
- Strategy rules (Capacity, Duration, TypeSpecific)
- Observer notifications (with Mockito mocks)
- In-memory repositories (CRUD operations)
- ChatBot and search engine (DocumentChunker, TextEmbedder, ProductSearchEngine)

---

## 🚀 How to Run

### Prerequisites
- Java 8 or higher (`javac -version` should show ≥ 1.8)
- Maven installed (`mvn -version`)
- Eclipse IDE (recommended) or any Java IDE

### Steps

**1. Clone the repository:**
```bash
git clone https://github.com/ShahdHodaly/app_sys.git
cd app_sys
```

**2. Build the project:**
```bash
mvn compile
```

**3. Run the application:**
```bash
mvn exec:java -Dexec.mainClass="com.appointmentsystem.ui.MainApp"
```
Or run `MainApp.java` directly from Eclipse.

**4. Run tests:**
```bash
mvn test
```

---

## 📝 Documentation

All classes, methods, and fields include full Javadoc comments using standard syntax (`@param`, `@return`, `@author`, `@version`).

To generate the Javadoc HTML:
```bash
mvn javadoc:javadoc
```

---
