# PharmaCare – Pharmacy Management System

Java full stack app: **Spring Boot 3 + Thymeleaf + H2 database** (no MySQL needed).

## Features
- **Medicines** – add, edit, delete, search (name / category / manufacturer / batch)
- **Stock** – add or remove stock, low-stock alerts, expiry alerts, stock movement history
- **Billing** – multi-item bill, discount %, GST %, stock auto-reduces, printable invoice
- **Suppliers** – add, edit, delete, search
- **Sales History** – all bills, filter by date, total revenue
- **Dashboard** – today's bills and revenue, low stock, expiring medicines

## How to run (easiest)
1. Extract the zip.
2. Double-click **START-PharmaCare.bat**
3. The browser opens at http://localhost:8080

First time only: it builds the app, and downloads Java if needed (3-5 min, needs internet).
After that it starts in about 15 seconds. Close the black window to stop.

**From VS Code:** File > Open Folder > `PharmaCare`, then press **Ctrl + Shift + B**.

**Changed the code?** Double-click **REBUILD-and-START.bat**.

## Database
- Data is saved in the `data/` folder, so it stays after restart.
- To reset everything: stop the app and delete the `data/` folder.
- View tables in the browser: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/pharmacydb`  User: `sa`  Password: (blank)

## Project structure
```
src/main/java/com/pharmacy/
  model/        -> database tables (Medicine, Supplier, Sale, SaleItem, StockEntry)
  repository/   -> database queries
  service/      -> billing logic
  controller/   -> URLs and page handling
  DataLoader    -> sample data on first run
src/main/resources/
  templates/    -> HTML pages (Thymeleaf)
  static/css/   -> styling
  application.properties -> settings (port, database)
```

## Common errors
| Error | Why | Fix |
|---|---|---|
| `Port 8080 already in use` | App is already running somewhere | Stop the other one, or set `server.port=8081` in `application.properties` |
| `JAVA_HOME not found` / `java not recognized` | JDK not installed or not on PATH | Install JDK 17+, restart VS Code |
| "Windows protected your PC" | File came from the internet | Click More info > Run anyway |
