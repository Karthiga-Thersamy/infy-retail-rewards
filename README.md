# Infosys Retail Rewards Application

This is a Spring Boot application that calculates reward points for customers based on their transactions over a 3-month period and exposes both per-customer reward lookups and an aggregate summary endpoint.

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

The service supports two main endpoints:
* `GET /api/rewards/customers/{customerId}` for a single customer's reward details.
* `GET /api/rewards/summary` for an aggregated reward summary across all customers.

### Get Rewards for a Specific Customer
* **Endpoint:** `GET /api/rewards/customers/{customerId}`
* **Query Parameters:** Optional `startDate`, `endDate`, and `numberOfMonths`.
* **Description:** Retrieves total reward points, monthly reward totals, and individual transaction details for a specific customer over the requested date range.
* **Behavior:**
  * If both `startDate` and `endDate` are supplied, that exact range is used.
  * If `startDate` is supplied alone, the window extends for `numberOfMonths` months (default 3) from `startDate`.
  * If `endDate` is supplied alone, the window covers the `numberOfMonths` months ending on `endDate`.
  * If no dates are supplied, the endpoint uses a default 3-month window ending on today.
* **Example:** `GET /api/rewards/customers/1001?startDate=2026-04-01&numberOfMonths=3`

#### Example Response
```json
{
  "customerId": 1,
  "customerName": "Karthiga",
  "emailAddress": "karthiga@test.com",
  "startDate": "2026-04-01",
  "endDate": "2026-04-30",
  "rewardPoints": 90.0,
  "transactions": [
    {
      "transactionId": 2001,
      "transactionDate": "2026-04-02",
      "amount": 120.0,
      "rewardPoints": 90.0
    }
  ]
}
```

### Get Reward Summary Across Customers
* **Endpoint:** `GET /api/rewards/summary`
* **Query Parameters:** Optional `startDate`, `endDate`, and `numberOfMonths`.
* **Description:** Retrieves aggregated reward points across all customers for the requested date range, including a per-customer summary.
* **Behavior:**
  * If both `startDate` and `endDate` are supplied, that exact range is used.
  * If `startDate` is supplied alone, the window extends for `numberOfMonths` months (default 3) from `startDate`.
  * If `endDate` is supplied alone, the window covers the `numberOfMonths` months ending on `endDate`.
  * If no dates are supplied, the endpoint uses a default 3-month window ending on today.
* **Example:** `GET /api/rewards/summary?endDate=2026-06-30&numberOfMonths=3`

#### Example Response
```json
{
  "startDate": "2026-04-01",
  "endDate": "2026-04-30",
  "totalRewardPoints": 180.0,
  "customerList": [
    {
      "customerId": 1,
      "customerName": "Karthiga",
      "totalRewardPoints": 90.0
    }
  ]
}
```

## API Behavior Rules
* `startDate` and `endDate` are inclusive.
* If both `startDate` and `endDate` are provided, the exact range is used.
* If only `startDate` is provided, the window extends for `numberOfMonths` months (default 3) from `startDate`.
* If only `endDate` is provided, the window covers the `numberOfMonths` months ending on `endDate`.
* If no dates are supplied, a default 3-month window ending on today is used.
* The `numberOfMonths` parameter is optional and defaults to `3`.
* Dates must use the `YYYY-MM-DD` format. Invalid dates such as `2026-02-30` are rejected.
* No other query parameters are supported for these endpoints.
* If a customer exists but has no transactions in the requested range, the customer response returns zero reward points and an empty transactions list.

## Error Responses
The application uses a structured JSON format for all error responses to provide clear debugging context.

### Common Error Payload Schema
```json
{
  "timestamp": "2026-07-14T14:39:24.135",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message here"
}
```

### Supported HTTP Error Statuses
* `400 Bad Request` - returned when required query parameters are missing or date format is invalid.
* `404 Not Found` - returned when the requested customer does not exist.
* `200 OK` - returned for valid requests, including responses with zero points.

## Mock Data
The application uses an in-memory mock dataset spanning a three-month period to demonstrate the solution.