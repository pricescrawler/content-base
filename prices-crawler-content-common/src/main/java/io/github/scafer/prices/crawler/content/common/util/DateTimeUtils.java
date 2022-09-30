package io.github.scafer.prices.crawler.content.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {
    public static String getCurrentDateTime() {
        return ZonedDateTime.now(Clock.systemUTC()).toString();
    }

    public static String extractDate(String dateTime) {
        return ZonedDateTime.parse(dateTime).toLocalDate().toString();
    }

    public static boolean isSameDay(String a, String b) {
        return ZonedDateTime.parse(a).toLocalDate().isEqual(ZonedDateTime.parse(b).toLocalDate());
    }

    public static boolean isBetweenDates(String date, ZonedDateTime beforeDate, ZonedDateTime afterDate) {
        var chronoZonedDateTime = ChronoZonedDateTime.from(LocalDateTime.parse(date).toLocalDate());

        return (beforeDate.isBefore(chronoZonedDateTime) || beforeDate.isEqual(chronoZonedDateTime)) &&
                (afterDate.isAfter(chronoZonedDateTime) || afterDate.isEqual(chronoZonedDateTime));
    }
}
