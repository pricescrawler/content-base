package io.github.pricescrawler.content.common.util;

import lombok.experimental.UtilityClass;

import java.time.*;

@UtilityClass
public class DateTimeUtils {

    /**
     * Returns the current date and time in UTC as a string.
     */
    public static String getCurrentDateTime() {
        return ZonedDateTime.now(Clock.systemUTC()).toString();
    }

    /**
     * Returns the date portion of a date-time string in ISO-8601 format.
     */
    public static String getDateFromDateTime(String dateTime, String timezone) {
        return ZonedDateTime.parse(dateTime).withZoneSameInstant(getZoneId(timezone)).toLocalDate().toString();
    }

    /**
     * Returns true if the two date-time strings represent the same day.
     */
    public static boolean areDatesOnSameDay(String date1, String date2, String timezone) {
        var zoneId = getZoneId(timezone);
        return ZonedDateTime.parse(date1).withZoneSameInstant(zoneId).toLocalDate().isEqual(ZonedDateTime.parse(date2)
                .withZoneSameInstant(zoneId).toLocalDate());
    }

    /**
     * Returns true if the input date is between the beforeDate and afterDate (inclusive).
     */
    public static boolean isDateBetween(String date, ZonedDateTime beforeDate, ZonedDateTime afterDate) {
        var zonedDateTime = ZonedDateTime.parse(date);

        return (beforeDate.isBefore(zonedDateTime) || beforeDate.isEqual(zonedDateTime)) &&
                (afterDate.isAfter(zonedDateTime) || afterDate.isEqual(zonedDateTime));
    }

    /**
     * Returns the duration between two date-time strings.
     */
    public static Duration getDurationBetweenDates(String startDate, String endDate) {
        return Duration.between(ZonedDateTime.parse(startDate), ZonedDateTime.parse(endDate));
    }

    /**
     * Returns a string representation of the date that is the given duration after the start date.
     */
    public static String getDateAfterDuration(String startDate, Duration duration) {
        return ZonedDateTime.parse(startDate).plus(duration).toLocalDate().toString();
    }

    /**
     * Returns the ZoneId for the specified String value
     */
    public static ZoneId getZoneId(String timezone) {
        return timezone == null ? ZoneOffset.UTC : ZoneId.of(timezone);
    }
}
