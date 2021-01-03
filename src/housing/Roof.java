package housing;


import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

public abstract class Roof {
	int num;
	float buildingheight;
	WB_Polygon outline;

	Roof(WB_Polygon _outline, float _buildingheight) {
		outline = _outline;
		buildingheight = _buildingheight;
	}
	public abstract void display(PApplet app,WB_Render3D hpp);
}
