package gov.usgs.aqcu.builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

import gov.usgs.aqcu.calc.LastValidVisitCalculator;
import gov.usgs.aqcu.model.AssociatedIvQualifier;
import gov.usgs.aqcu.model.MinMaxData;
import gov.usgs.aqcu.model.MinMaxPoint;
import gov.usgs.aqcu.model.SVPReportMetadata;
import gov.usgs.aqcu.model.SVPReportReading;
import gov.usgs.aqcu.model.SiteVisitPeakReport;
import gov.usgs.aqcu.parameter.SiteVisitPeakRequestParameters;
import gov.usgs.aqcu.retrieval.FieldVisitDataService;
import gov.usgs.aqcu.retrieval.FieldVisitDescriptionListService;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.QualifierLookupService;
import gov.usgs.aqcu.retrieval.TimeSeriesDataCorrectedService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;
import gov.usgs.aqcu.util.BigDecimalSummaryStatistics;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;
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
	private QualifierLookupService qualifierLookupService;

	@Autowired
	public SiteVisitPeakReportBuilderService(
			TimeSeriesDescriptionListService timeSeriesDescriptionListService,
			FieldVisitDescriptionListService fieldVisitDescriptionListService,
			FieldVisitDataService fieldVisitDataService,
			LocationDescriptionListService locationDescriptionListService,
			TimeSeriesDataCorrectedService timeSeriesDataCorrectedService,
			QualifierLookupService qualifierLookupService) {
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
		this.fieldVisitDescriptionListService = fieldVisitDescriptionListService;
		this.fieldVisitDataService = fieldVisitDataService;
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDataCorrectedService = timeSeriesDataCorrectedService;
		this.qualifierLookupService = qualifierLookupService;
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
		
		for (SVPReportReading reading : readings) {
			if (null != reading.getLastVisitPrior()) {
				TimeSeriesDataServiceResponse timeSeriesDataServiceResponse = timeSeriesDataCorrectedService.getRawResponse(requestParameters.getPrimaryTimeseriesIdentifier(), reading.getLastVisitPrior(), reading.getVisitTime());
				ArrayList<Qualifier> qualifiers = timeSeriesDataServiceResponse.getQualifiers();
				Map<String, QualifierMetadata> qualifierMetdata = qualifierLookupService.getByQualifierList(qualifiers);
				for (Qualifier qualifier : qualifiers) {
					QualifierMetadata qualifierMetadata = qualifierMetdata.get(qualifier.getIdentifier());
					AssociatedIvQualifier associatedIvQualifier = new AssociatedIvQualifier(qualifierMetadata);
					reading.getAssociatedIvQualifiers().add(associatedIvQualifier);
				}
				MinMaxData minMaxData = getMinMaxData(timeSeriesDataServiceResponse.getPoints());
				MinMaxPoint minMaxPoint = minMaxData.getMax().get(0);
				reading.setAssociatedIvTime(minMaxPoint.getTime());
				reading.setAssociatedIvValue(minMaxPoint.getValue().toPlainString());
			}
		}
		
		report.setReadings(readings);
		report.setReportMetadata(reportMetadata);
		return report;
	}

	/**
	 * This method should only be called if the timeSeriesPoints list is not null.
	 */
	protected MinMaxData getMinMaxData(List<TimeSeriesPoint> timeSeriesPoints) {
		Map<BigDecimal, List<MinMaxPoint>> minMaxPoints = timeSeriesPoints.parallelStream()
				.map(x -> {
					MinMaxPoint point = new MinMaxPoint(x.getTimestamp().getDateTimeOffset(), DoubleWithDisplayUtil.getRoundedValue(x.getValue()));
					return point;
				})
				.filter(x -> x.getValue() != null)
				.collect(Collectors.groupingByConcurrent(MinMaxPoint::getValue));

		BigDecimalSummaryStatistics stats = minMaxPoints.keySet().parallelStream()
				.collect(BigDecimalSummaryStatistics::new,
						BigDecimalSummaryStatistics::accept,
						BigDecimalSummaryStatistics::combine);

		return new MinMaxData(stats.getMin(), stats.getMax(), minMaxPoints);
	}
}