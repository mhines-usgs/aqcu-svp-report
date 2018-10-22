package gov.usgs.aqcu.retrieval;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

import gov.usgs.aqcu.parameter.SiteVisitPeakRequestParameters;

@Repository
public class TimeSeriesDataCorrectedService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataCorrectedService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDataCorrectedService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public TimeSeriesDataServiceResponse get(String timeseriesIdentifier, SiteVisitPeakRequestParameters requestParameters, boolean isDaily, ZoneOffset zoneOffset) {
		TimeSeriesDataServiceResponse timeSeriesResponse = new TimeSeriesDataServiceResponse();

		//Daily values time series need to be offset a day into the future to handle the "2400" situation.
		Instant startDate = adjustIfDv(requestParameters.getStartInstant(zoneOffset), isDaily);
		Instant endDate = adjustIfDv(requestParameters.getEndInstant(zoneOffset), isDaily);

		try {
			timeSeriesResponse = get(timeseriesIdentifier,
					startDate,
					endDate);
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch TimeSeriesDataCorrectedRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		return timeSeriesResponse;
	}

	protected TimeSeriesDataServiceResponse get(String timeSeriesIdentifier, Instant startDate, Instant endDate) throws Exception {
		TimeSeriesDataCorrectedServiceRequest request = new TimeSeriesDataCorrectedServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setIncludeGapMarkers(true);
		TimeSeriesDataServiceResponse timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		return timeSeriesResponse;
	}

	protected Instant adjustIfDv(Instant instant, boolean isDaily) {
		return isDaily ? instant.plus(Duration.ofDays(1)) : instant;
	}
}