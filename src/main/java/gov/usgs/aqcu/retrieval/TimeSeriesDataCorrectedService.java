package gov.usgs.aqcu.retrieval;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;

@Repository
public class TimeSeriesDataCorrectedService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataCorrectedService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDataCorrectedService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public TimeSeriesDataServiceResponse get(String timeSeriesIdentifier, Instant startDate, Instant endDate) {
		try {
		TimeSeriesDataCorrectedServiceRequest request = new TimeSeriesDataCorrectedServiceRequest()
				.setTimeSeriesUniqueId(timeSeriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate)
				.setApplyRounding(true)
				.setIncludeGapMarkers(true);
		TimeSeriesDataServiceResponse timeSeriesResponse = aquariusRetrievalService.executePublishApiRequest(request);
		return timeSeriesResponse;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch FieldVisitDescriptionListServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
}