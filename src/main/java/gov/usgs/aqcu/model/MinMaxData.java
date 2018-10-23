package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MinMaxData {
	private List<MinMaxPoint> max;
	private List<MinMaxPoint> min;

	/**
	 * We expect the map to have at least one entry and one or two keys - a single value or min and max values
	 * The report also expects the data to be sorted in time order
	 * @param minValue
	 * @param maxValue
	 * @param minMaxPoints
	 */
	public MinMaxData(BigDecimal minValue, BigDecimal maxValue, Map<BigDecimal, List<MinMaxPoint>> minMaxPoints) {
		this.min = new ArrayList<>();
		this.max = new ArrayList<>();
		if (minValue != null && maxValue != null && minMaxPoints != null && !minMaxPoints.isEmpty()) {
			min.addAll(minMaxPoints.get(minValue).stream().sorted(Comparator.comparing(MinMaxPoint::getTime)).collect(Collectors.toList()));
			max.addAll(minMaxPoints.get(maxValue).stream().sorted(Comparator.comparing(MinMaxPoint::getTime)).collect(Collectors.toList()));
		}
	}

	public List<MinMaxPoint> getMax() {
		return max;
	}
	public void setMax(List<MinMaxPoint> max) {
		this.max = max;
	}
	public List<MinMaxPoint> getMin() {
		return min;
	}
	public void setMin(List<MinMaxPoint> min) {
		this.min = min;
	}
}