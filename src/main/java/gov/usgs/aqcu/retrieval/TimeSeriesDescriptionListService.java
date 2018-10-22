package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;

import gov.usgs.aqcu.exception.AquariusProcessingException;


@Repository
public class TimeSeriesDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDescriptionListService.class);	

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public TimeSeriesDescriptionListService(
		AquariusRetrievalService aquariusRetrievalService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	protected TimeSeriesDescriptionListByUniqueIdServiceResponse getRawResponse(List<String> timeSeriesUniqueIds) {
		TimeSeriesDescriptionListByUniqueIdServiceRequest request = new TimeSeriesDescriptionListByUniqueIdServiceRequest()
				.setTimeSeriesUniqueIds(new ArrayList<>(new HashSet<>(timeSeriesUniqueIds)));
		TimeSeriesDescriptionListByUniqueIdServiceResponse tssDesc = aquariusRetrievalService.executePublishApiRequest(request);
		return tssDesc;
	}

	public List<TimeSeriesDescription> getTimeSeriesDescriptionList(List<String> timeSeriesUniqueIds) {
		List<TimeSeriesDescription> descList = getRawResponse(timeSeriesUniqueIds).getTimeSeriesDescriptions();

		if(descList.size() != timeSeriesUniqueIds.size()) {
			String errorString = "Failed to fetch descriptions for all requested Time Series Identifiers: \nRequested: " + timeSeriesUniqueIds.size() + 
				"\nReceived: "  + descList.size();
			LOG.error(errorString);
			throw new AquariusProcessingException(errorString);
		}
		return descList;
	}

	public TimeSeriesDescription getTimeSeriesDescription(String timeSeriesUniqueId) {
		return getTimeSeriesDescriptionList(Arrays.asList(timeSeriesUniqueId)).get(0);
	}
}
