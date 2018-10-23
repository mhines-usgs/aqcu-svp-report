package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

public class AssociatedIvQualifier {
	
	private String identifier;
	private String code;
	private String displayName;

	public AssociatedIvQualifier(QualifierMetadata qualifierMetadata) {
		this.identifier = qualifierMetadata.getIdentifier();
		this.code = qualifierMetadata.getCode();
		this.displayName = qualifierMetadata.getDisplayName();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
