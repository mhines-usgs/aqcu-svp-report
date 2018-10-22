package gov.usgs.aqcu.model;

import java.util.ArrayList;
import java.util.List;

public class SiteVisitPeakReport {
	
	private List<SVPReportReading> readings = new ArrayList<>();
	private SVPReportMetadata reportMetadata;
	
	public List<SVPReportReading> getReadings() {
		return readings;
	}

	public void setReadings(List<SVPReportReading> readings) {
		this.readings = readings;
	}

	public SVPReportMetadata getReportMetadata() {
		return reportMetadata;
	}

	public void setReportMetadata(SVPReportMetadata reportMetadata) {
		this.reportMetadata = reportMetadata;
	}
}
