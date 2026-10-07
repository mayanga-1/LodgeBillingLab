package org.example.lodgebillinglab;

public class Bill {
    private final String id;
    private final String guest;
    private final String roomType;
    private final int nights;
    private final double total;
    private final double paid;
    private final double balance;
    private final String currency;

    public Bill(String id, String guest, String roomType, int nights,
                double total, double paid, String currency) {
        this.id = id;
        this.guest = guest;
        this.roomType = roomType;
        this.nights = nights;
        this.total = total;
        this.paid = paid;
        this.balance = total - paid;
        this.currency = currency;
    }

    public String getId() { return id; }
    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
    public int getNights() { return nights; }
    public double getTotal() { return total; }
    public double getPaid() { return paid; }
    public double getBalance() { return balance; }
    public String getCurrency() { return currency; }
}
