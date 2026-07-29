package com.mwibutsa.store.payments;

import com.mwibutsa.store.entities.Order;
import com.mwibutsa.store.entities.OrderItem;
import com.mwibutsa.store.entities.PaymentStatus;
import com.mwibutsa.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;
    private OrderRepository orderRepository;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().putMetadata("order_id", order.getId().toString()).build());
            order.getItems().forEach(item -> builder.addLineItem(createLineItem(item)));
            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException ex) {
            throw new CheckoutController.PaymentException();
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var signature = request.getHeaders().get("Stripe-Signature");
            var event = Webhook.constructEvent(request.getPayload(), signature, webhookSecretKey);

            return switch (event.getType()) {
                case "payment_intent.succeeded" -> Optional.of(
                        new PaymentResult(extractOrderId(event), PaymentStatus.PAID)
                );
                case "payment_intent.payment_failed" -> Optional.of(
                        new PaymentResult(extractOrderId(event), PaymentStatus.FAILED)
                );
                default -> Optional.empty();
            };

        } catch (SignatureVerificationException ex) {
            throw new CheckoutController.PaymentException("Invalid signature");
        }
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder().
                setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item)).build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(item.getUnitPrice().multiply(new BigDecimal("100")))
                .setProductData(createProductData(item)).build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName()).build();
    }


    private Long extractOrderId(Event event) {

        var stripeObject = event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new CheckoutController.PaymentException(
                        "Could not deserialize stripe event. check the sdk and api version"
                ));
        var paymentIntent = (PaymentIntent) stripeObject;

        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }
}
