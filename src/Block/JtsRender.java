package Block;


import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import processing.core.PApplet;

/**
 * Render of JTS datum in Processing 
 * @author by Li Biao & Zhang Baizhou
 * */

public class JtsRender {
	public static GeometryFactory gf = new GeometryFactory();

	PApplet app;

	public JtsRender(PApplet app) {
		this.app = app;
	}

	public void draw(Geometry geo) {
		String type = geo.getGeometryType();
		if (type == "Point") {
			drawPoint(geo);
		} else if (type == "LineString") {
			drawLineString(geo);
		} else if (type == "LinearRing") {
			drawLinearRing(geo);
		} else if (type == "Polygon") {
			drawPolygon(geo);
		} else if (type == "MultiPoint") {
			drawMultiPoint(geo);
		}else if (type == "MultiLineString") {
			drawMultiLineString(geo);
		}else if (type == "MultiPolygon") {
			drawMultiPolygon(geo);
		}else {
			app.println("a new type...");
		}

	}
	
	private void drawPoint(Geometry geo) {
		Point point = (Point) geo;
		app.ellipse((float) point.getX(), (float) point.getY(), 10, 10);
	}//����

	private void drawLineString(Geometry geo) {
		LineString ls = (LineString) geo;
		Coordinate[] vs = ls.getCoordinates();
		app.beginShape();
		for (Coordinate v : vs) {
			app.vertex((float) v.x, (float) v.y);
		} 
		app.endShape(app.OPEN);//����
	}

	private void drawLinearRing(Geometry geo) {
		LinearRing lr = (LinearRing) geo;
		Coordinate[] vs = lr.getCoordinates();
		app.beginShape();
		for (Coordinate v : vs) {
			app.vertex((float) v.x, (float) v.y);
		}
		app.endShape(app.CLOSE);//�պ�
	}

	private void drawPolygon(Geometry geo) {
		Polygon poly = (Polygon) geo;

		app.beginShape();

		LineString shell = poly.getExteriorRing();
		Coordinate[] coord_shell = shell.getCoordinates();
		for (Coordinate c_s : coord_shell) {
			app.vertex((float) c_s.x, (float) c_s.y);
		}

		int interNum = poly.getNumInteriorRing();

		for (int i = 0; i < interNum; i++) {
			LineString in_poly = poly.getInteriorRingN(i);
			Coordinate[] in_coord = in_poly.getCoordinates();
			app.beginContour();
			for (int j = 0; j < in_coord.length; j++) {
				app.vertex((float) in_coord[j].x, (float) in_coord[j].y);
			}
			app.endContour();
		}

		app.endShape();

	}
	
	
	private void drawMultiPoint(Geometry geo) {
		int v_num=geo.getNumGeometries();
		Geometry[] geoms=new Geometry[v_num];
		for(int i=0;i<v_num;i++) {
			geoms[i]=geo.getGeometryN(i);
		}
		for(Geometry geom:geoms) {
			drawPoint(geom);
		}
	}
		
	private void drawMultiLineString(Geometry geo) {
		int v_num=geo.getNumGeometries();
		Geometry[] geoms=new Geometry[v_num];
		for(int i=0;i<v_num;i++) {
			geoms[i]=geo.getGeometryN(i);
		}
		for(Geometry geom:geoms) {
			drawLinearRing(geom);
		}
	}
	
	private void drawMultiPolygon(Geometry geo) {
		int v_num=geo.getNumGeometries();
		Geometry[] geoms=new Geometry[v_num];
		for(int i=0;i<v_num;i++) {
			geoms[i]=geo.getGeometryN(i);
		}
		for(Geometry geom:geoms) {
			drawPolygon(geom);	
		}
	}

	public void drawDelaunayTriangle(ConformingDelaunayTriangulationBuilder delaunayBuilder) {
		Geometry triangles = delaunayBuilder.getTriangles(gf);
		int num = triangles.getNumGeometries();
		for (int i = 0; i < num; i++) {
			this.draw(triangles.getGeometryN(i));
		}
	}

	public void drawVoronoi(VoronoiDiagramBuilder voronoiBuilder) {
		Geometry voronois = voronoiBuilder.getDiagram(gf);
		int num = voronois.getNumGeometries();
		for (int i = 0; i < num; i++) {
			this.draw(voronois.getGeometryN(i));
		}
	}
	
	public PApplet getApp() {
		return this.app;
	}
}
