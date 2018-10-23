package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.Instant;

public class MinMaxPoint {
	private Instant time;
	private BigDecimal value;

	public MinMaxPoint (Instant time, BigDecimal value) {
		this.time = time;
		this.value = value;
	}

	public Instant getTime() {
		return time;
	}
	public void setTime(Instant time) {
		this.time = time;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
}