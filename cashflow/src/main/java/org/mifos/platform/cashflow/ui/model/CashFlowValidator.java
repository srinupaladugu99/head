/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
package org.mifos.platform.cashflow.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;

import java.math.BigDecimal;

import static java.text.MessageFormat.format;

public class CashFlowValidator {

    public void validateCaptureCashFlow(CashFlowForm cashFlow, ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();
        for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlow.getMonthlyCashFlows()) {
            validateExpense(messageContext, monthlyCashFlowForm);
            validateRevenue(messageContext, monthlyCashFlowForm);
            validateNotes(messageContext, monthlyCashFlowForm);
        }
    }

    private void validateExpense(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (isNull(monthlyCashFlowForm.getExpense())) {
            String message = format("Please specify expense for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_EXPENSE, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateRevenue(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (isNull(monthlyCashFlowForm.getRevenue())) {
            String message = format("Please specify revenue for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_REVENUE, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateNotes(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (!StringUtils.isEmpty(monthlyCashFlowForm.getNotes()) && monthlyCashFlowForm.getNotes().length() > 300) {
            String message = format("Notes should be less than 300 characters for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_NOTES, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private boolean isNull(BigDecimal value) {
        return value == null;
    }

    void constructErrorMessage(String code, String message, MessageContext context, Object... args) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).defaultText(message).args(args).build();
        context.addMessage(messageResolver);
    }
}
