package gov.usgs.aqcu.model;

import java.util.ArrayList;
import java.util.List;

public class SiteVisitPeakReport {
	
	private List<FieldVisitReading> readings = new ArrayList<>();
	private SVPReportMetadata reportMetadata;
	
	public List<FieldVisitReading> getReadings() {
		return readings;
	}

	public void setReadings(List<FieldVisitReading> readings) {
		this.readings = readings;
	}

	public SVPReportMetadata getReportMetadata() {
		return reportMetadata;
	}

	public void setReportMetadata(SVPReportMetadata reportMetadata) {
		this.reportMetadata = reportMetadata;
	}
}
