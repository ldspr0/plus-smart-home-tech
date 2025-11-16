package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interactionapi.enums.PaymentState;

import java.math.BigDecimal;
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
    private BigDecimal productsTotal;
    private BigDecimal deliveryTotal;
    private BigDecimal totalPayment;
    private BigDecimal feeTotal;
    @Enumerated(EnumType.STRING)
    private PaymentState status;
}