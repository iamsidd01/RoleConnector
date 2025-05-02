# Agri-Supply Chain Application (Java/Spring Boot/MongoDB)

This is a full-stack application designed to connect participants in an agricultural supply chain: Buyers, Suppliers, Transporters, and Drivers. This initial version focuses on the core functionalities for Buyers and Suppliers.

## Features (Phase 1: Buyer & Supplier)

### 🛒 Buyer
- **Post Purchase Requests:** Buyers can create posts specifying the item, quantity, and description of goods they need.
- **View Live Bids:** Buyers can see items currently available for bidding.
- **Place Bids:** Buyers can participate in live bidding events.
- **View Own Requests & Bids:** Buyers can track their purchase requests and bidding history.

### 🚜 Supplier
- **Post Sale Items:** Suppliers can list items available for sale.
    - **Direct Sale:** Offer items at a fixed price (future enhancement).
    - **Bidding:** Put items up for auction.
        - **Instant Bidding:** Bidding starts immediately upon posting and lasts for a configurable duration (e.g., 1 hour).
        - **Slot Bidding:** Schedule a bidding event for a specific future time slot (e.g., 9:00 AM - 10:00 AM tomorrow).
- **View Open Buyer Requests:** Suppliers can see what Buyers are looking to purchase.
- **View Bids on Own Items:** Suppliers can monitor bidding activity on their posted items.

### 🔔 Notifications (Basic Implementation)
- Buyers receive notifications when new bidding events (instant or slot) are created by Suppliers.
- Suppliers receive notifications when a Buyer places a bid on their item.
- Buyers/Suppliers receive notifications when bidding closes (including winner/loser status).

### 🔒 Security
- **JWT Authentication:** Secure endpoints using JSON Web Tokens.
- **Role-Based Authorization:** Endpoints are protected based on user roles (BUYER, SUPPLIER).

## Technology Stack

- **Backend:** Java 17, Spring Boot 3.x
- **Database:** MongoDB
- **Security:** Spring Security, JWT
- **Build Tool:** Maven

## Setup and Running

### Prerequisites
- Java Development Kit (JDK) 17 or later
- Apache Maven 3.6+
- MongoDB instance running (e.g., locally on `localhost:27017`)

### Configuration
1.  **Database:**
    - Open `src/main/resources/application.properties`.
    - Configure the `spring.data.mongodb.uri` or individual properties (`host`, `port`, `database`, `username`, `password`) to point to your MongoDB instance. Ensure the database specified (e.g., `agrisupply_db`) exists or MongoDB is configured to create it automatically.
2.  **JWT Secret:**
    - **CRITICAL:** Generate a strong, Base64-encoded secret key of **at least 256 bits (32 bytes)**. You can use OpenSSL:
      ```bash
      openssl rand -base64 32
      ```
    - Replace the placeholder value for `app.jwtSecret` in `application.properties` with your generated key. **Do not use the example key in production.**
    - Adjust `app.jwtExpirationInMs` if needed (default is 24 hours).

### Running the Application
1.  **Build the project:**
    ```bash
    mvn clean package
    ```
2.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, run the packaged JAR:
    ```bash
    java -jar target/agri-supply-0.0.1-SNAPSHOT.jar
    ```

The application should start, and the backend API will be available, typically at `http://localhost:8080`.

## API Endpoints (Examples)

- **Authentication:**
    - `POST /api/auth/register`: Register a new user (provide `email`, `password`, `roles` - e.g., `["BUYER"]`).
    - `POST /api/auth/login`: Login with `email` and `password`, returns JWT token.
- **Buyer:** (`Authorization: Bearer <token>` required, user must have `ROLE_BUYER`)
    - `POST /api/buyer/requests`: Create a purchase request.
    - `GET /api/buyer/requests`: Get logged-in buyer's requests.
    - `GET /api/buyer/live-bids`: View currently active bidding items.
    - `POST /api/buyer/bids/{saleItemId}`: Place a bid on an item.
    - `GET /api/buyer/bids`: Get logged-in buyer's bid history.
- **Supplier:** (`Authorization: Bearer <token>` required, user must have `ROLE_SUPPLIER`)
    - `POST /api/supplier/sales`: Create a sale item (direct or bidding).
    - `GET /api/supplier/sales`: Get logged-in supplier's sale items.
    - `GET /api/supplier/requests`: View open purchase requests from all buyers.
    - `GET /api/supplier/bids/{saleItemId}`: View bids placed on a specific item owned by the supplier.
- **Notifications:** (`Authorization: Bearer <token>` required)
    - `GET /api/notifications`: Get all notifications for the logged-in user.
    - `GET /api/notifications/unread`: Get unread notifications.
    - `POST /api/notifications/{notificationId}/read`: Mark a notification as read.
    - `POST /api/notifications/read-all`: Mark all notifications as read.

*(Note: Request/Response bodies use DTOs defined in the `com.example.agrisupply.dto` package.)*

## Future Enhancements

- Implement Transporter and Driver roles and features.
- Add direct purchase flow (non-bidding).
- Implement matching logic between buyer requests and supplier items.
- Enhance notification system (WebSockets, push notifications).
- Add UI (e.g., using React, Angular, Vue).
- More robust error handling and logging.
- Unit and integration tests.
- Implement item categories, location filtering, etc.
- Supplier/Buyer rating system.
- Payment integration.
