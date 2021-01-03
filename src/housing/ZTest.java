package housing;

import igeo.ICurve;
import igeo.IG;
import igeo.IVecI;
import peasy.PeasyCam;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;

public class ZTest extends PApplet {
	ArrayList<BuildingStyle> bs = new ArrayList<>();
	PeasyCam cam;
	WB_Render3D hpp;

	public static void main(String[] args) {
		PApplet.main("housing.ZTest");
	}

	public  void settings(){

	}

	public void setup() {
		cam = new PeasyCam(this, 300);
		hpp = new WB_Render3D(this);

		IG.init();
		IG.open("E:\\INST.AAA\\Term-1\\codeExample\\Prato1017\\Prato1017\\testroof.3dm");
		ICurve polys[] = IG.curves();
		
		for (ICurve curve : polys) {
			IVecI[] pts = curve.cps();
			ArrayList<WB_Point> ps3 = new ArrayList<>();
			for (int i = 0; i < pts.length - 1; i++) {
				ps3.add(new WB_Point(pts[i].x(), -pts[i].y(), pts[i].z()));
			}
			if (ps3.size() > 5)
				bs.add(new Building_dented(new WB_Polygon(ps3), 18, 1));
			else
				bs.add(new Building_slope(new WB_Polygon(ps3), 10, 1));
		}


	}


	public void draw() {
		background(255);
		for (BuildingStyle b : bs) {
			b.display(this,hpp);
		}
	}

	@Override
	public void keyPressed() {
		if (key == 'z')
			;
		if (key == 'x')
			;
		if (key == 'd' || key == 'D') {
			;
		}
	}
}