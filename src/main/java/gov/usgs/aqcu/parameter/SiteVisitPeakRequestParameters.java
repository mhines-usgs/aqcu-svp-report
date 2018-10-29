package gov.usgs.aqcu.parameter;

public class SiteVisitPeakRequestParameters extends ReportRequestParameters {

	private Boolean excludedComments;

	public SiteVisitPeakRequestParameters() {
		excludedComments = false;
	}

	public Boolean getExcludedComments() {		
		return excludedComments;
	}

	public void setExcludedComments(Boolean val) {
		this.excludedComments = val != null ? val : false;
	}

	@Override 
	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = super.getAsQueryString(overrideIdentifier, absoluteTime);
		
		queryString += "&excludeComments=" + getExcludedComments();

		return queryString;
	}
}
