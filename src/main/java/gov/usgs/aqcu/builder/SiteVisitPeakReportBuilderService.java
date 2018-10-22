package gov.usgs.aqcu.builder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.calc.LastValidVisitCalculator;
import gov.usgs.aqcu.model.SVPReportMetadata;
import gov.usgs.aqcu.model.SVPReportReading;
import gov.usgs.aqcu.model.SiteVisitPeakReport;
import gov.usgs.aqcu.parameter.SiteVisitPeakRequestParameters;
import gov.usgs.aqcu.retrieval.FieldVisitDataService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionListService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.util.TimeSeriesUtils;

@Service
public class SiteVisitPeakReportBuilderService {
	public static final String REPORT_TITLE = "Site Visit Peak";
	public static final String REPORT_TYPE = "siteVisitPeak";

	private static final Logger LOG = LoggerFactory.getLogger(SiteVisitPeakReportBuilderService.class);

	private TimeSeriesDescriptionListService timeSeriesDescriptionListService;
	private FieldVisitDescriptionListService fieldVisitDescriptionListService;
	private FieldVisitDataService fieldVisitDataService;
	
//	private LocationDescriptionListService locationDescriptionListService;

	@Autowired
	public SiteVisitPeakReportBuilderService(
			TimeSeriesDescriptionListService timeSeriesDescriptionListService,
			FieldVisitDescriptionListService fieldVisitDescriptionListService,
			FieldVisitDataService fieldVisitDataService) {
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.fieldVisitDescriptionListService = fieldVisitDescriptionListService;
		this.fieldVisitDataService = fieldVisitDataService;
	}

	public SiteVisitPeakReport buildReport(SiteVisitPeakRequestParameters requestParameters, String requestingUser) {
		SiteVisitPeakReport report = new SiteVisitPeakReport();
		SVPReportMetadata reportMetadata = new SVPReportMetadata();
		LOG.debug("This looks like a boolean in the JSON but it's not in the request?");
		reportMetadata.setExcludeComments(!requestParameters.getExcludedComments().isEmpty());
		
		List<SVPReportReading> readings = new ArrayList<>();
		
		LOG.debug("Requesting time series");
		TimeSeriesDescription timeSeriesDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(requestParameters.getPrimaryTimeseriesIdentifier());
		reportMetadata.setTimeseriesLabel(timeSeriesDescription.getIdentifier());

		ZoneOffset zoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescription);
		Instant startInstant = requestParameters.getStartInstant(zoneOffset);
		reportMetadata.setStartDate(requestParameters.getStartInstant(ZoneOffset.UTC));
		Instant endInstant = requestParameters.getEndInstant(zoneOffset);
		reportMetadata.setEndDate(requestParameters.getEndInstant(ZoneOffset.UTC));
		String locationIdentifier = timeSeriesDescription.getLocationIdentifier();
		reportMetadata.setStationId(locationIdentifier);
		
		LOG.debug("Requesting field visits for time series location in date range");
		FieldVisitDescriptionListServiceResponse fieldVisitDescriptionListServiceResponse = fieldVisitDescriptionListService.get(locationIdentifier, startInstant, endInstant);
		
		for (FieldVisitDescription fieldVisitDescription : fieldVisitDescriptionListServiceResponse.getFieldVisitDescriptions()) {
			FieldVisitDataServiceResponse fieldVisitDataServiceResponse = fieldVisitDataService.get(fieldVisitDescription.getIdentifier());
			InspectionActivity inspectionActivity = fieldVisitDataServiceResponse.getInspectionActivity();
			for (Reading reading : inspectionActivity.getReadings()) {
				if (ReadingType.ExtremeMax.equals(reading.getReadingType())) {
					LOG.debug("Only want Extreme Max readings");
					readings.add(new SVPReportReading(fieldVisitDescription.getStartTime(), inspectionActivity.getParty(), reading));
				}
			}
		}
		
		LOG.debug("Add lastVisitPrior to each reading, maybe?");
		new LastValidVisitCalculator().fill(readings);
		
		report.setReadings(readings);
		report.setReportMetadata(reportMetadata);
		return report;
	}
}