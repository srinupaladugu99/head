/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.accounts.savings.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsPaymentStrategyImpl implements SavingsPaymentStrategy {

    private final SavingsTransactionActivityHelper savingsTransactionActivityHelper;

    public SavingsPaymentStrategyImpl(final SavingsTransactionActivityHelper savingsTransactionActivityHelper) {
        this.savingsTransactionActivityHelper = savingsTransactionActivityHelper;
    }
    
    @Override
    public Money makeScheduledPayments(final AccountPaymentEntity payment,
            final List<SavingsScheduleEntity> scheduledDeposits, final CustomerBO payingCustomer,
            final SavingsType savingsType, final Money savingsBalanceBeforeDeposit) {
        
        MifosCurrency currency = payment.getAccount().getCurrency();
        Money amountRemaining = new Money(currency, payment.getAmount().getAmount());
        Money runningBalance = new Money(currency, savingsBalanceBeforeDeposit.getAmount());
        final Date transactionDate = payment.getPaymentDate();
        Money depositAmount;
        PaymentStatus paymentStatus;
        
        if (savingsType.getValue().equals(SavingsType.VOLUNTARY.getValue())) {
            // For voluntary savings accounts - mark all outstanding
            // installments as paid and then put the deposit amount in the
            // latest installment
            paymentStatus = PaymentStatus.PAID;
            SavingsScheduleEntity lastExpectedPayment = null;
            for (SavingsScheduleEntity expectedPayment : scheduledDeposits) {
                lastExpectedPayment = expectedPayment;
                expectedPayment.setPaymentDetails(new Money(currency), paymentStatus,
                        new java.sql.Date(transactionDate.getTime()));
            }
            
            if (lastExpectedPayment != null) {
                if (amountRemaining.getAmountDoubleValue() >= lastExpectedPayment.getTotalDepositDue()
                        .getAmountDoubleValue()) {
                    depositAmount = lastExpectedPayment.getTotalDepositDue();
                    amountRemaining = amountRemaining.subtract(lastExpectedPayment.getTotalDepositDue());
                } else {
                    depositAmount = new Money(currency, amountRemaining.getAmount());
                    amountRemaining = new Money(currency);
                }
                
                lastExpectedPayment.setPaymentDetails(depositAmount, paymentStatus, new java.sql.Date(transactionDate
                        .getTime()));
                runningBalance = runningBalance.add(depositAmount);
                
                final SavingsTrxnDetailEntity voluntaryPaymentTrxn = savingsTransactionActivityHelper
                        .createSavingsTrxnForDeposit(payment, depositAmount, payingCustomer, lastExpectedPayment,
                                runningBalance);
                payment.addAccountTrxn(voluntaryPaymentTrxn);
            }

        } else {
            // mandatory savings - pay off mandatory amounts as much as possible
            for (SavingsScheduleEntity accountAction : scheduledDeposits) {
                paymentStatus = PaymentStatus.UNPAID;
                if (amountRemaining.getAmountDoubleValue() >= accountAction.getTotalDepositDue().getAmountDoubleValue()) {
                    depositAmount = accountAction.getTotalDepositDue();
                    amountRemaining = amountRemaining.subtract(accountAction.getTotalDepositDue());
                    paymentStatus = PaymentStatus.PAID;
                } else {
                    depositAmount = new Money(currency, amountRemaining.getAmount());
                    amountRemaining = new Money(currency);
                }

                accountAction.setPaymentDetails(depositAmount, paymentStatus, new java.sql.Date(transactionDate
                        .getTime()));
                runningBalance = runningBalance.add(depositAmount);
                final SavingsTrxnDetailEntity mandatoryScheduledPaymentTrxn = savingsTransactionActivityHelper
                        .createSavingsTrxnForDeposit(payment, depositAmount, payingCustomer,
                        accountAction,
                                runningBalance);
                payment.addAccountTrxn(mandatoryScheduledPaymentTrxn);

                if (amountRemaining.getAmountDoubleValue() <= 0.0) {
                    return amountRemaining;
                }
            }
        }
        return amountRemaining;
    }
}
