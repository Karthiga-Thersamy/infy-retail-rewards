# Infosys Retail Rewards Application

This is a Spring Boot application that calculates reward points for customers based on their transactions over a 3-month period.

## Business Logic
A customer receives:
* 2 points for every dollar spent over $100 in each transaction.
* 1 point for every dollar spent between $50 and $100 in each transaction.
* Example: A $120 purchase = 2x$20 + 1x$50 = 90 points.

## Technologies Used
* Java 1.8
* Spring Boot 2.5.14
* Maven

## How to Run
1. Navigate to the project directory.
2. Build the project using Maven: `mvn clean install`
3. Run the application: `mvn spring-boot:run`
4. The application will start on `http://localhost:8080`.

## API Endpoints

### Get Rewards for a Specific Customer
* **Endpoint:** `GET /api/rewards/customers/{customerId}`
* **Description:** Retrieves the total and monthly reward points for a specific customer.
* **Example:** `GET /api/rewards/customers/1001`

## Mock Data
The application uses an in-memory mock dataset spanning a three-month period to demonstrate the solution.