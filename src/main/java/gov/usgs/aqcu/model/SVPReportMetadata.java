package gov.usgs.aqcu.model;

public class SVPReportMetadata extends ReportMetadata {

	private String timeseriesLabel;
	private boolean excludeComments;

	public String getTimeseriesLabel() {
		return timeseriesLabel;
	}

	public void setTimeseriesLabel(String timeseriesLabel) {
		this.timeseriesLabel = timeseriesLabel;
	}

	public boolean isExcludeComments() {
		return excludeComments;
	}

	public void setExcludeComments(boolean excludeComments) {
		this.excludeComments = excludeComments;
	}
}
