package housing;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

import java.util.ArrayList;
import java.util.List;

public class Z_Tool {
    //_______________________________________________________________________

    public static int ranint(float left, float right) {
        return Math.round(random(left, right));
    }
    public static float random(float left, float right) {
        return (float) (left + Math.random() * (right - left));
    }
    static public WB_Point getIntersectionPoint2Lines(WB_Point A, WB_Point B, WB_Point C, WB_Point D) {
        if (!IsParallel(A, B, C, D)) {
            WB_Point p = calculateIntersectionPoint(A, B, C, D);
            if (isPointInSegment02(p, A, B) && isPointInSegment02(p, C, D)) {
                return p;
            }
        }
        return null;
    }

    static boolean IsParallel(WB_Point P1, WB_Point P2, WB_Point P3, WB_Point P4) {
        WB_Vector v1 = P2.sub(P1);
        WB_Vector v2 = P4.sub(P3);
        return v1.isParallel(v2);
    }
    static WB_Point calculateIntersectionPoint(WB_Point A, WB_Point B, WB_Point C, WB_Point D) {
        float t1 = calculateVectorProduct(C, D, A, B);
        float t2 = calculateVectorProduct(A, B, A, C);
        float x = C.xf() + (D.xf() - C.xf()) * t2 / t1;
        float y = C.yf() + (D.yf() - C.yf()) * t2 / t1;
        return new WB_Point(x, y);

    }
    static float calculateVectorProduct(WB_Point P1, WB_Point P2, WB_Point P3, WB_Point P4) {

        return (P2.xf() - P1.xf()) * (P4.yf() - P3.yf()) - (P2.yf() - P1.yf()) * (P4.xf() - P3.xf());

    }
    static public boolean isPointInSegment02(WB_Point P1, WB_Point LineStart, WB_Point LineEnd) {

        boolean pointIn = false;
        if (WB_Point.getDistance2D(P1, LineEnd) < 0.05f || WB_Point.getDistance2D(P1, LineStart) < 0.05f) {
            pointIn = true;
        } else {
            WB_Vector n1 = WB_Point.sub(LineEnd, P1);
            WB_Vector n2 = WB_Point.sub(LineStart, P1);
            float a = (float) Math.abs(n1.getAngle(n2));
            if (Math.PI - a <= 0.01f) { // 由于浮点数的精度问题，因此引入误差防止误判。这个浮点数设置多大？
                pointIn = true;
            }
        }

        return pointIn;
    }

    static public boolean IsIntersected_Segments(WB_Point A, WB_Point B, WB_Point C, WB_Point D) {
        if (IsParallel(A, B, C, D)) {
            WB_Point p = calculateIntersectionPoint(A, B, C, D);
            if (isPointInSegment(p, A, B) && isPointInSegment(p, C, D)) {
                return true;
            }
        }
        return false;
    }
    static boolean isPointInSegment(WB_Point P1, WB_Point LineStart, WB_Point LineEnd) {
        return (isBetween(P1.xf(), LineStart.xf(), LineEnd.xf())
                && isBetween(P1.yf(), LineStart.yf(), LineEnd.yf()));
    }
    public static boolean isBetween(float Num, float Num1, float Num2) {
        float deviation = 0.1f; // 由于浮点数的精度问题，因此引入误差防止误判
        return Num >= min(Num1, Num2) - deviation && Num <= max(Num1, Num2) + deviation;
    }
    public static float min(float Num1, float Num2) {
        return Num1 > Num2 ? Num2 : Num1;
    }

    public static float max(float Num1, float Num2) {
        return Num1 > Num2 ? Num1 : Num2;
    }
    /**
     * angle in radian, with reference axis to decide which is negative direction,
     * ranging from -Pi to Pi angle is not measured on the plane of axis. axis just
     * define if it's positive angle or negative angle
     */
    public static double getAngle(WB_Vector v1, WB_Vector v2, WB_Vector axis) {
        double len1 = v1.getLength3D();
        if (len1 == 0)
            return 0;
        double len2 = v2.getLength3D();
        if (len2 == 0)
            return 0;
        double cos = v1.dot(v2) / (len1 * len2);
        WB_Vector cross = v1.cross(v2);
        if (cos > 1.)
            cos = 1;
        else if (cos < -1.)
            cos = -1; // in case of rounding error
        double angle = Math.acos(cos);
        if (cross.dot(axis) < 0)
            return -angle;
        return angle;
    }

    public static List<WB_Point> ArraytoList(WB_Point[] p) {
        List<WB_Point> points = new ArrayList<>();
        for (int i = 0; i < p.length; i++) {
            points.add(p[i]);
        }
        return points;
    }
    //________________________________________________________________________
}
