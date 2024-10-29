package report;

import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.SpcRecord;
import dataset.Query;
import dataset.Record;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.eSysuser;
import enums.TypeArt;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//������� � ���
public class Manufactory {

    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public void parseDoc(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Manufactory.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //�������� �����
            loadDoc(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("������:HtmlOfManufactory.manufactory() " + e);
        }
    }

    private static void loadDoc(Record projectRec, Document doc) {
        try {
            Elements tab2List = doc.getElementsByClass("tab2"),
                    tab3List = doc.getElementsByClass("tab3"), tab4List = doc.getElementsByClass("tab4"),
                    tab5List = doc.getElementsByClass("tab5"), tab6List = doc.getElementsByClass("tab6");
            String template2Rec = tab2List.get(0).getElementsByTag("tr").get(1).html(),
                    template3Rec = tab3List.get(0).getElementsByTag("tr").get(1).html(),
                    template4Rec = tab4List.get(0).getElementsByTag("tr").get(1).html(),
                    template5Rec = tab5List.get(0).getElementsByTag("tr").get(1).html(),
                    template6Rec = tab6List.get(0).getElementsByTag("tr").get(1).html();

            Query qPrjprod = new Query(ePrjprod.values()).sql(ePrjprod.data(), ePrjprod.project_id, projectRec.getInt(eProject.id));
            Query qPrjpart = new Query(ePrjpart.values()).sql(ePrjpart.data(), ePrjpart.id, projectRec.getInt(eProject.prjpart_id));
            qPrjpart.add(ePrjpart.up.newRecord("SEL"));
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, qPrjpart.get(0).getInt(ePrjpart.login));  
            qSysuser.add(eSysuser.up.newRecord("SEL"));
            List<Wincalc> wincList = URep.wincList(qPrjprod, 400);

            doc.getElementById("h02").text("����� �" + projectRec.getStr(eProject.num_ord) + " �� '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            String fio = (qPrjpart.get(0).getInt(ePrjpart.flag1) == 1) 
                    ? qPrjpart.get(0).getStr(ePrjpart.org_contact)
                    : qPrjpart.get(0).getStr(ePrjpart.partner);
            System.out.println(qPrjpart.get(0).getStr(ePrjpart.org_contact));
            System.out.println(qPrjpart.get(0).getStr(ePrjpart.partner));
            
            //������� �1 ��������
            Element tab1 =  doc.getElementById("tab1");
            Elements tdList1 = tab1.getElementsByTag("tr").get(0).getElementsByTag("td");
            tdList1.get(1).text(fio);

            //�������� ���� ��������� �������
            Element div2 = doc.getElementById("div2");
            String templateBody = div2.html();
            for (int i = 1; i < qPrjprod.size(); i++) {
                div2.append(templateBody);
            }

            //���� �� ��������
            for (int i = 0; i < qPrjprod.size(); i++) {

                String script = qPrjprod.get(i).getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specification(true);

                //������� �3 ������� / �����������  
                Element tab3 = tab3List.get(i);
                List<SpcRecord> spcList3 = new ArrayList(), spcList3a = new ArrayList();
                loadTab3(winc, tab3, template3Rec, spcList3, spcList3a); //������������ ��� ������� 
                spcList3.forEach(act -> tab3.append(template3Rec));
                tab3.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList3.size(); j++) { //�������� ������ 
                    Elements tdList3 = tab3.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList3.get(0).text(String.valueOf(j + 1));
                    tdList3.get(1).text(str(spcList3.get(j).artikl));
                    tdList3.get(2).text(str(spcList3.get(j).name));
                    tdList3.get(3).text(str(spcList3.get(j).width));
                    tdList3.get(4).text(str(spcList3.get(j).anglCut0));
                    tdList3.get(5).text(str(spcList3.get(j).anglCut1));
                    tdList3.get(6).text(str(spcList3.get(j).count));
                    tdList3.get(7).text(str(spcList3.get(j).anglHoriz));
                    tdList3.get(8).text("");
                    tdList3.get(9).text(str(spcList3a.get(j).width));
                    tdList3.get(10).text(str(spcList3a.get(j).artikl));
                }

                //������� �4 ����������� 
                Element tab4 = tab4List.get(i);
                List<SpcRecord> spcList4 = loadTab4(winc, tab4, template4Rec);
                spcList4.forEach(act -> tab4.append(template4Rec));
                tab4.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList4.size(); j++) { //�������� ������ 
                    Elements tdList4 = tab4.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList4.get(0).text(String.valueOf(j + 1));
                    tdList4.get(1).text(str(spcList4.get(j).artikl));
                    tdList4.get(2).text(str(spcList4.get(j).name));
                    tdList4.get(3).text(str(spcList4.get(j).unit));
                    tdList4.get(4).text(str(spcList4.get(j).width));
                }

                //������� �5 ������  
                Element tab5 = tab5List.get(i);
                List<SpcRecord> spcList5 = loadTab5(winc, tab5, template5Rec);
                spcList5.forEach(act -> tab5.append(template5Rec));
                tab5.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList5.size(); j++) { //�������� ������ 
                    Elements tdList5 = tab5.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList5.get(0).text(String.valueOf(j + 1));
                    tdList5.get(1).text(str(spcList5.get(j).artikl));
                    tdList5.get(2).text(str(spcList5.get(j).name));
                    tdList5.get(3).text(str(spcList5.get(j).width));
                    tdList5.get(4).text(str(spcList5.get(j).count));
                    tdList5.get(5).text(str(spcList5.get(j).anglCut0));
                    tdList5.get(6).text(str(spcList5.get(j).anglCut1));
                }

                //������� �6 ����������  
                Element tab6 = tab6List.get(i);
                List<SpcRecord> spcList6 = loadTab6(winc, tab6, template6Rec);
                spcList6.forEach(act -> tab6.append(template6Rec));
                tab6.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList6.size(); j++) { //�������� ������ 
                    Elements tdList6 = tab6.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList6.get(0).text(String.valueOf(j + 1));
                    tdList6.get(1).text(str(spcList6.get(j).name));
                    tdList6.get(2).text(str(spcList6.get(j).width));
                    tdList6.get(3).text(str(spcList6.get(j).height));
                    tdList6.get(4).text(str(spcList6.get(j).weight));
                    tdList6.get(5).text(str(spcList6.get(j).count));
                    tdList6.get(6).text(str(spcList6.get(j).weight));
                }

            }

            //�������� �����������
            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }

        } catch (Exception e) {
            System.err.println("������:HtmlOfManufactory.load() " + e);
        }
    }

    //������� / �����������
    public static void loadTab3(Wincalc winc, Element tab, String templateRec, List<SpcRecord> spcList2, List<SpcRecord> spcList3) {

        winc.listSpec.forEach(spcRec -> { //�������
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(new SpcRecord(spcRec));
            }
        });
        spcList2.forEach(spcRec1 -> { //�����������
            SpcRecord spcRec3 = new SpcRecord();
            for (SpcRecord spcRec2 : winc.listSpec) {
                if (TypeArt.isType(spcRec2.artiklRec(), TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id) {
                    spcRec3 = spcRec2;
                }
            }
            spcList3.add(new SpcRecord(spcRec3));
        });
    }

    //�����������
    public static List<SpcRecord> loadTab4(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X135) == true) {
                spcList.add(new SpcRecord(spcRec));
            }
        });
        return spcList;
    }

    //������
    public static List<SpcRecord> loadTab5(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X108) == true) {
                spcList.add(new SpcRecord(spcRec));
            }
        });
        return spcList;
    }

    //����������
    public static List<SpcRecord> loadTab6(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X502) == true) {
                spcList.add(new SpcRecord(spcRec));
            }
        });
        return spcList;
    }

    private static String str(Object txt) {
        if (txt == null) {
            return "";
        } else if (txt instanceof Number) {
            return df2.format(txt);
        }
        return txt.toString();
    }
}
