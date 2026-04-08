package com.bank.sentinel.infrastructure.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

public class PiiMaskingConverter extends ClassicConverter {

    private static final Pattern ACCOUNT_ID = Pattern.compile("\\b([A-Za-z0-9]{4})[A-Za-z0-9]{4,}\\b");
    private static final Pattern IP_ADDRESS  = Pattern.compile("\\b\\d{1,3}(\\.\\d{1,3}){3}\\b");

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        message = ACCOUNT_ID.matcher(message).replaceAll("$1****");
        return IP_ADDRESS.matcher(message).replaceAll("[MASKED_IP]");
    }
}
