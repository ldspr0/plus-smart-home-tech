package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interactionapi.enums.PaymentState;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;
    @NotNull
    private UUID orderId;
    private double productsTotal;
    private double deliveryTotal;
    private double totalPayment;
    private double feeTotal;
    @Enumerated(EnumType.STRING)
    private PaymentState status;
}