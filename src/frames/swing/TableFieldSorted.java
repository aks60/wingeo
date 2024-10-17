package frames.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <P>
 * —ортировка таблиц и поиск в таблицах </p>
 * ¬ этом классе предусмотрена сортировка не только по ывбранным пол€м одной
 * таблицы, но и по ссылкам на пол€ других таблиц
 */
public class TableFieldSorted { //implements Comparator<ArrayList> {

    private static int ret = 0;

    /**
     * —ортировка таблицы
     *
     * @param rsTable - таблица которую надо отсортировать
     * @param rsSorted - первый объект строки rsTable, а остальные пол€
     * сортировки с учЄтом ссылок на другие таблицы
     */
    public static void sortedSet(List rsTable, ArrayList<ArrayList> rsSorted) {

        if (rsTable.isEmpty() == false) {
            //сортируем rsSorted использу€ свой компаратор
            Collections.sort(rsSorted, new Comparator() {

                public int compare(Object o1, Object o2) {

                    ArrayList list1 = (ArrayList) o1;
                    ArrayList list2 = (ArrayList) o2;
                    //цикл по всей строке, начина€ с index = 1 т.к. в 0 recordTable
                    for (int index = 1; index < list1.size(); index++) {

                        o1 = list1.get(index);
                        o2 = list2.get(index);

                        if (o1 == null && o2 == null) {
                            return 0;
                        } else if (o1 == null && o2 != null) {
                            return -1;
                        } else if (o1 != null && o2 == null) {
                            return 1;
                        } else {
                            //окончательное решение на сравнение
                            if (o1 instanceof Integer) {
                                ret = ((Integer) o1).compareTo((Integer) o2);
                            } else if (o1 instanceof Double) {
                                ret = ((Double) o1).compareTo((Double) o2);
                            } else if (o1 instanceof String) {
                                ret = ((String) o1).compareTo((String) o2);
                            } else {
                                ret = 0;
                            }
                            if (ret != 0) {
                                return ret;
                            }
                        }
                    }
                    return ret;
                }
            });
            //теперь перезапишем сортировку из rsSorted в rsTale
            for (int index = 0; index < rsTable.size(); index++) {
                rsTable.set(index, (List) rsSorted.get(index).get(0));
            }
        }
    }

    /**
     *  омпаратор дл€ целых чисе
     */
    public static int compareInt(Object v1, Object v2) {
        if (!(v1 instanceof Integer)) {
            v1 = null;
        }
        if (!(v2 instanceof Integer)) {
            v2 = null;
        }
        if (v1 == null) {
            if (v2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (v2 == null) {
            return 1;
        } else {
            return ((Integer) v1).compareTo((Integer) v2);
        }
    }
}
