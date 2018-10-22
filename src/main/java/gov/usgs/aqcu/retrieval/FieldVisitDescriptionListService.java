package gov.usgs.aqcu.retrieval;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;

@Repository
public class FieldVisitDescriptionListService {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDescriptionListService.class);

	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public FieldVisitDescriptionListService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public FieldVisitDescriptionListServiceResponse get(String locationIdentifier, Instant startDate, Instant endDate) {
		try {
			FieldVisitDescriptionListServiceRequest request = new FieldVisitDescriptionListServiceRequest()
					.setLocationIdentifier(locationIdentifier)
					.setQueryFrom(startDate)
					.setQueryTo(endDate);
			FieldVisitDescriptionListServiceResponse fieldVisitDescriptionListServiceResponse = aquariusRetrievalService.executePublishApiRequest(request);
			return fieldVisitDescriptionListServiceResponse;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch FieldVisitDescriptionListServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}
}