package io.github.pricescrawler.content.common.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @Test
    void getCurrentDateTime_shouldReturnCurrentDateTimeInUTC() {
        assertNotNull(DateTimeUtils.getCurrentDateTime());
    }

    @Test
    void getDateFromDateTime_shouldReturnDateFromDateTimeString() {
        var dateTime = "2023-06-01T10:15:30Z";
        assertEquals("2023-06-01", DateTimeUtils.getDateFromDateTime(dateTime, null));
    }

    @Test
    void areDatesOnSameDay_shouldReturnTrueForSameDayDates() {
        var date1 = "2023-06-01T10:00:00Z";
        var date2 = "2023-06-01T15:30:00Z";

        assertTrue(DateTimeUtils.areDatesOnSameDay(date1, date2, null));
    }

    @Test
    void areDatesOnSameDay_shouldReturnFalseForDifferentDayDates() {
        var date1 = "2023-06-01T10:00:00Z";
        var date2 = "2023-06-02T15:30:00Z";
        assertFalse(DateTimeUtils.areDatesOnSameDay(date1, date2, null));
    }

    @Test
    void isDateBetween_shouldReturnTrueForDateWithinRange() {
        var date = "2023-06-01T12:00:00Z";
        var beforeDate = ZonedDateTime.parse("2023-06-01T10:00:00Z");
        var afterDate = ZonedDateTime.parse("2023-06-01T15:00:00Z");
        assertTrue(DateTimeUtils.isDateBetween(date, beforeDate, afterDate));
    }

    @Test
    void isDateBetween_shouldReturnFalseForDateOutsideRange() {
        var date = "2023-06-01T16:00:00Z";
        var beforeDate = ZonedDateTime.parse("2023-06-01T10:00:00Z");
        var afterDate = ZonedDateTime.parse("2023-06-01T15:00:00Z");
        assertFalse(DateTimeUtils.isDateBetween(date, beforeDate, afterDate));
    }

    @Test
    void getDurationBetweenDates_shouldReturnDurationBetweenDates() {
        var startDate = "2023-06-01T10:00:00Z";
        var endDate = "2023-06-01T15:30:00Z";
        var expectedDuration = Duration.ofHours(5).plusMinutes(30);
        assertEquals(expectedDuration, DateTimeUtils.getDurationBetweenDates(startDate, endDate));
    }

    @Test
    void getDateAfterDuration_shouldReturnDateAfterDuration() {
        var startDate = "2023-06-01T10:00:00Z";
        var duration = Duration.ofDays(3);
        var expectedDate = "2023-06-04";
        assertEquals(expectedDate, DateTimeUtils.getDateAfterDuration(startDate, duration));
    }
}

