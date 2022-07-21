package com.sfm.obd.dto.mesures;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DateValeur {

	private long date;
	private String val;

	public DateValeur(Date date, String val) {
		super();
		this.date = date.getTime();
		this.val = val;
	}

}
