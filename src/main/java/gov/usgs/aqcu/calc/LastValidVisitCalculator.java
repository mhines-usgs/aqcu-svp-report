package gov.usgs.aqcu.calc;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.usgs.aqcu.model.SVPReportReading;

//TODO needs testing
public class LastValidVisitCalculator {
	Map<String, Instant> lastVisitMap = new HashMap<>();

	public List<SVPReportReading> fill(List<SVPReportReading> readings) {
		Collections.sort(readings, new Comparator<SVPReportReading>() {
			@Override
			public int compare(SVPReportReading reading1, SVPReportReading reading2) {
				if(reading1 == null && reading2 == null) {
					return 0;
				}
				
				if(reading1 == null && reading2 != null) {
					return -1;
				}
				
				if(reading1 != null && reading2 == null) {
					return 1;
				}
				
				return reading1.getVisitTime().compareTo(reading2.getVisitTime());
			}
		});
		
		for(SVPReportReading reading : readings) {
			String value = reading.getValue();
			String method = reading.getMonitoringMethod();
			
			if(value != null && (value.matches("[-+]?\\d*\\.?\\d+") || "no mark".equals(value.toLowerCase().trim())) && method != null) {
				Instant previousDate = lastVisitMap.get(method);
				
				if(previousDate != null) {
					reading.setLastVisitPrior(previousDate);
				}
				
				lastVisitMap.put(method, reading.getVisitTime());
			}
		}
		
		return readings;
	}
}
