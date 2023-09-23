package builder.model2.xlam;

import java.util.ArrayList;


//http://forums.balancer.ru/tech/forum/2005/03/t32285--zadacha-o-prinadlezhnosti-tochki-figure.html
public class L2Area {

    protected class Point {

        protected double x, y;

        Point(double _x, double _y) {
            x = _x;
            y = _y;
        }
    }
    private ArrayList<Point> _points;

    L2Area() {
        _points = new ArrayList<Point>();
    }

    public void add(double x, double y) {
        _points.add(new Point(x, y));
    }

    public boolean isIntersect(double x, double y, Point p1, Point p2) {
        double dy1 = p1.y - y;
        double dy2 = p2.y - y;
        if (Math.signum(dy1) == Math.signum(dy2)) {
            return false;
        }
        double dx1 = p1.x - x;
        double dx2 = p2.x - x;

        if (dx1 >= 0 && dx2 >= 0) {
            return true;
        }
        if (dx1 < 0 && dx2 < 0) {
            return false;
        }
        double dx0 = dy1 * (p1.x - p2.x) / (p1.y - p2.y);
        return dx0 <= dx1;
    }   

    public boolean isInside(double x, double y) {
        int intersect_count = 0;
        for (int i = 0; i < _points.size(); i++) {
            Point p1 = _points.get(i > 0 ? i - 1 : _points.size() - 1);
            Point p2 = _points.get(i);
            
            System.out.println("(" + p1.x + "," + p1.y + ")-(" + p2.x + "," + p2.y + ") => " + isIntersect(x, y, p1, p2));
            
            if (isIntersect(x, y, p1, p2)) {
                intersect_count++;
            }
        }

        return intersect_count % 2 == 1;
    }     

    public static void main(String[] args) {
        L2Area a = new L2Area();
        a.add(0,0);
        a.add(1000,0);
        a.add(0,1000);
        System.out.println(a.isInside(499, 500));
    }
}
/*
//https://qastack.ru/programming/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon
int pnpoly(int nvert, double *vertx, double *verty, double testx, double testy)
{
  int i, j, c = 0;
  for (i = 0, j = nvert-1; i < nvert; j = i++) {
    if ( ((verty[i]>testy) != (verty[j]>testy)) &&
     (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
       c = !c;
  }
  return c;
}

//https://ru.wikibooks.org/wiki/%D0%A0%D0%B5%D0%B0%D0%BB%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8_%D0%B0%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC%D0%BE%D0%B2/%D0%97%D0%B0%D0%B4%D0%B0%D1%87%D0%B0_%D0%BE_%D0%BF%D1%80%D0%B8%D0%BD%D0%B0%D0%B4%D0%BB%D0%B5%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D0%B8_%D1%82%D0%BE%D1%87%D0%BA%D0%B8_%D0%BC%D0%BD%D0%BE%D0%B3%D0%BE%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA%D1%83
  int pnpoly(int npol, double * xp, double * yp, double x, double y)
  {
    int c = 0;
    for (int i = 0, j = npol - 1; i < npol; j = i++) 
    {
      if ((
        (yp[i] < yp[j]) && (yp[i] <= y) && (y <= yp[j]) &&
        ((yp[j] - yp[i]) * (x - xp[i]) > (xp[j] - xp[i]) * (y - yp[i]))
      ) || (
        (yp[i] > yp[j]) && (yp[j] <= y) && (y <= yp[i]) &&
        ((yp[j] - yp[i]) * (x - xp[i]) < (xp[j] - xp[i]) * (y - yp[i]))
      ))
        c = !c;
    }
    return c;
  }

//https://ru.stackoverflow.com/questions/464787/%D0%A2%D0%BE%D1%87%D0%BA%D0%B0-%D0%B2%D0%BD%D1%83%D1%82%D1%80%D0%B8-%D0%BC%D0%BD%D0%BE%D0%B3%D0%BE%D1%83%D0%B3%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA%D0%B0
bool result = false;
int j = size - 1;
for (int i = 0; i < size; i++) {
    if ( (p[i].Y < point.Y && p[j].Y >= point.Y || p[j].Y < point.Y && p[i].Y >= point.Y) &&
         (p[i].X + (point.Y - p[i].Y) / (p[j].Y - p[i].Y) * (p[j].X - p[i].X) < point.X) )
        result = !result;
    j = i;
}
*/