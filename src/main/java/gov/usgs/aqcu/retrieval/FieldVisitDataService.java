package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;

@Repository
public class FieldVisitDataService {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDataService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public FieldVisitDataService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public FieldVisitDataServiceResponse get(String fieldVisitIdentifier) {
		try {
			FieldVisitDataServiceRequest request = new FieldVisitDataServiceRequest()
					.setFieldVisitIdentifier(fieldVisitIdentifier)
					.setApplyRounding(true);
			FieldVisitDataServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
			return fieldVisitResponse;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch FieldVisitDataServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
}