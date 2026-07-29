package com.mwibutsa.store.payments;


import com.mwibutsa.store.dto.ErrorDto;
import com.mwibutsa.store.exceptions.CartEmptyException;
import com.mwibutsa.store.exceptions.CartNotFoundException;
import com.mwibutsa.store.repositories.OrderRepository;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;


    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload

    ) {
        checkoutService.handleWebhookEvent(new WebhookRequest(headers, payload));
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

    @NoArgsConstructor
    public static class PaymentException extends RuntimeException {
        public PaymentException(String message) {
            super(message);
        }
    }
}
