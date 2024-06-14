package com.example.restologi.Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionModel {
    private String orderId;
    private String amount;
    private String paymentMethod;
    private String date;

    public TransactionModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public TransactionModel(String orderId, String amount, String paymentMethod) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;

        LocalDateTime ldt_date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        this.date = ldt_date.format(formatter);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDate() {
        return date;
    }
}

