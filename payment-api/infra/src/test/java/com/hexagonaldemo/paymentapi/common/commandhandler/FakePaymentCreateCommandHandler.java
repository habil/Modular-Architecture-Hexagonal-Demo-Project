package com.hexagonaldemo.paymentapi.common.commandhandler;

import com.hexagonaldemo.paymentapi.common.exception.PaymentApiBusinessException;
import com.hexagonaldemo.paymentapi.payment.command.PaymentCreate;
import com.hexagonaldemo.paymentapi.payment.model.Payment;
import com.hexagonaldemo.paymentapi.payment.model.PaymentState;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@ConditionalOnProperty(name = "commandhandler.enabled", havingValue = "false", matchIfMissing = true)
public class FakePaymentCreateCommandHandler implements CommandHandler<Payment, PaymentCreate> {

    private static final Long PAYMENT_FAIL_ACCOUNT_ID = 6661L;
    private static final List<Long> FAILING_IDS = List.of(
            PAYMENT_FAIL_ACCOUNT_ID
    );

    @Override
    public Payment handle(PaymentCreate paymentCreate) {
        failedPaymentScenario(paymentCreate);
        return succeededPaymentCreateScenario(paymentCreate);
    }

    private void failedPaymentScenario(PaymentCreate paymentCreate) {
        if (paymentCreate.getAccountId().equals(PAYMENT_FAIL_ACCOUNT_ID)) {
            throw new PaymentApiBusinessException("paymentapi.balance.notSufficient");
        }
    }

    private Payment succeededPaymentCreateScenario(PaymentCreate paymentCreate) {
        if (!FAILING_IDS.contains(paymentCreate.getAccountId())) {
            return Payment.builder()
                    .id(1L)
                    .accountId(paymentCreate.getAccountId())
                    .state(PaymentState.SUCCESS)
                    .createdAt(LocalDateTime.of(2021,1,1,19,0,0))
                    .price(BigDecimal.valueOf(10.0))
                    .referenceCode("ref1")
                    .build();
        }
        throw new RuntimeException("uncovered test scenario occurred");
    }
}
