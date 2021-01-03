package housing;


import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

public class Roof_simple extends Roof {

	Roof_simple(WB_Polygon _outline, float _buildingheight) {
		super(_outline, _buildingheight);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void display(PApplet app, WB_Render3D hpp) {
		app.fill(150);
		app.pushMatrix();
		app.translate(0, 0, buildingheight);
		hpp.drawPolygon(outline);
		app.popMatrix();
	}
}
