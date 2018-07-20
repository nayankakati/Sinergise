package com.sinergise.factory;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

/**
 * Created by nayan.kakati on 7/23/17.
 */
public class GeomteryFactory {


	public static LineString createLineString(List coordinates) {
		double[] lineCoord = new double[coordinates.size() * 2];
		int i = 0;
		for ( Object point : coordinates) {
			lineCoord[i++] = ((Point) point).getX();
			lineCoord[i++] = ((Point) point).getY();
		}
		LineString lineString = new LineString(lineCoord);
		return lineString;
	}

	public static Polygon createPolygonString(LineString polygon, List<LineString> holes) {
		LineString[] insideHoles = new LineString[holes.size()];
		int i = 0;
		for (LineString hole : holes) {
			insideHoles[i++] = hole;
		}
		return new Polygon(polygon, insideHoles);
	}

	public static MultiPoint createMultiPointString(Set<Point> points){
		Point[] multiPoint = new Point[points.size()];
		int i = 0;
		for (Point point : points) {
			multiPoint[i++] = point;
		}
		return new MultiPoint(multiPoint);
	}

	public static MultiPolygon createMultiPolygonString(List<Polygon> polygons){
		Polygon[] multiPolygon = new Polygon[polygons.size()];
		int i = 0;
		for (Polygon polygon : polygons) {
			multiPolygon[i++] = polygon;
		}
		return new MultiPolygon(multiPolygon);
	}

	public static MultiLineString createMultiLineString(Set<LineString> lineStrings){
		LineString[] multiLineString = new LineString[lineStrings.size()];
		int i = 0;
		for (LineString line : lineStrings) {
			multiLineString[i++] = line;
		}
		return new MultiLineString(multiLineString);
	}

	public static GeometryCollection createGeometryString(Set<Geometry> geometryCollection){
		Geometry[] geometryCollections = new GeometryCollection[geometryCollection.size()];
		int i = 0;
		for (Geometry geom : geometryCollection) {
			geometryCollections[i++] = geom;
		}
		return new GeometryCollection(geometryCollections);
	}

	public static Point getPreciseCoordinate(StreamTokenizer tokenizer)
		throws IOException, ParseException {
		return new Point(getNextNumber(tokenizer),getNextNumber(tokenizer));
	}

	private static double getNextNumber(StreamTokenizer tokenizer) throws IOException,
		ParseException {
		int type = tokenizer.nextToken();
		switch (type) {
			case StreamTokenizer.TT_WORD: {
				try {
					return Double.parseDouble(tokenizer.sval);
				} catch (NumberFormatException ex) {
					throw new ParseException("Invalid number: " + tokenizer.sval, tokenizer.lineno());
				}
			}
			default:
		}
		return 0.0;
	}
}
