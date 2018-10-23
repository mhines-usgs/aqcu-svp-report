package gov.usgs.aqcu.util;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class BigDecimalSummaryStatistics implements Consumer<BigDecimal> {

	private BigDecimal min = new BigDecimal(Integer.MAX_VALUE);
	private BigDecimal max = new BigDecimal(Integer.MIN_VALUE);

	public BigDecimalSummaryStatistics() {}

	@Override
	public void accept(BigDecimal value) {
		min = min.min(value);
		max = max.max(value);
	}

	public void combine(BigDecimalSummaryStatistics other) {
		min = min.min(other.min);
		max = max.max(other.max);
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}
}