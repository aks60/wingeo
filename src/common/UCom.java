package common;

import builder.making.TRecord;
import builder.making.TTariffic;
import builder.model.Com5t;
import builder.model.ElemJoining;
import builder.model.ElemSimple;
import builder.model.UGeo;
import builder.script.GsonElem;
import dataset.Field;
import dataset.Query;
import domain.eArtikl;
import domain.eColor;
import enums.Layout;
import enums.Type;
import enums.TypeJoin;
import enums.UseUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.algorithm.PointLocation;
import org.locationtech.jts.geom.Coordinate;

public class UCom {

    //private static SimpleDateFormat fd = new SimpleDateFormat("dd.MM.yyyy");
    private static DecimalFormat df = new DecimalFormat();

    public static boolean check(String val, int pattern) {
        try {
            if (val != null && pattern == 2 && "0123456789,;".indexOf(val) != -1) {
                return true;
            } else if (val != null && pattern == 3 && "0123456789,".indexOf(val) != -1) {
                return true;
            } else if (val != null && pattern == 4 && "0123456789;".indexOf(val) != -1) {
                return true;
            } else if (val != null && pattern == 5 && "0123456789-;".indexOf(val) != -1) {
                return true;
            } else if (val != null && pattern == 6 && "0123456789,-;".indexOf(val) != -1) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.check() " + e);
        }
        return false;
    }

    public static String format(Object val, int scale) {
        val = (val == null) ? 0 : val;
        try {
            if (scale == 1) {
                df.applyPattern("#0.#");
            } else if (scale == 2) {
                df.applyPattern("#0.##");
            } else if (scale == 3) {
                df.applyPattern("#0.###");
            } else if (scale == 4) {
                df.applyPattern("#0.####");
            } else if (scale == 9) {
                df.applyPattern("#,##0.##");
            } else if (scale == -1) {
                df.applyPattern("#0.0");
            } else if (scale == -2) {
                df.applyPattern("#0.00");
            } else if (scale == -3) {
                df.applyPattern("#0.000");
            } else if (scale == -9) {
                df.applyPattern("#,##0.00");
            }
            return df.format(val);

        } catch (Exception e) {
            System.err.println("Ошибка:UCom.format() " + e);
            return val.toString();
        }
    }

    public static String format(Object val, String pattern) {
        try {
            df.applyPattern(pattern);
            return df.format(val);

        } catch (Exception e) {
            System.err.println("Ошибка:UCom.format2() " + e);
            return val.toString();
        }
    }

    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean[] getArr(boolean... b) {
        return Arrays.copyOf(b, b.length);
    }

    public static int[] getArr(int... b) {
        return Arrays.copyOf(b, b.length);
    }

    public static Integer getInt(String str) {
        try {
            if (str == null || str.isEmpty()) {
                return 0;
            }
            str = str.replace(".", ",");
            if (str.charAt(str.length() - 1) == ';') {
                str = str.substring(0, str.length() - 1);
            }
            return Integer.valueOf(str);

        } catch (Exception e) {
            System.err.println("Ошибка:UCom.getInt()");
            return 0;
        }
    }

    public static Float getFloat(String str) {

        if (str != null && str.isEmpty() == false) {
            str = str.replace(",", ".");
            try {
                return Float.valueOf(str);

            } catch (java.lang.NumberFormatException e) {
                System.err.println("Ошибка:UCom.getDbl() " + e);
            }
        }
        throw new NumberFormatException("Ошибка:UCom.getDbl(\"" + str + "\")");
    }

    public static Double getDbl(Object obj, Double def) {
        try {
            return getDbl(obj.toString());

        } catch (java.lang.NumberFormatException e) {
            return def;
        }
    }

    public static Double getDbl(String str) {
        if (str != null && str.isEmpty() == false) {
            str = str.replace(",", ".");
            try {
                return Double.valueOf(str);
            } catch (java.lang.NumberFormatException e) {
                System.err.println("Ошибка:UCom.getDbl() " + e);
            }
        }
        throw new NumberFormatException("Ошибка:UCom.getDbl(\"" + str + "\")");
    }

    public static String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";//или return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static int max(Query query, Field field) {
        return query.stream().max((a, b) -> {
            if (a.getInt(field) > b.getInt(field)) {
                return 1;
            } else if (a.getInt(field) < b.getInt(field)) {
                return -1;
            } else {
                return 0;
            }
        }).get().getInt(field);
    }

    public static int scaleFont(double scale) {
        if (scale > .44) {
            return 50;
        } else if (scale > .24) {
            return 55;
        } else if (scale > .18) {
            return 62;
        } else {
            return 64;
        }
    }

    public static <E extends Com5t> ArrayList<E> filter(ArrayList<E> lst, Type... type) {
        List tp = List.of(type);
        ArrayList<E> list2 = new ArrayList<E>();
        for (E el : lst) {
            if (tp.contains(el.type)) {
                list2.add(el);
            }
        }
        return list2;
    }

    public static ArrayList<Com5t> filterNo(ArrayList<Com5t> lst, Type... type) {
        List tp = List.of(type);
        ArrayList<Com5t> list2 = new ArrayList<Com5t>();
        for (Com5t el : lst) {
            if (tp.contains(el.type) == false) {
                list2.add(el);
            }
        }
        return list2;
    }

    public static <E extends Com5t> E layout(ArrayList<E> lst, Layout layout) {
        try {
            E elemFrame = lst.stream().filter(e -> e.layout() == layout).findFirst().get();
            return elemFrame;

        } catch (Exception e) {
            System.err.println("Ошибка:UCom.layout()");
        }
        return null;
    }

    public static <E extends Com5t> GsonElem gson(ArrayList<E> list, final double ID) {
        GsonElem gson = list.stream().filter(g -> g.id == ID).findFirst().get().gson;
        return gson;
    }

    //1;79-10;0-10 => [1,1,79,10,0,10]
    public static Integer[] parserInt(String txt) {
        if (txt.isEmpty()) {
            return new Integer[]{};
        }
        ArrayList<Object> arrList = new ArrayList<Object>();
        try {
            txt = (txt.charAt(txt.length() - 1) == '@') ? txt.substring(0, txt.length() - 1) : txt;
            String[] arr = txt.split(";");
            if (arr.length == 1) {
                arr = arr[0].split("-");
                if (arr.length == 1) {
                    arrList.add(Integer.valueOf(arr[0]));
                    arrList.add(Integer.valueOf(arr[0]));
                } else {
                    arrList.add(Integer.valueOf(arr[0]));
                    arrList.add(Integer.valueOf(arr[1]));
                }
            } else {
                for (int index = 0; index < arr.length; index++) {
                    String[] arr2 = arr[index].split("-");
                    if (arr2.length == 1) {
                        arrList.add(Integer.valueOf(arr2[0]));
                        arrList.add(Integer.valueOf(arr2[0]));
                    } else {
                        arrList.add(Integer.valueOf(arr2[0]));
                        arrList.add(Integer.valueOf(arr2[1]));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.parserInt() " + e);
            arrList = new ArrayList<Object>(List.of(-1, -1));
        }
        return arrList.stream().toArray(Integer[]::new);
    }

    //0.55;79,01-10;0-10 => [0.55,0.55,79.01,10.0,0.0,10.0]
    public static Double[] parserFloat(String str) {
        if (str.isEmpty()) {
            return new Double[]{};
        }
        ArrayList<Object> arrList = new ArrayList<Object>();
        try {
            str = str.replace(",", ".");
            String[] arr = str.split(";");
            if (arr.length == 1) {
                arr = arr[0].split("-");
                if (arr.length == 1) {
                    arrList.add(Double.valueOf(arr[0]));
                    arrList.add(Double.valueOf(arr[0]));
                } else {
                    arrList.add(Double.valueOf(arr[0]));
                    arrList.add(Double.valueOf(arr[1]));
                }
            } else {
                for (int index = 0; index < arr.length; index++) {
                    String[] arr2 = arr[index].split("-");
                    if (arr2.length == 1) {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[0]));
                    } else {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[1]));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.parserFloat() " + e);
            arrList = new ArrayList<Object>(List.of(-1, -1));
        }
        return arrList.stream().toArray(Double[]::new);
    }

    //"180",  "30-179",  "0-89,99;90, 01-150;180, 01-269, 99;270, 01-359, 99"
    //Если не диапазон, то точный поиск
    public static boolean containsColor(String txt, int value) {
        try {
            if (txt == null || txt.isEmpty() || txt.equals("*")) {
                return true;
            }
            int code = eColor.get(value).getInt(eColor.code);
            ArrayList<Integer> arrList = new ArrayList<Integer>();
            txt = txt.replace(",", ".");
            String[] arr = txt.split(";");
            if (arr.length == 1) {
                arr = arr[0].split("-");
                if (arr.length == 1) { //если не диапазон, то точный поиск
                    arrList.add(Integer.valueOf(arr[0]));
                    arrList.add(Integer.valueOf(arr[0]));
                } else {
                    arrList.add(Integer.valueOf(arr[0]));
                    arrList.add(Integer.valueOf(arr[1]));
                }
            } else {
                for (int index = 0; index < arr.length; index++) {
                    String[] arr2 = arr[index].split("-");
                    if (arr2.length == 1) {
                        arrList.add(Integer.valueOf(arr2[0]));
                        arrList.add(Integer.valueOf(arr2[0]));
                    } else {
                        arrList.add(Integer.valueOf(arr2[0]));
                        arrList.add(Integer.valueOf(arr2[1]));
                    }
                }
            }
            for (int index = 0; index < arrList.size(); ++index) {
                int v1 = arrList.get(index);
                int v2 = arrList.get(++index);
                if (v1 <= code && code <= v2) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsColor() " + e);
        }
        return false;
    }

    //"180",  "30-179",  "0-89,99;90, 01-150;180, 01-269, 99;270, 01-359, 99"
    //Если не диапазон, то точный поиск
    public static boolean containsNumbJust(String txt, Number value) {
        try {
            if (txt == null || txt.isEmpty() || txt.equals("*")) {
                return true;
            }
            ArrayList<Double> arrList = new ArrayList<Double>();
            txt = txt.replace(",", ".");
            String[] arr = txt.split(";");
            if (arr.length == 1) {
                arr = arr[0].split("-");
                if (arr.length == 1) { //если не диапазон, то точный поиск
                    arrList.add(Double.valueOf(arr[0]));
                    arrList.add(Double.valueOf(arr[0]));
                } else {
                    arrList.add(Double.valueOf(arr[0]));
                    arrList.add(Double.valueOf(arr[1]));
                }
            } else {
                for (int index = 0; index < arr.length; index++) {
                    String[] arr2 = arr[index].split("-");
                    if (arr2.length == 1) {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[0]));
                    } else {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[1]));
                    }
                }
            }
            for (int index = 0; index < arrList.size(); ++index) {
                double v1 = arrList.get(index);
                double v2 = arrList.get(++index);
                double v3 = Double.valueOf(value.toString());
                if (v1 <= v3 && v3 <= v2) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsNumbJust() " + e);
        }
        return false;
    }

    //"180",  "30-179",  "0-89,99;90, 01-150;180, 01-269, 99;270, 01-359, 99"
    //Если не диапазон, то поиск с нуля
    public static boolean containsNumbExp(String txt, Number value) {
        try {
            if (txt == null || txt.isEmpty() || txt.equals("*")) {
                return true;
            }
            ArrayList<Double> arrList = new ArrayList<Double>();
            txt = txt.replace(",", ".");
            String[] arr = txt.split(";");
            if (arr.length == 1) {
                arr = arr[0].split("-");
                if (arr.length == 1) { //если не диапазон
                    arrList.add(0.0);   //то поиск с нуля
                    arrList.add(Double.valueOf(arr[0]));
                } else {
                    arrList.add(Double.valueOf(arr[0]));
                    arrList.add(Double.valueOf(arr[1]));
                }
            } else {
                for (int index = 0; index < arr.length; index++) {
                    String[] arr2 = arr[index].split("-");
                    if (arr2.length == 1) {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[0]));
                    } else {
                        arrList.add(Double.valueOf(arr2[0]));
                        arrList.add(Double.valueOf(arr2[1]));
                    }
                }
            }
            for (int index = 0; index < arrList.size(); ++index) {
                double v1 = arrList.get(index);
                double v2 = arrList.get(++index);
                double v3 = Double.valueOf(value.toString());
                if (v1 <= v3 && v3 <= v2) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsNumbExp() " + e);
        }
        return false;
    }

    //"288-488/1028,01-1128",  "2000,2-3000/0-1250@",  "55;/*"
    //TODO необходимо учесть такой вариант -27,5/-27,5 см. 34049
    public static boolean containsNumb(String txt, Number val1, Number val2) {
        try {
            if (txt == null || txt.isEmpty()) {
                return true;
            }
            char symmetry = txt.charAt(txt.length() - 1);
            if (symmetry == '@') {
                txt = txt.substring(0, txt.length() - 1);
            }
            String[] arr = txt.split("/");
            if (symmetry == '@') {
                if (containsNumbJust(arr[0], val1) == true || containsNumbJust(arr[1], val2) == true) {
                    return true;
                }
                if (containsNumbJust(arr[1], val1) == true || containsNumbJust(arr[0], val2) == true) {
                    return true;
                }
            } else {
                if ((arr[0].equals("*") || containsNumbJust(arr[0], val1)) == true
                        && (arr[1].equals("*") || containsNumbJust(arr[1], val2) == true)) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsNumb() " + e);
        }
        return false;
    }

    //"288-488/1028,01-1128", "2000,2-3000/0-1250"
    public static boolean containsNumbAny(String txt, Number val1, Number val2) {
        try {
            if (txt == null || txt.isEmpty()) {
                return true;
            }
            String[] arr = txt.split("/");
            if (containsNumbJust(arr[0], val1) == true || containsNumbJust(arr[1], val2) == true) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsNumbAny() " + e);
        }
        return false;
    }

    //"Стойка 172;Стойка 240;
    public static boolean containsStr(String str, String val) {
        try {
            String[] arr = str.split(";");
            for (String str2 : arr) {
                if (str2.equals(val)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsStr() " + e);
        }
        return false;
    }

    //"Стойка 100;Стойка 200/*",  "Slidors 60;@/Slidors 60;@"
    public static boolean containsStr(String str, String val1, String val2) {
        try {
            if (str == null || str.isEmpty()) {
                return true;
            }
            String[] arr = str.split("/");
            if ((arr[0].equals("*") || containsStr(arr[0], val1) == true)
                    && (arr[1].equals("*") || containsStr(arr[1], val2) == true)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UCom.containsStr() " + e);
        }
        return false;
    }

    /**
     * Получить элемент соединения профилей.
     *
     * @param elem - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - класс описатель соединения
     */
    public static ElemJoining join(ArrayList<ElemJoining> lst, ElemSimple elem, int side) {
        boolean imp = Type.isCross(elem.type);
        try {
            for (ElemJoining join : lst) {
                //Угл.соединение
                if (imp == false && (side == 0 || side == 1)) {
                    if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                        return join;

                    } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                        return join;
                    }
                    //T- соединение левое
                } else if (imp == true && side == 0 && elem.id == join.elem2.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem1.x1(), join.elem1.y1(), join.elem1.x2(), join.elem1.y2());
                    Coordinate point = new Coordinate(elem.x1(), elem.y1());
                    if (PointLocation.isOnLine(point, line)) {
                        return join;
                    }
                    //T- соединение правое
                } else if (imp == true && side == 1 && elem.id == join.elem1.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                    Coordinate point = new Coordinate(elem.x2(), elem.y2());
                    if (PointLocation.isOnLine(point, line)) {
                        return join;
                    }
                    //Прил.соединение
                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    if (elem.type == Type.STVORKA_SIDE && elem.equals(join.elem1)) {
                        return join;
                    } else if (elem.equals(join.elem2)) {
                        return join;
                    }
                }
            }
        } catch (Exception e) {
            String message = "Соединение не найдено для elem.id=" + elem.id + ", side=" + side;
            System.err.println("Ошибка:UCom.join() " + message + " " + e);
        }
        if (side != 2) {
            String message = "Соединение не найдено для elem.id=" + elem.id + ", side=" + side;
            System.err.println("Неудача:ArrayJoin.join() " + message);
        }
        return null;
    }

    /**
     * Получить элемент соединения профилей.
     *
     * @param elem - элемент соединения,
     * @param side - сторона соединения 0-пред.артикул, 1-след.артикл,
     * 2-прилег.артикл
     * @return - элемент соединения
     */
    public static ElemSimple elem(ArrayList<ElemJoining> lst, ElemSimple elem, int side) {
        boolean imp = Type.isCross(elem.type);
        try {
            for (ElemJoining join : lst) {
                //Угл.соединение
                if (imp == false && (side == 0 || side == 1)) {
                    if (side == 0 && elem.x1() == join.elem1.x2() && elem.y1() == join.elem1.y2()) { //0-пред.артикул
                        return join.elem1;

                    } else if (side == 1 && elem.x2() == join.elem2.x1() && elem.y2() == join.elem2.y1()) { //1-след.артикл
                        return join.elem2;
                    }
                    //T- соединение левое
                } else if (imp == true && side == 0 && elem.id == join.elem2.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem1.x1(), join.elem1.y1(), join.elem1.x2(), join.elem1.y2());
                    Coordinate point = new Coordinate(elem.x1(), elem.y1());
                    if (PointLocation.isOnLine(point, line)) {
                        return join.elem1;
                    }
                    //T- соединение правое
                } else if (imp == true && side == 1 && elem.id == join.elem1.id) {
                    Coordinate[] line = UGeo.arrCoord(join.elem2.x1(), join.elem2.y1(), join.elem2.x2(), join.elem2.y2());
                    Coordinate point = new Coordinate(elem.x2(), elem.y2());
                    if (PointLocation.isOnLine(point, line)) {
                        return join.elem2;
                    }
                    //Прил.соединение
                } else if (side == 2 && join.type() == TypeJoin.FLAT) {
                    if (elem.type == Type.STVORKA_SIDE && elem.equals(join.elem1)) {
                        return join.elem2;
                    } else if (elem.equals(join.elem2)) {
                        return join.elem1;
                    }
                }
            }
        } catch (Exception e) {
            String message = "Соединение не найдено для elem.id=" + elem.id + ", side=" + side;
            System.err.println("Ошибка:ArrayJoin.elem() " + message + " " + e);
        }
        if (side != 2) {
            String message = "Соединение не найдено для elem.id=" + elem.id + ", side=" + side;
            System.err.println("Неудача:ArrayJoin.elem() " + message);
        }
        return null;
    }
}
