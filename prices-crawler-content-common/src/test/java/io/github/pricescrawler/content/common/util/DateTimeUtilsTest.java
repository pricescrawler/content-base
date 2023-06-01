package io.github.pricescrawler.content.common.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @Test
    void getCurrentDateTime_ShouldReturnCurrentDateTimeInUTC() {
        String result = DateTimeUtils.getCurrentDateTime();
        assertNotNull(result);
    }

    @Test
    void getDateFromDateTime_ShouldReturnDateFromDateTimeString() {
        String dateTime = "2023-06-01T10:15:30Z";
        String expectedDate = "2023-06-01";

        String result = DateTimeUtils.getDateFromDateTime(dateTime);
        assertEquals(expectedDate, result);
    }

    @Test
    void areDatesOnSameDay_ShouldReturnTrueForSameDayDates() {
        String date1 = "2023-06-01T10:00:00Z";
        String date2 = "2023-06-01T15:30:00Z";

        boolean result = DateTimeUtils.areDatesOnSameDay(date1, date2);
        assertTrue(result);
    }

    @Test
    void areDatesOnSameDay_ShouldReturnFalseForDifferentDayDates() {
        String date1 = "2023-06-01T10:00:00Z";
        String date2 = "2023-06-02T15:30:00Z";

        boolean result = DateTimeUtils.areDatesOnSameDay(date1, date2);
        assertFalse(result);
    }

    @Test
    void isDateBetween_ShouldReturnTrueForDateWithinRange() {
        String date = "2023-06-01T12:00:00Z";
        ZonedDateTime beforeDate = ZonedDateTime.parse("2023-06-01T10:00:00Z");
        ZonedDateTime afterDate = ZonedDateTime.parse("2023-06-01T15:00:00Z");

        boolean result = DateTimeUtils.isDateBetween(date, beforeDate, afterDate);
        assertTrue(result);
    }

    @Test
    void isDateBetween_ShouldReturnFalseForDateOutsideRange() {
        String date = "2023-06-01T16:00:00Z";
        ZonedDateTime beforeDate = ZonedDateTime.parse("2023-06-01T10:00:00Z");
        ZonedDateTime afterDate = ZonedDateTime.parse("2023-06-01T15:00:00Z");

        boolean result = DateTimeUtils.isDateBetween(date, beforeDate, afterDate);
        assertFalse(result);
    }

    @Test
    void getDurationBetweenDates_ShouldReturnDurationBetweenDates() {
        String startDate = "2023-06-01T10:00:00Z";
        String endDate = "2023-06-01T15:30:00Z";
        Duration expectedDuration = Duration.ofHours(5).plusMinutes(30);

        Duration result = DateTimeUtils.getDurationBetweenDates(startDate, endDate);
        assertEquals(expectedDuration, result);
    }

    @Test
    void getDateAfterDuration_ShouldReturnDateAfterDuration() {
        String startDate = "2023-06-01T10:00:00Z";
        Duration duration = Duration.ofDays(3);
        String expectedDate = "2023-06-04";

        String result = DateTimeUtils.getDateAfterDuration(startDate, duration);
        assertEquals(expectedDate, result);
    }
}

