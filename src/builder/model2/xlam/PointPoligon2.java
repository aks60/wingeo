package builder.model2.xlam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Class Main.
 *
 * Created on: 13.08.2011
 *
 * @author: M128K145
 */
//https://www.cyberforum.ru/java-j2se/thread341527.html
public class PointPoligon2 {

    /**
     * The Class PairDouble.
     *
     * Created on: 13.08.2011
     *
     * @author: M128K145
     */
    public static class PairDouble implements Comparator<PairDouble> {

        /**
         * The first.
         */
        private double first;

        /**
         * The second.
         */
        private double second;

        /**
         * Instantiates a new pair double.
         */
        public PairDouble() {
            this.setFirst(0);
            this.setSecond(0);
        }

        /**
         * Instantiates a new pair double.
         *
         * @param first the first
         * @param second the second
         */
        public PairDouble(double first, double second) {
            this.setFirst(first);
            this.setSecond(second);
        }

        /**
         * Gets the first.
         *
         * @return the first
         */
        public double getFirst() {
            return first;
        }

        /**
         * Sets the first.
         *
         * @param first the first to set
         */
        public void setFirst(double first) {
            this.first = first;
        }

        /**
         * Gets the second.
         *
         * @return the second
         */
        public double getSecond() {
            return second;
        }

        /**
         * Sets the second.
         *
         * @param second the second to set
         */
        public void setSecond(double second) {
            this.second = second;
        }

        /**
         * Compare.
         *
         * @param o1 the o1
         * @param o2 the o2
         * @return the int
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(PairDouble o1, PairDouble o2) {
            double tmp = o1.getFirst() - o2.getSecond();
            return tmp < 0 ? -1 : tmp == 0 ? 0 : 1;
        }

    }

    /**
     * The Class PairDoubleComparator.
     *
     * Created on: 13.08.2011
     *
     * @author: M128K145
     */
    public static class PairDoubleComparator implements Comparator<PairDouble> {

        /**
         * Compare.
         *
         * @param o1 the o1
         * @param o2 the o2
         * @return the int
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(PairDouble o1, PairDouble o2) {
            double tmp = o1.getFirst() - o2.getFirst();
            return tmp < 0 ? -1 : tmp == 0 ? 0 : 1;

        }

    }

    /**
     * Cw.
     *
     * @param a the a
     * @param b the b
     * @param c the c
     * @return true, if successful
     */
    private static boolean cw(final PairDouble a, final PairDouble b, final PairDouble c) {
        return (b.getFirst() - a.getSecond()) * (c.second - a.second)
                - (b.second - a.second) * (c.first - a.first) < 0;
    }

    /**
     * Convex hull.
     *
     * @param p the p
     * @return the list
     */
    private static List<PairDouble> convexHull(List<PairDouble> p) {
        int n = p.size();
        if (n <= 1) {
            return p;
        }
        int k = 0;
        Collections.sort(p, new PairDoubleComparator());
        List<PairDouble> q = new ArrayList<PairDouble>();
        for (int i = 0; i < n; q.add(p.get(i++)), ++k) {
            for (; k >= 2 && !cw(q.get(k - 2), q.get(k - 1), p.get(i)); --k)
            ;
        }
        for (int i = n - 2, t = k; i >= 0; q.add(p.get(i--)), ++k) {
            for (; k > t && !cw(q.get(k - 2), q.get(k - 1), p.get(i)); --k)
            ;
        }
        resize(q, k - 1 - (q.get(0) == q.get(1) ? 1 : 0));
        return q;

    }

    /**
     * Resize.
     *
     * @param <T> the generic type
     * @param list the list
     * @param size the size
     * @return the list
     */
    private static <T> List<T> resize(List<T> list, int size) {
        if (list.size() > size) {
            for (int i = list.size() - 1; i >= size - 1; list.remove(i), --i)
            ;
        } else if (list.size() < size) {
            T temp = list.get(list.size() - 1);
            for (int i = 0, iSize = size - list.size(); i < iSize; list.add(temp), ++i)
            ;
        }
        return list;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        List<PairDouble> points = new ArrayList<PairDouble>();
        
        points.add(0, new PairDouble(3, 0));
        points.add(1, new PairDouble(0, 3));
        points.add(2, new PairDouble(0, 0));
        points.add(3, new PairDouble(1, 1));
        List<PairDouble> hull = convexHull(points);
        for (PairDouble pair : hull) {
            System.out.println(pair.getFirst() + "  " + pair.getSecond());
        }
        System.out.println(00 + "  " + 3.0);
    }
}
