# Lodge Billing Lab

ICT261 Advanced Java Programming – Lecture 3 Lab

**Student:** Mapalo Mayanga
**Student number:** 202407692
**Programme:** BSc Cyber Security, Mulungushi University
**Lecturer:** Mr E. Nyirenda

## About
A JavaFX desktop app that calculates and stores lodge bills.

## Features
- Room types: Single K350, Double K500, Family K850 per night
- 10% discount on the room charge for 5 nights or more (extras are not discounted)
- Total shown in the chosen currency: ZMW, USD, GBP, EUR, ZAR, CNY
  (fixed practice exchange rates, not live rates)
- Input validation with clear messages
- Results shown only when Calculate is pressed
- Save bills into a table; delete a selected bill with confirmation
- View receipt in its own window with a Print button
- Menu bar (File, Help) and toolbar (New guest, View receipt, Delete selected bill)
- Aquatic premium theme using a CSS stylesheet

## How to run
Open in IntelliJ IDEA (JDK 21, Gradle) and run `LodgeBillingApp`.

## Files
- `LodgeBillingApp.java` – the window, form, table and buttons
- `BillingCalculator.java` – prices, discount, totals and currency conversion
- `Bill.java` – one saved bill
- `lodge.css` – colours and styling