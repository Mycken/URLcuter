package com.madvena.shorturl;

import com.madvena.shorturl.exception.Base62Exception;
import com.madvena.shorturl.util.Base62;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base62Test {
    @Test
    @DisplayName(value = "Throw Exception of cause negative parameter")
    void shouldThrowIfParameterNegative() {
        assertThrows(Base62Exception.class, () -> Base62.to(-1));
    }

    @Test
    @DisplayName(value = "Throw Exception when decoding parameter isn't Base62")
    void shouldThrowIfNotABase62String() {
        assertThrows(Base62Exception.class, () -> Base62.from("/?13"));
    }

    @Test
    @DisplayName(value = "Throw Exception when decoding parameter is out of Integer")
    void shouldThrowIfIntegerOverflow() {
        assertThrows(Base62Exception.class, () -> {
            Base62.from("2lkCB2"); // 2 ^ 31
        });
    }

    @Test
    @DisplayName(value = "Throw Exception when encoding parameter is too long")
    void shouldThrowIfInputStringTooLong() {
        assertThrows(Base62Exception.class, () -> {
            Base62.from("2lkCB20"); // 2 ^ 31 * 62
        });
    }

    private static Stream<Arguments> data() {
        return Stream.of(
                // Base 10  | Base 62
                Arguments.of(0, "0"),
                Arguments.of(68, "16"),
                Arguments.of(2147483647, "2lkCB1")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    @DisplayName(value = "Correct decoding - from()")
    void shouldCorrectlyPerformTranslationFrom(int base10, String base62) {
        assertThat(Base62.from(base62), is(base10));
    }

    @ParameterizedTest
    @MethodSource("data")
    @DisplayName(value = "Correct encoding - to()")
    void shouldCorrectlyPerformTranslationTo(int base10, String base62) {
        assertThat(Base62.to(base10), is(base62));
    }
}
