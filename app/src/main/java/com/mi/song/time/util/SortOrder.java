package com.mi.song.time.util;

/**
 * Created by BarryAllen
 *
 * @TheBotBox boxforbot@gmail.com
 */

public enum SortOrder {
    TODAY(0), YESTERDAY(1), THIS_WEEK(2), MONTH(3), THIS_YEAR(4);

    int sort;

    SortOrder(int sort) {
        this.sort = sort;
    }

    public static SortOrder getSortEnum(int sort) {
        switch (sort) {
            case 0:
                return SortOrder.TODAY;
            case 1:
                return SortOrder.YESTERDAY;
            case 2:
                return SortOrder.THIS_WEEK;
            case 3:
                return SortOrder.MONTH;
            case 4:
                return SortOrder.THIS_YEAR;
        }
        return SortOrder.TODAY;
    }
}
