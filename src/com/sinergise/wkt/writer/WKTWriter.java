package com.sinergise.wkt.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;

public class WKTWriter {

	/**
	 * Transforms the input Geometry object into WKT-formatted String. e.g.
	 * <pre><code>
	 * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
	 * //returns "LINESTRING (30 10, 10 30, 40 40)"
	 * </code></pre>
	 */
	public String write(Geometry geom) {

		geom = new GeometryCollection<Geometry>(new Geometry[]{new Point(4, 6), new LineString(new double[]{4, 6, 7, 10})});

		Writer writer = new StringWriter();
		return readGeometry(geom);
	}

	private String readGeometry(Geometry geometry) {
		if (geometry instanceof Point) {
			Point point = (Point) geometry;
			appendPointTaggedText(point.getCoordinate(), 0, writer);
		}
		return null;
	}

	private void appendPointTaggedText(Coordinate coordinate, int level, Writer writer, PrecisionModel precisionModel) throws IOException {
		writer.write("POINT ");
		appendPointText(coordinate, level, writer, precisionModel);
	}
}
