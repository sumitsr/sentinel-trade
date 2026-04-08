package com.bank.sentinel.infrastructure.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PiiMaskingConverterTest {

    @Mock
    private ILoggingEvent event;

    private final PiiMaskingConverter converter = new PiiMaskingConverter();

    @Test
    void should_mask_account_id_pattern_keeping_first_four_chars() {
        when(event.getFormattedMessage()).thenReturn("Processing account ACC12345XYZ for trade");

        String result = converter.convert(event);

        assertThat(result).contains("ACC1****").doesNotContain("ACC12345XYZ");
    }

    @Test
    void should_mask_ip_address_pattern() {
        when(event.getFormattedMessage()).thenReturn("Request received from 192.168.1.100");

        String result = converter.convert(event);

        assertThat(result).contains("[MASKED_IP]").doesNotContain("192.168.1.100");
    }

    @Test
    void should_not_modify_message_with_no_sensitive_patterns() {
        when(event.getFormattedMessage()).thenReturn("Trade OK, buy done");

        String result = converter.convert(event);

        assertThat(result).isEqualTo("Trade OK, buy done");
    }
}
