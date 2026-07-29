package com.mwibutsa.store.controllers;


import com.mwibutsa.store.dto.CheckoutRequest;
import com.mwibutsa.store.dto.ErrorDto;
import com.mwibutsa.store.entities.OrderStatus;
import com.mwibutsa.store.exceptions.CartEmptyException;
import com.mwibutsa.store.exceptions.CartNotFoundException;
import com.mwibutsa.store.exceptions.PaymentException;
import com.mwibutsa.store.repositories.OrderRepository;
import com.mwibutsa.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String payload

    ) {
        try {
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            IO.println(event.getType());
            var stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {

                    var paymentIntent = (PaymentIntent) stripeObject;
                    // PAID
                    assert paymentIntent != null;
                    var order = orderRepository.findById(Long.valueOf(paymentIntent.getMetadata().get("order_id"))).orElseThrow();
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                }

                case "payment_intent.failed" -> {
                    // failed.
                }
            }
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException ex) {
            IO.println(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest payload
    ) {

        return ResponseEntity.ok(checkoutService.checkout(payload));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }
}
