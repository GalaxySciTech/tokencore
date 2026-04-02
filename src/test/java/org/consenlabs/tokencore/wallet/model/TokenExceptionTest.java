package org.consenlabs.tokencore.wallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenExceptionTest {

    @Test
    void shouldCarryMessage() {
        TokenException ex = new TokenException("test_error");
        assertEquals("test_error", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void shouldCarryCause() {
        Exception cause = new IllegalStateException("root cause");
        TokenException ex = new TokenException("wrapper", cause);
        assertEquals("wrapper", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
