package com.sinergise.wkt.reader;

import static com.sinergise.factory.GeomteryFactory.createGeometryString;
import static com.sinergise.factory.GeomteryFactory.createLineString;
import static com.sinergise.factory.GeomteryFactory.createMultiLineString;
import static com.sinergise.factory.GeomteryFactory.createMultiPointString;
import static com.sinergise.factory.GeomteryFactory.createMultiPolygonString;
import static com.sinergise.factory.GeomteryFactory.createPolygonString;
import static com.sinergise.factory.GeomteryFactory.getPreciseCoordinate;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

public class WKTReader {

	private static final String EMPTY = "EMPTY";
	private static final String COMMA = ",";
	private static final String L_PAREN = "(";
	private static final String R_PAREN = ")";

	/**
	 * Transforms the input WKT-formatted String into Geometry object
	 */
	public Geometry read(String wktString) throws IOException, ParseException {
		//TODO: Implement this
    //wktString = "GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))";
		//wktString = "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10),(20 30, 35 35, 30 20, 20 30),(20 30, 35 35, 30 20, 20 30))";
    //wktString = "POINT (4 5)";
		//new GeometryCollection<Geometry>(new Geometry[]{new Point(4,6), new LineString(new double[] {4,6,7,10})})
    //wktString = "MULTIPOINT (10 40, 40 30, 20 20, 30 10)";
		wktString = "MULTIPOLYGON (((30 20, 45 40, 10 40, 30 20)), ((15 5, 40 10, 10 20, 5 10, 15 5)))";
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(wktString));
		setUpTokenizer(tokenizer);

		return readGeometryTaggedText(tokenizer);
	}

	private Geometry readGeometryTaggedText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String type = getNextWord(tokenizer);
		if (type.equals("POINT")) {
			return readPointText(tokenizer);
		}  else if (type.equalsIgnoreCase("LINESTRING")) {
			return readLineStringText(tokenizer);
		} else if (type.equalsIgnoreCase("POLYGON")) {
			return readPolygonText(tokenizer);
		} else if (type.equalsIgnoreCase("MULTIPOINT")) {
			return readMultiPointText(tokenizer);
		} else if (type.equalsIgnoreCase("MULTIPOLYGON")) {
			return readMultiPolygonText(tokenizer);
		} else if (type.equalsIgnoreCase("MULTILINESTRING")) {
			return readMultiLineStringText(tokenizer);
		}else if (type.equalsIgnoreCase("GEOMETRYCOLLECTION")) {
			return readGeometryCollectionText(tokenizer);
		}
		throw new ParseException("Unknown geometry type: " + type, tokenizer.lineno());
	}


	private Point readPointText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return new Point(0,0);
		}
		Point point = getPreciseCoordinate(tokenizer);
		getNextCloser(tokenizer);
		return point;
	}

	private Geometry readLineStringText(StreamTokenizer tokenizer) throws IOException, ParseException {
		List coordList = getCoordinates(tokenizer);
		return createLineString(coordList);
	}

	private Geometry readPolygonText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return null;
		}
		LineString polygon = (LineString) readLineStringText(tokenizer);

		List holes = new ArrayList();
		nextToken = getNextCloserOrComma(tokenizer);
		while (nextToken.equals(COMMA)) {
			LineString holeString = (LineString) readLineStringText(tokenizer);
			holes.add(holeString);
			nextToken = getNextCloserOrComma(tokenizer);
		}
		//create polygon with ring and holes
		return createPolygonString(polygon, holes);
	}

	private Geometry readMultiPointText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return null;
		}
		Point point = getPreciseCoordinate(tokenizer);
		Set elements = new HashSet();
		elements.add(point);
		nextToken = getNextCloserOrComma(tokenizer);
		while (nextToken.equals(COMMA)) {
			point = getPreciseCoordinate(tokenizer);
			elements.add(point);
			nextToken = getNextCloserOrComma(tokenizer);
		}
		return createMultiPointString(elements);
	}

	private Geometry readMultiPolygonText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return null;
		}
		Polygon polygon = (Polygon) readPolygonText(tokenizer);
		List polygons = new ArrayList();
		polygons.add(polygon);
		nextToken = getNextCloserOrComma(tokenizer);
		while (nextToken.equals(COMMA)) {
			Polygon poly = (Polygon) readPolygonText(tokenizer);
      polygons.add(poly);
			nextToken = getNextCloserOrComma(tokenizer);
		}
		return createMultiPolygonString(polygons);
	}

	private Geometry readMultiLineStringText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return null;
		}

		LineString lineString = (LineString) readLineStringText(tokenizer);
		//multi.getElements().add(curve);
		Set lineStrings = new HashSet();
		lineStrings.add(lineString);
		nextToken = getNextCloserOrComma(tokenizer);
		while (nextToken.equals(COMMA)) {
			LineString line = (LineString) readLineStringText(tokenizer);
			lineStrings.add(lineString);
			nextToken = getNextCloserOrComma(tokenizer);
		}
		return createMultiLineString(lineStrings);
	}


	private Geometry readGeometryCollectionText(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		if (nextToken.equals(EMPTY)) {
			return null;
		}

		Geometry geom = readGeometryTaggedText(tokenizer);
		//multi.getElements().add(geom);
		Set geometries = new HashSet();
		geometries.add(geom);
		nextToken = getNextCloserOrComma(tokenizer);
		while (nextToken.equals(COMMA)) {
			geom  = readGeometryTaggedText(tokenizer);
			//multi.getElements().add(geom);
			geometries.add(geom);
			nextToken = getNextCloserOrComma(tokenizer);
		}
		return createGeometryString(geometries);
	}


	private String getNextWord(StreamTokenizer tokenizer) throws IOException, ParseException {
		int type = tokenizer.nextToken();
		String value;
		switch (type) {
			case StreamTokenizer.TT_WORD:
				String word = tokenizer.sval;
				if (word.equalsIgnoreCase(EMPTY)) {
					value = EMPTY;
				}
				value = word;
				break;
			case'(':
				value = L_PAREN;
				break;
			case')':
				value = R_PAREN;
				break;
			case',':
				value = COMMA;
				break;
			default:
				value = null;
				break;
		}
		return value;
	}


	private String getNextEmptyOrOpener(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextWord = getNextWord(tokenizer);
		if (nextWord.equals(EMPTY) || nextWord.equals(L_PAREN)) {
			return nextWord;
		}
		return null;
	}


	private String getNextCloser(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextWord = getNextWord(tokenizer);
		if (nextWord.equals(R_PAREN)) {
			return nextWord;
		}
		return null;
	}

	private String getNextCloserOrComma(StreamTokenizer tokenizer) throws IOException, ParseException {
		String nextWord = getNextWord(tokenizer);
		if (nextWord.equals(COMMA) || nextWord.equals(R_PAREN)) {
			return nextWord;
		}
		return null;
	}

	private List getCoordinates(StreamTokenizer tokenizer)
		throws IOException, ParseException {
		String nextToken = getNextEmptyOrOpener(tokenizer);
		List coordinates = new ArrayList();
		if (!nextToken.equals(EMPTY)) {
			coordinates.add(getPreciseCoordinate(tokenizer));
			nextToken = getNextCloserOrComma(tokenizer);
			while (nextToken.equals(COMMA)) {
				coordinates.add(getPreciseCoordinate(tokenizer));
				nextToken = getNextCloserOrComma(tokenizer);
			}
		}
		return coordinates;
	}


	private void setUpTokenizer(StreamTokenizer tokenizer) {
		final int char128 = 128;
		final int skip32 = 32;
		final int char255 = 255;
		// set tokenizer to NOT parse numbers
		tokenizer.resetSyntax();
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars(char128 + skip32, char255);
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('-', '-');
		tokenizer.wordChars('+', '+');
		tokenizer.wordChars('.', '.');
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.commentChar('#');
	}

}
