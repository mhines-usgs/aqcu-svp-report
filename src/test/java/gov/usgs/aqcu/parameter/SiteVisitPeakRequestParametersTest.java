package gov.usgs.aqcu.parameter;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.Test;

public class SiteVisitPeakRequestParametersTest {

	Instant reportEndInstant = Instant.parse("2018-03-16T23:59:59.999999999Z");
	Instant reportStartInstant = Instant.parse("2018-03-16T00:00:00.00Z");
	LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	LocalDate reportStartDate = LocalDate.of(2018, 03, 16);
    String primaryIdentifier = "test-identifier";

    @Test
	public void getAsQueryStringTest() {
        SiteVisitPeakRequestParameters params = new SiteVisitPeakRequestParameters();
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
        params.setExcludeComments(false);
        String expected = "startDate=2018-03-16&endDate=2018-03-16&primaryTimeseriesIdentifier=test-identifier&excludeComments=false";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
        params.setExcludeComments(true);
        expected = "startDate=2018-03-16&endDate=2018-03-16&primaryTimeseriesIdentifier=test-identifier&excludeComments=true";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}
}