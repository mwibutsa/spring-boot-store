package com.mwibutsa.store.services;

import com.mwibutsa.store.entities.Order;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(Order order);
}
