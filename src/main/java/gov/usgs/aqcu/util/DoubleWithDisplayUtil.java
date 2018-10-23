package gov.usgs.aqcu.util;

import java.math.BigDecimal;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;

public abstract class DoubleWithDisplayUtil {

	public static final String GAP_MARKER_POINT_VALUE = "EMPTY";

	public static BigDecimal getRoundedValue(DoubleWithDisplay referenceVal) {
		BigDecimal ret;

		if (referenceVal != null) {
			String tmp = referenceVal.getDisplay();
			if (tmp == null || tmp.equals(GAP_MARKER_POINT_VALUE)) {
				// Should be null but just in case.
				ret = referenceVal.getNumeric() == null ? null : BigDecimal.valueOf(referenceVal.getNumeric());
			} else {
				ret = new BigDecimal(tmp);
			}
		} else {
			ret = null;
		}
		return ret;
	}
}