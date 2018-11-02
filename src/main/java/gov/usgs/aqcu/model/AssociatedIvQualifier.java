package gov.usgs.aqcu.model;

import java.time.Instant;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

public class AssociatedIvQualifier {
	private String identifier;
	private Instant startDate;
	private Instant endDate;

	public AssociatedIvQualifier(Qualifier qual) {
		this.identifier = qual.getIdentifier();
		this.startDate = qual.getStartTime();
		this.endDate = qual.getEndTime();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}
}