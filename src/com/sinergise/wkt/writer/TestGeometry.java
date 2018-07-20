package com.sinergise.wkt.writer;

import java.io.IOException;
import java.text.ParseException;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.wkt.reader.WKTReader;

/**
 * Created by nayan.kakati on 7/19/17.
 */
public class TestGeometry {

	public static void main(String[] args) throws IOException, ParseException {
		WKTReader reader = new WKTReader();
		System.out.println(reader.read("").isEmpty());

		if (reader.read("") instanceof Geometry) {
			System.out.println("YAHOOOOO------>");
		}
		Geometry geom = reader.read("");
		MultiPolygon lineString = (MultiPolygon) geom;
		System.out.println(lineString.toString());
	}

	public static void shape (int a)  {
		System.out.println(a);
	}

}
