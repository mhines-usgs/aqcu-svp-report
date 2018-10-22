package gov.usgs.aqcu.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;

public class SVPReportReading {

	private Instant visitTime;
	private List<String> comments = new ArrayList<>();
	private Instant lastVisitPrior; // not used in the actual report, only to get AssociatedIv
	private Instant time;
	private String monitoringMethod;
	private String uncertainty;
	private String value;
	private String party;
	private String sublocation;
	private String associatedIvValue;
	private Instant associatedIvTime;
	private List<AssociatedIvQualifier> associatedIvQualifiers;

	public SVPReportReading(FieldVisitDescription fieldVisitDescription, String party, Reading reading) {
		this.visitTime = fieldVisitDescription.getStartTime();
		this.comments.add(reading.getComments());
//		lastVisitPrior is set after creation
		this.time = reading.getTime();
		this.monitoringMethod = reading.getMonitoringMethod();
		this.uncertainty = reading.getUncertainty().getDisplay();
		this.value = reading.getValue().getDisplay();
		this.party = party;
		this.sublocation = reading.getSubLocationIdentifier();
//		associatedIvValue is set after creation
//		associatedIvTime is set after creation
//		associatedIvQualifiers is set after creation
	}
	
	public Instant getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Instant visitTime) {
		this.visitTime = visitTime;
	}

	public void setLastVisitPrior(Instant previousDate) {
		this.lastVisitPrior = previousDate;
	}

	public String getMonitoringMethod() {
		return monitoringMethod;
	}

	public void setMonitoringMethod(String monitoringMethod) {
		this.monitoringMethod = monitoringMethod;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
