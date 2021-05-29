package ai.trippy.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ai.trippy.common.models.TimedLatLng;

public class TimeAwarePolylineUtilTest {

	@Test
	public void testEncode() {

		List<TimedLatLng> points = new ArrayList<>();
		points.add(new TimedLatLng(19.13626, 72.92506, 1619102024588L));
		points.add(new TimedLatLng(19.13597, 72.93495, 1619102029534L));
		points.add(new TimedLatLng(19.13753, 72.92469, 1619102033406L));

		String polyline = TimeAwarePolylineUtil.encodeTimeAwarePolyline(points);

		assertEquals("spxsBsdb|Lww``_yf}Ax@y|@ctHwHb_A_qF", polyline);
	}

	@Test
	public void testDecode() {

		List<TimedLatLng> points = TimeAwarePolylineUtil.decodeTimeAwarePolyline("spxsBsdb|Lww``_yf}Ax@y|@ctHwHb_A_qF");

		List<TimedLatLng> expected = new ArrayList<>();
		expected.add(new TimedLatLng(19.13626, 72.92506, 1619102024588L));
		expected.add(new TimedLatLng(19.13597, 72.93495, 1619102029534L));
		expected.add(new TimedLatLng(19.13753, 72.92469, 1619102033406L));

		roundOffLatLngs(points);

		assertEquals(expected, points);
	}

	@Test
	public void testEmpty() {
		String polyline = TimeAwarePolylineUtil.encodeTimeAwarePolyline(new ArrayList<>());
		assertEquals("", polyline);
	}

	@Test
	public void testNoTimestamp() {

		List<TimedLatLng> points = new ArrayList<>();
		points.add(new TimedLatLng(19.13626, 72.92506, 0));
		points.add(new TimedLatLng(19.13597, 72.93495, 0));
		points.add(new TimedLatLng(19.13753, 72.92469, 0));

		String polyline = TimeAwarePolylineUtil.encodeTimeAwarePolyline(points);
		assertEquals("spxsBsdb|L?x@y|@?wHb_A?", polyline);
	}

	private static void roundOffLatLngs(List<TimedLatLng> points) {

		DecimalFormat df = new DecimalFormat("#.#####");
		for (TimedLatLng point : points) {
			point.setLat(Double.valueOf(df.format(point.getLat())));
			point.setLng(Double.valueOf(df.format(point.getLng())));
		}
	}

}