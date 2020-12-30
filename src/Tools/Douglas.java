package Tools;

import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;


class Douglas {
    List<WB_Coord> pointList;
    List<WB_Coord> out;
    double epsilon;

    public Douglas() {

    }

    public Douglas(WB_Polygon polygon, double epsilon) {
        this.epsilon = epsilon;
        WB_CoordCollection coords = polygon.getPoints();
        pointList = coords.toList();
        sort(pointList);
        pointList.add(pointList.get(0));
        out = new ArrayList<>();
        ramerDouglasPeucker(pointList, epsilon, out);
    }

    private void sort(List<WB_Coord> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            WB_Point a = new WB_Point(list.get(i));
            WB_Point b = new WB_Point(list.get((i + 1) % list.size()));
            WB_Point c = new WB_Point(list.get((i + 2) % list.size()));
            WB_Vector x1 = b.subToVector3D(a);
            WB_Vector x2 = c.subToVector3D(b);
            if (x1.cross(x2).getLength() > 0.1)
                index = (i + 1) % list.size();
        }
        List<WB_Coord> coords = new ArrayList<>();
        if (index != -1) {
            for (int i = index; i < list.size(); i++)
                coords.add(list.get(i));
            for (int i = 0; i < index; i++)
                coords.add(list.get(i));
            list.clear();
            list.addAll(coords);
        }
    }

    public WB_Polygon getTransPolygon() {
        out.remove(out.size() - 1);
        return new WB_Polygon(out);
    }

    private double perpendicularDistance(WB_Coord pt, WB_Coord lineStart, WB_Coord lineEnd) {
        double dx = lineEnd.xd() - lineStart.xd();
        double dy = lineEnd.yd() - lineStart.yd();

        // Normalize
        double mag = Math.hypot(dx, dy);
        if (mag > 0.0) {
            dx /= mag;
            dy /= mag;
        }
        double pvx = pt.xd() - lineStart.xd();
        double pvy = pt.yd() - lineStart.yd();

        // Get dot product (project pv onto normalized direction)
        double pvdot = dx * pvx + dy * pvy;

        // Scale line direction vector and subtract it from pv
        double ax = pvx - pvdot * dx;
        double ay = pvy - pvdot * dy;

        return Math.hypot(ax, ay);
    }

    private void ramerDouglasPeucker(List<WB_Coord> pointList, double epsilon, List<WB_Coord> out) {
        if (pointList.size() < 2)
            throw new IllegalArgumentException("Not enough points to simplify");

        // Find the point with the maximum distance from line between the start and end
        double dmax = 0.0;
        int index = 0;
        int end = pointList.size() - 1;
        for (int i = 1; i < end; ++i) {
            double d = perpendicularDistance(pointList.get(i), pointList.get(0), pointList.get(end));
            if (d > dmax) {
                index = i;
                dmax = d;
            }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            List<WB_Coord> recResults1 = new ArrayList<>();
            List<WB_Coord> recResults2 = new ArrayList<>();
            List<WB_Coord> firstLine = pointList.subList(0, index + 1);
            List<WB_Coord> lastLine = pointList.subList(index, pointList.size());
            ramerDouglasPeucker(firstLine, epsilon, recResults1);
            ramerDouglasPeucker(lastLine, epsilon, recResults2);

            // build the result list
            out.addAll(recResults1.subList(0, recResults1.size() - 1));
            out.addAll(recResults2);
            if (out.size() < 2)
                throw new RuntimeException("Problem assembling output");
        } else {
            // Just return start and end points
            out.clear();
            out.add(pointList.get(0));
            out.add(pointList.get(pointList.size() - 1));
        }
    }

}

