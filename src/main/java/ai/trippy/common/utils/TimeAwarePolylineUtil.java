package ai.trippy.common.utils;

import java.util.ArrayList;
import java.util.List;

import ai.trippy.common.models.TimedLatLng;

/**
 * Ported from: https://github.com/gridwise/time-aware-polyline-js
 */

public class TimeAwarePolylineUtil {

	public static String encodeTimeAwarePolyline(List<TimedLatLng> points) {
		return extendTimeAwarePolyline("", points, null);
	}

	public static List<TimedLatLng> decodeTimeAwarePolyline(String polyline) {

		// Method to decode a time aware polyline and return timedLatLngs
		List<TimedLatLng> points = new ArrayList<>();
		long index = 0;
		long lat = 0;
		long lng = 0;
		long timeStamp = 0;
		int polylineLine = polyline.length();

		while (index < polylineLine) {

			// Decoding dimensions one by one
			long[] latResult = getDecodedDimensionFromPolyline(polyline, index);
			index = latResult[0];
			long[] lngResult = getDecodedDimensionFromPolyline(polyline, index);
			index = lngResult[0];
			long[] timeResult = getDecodedDimensionFromPolyline(polyline, index);
			index = timeResult[0];

			// Resultant variables
			lat += latResult[1];
			lng += lngResult[1];
			timeStamp += timeResult[1];

			points.add(toTimedLatLng(lat, lng, timeStamp));
		}

		return points;
	}

	public static String extendTimeAwarePolyline(String polyline, List<TimedLatLng> points, TimedLatLng lastPoint) {

		if (polyline == null) {
			polyline = "";
		}

		StringBuilder polylineBuilder = new StringBuilder(polyline);

		long lastLat = 0;
		long lastLng = 0;
		long lastTimeStamp = 0;

		if (lastPoint != null) {
			lastLat = getLat(lastPoint);
			lastLng = getLng(lastPoint);
			lastTimeStamp = lastPoint.getTimestamp();
		}

		if (points.isEmpty()) {
			return polylineBuilder.toString();
		}

		for (int i = 0; i < points.size(); i++) {

			TimedLatLng currentPoint = points.get(i);
			long lat = getLat(currentPoint);
			long lng = getLng(currentPoint);
			long timeStamp = currentPoint.getTimestamp();

			long[] diffArray = new long[] { lat - lastLat, lng - lastLng, timeStamp - lastTimeStamp };

			for (int j = 0; j < diffArray.length; j++) {
				long currentDiff = diffArray[j];
				currentDiff = (currentDiff < 0) ? notOperator(lshiftOperator(currentDiff, 1))
						: lshiftOperator(currentDiff, 1);

				while (currentDiff >= 0x20) {
					polylineBuilder.append(fromCharCode((int) (0x20 | (currentDiff & 0x1f)) + 63));
					currentDiff = rshiftOperator(currentDiff, 5);
				}

				polylineBuilder.append(fromCharCode(currentDiff + 63));
			}

			lastLat = lat;
			lastLng = lng;
			lastTimeStamp = timeStamp;
		}

		return polylineBuilder.toString();
	}

	private static long[] getDecodedDimensionFromPolyline(String polyline, long index) {

		// Method to decode one dimension of the polyline
		long result = 1;
		int shift = 0;

		while (true) {
			long b = Character.codePointAt(polyline, (int) index) - 63 - 1;
			index++;
			result += lshiftOperator(b, shift);
			shift += 5;

			if (b < 0x1f) {
				break;
			}
		}

		if ((result % 2) != 0) {
			return new long[] { index, rshiftOperator(notOperator(result), 1) };
		}

		return new long[] { index, rshiftOperator(result, 1) };
	}

	private static double getCoordinate(long longRepresentation) {
		return longRepresentation * 0.00001;
	}

	private static TimedLatLng toTimedLatLng(long lat, long lng, long timeStamp) {
		return new TimedLatLng(getCoordinate(lat), getCoordinate(lng), timeStamp);
	}

	private static long notOperator(long lshiftOperator) {
		return ~lshiftOperator;
	}

	private static String fromCharCode(long codePoints) {
		return new String(new int[] { (int) codePoints }, 0, 1);
	}

	private static long getLat(TimedLatLng point) {
		return Math.round(point.getLat() * 100000);
	}

	private static long getLng(TimedLatLng point) {
		return Math.round(point.getLng() * 100000);
	}

	private static long lshiftOperator(long num, int bits) {
		return num << bits;
	}

	private static long rshiftOperator(long num, int bits) {
		return num >> bits;
	}

}