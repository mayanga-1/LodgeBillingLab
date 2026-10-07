package org.example.lodgebillinglab;

import java.util.LinkedHashMap;
import java.util.Map;

public class BillingCalculator {

    public static final double DISCOUNT_RATE = 0.10;
    public static final int DISCOUNT_FROM_NIGHTS = 5;

    // Room prices in Kwacha per night
    public static final Map<String, Double> ROOM_RATES = new LinkedHashMap<>();

    // Practice rates: how many Kwacha one unit of the currency costs
    public static final Map<String, Double> CURRENCY_RATES = new LinkedHashMap<>();

    static {
        ROOM_RATES.put("Single", 350.0);
        ROOM_RATES.put("Double", 500.0);
        ROOM_RATES.put("Family", 850.0);

        CURRENCY_RATES.put("ZMW", 1.0);
        CURRENCY_RATES.put("USD", 25.0);
        CURRENCY_RATES.put("GBP", 33.0);
        CURRENCY_RATES.put("EUR", 29.0);
        CURRENCY_RATES.put("ZAR", 1.4);
        CURRENCY_RATES.put("CNY", 3.5);
    }

    public static double roomCharge(String roomType, int nights) {
        return ROOM_RATES.get(roomType) * nights;
    }

    public static double discount(String roomType, int nights) {
        if (nights >= DISCOUNT_FROM_NIGHTS) {
            return roomCharge(roomType, nights) * DISCOUNT_RATE;
        }
        return 0;
    }

    public static double total(String roomType, int nights, double extras) {
        return roomCharge(roomType, nights) - discount(roomType, nights) + extras;
    }

    public static double convert(double amountInKwacha, String currency) {
        return amountInKwacha / CURRENCY_RATES.get(currency);
    }
}
