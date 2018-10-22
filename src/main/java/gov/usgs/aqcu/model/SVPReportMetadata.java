package gov.usgs.aqcu.model;

import java.time.Instant;

public class SVPReportMetadata {

	private Instant endDate;
	private String title = "Site Visit Peak";
	private String timeseriesLabel; //TODO yes the s is lowercase, fix here and repgen?
	private String stationName;
	private Instant startDate;
	private String stationId;
	private boolean excludeComments;

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTimeseriesLabel() {
		return timeseriesLabel;
	}

	public void setTimeseriesLabel(String timeseriesLabel) {
		this.timeseriesLabel = timeseriesLabel;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public boolean isExcludeComments() {
		return excludeComments;
	}

	public void setExcludeComments(boolean excludeComments) {
		this.excludeComments = excludeComments;
	}
}
