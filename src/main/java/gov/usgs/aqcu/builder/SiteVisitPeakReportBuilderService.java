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
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

import gov.usgs.aqcu.calc.LastValidVisitCalculator;
import gov.usgs.aqcu.model.AssociatedIvQualifier;
import gov.usgs.aqcu.model.SVPReportMetadata;
import gov.usgs.aqcu.model.SVPReportReading;
import gov.usgs.aqcu.model.SiteVisitPeakReport;
import gov.usgs.aqcu.parameter.SiteVisitPeakRequestParameters;
import gov.usgs.aqcu.retrieval.FieldVisitDataService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionListService;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.TimeSeriesDataCorrectedService;
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
	private LocationDescriptionListService locationDescriptionListService;
	private TimeSeriesDataCorrectedService timeSeriesDataCorrectedService;

	@Autowired
	public SiteVisitPeakReportBuilderService(
			TimeSeriesDescriptionListService timeSeriesDescriptionListService,
			FieldVisitDescriptionListService fieldVisitDescriptionListService,
			FieldVisitDataService fieldVisitDataService,
			LocationDescriptionListService locationDescriptionListService,
			TimeSeriesDataCorrectedService timeSeriesDataCorrectedService) {
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.fieldVisitDescriptionListService = fieldVisitDescriptionListService;
		this.fieldVisitDataService = fieldVisitDataService;
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDataCorrectedService = timeSeriesDataCorrectedService;
	}

	public SiteVisitPeakReport buildReport(SiteVisitPeakRequestParameters requestParameters, String requestingUser) {
		SiteVisitPeakReport report = new SiteVisitPeakReport();
		SVPReportMetadata reportMetadata = new SVPReportMetadata();
		reportMetadata.setTitle(REPORT_TITLE);
		LOG.debug("This looks like a boolean in the JSON but it's not in the request?");
		reportMetadata.setExcludeComments(!requestParameters.getExcludedComments().isEmpty());
		
		List<SVPReportReading> readings = new ArrayList<>();
		
		LOG.debug("Requesting time series");
		TimeSeriesDescription timeSeriesDescription = timeSeriesDescriptionListService.getTimeSeriesDescription(requestParameters.getPrimaryTimeseriesIdentifier());
		reportMetadata.setTimeseriesLabel(timeSeriesDescription.getIdentifier());

		String locationIdentifier = timeSeriesDescription.getLocationIdentifier();
		ZoneOffset zoneOffset = TimeSeriesUtils.getZoneOffset(timeSeriesDescription);
		Instant startInstant = requestParameters.getStartInstant(zoneOffset);
		Instant endInstant = requestParameters.getEndInstant(zoneOffset);
		
		reportMetadata.setStartDate(requestParameters.getStartInstant(ZoneOffset.UTC));
		reportMetadata.setEndDate(requestParameters.getEndInstant(ZoneOffset.UTC));
		reportMetadata.setStationId(locationIdentifier);
		reportMetadata.setStationName(locationDescriptionListService.getByLocationIdentifier(locationIdentifier).getName());

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
		
		//find associated IV values - this attempt killed it
		for (SVPReportReading reading : readings) {
			if (null != reading.getLastVisitPrior()) {
				TimeSeriesDataServiceResponse timeSeriesDataServiceResponse = timeSeriesDataCorrectedService.getRawResponse(requestParameters.getPrimaryTimeseriesIdentifier(), reading.getLastVisitPrior(), reading.getVisitTime());
				for (Qualifier qualifier : timeSeriesDataServiceResponse.getQualifiers()) {
					AssociatedIvQualifier associatedIvQualifier = new AssociatedIvQualifier(qualifier);
					// no idea where code and display name come from
					reading.getAssociatedIvQualifiers().add(associatedIvQualifier);
				}
				
//				need to max/min the points
				timeSeriesDataServiceResponse.getPoints();
//				maxTime ==> reading.setAssociatedIvTime(associatedIvTime);
//				maxVale ==> reading.setAssociatedIvValue(associatedIvValue);
			}
		}
		
		report.setReadings(readings);
		report.setReportMetadata(reportMetadata);
		return report;
	}
}