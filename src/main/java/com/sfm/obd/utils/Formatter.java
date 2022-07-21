package com.sfm.obd.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Formatter {
	
	public static String formatDate(Date date) {
		Locale locale = new Locale("fr", "FR");
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
		return dateFormat.format(date);
	}
	
}
