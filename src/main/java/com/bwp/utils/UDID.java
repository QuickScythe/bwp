package com.bwp.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Unique Date Identifier (UDID) generator and parser.
 * <p>
 * Produces compact, sortable identifiers composed of a caller-supplied prefix and a
 * timestamp formatted as yyyyMMddHHmm, with an optional "-N" suffix for disambiguation.
 * Example for 07/15/2025 18:35 with prefix "CID": CID202507151835 or CID202507151835-1
 */
public class UDID {

    //Example format for 07/15/2025 at 6:35pm: "CID202507151835"
    //If another case is created in the same minute, the ID will have an identifier appended, e.g., "CID202507151835-1"

    private final String prefix;
    private final long date;
    private int id = 0;

    UDID(String prefix, long date) {
        this.date = date;
        this.prefix = prefix;
    }

    UDID(String prefix, String date) {
        this.prefix = prefix;
        this.date = parseDate(date);
        if(date.length() > 12) {
            String[] parts = date.split("-");
            if(parts.length > 1) {
                try {
                    this.id = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid UDID ID part: " + parts[1], e);
                }
            }
        }
    }

    /**
     * Parses a UDID from its textual representation without the prefix, e.g., 202507151835-1.
     *
     * @param prefix the identifier prefix to include when rendering back to string
     * @param date   the timestamp portion formatted as yyyyMMddHHmm with optional -N suffix
     * @return a UDID instance
     * @throws IllegalArgumentException if the date or suffix are invalid
     */
    public static UDID fromString(String prefix, String date) {
        return new UDID(prefix, date);
    }

    /**
     * Creates a UDID from a millisecond epoch timestamp.
     *
     * @param prefix the identifier prefix
     * @param date   epoch milliseconds
     * @return a UDID instance representing the specified time
     */
    public static UDID fromDate(String prefix, long date) {
        return new UDID(prefix, date);
    }

    /**
     * Convenience factory for a UDID representing the current time.
     *
     * @param prefix the identifier prefix
     * @return a UDID for now
     */
    public static UDID now(String prefix) {
        return fromDate(prefix, System.currentTimeMillis());
    }

    private long parseDate(String date) {
        try {
            Calendar calendar = getCalendar(date);
            return calendar.getTimeInMillis();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid UDID date format", e);
        }
    }

    private static Calendar getCalendar(String datePart) {
        int year = Integer.parseInt(datePart.substring(0, 4));
        int month = Integer.parseInt(datePart.substring(4, 6)) - 1; // Months are 0-based in Calendar
        int day = Integer.parseInt(datePart.substring(6, 8));
        int hour = Integer.parseInt(datePart.substring(8, 10)); // Hour in 24-hour format
        int minute = Integer.parseInt(datePart.substring(10, 12));

        // Create a Calendar instance to set the date and time
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        return calendar;
    }

    /**
     * Renders the UDID in the canonical prefix+yyyyMMddHHmm[-N] format.
     */
    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Hour in 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
        String format = String.format(prefix + "%04d%02d%02d%02d%02d", year, month, day, hour, minute);
        if (id > 0) {
            format += "-" + id;
        }
        return format;
    }

    /**
     * Compares UDIDs by their canonical string representation.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UDID udid)
            return udid.toString().equals(toString());
        return super.equals(obj);
    }

    /**
     * Returns the timestamp component as a java.util.Date.
     */
    public Date date() {
        return new Date(this.date);
    }
}
