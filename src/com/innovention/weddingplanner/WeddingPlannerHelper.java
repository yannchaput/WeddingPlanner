package com.innovention.weddingplanner;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class WeddingPlannerHelper {
	
	private WeddingPlannerHelper() {
		
	}

	/**
	 * Compute the remaining days between now and the wedding
	 * @param year
	 * @param month (joda time format)
	 * @param day
	 * @return
	 */
	public static final int computeDate(final int year, final int month, final int day) {
		DateTime now = DateTime.now();
		DateTime weddingDate = new DateTime(year, month, day, 0, 0);
		return Days.daysBetween(now.toDateMidnight(), weddingDate.toDateMidnight()).getDays();
	}
}
