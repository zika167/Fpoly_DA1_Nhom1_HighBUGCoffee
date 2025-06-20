package poly.cafe.util;

import java.time.LocalDate;
import java.util.Date;

public class TimeRange {

    private Date begin = new Date();
    private Date end = new Date();

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public TimeRange(LocalDate begin, LocalDate end) {
        this.begin = java.sql.Date.valueOf(begin); // Chuyển LocalDate sang java.sql.Date
        this.end = java.sql.Date.valueOf(end);     // Chuyển LocalDate sang java.sql.Date
    }
    
    public static TimeRange today() {
        LocalDate begin = LocalDate.now();
        return new TimeRange(begin, begin.plusDays(1));
    }
    
    public static TimeRange thisWeek() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.minusDays(now.getDayOfWeek().getValue() - 1);
        return new TimeRange(begin, begin.plusDays(7));
    }
    
    public static TimeRange thisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.withDayOfMonth(1);
        return new TimeRange(begin, begin.plusDays(now.lengthOfMonth()));
    }
    
    public static TimeRange thisQuarter() {
        LocalDate now = LocalDate.now();
        int firstMonth = now.getMonth().firstMonthOfQuarter().getValue();
        LocalDate begin = now.withMonth(firstMonth).withDayOfMonth(1);
        return new TimeRange(begin, begin.plusMonths(3));
    }
    
    public static TimeRange thisYear() {
        LocalDate now = LocalDate.now();
        LocalDate begin = now.withMonth(1).withDayOfMonth(1);
        return new TimeRange(begin, begin.plusMonths(12));
    }
}