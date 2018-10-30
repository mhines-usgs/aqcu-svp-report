package gov.usgs.aqcu.parameter;

public class SiteVisitPeakRequestParameters extends ReportRequestParameters {

	private Boolean excludeComments;

	public SiteVisitPeakRequestParameters() {
		excludeComments = false;
	}

	public Boolean getExcludeComments() {		
		return excludeComments;
	}

	public void setExcludeComments(Boolean val) {
		this.excludeComments = val != null ? val : false;
	}

	@Override 
	public String getAsQueryString(String overrideIdentifier, boolean absoluteTime) {
		String queryString = super.getAsQueryString(overrideIdentifier, absoluteTime);
		
		queryString += "&excludeComments=" + getExcludeComments();

		return queryString;
	}
}
