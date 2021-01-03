package housing;

import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;

public abstract class BuildingStyle {
	WB_Polygon outline;
	Roof roof;
	ArrayList<Floor> floors;
	float floorheight = 300;
	int floornum = 1;

	public BuildingStyle(WB_Polygon _outline, float _floorheight, int _floornum) {
		outline = _outline;
		floorheight = _floorheight;
		floornum = _floornum;
	}

	public abstract void display(PApplet app,WB_Render3D hpp );

}
