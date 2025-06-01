package report.sup;

import report.sup.RColumn;
import common.eProp;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JTable;
import dataset.Field;
import dataset.Record;
import dataset.Table;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;

//�������������� ��������� � HTML </p>
public class RTable {

    private static String charset = "windows-1251";
    private static int npp = 0;

    public static void load(String title, JTable table) {
        String str = String.join("",
                "<HTML><META http-equiv=Content-Type content='text/html; charset=" + charset + "'>",
                "<HEAD>",
                "<STYLE>",
                "   TABLE{ border: none; border-collapse:collapse;}",
                "   CAPTION{ color: #0000FF; font-size: 18px}",
                "   TH {border: 0.5pt solid black;}",
                "   TD {border: 0.5pt solid black;}",
                "   TD.TDFC {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDFR {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDC  {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDCB {height: 20px; border-left: none;  border-right: none;}",
                " </STYLE>",
                "</HEAD>",
                "<BODY>",
                "  <TABLE BORDER=1 CELLSPACING=0 CELLPADDING=1>",
                "  <CAPTION><br>" + title + "<br><br></CAPTION>");

        //������� �������� ��������� ��������
        for (int index = 0; index < table.getColumnCount(); index++) {
            String colName = table.getColumnName(index);
            str += "<TH>" + colName + "</TH>";
        }
        //������� !!!
        for (int row = 0; row < table.getRowCount(); row++) {
            str += "<TR>";//������ ������
            //������ �������, ������������� ������
            Object obj = table.getValueAt(row, 0);
            String str2 = (obj == null) ? "" : obj.toString();
            str += "<TD class=TDFC>" + str2 + "</TD>";
            //��������� �������
            for (int col = 1; col < table.getColumnCount(); col++) {
                obj = table.getValueAt(row, col);
                str2 = (obj == null) ? "" : obj.toString();
                str += "<TD class=TDC>" + str2 + "</TD>";
            }
            str += "</TR>";//����� ������
        }
        str += "</TABLE></BODY>";

        write(str);
    }

    public static void load(String title, Table table, ArrayList<RColumn> listRColumn) {
        String str = String.join("",
                "<HTML><META http-equiv=Content-Type content='text/html; charset=" + charset + "'>",
                "<HEAD>",
                " <STYLE>",
                "   TABLE{ border: none; border-collapse:collapse;}",
                "   CAPTION{ color: #0000FF; font-size: 18px}",
                "   TH {border: 0.5pt solid black;}",
                "   TD {border: 0.5pt solid black;}",
                "   TD.TDFC {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDFR {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDC  {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDCB {height: 20px; border-left: none;  border-right: none;}",
                " </STYLE>",
                "</HEAD>",
                "<BODY>",
                "  <TABLE BORDER=1 CELLSPACING=0 CELLPADDING=1>",
                "  <CAPTION><br>" + title + "<br><br></CAPTION>",
                "  <col width='250' valign='top'>");

        //������� �������� ��������� ��������
        str += "<TH>���</TH>";
        for (RColumn rColumn : listRColumn) {
            str += "<TH>" + rColumn + "</TH>";
        }
        //������� !!!
        npp = 0;
        for (int indexMaster = 0; indexMaster < table.size(); indexMaster++) {
            str += "<TR>";//������ ������     

            //����� �� �������
            String nppReport = table.get(indexMaster).get(0).equals("") ? "" : String.valueOf(++npp);
            str += "<TD class=TDFC>" + nppReport + "</TD>";

            //������ �������, ������������� ������           
            RColumn firsColumn = listRColumn.get(0);
            Field firstField = firsColumn.fields().get(0);
            str += "<TD class=TDFC>" + table.getAs(indexMaster, firstField);
            for (int indexField = 1; indexField < firsColumn.fields().size(); indexField++) {
                Field field = firsColumn.fields().get(indexField);
                str += "<br>" + table.getAs(indexMaster, field);
            }
            str += "</TD>";

            //��������� �������
            for (int indexNode = 1; indexNode < listRColumn.size(); indexNode++) {
                RColumn nextColumn = listRColumn.get(indexNode);
                Field nextField = nextColumn.fields().get(0);
                str += "<TD class=TDC>" + table.getAs(indexMaster, nextField);
                for (int indexField = 1; indexField < nextColumn.fields().size(); indexField++) {
                    Field field = nextColumn.fields().get(indexField);
                    str += "<br>" + table.getAs(indexMaster, field);
                }
                str += "</TD>";
            }
            str += "</TR>";//����� ������
        }
        str += "</TABLE></BODY>";
        write(str);
    }

    public static void load(String title, Table table, Field... fields) {
        String str = String.join("",
                "<HTML><META http-equiv=Content-Type content='text/html; charset=" + charset + "'>",
                "<HEAD>",
                " <STYLE>",
                "   TABLE{ border: none; border-collapse:collapse;}",
                "   CAPTION{ color: #0000FF; font-size: 18px}",
                "   TH {border: 0.5pt solid black;}",
                "   TD {border: 0.5pt solid black;}",
                "   TD.TDFC {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDFR {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDC  {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDCB {height: 20px; border-left: none;  border-right: none;}",
                " </STYLE>",
                "</HEAD>",
                "<BODY>",
                "  <TABLE BORDER=1 CELLSPACING=0 CELLPADDING=1>",
                "  <CAPTION><br>" + title + "<br><br></CAPTION>");

        //������� �������� ��������� ��������
        for (int index = 0; index < fields.length; index++) {
            String colName = fields[index].meta().descr;
            str += "<TH>" + colName + "</TH>";
        }
        //������� !!!
        for (Record record : table) {
            str += "<TR>";//������ ������
            //������ �������, ������������� ������
            Object obj = record.get(fields[0]);
            String str2 = (obj == null) ? "" : obj.toString();
            str += "<TD class=TDFC>" + str2 + "</TD>";
            //��������� �������
            for (Field field : fields) {
                obj = record.get(field);
                str2 = (obj == null) ? "" : obj.toString();
                str += "<TD class=TDC>" + str2 + "</TD>";
            }
            str += "</TR>";//����� ������
        }
        str += "</TABLE></BODY>";

        write(str);
    }

    public static void load(String title, JTable table1, JTable table2) {
        String str = String.join("",
                "<HTML><META http-equiv=Content-Type content='text/html; charset=" + charset + "'>",
                "<HEAD>",
                "<STYLE>",
                "   TABLE{ border: none; border-collapse:collapse;}",
                "   CAPTION{ color: #0000FF; font-size: 18px}",
                "   TH {border: 0.5pt solid black;}",
                "   TD {border: 0.5pt solid black;}",
                "   TD.TDFC {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDFR {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDC  {background-color: #FFFFFF; border: 0.5pt solid black;}",
                "   TD.TDCB {height: 20px; border-left: none;  border-right: none;}",
                " </STYLE>",
                "</HEAD>",
                "<BODY>",
                "  <TABLE BORDER=1 CELLSPACING=0 CELLPADDING=1>",
                "  <CAPTION><br>" + title + "<br><br></CAPTION>",
                "  <col width='250' valign='top'>");

        //������� �������� ��������� ��������
        int indexTitle = 0;
        for (indexTitle = 0; indexTitle < table1.getColumnCount(); indexTitle++) {
            String colName = table1.getColumnName(indexTitle);
            str += "<TH>" + colName + "</TH>";
        }
        for (indexTitle = 0; indexTitle < table2.getColumnCount(); indexTitle++) {
            String colName = table2.getColumnName(indexTitle);
            str += "<TH>" + colName + "</TH>";
        }

        //������� !!!
        for (int row = 0; row < table1.getRowCount(); row++) {
            str += "<TR>";//������ ������
            //������ �������, ������������� ������
            Object obj = table1.getValueAt(row, 0);
            String str2 = (obj == null) ? "" : obj.toString();
            str += "<TD class=TDFC>" + str2 + "</TD>";
            //��������� �������
            for (int col = 1; col < table1.getColumnCount(); col++) {
                obj = table1.getValueAt(row, col);
                str2 = (obj == null) ? "" : obj.toString();
                str += "<TD class=TDC>" + str2 + "</TD>";
            }
            for (int col = 0; col < table2.getColumnCount(); col++) {
                obj = table2.getValueAt(row, col);
                str2 = (obj == null) ? "" : obj.toString();
                str2 = str2.replace('.', ',');
                str += "<TD class=TDC>" + str2 + "</TD>";
            }

            str += "</TR>";//����� ������
        }
        str += "</TABLE></BODY>";

        write(str);
    }

    //�������� ����� � ����
    public static void write(String text) {
        try {
            File directory = new File(eProp.genl.getProp());
            if (directory.exists() == false) {
                directory.mkdirs();
            }
            File file = new File(directory, "report.html");
            PrintWriter out = new PrintWriter(file, "windows-1251");
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "*��� ������� � �����. ������� �� ����� �������� ������ � �����, ��� ��� ���� ���� ����� ������ ���������.", "��������!", 1);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
