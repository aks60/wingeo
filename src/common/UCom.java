package common;

import builder.IElem5e;
import dataset.Field;
import dataset.Query;
import domain.eColor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class UCom {

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
            } else if (scale == 12) {
                df.applyPattern("#0.00");
            } else if (scale == 19) {
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
        return bd.floatValue();
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

    //1;79-10;0-10 => [1,1,79,10,0,10]
    public static Integer[] parserInt(String txt) {
        if (txt.isEmpty()) {
            return new Integer[]{};
        }
        ArrayList<Object> arrList = new ArrayList();
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
            System.out.println("Ошибка:UCom.parserInt() " + e);
            arrList = new ArrayList(List.of(-1, -1));
        }
        return arrList.stream().toArray(Integer[]::new);
    }

    //0.55;79,01-10;0-10 => [0.55,0.55,79.01,10.0,0.0,10.0]
    public static Double[] parserFloat(String str) {
        if (str.isEmpty()) {
            return new Double[]{};
        }
        ArrayList<Object> arrList = new ArrayList();
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
            System.out.println("Ошибка:UCom.parserFloat() " + e);
            arrList = new ArrayList(List.of(-1, -1));
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
            ArrayList<Integer> arrList = new ArrayList();
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
            System.out.println("Ошибка:UCom.containsColor() " + e);
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
            ArrayList<Double> arrList = new ArrayList();
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
            System.out.println("Ошибка:UCom.containsNumbJust() " + e);
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
            ArrayList<Double> arrList = new ArrayList();
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
            System.out.println("Ошибка:UCom.containsNumbExp() " + e);
        }
        return false;
    }

    //"288-488/1028,01-1128", "2000,2-3000/0-1250@", "55;/*"
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
                if (containsNumbJust(arr[0], val1) == true && containsNumbJust(arr[1], val2) == true) {
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка:UCom.containsNumb() " + e);
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
            System.out.println("Ошибка:UCom.containsNumbAny() " + e);
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
            System.out.println("Ошибка:UCom.containsStr() " + e);
        }
        return false;
    }

    //"Стойка 100;Стойка 200/*", "Slidors 60;@/Slidors 60;@"
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
            System.out.println("Ошибка:UCom.containsStr() " + e);
        }
        return false;
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
}
