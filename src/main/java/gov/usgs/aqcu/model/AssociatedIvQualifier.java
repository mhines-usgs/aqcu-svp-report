package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;

public class AssociatedIvQualifier {
    private String identifier;

    public AssociatedIvQualifier(Qualifier qual) {
        this.identifier = qual.getIdentifier();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}