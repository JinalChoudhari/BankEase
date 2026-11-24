# BankEase - Beginner Java Banking Management System

## Technology
- Java 17, Spring Boot 3.4.5, Maven
- Spring MVC, Thymeleaf, Spring Security
- Spring Data JPA, Hibernate, MySQL

## Setup
1. Install JDK 17, IntelliJ IDEA and MySQL Server.
2. In MySQL Workbench run `database/bankease.sql` (creates the `bankease` database).
3. Open `src/main/resources/application.properties` and replace `YOUR_MYSQL_PASSWORD` with your local MySQL root password. Keep `server.port=8081` unless you want another available port.
4. Open the project by selecting this folder's `pom.xml` in IntelliJ and allow Maven to load dependencies.
5. Run `src/main/java/com/bankease/BankEaseApplication.java`.
6. Open http://localhost:8081.

## Default admin
- Email: admin@bankease.com
- Password: Admin@123

The default admin is inserted only if that email does not already exist in the database. If you previously created it with another password, the original password remains.

## Customer flow
Register and log in, then use the dashboard tiles to open an account, view account details, deposit, withdraw, transfer, or review transaction history. Account opening collects mobile number, date of birth, address, account type, initial deposit and optional nominee details. The registered name and email are pre-filled from the user's profile.

## Database updates
`spring.jpa.hibernate.ddl-auto=update` lets Hibernate add the new account columns to an existing `bankease` database. Back up important data before changing schemas. Existing accounts will have null values for newly added optional personal-detail fields.

## Important
This is an educational simulation. It is not connected to a real bank, payment network, KYC provider or cash deposit/withdrawal service. Do not use real personal or financial information. The initial deposit and money movements update the local demo database only.
