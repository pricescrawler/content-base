package io.github.scafer.prices.crawler.content.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
        var zonedDateTime = ZonedDateTime.from(LocalDateTime.parse(date).toLocalDate());

        return (beforeDate.isBefore(zonedDateTime) || beforeDate.isEqual(zonedDateTime)) &&
                (afterDate.isAfter(zonedDateTime) || afterDate.isEqual(zonedDateTime));
    }

    public static Duration durationBetweenDates(String startDate, String endDate) {
        return Duration.between(
                ZonedDateTime.parse(startDate),
                ZonedDateTime.parse(endDate)
        );
    }
}
