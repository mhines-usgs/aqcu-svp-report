package gov.usgs.aqcu.parameter;

import java.util.ArrayList;
import java.util.List;

public class SiteVisitPeakRequestParameters extends ReportRequestParameters {

	private List<String> excludedComments;

	public SiteVisitPeakRequestParameters() {
		excludedComments = new ArrayList<>();
	}

	public List<String> getExcludedComments() {		
		return excludedComments;
	}

	public void setExcludedComments(List<String> val) {
		this.excludedComments = val != null ? val : new ArrayList<>();
	}

	@Override 
	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = super.getAsQueryString(overrideIdentifier, absoluteTime);

		if(getExcludedComments().size() > 0) {
			queryString += "&excludedComments=" + String.join(",", excludedComments);
		}

		return queryString;
	}
}
