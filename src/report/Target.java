package report;

import builder.model.AreaStvorka;
import common.UCom;
import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.SpcRecord;
import dataset.Query;
import dataset.Record;
import domain.eSysfurn;
import domain.eFurniture;
import domain.eArtikl;
import domain.eColor;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.eSysuser;
import enums.Type;
import enums.TypeArt;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import report.sup.RSpecific;

//������� � ���
public class Target {

    public void parseDoc(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Target.html");
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
            System.err.println("������:Target.parseDoc() " + e);
        }
    }

    private static void loadDoc(Record projectRec, Document doc) {
        Double square = 0.0; //�������
        try {
            Query qPrjprod = new Query(ePrjprod.values()).sql(ePrjprod.data(), ePrjprod.project_id, projectRec.getInt(eProject.id));
            Query qPrjpart = new Query(ePrjpart.values()).sql(ePrjpart.data(), ePrjpart.id, projectRec.getInt(eProject.prjpart_id));
            qPrjpart.add(ePrjpart.up.newRecord("SEL"));
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, qPrjpart.get(0).getInt(ePrjpart.login));
            qSysuser.add(eSysuser.up.newRecord("SEL"));

            doc.getElementById("h02").text("����� �" + projectRec.getStr(eProject.num_ord) + " �� '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //������� �1 ��������
            String ord = qPrjpart.get(0).getStr(ePrjpart.partner); //��������
            String kon = (qPrjpart.get(0).getInt(ePrjpart.flag2) == 1) //����. ����
                    ? qPrjpart.get(0).getStr(ePrjpart.org_contact)
                    : qSysuser.get(0).getStr(eSysuser.fio);
            Element tab1 = doc.getElementById("tab1");
            tab1.getElementsByTag("tr").get(0).getElementsByTag("td").get(1).text(ord);
            tab1.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(kon);

            //�������� ���� ��������� �������
            Element div2 = doc.getElementById("div2");
            String templateBody = div2.html();
            List<Wincalc> wincList = URep.wincList(qPrjprod, 400);
            for (int i = 1; i < qPrjprod.size(); i++) {
                div2.append(templateBody);
            }

            Elements tab2List = doc.getElementsByClass("tab2"),
                    tab3List = doc.getElementsByClass("tab3"), tab4List = doc.getElementsByClass("tab4"),
                    tab5List = doc.getElementsByClass("tab5"), tab6List = doc.getElementsByClass("tab6");
            String  template3Tr = tab3List.get(0).getElementsByTag("tr").get(1).html(),
                    template4Tr = tab4List.get(0).getElementsByTag("tr").get(1).html(),
                    template5Tr = tab5List.get(0).getElementsByTag("tr").get(1).html(),
                    template6Rec = tab6List.get(0).getElementsByTag("tr").get(1).html();

            //���� �� ��������
            for (int i = 0; i < qPrjprod.size(); i++) {
                Record prjprodRec = qPrjprod.get(i);
                Wincalc winc = wincList.get(i);

                //������� �2 ������� ���������  
                square += winc.root.area.getGeometryN(0).getArea();
                Record artiklRec = winc.root.frames.get(0).artiklRecAn;
                Record colorRec1 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID1).get(0);
                Record colorRec2 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID2).get(0);
                Record colorRec3 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID3).get(0);
                AreaStvorka areaStvorka = (AreaStvorka) UCom.filter(winc.listArea, Type.STVORKA).get(0);
                Record furnitureRec = new Query(eFurniture.values()).sql(eFurniture.data(), eFurniture.id, areaStvorka.sysfurnRec.getInt(eSysfurn.furniture_id)).get(0);

                Element tab2 = tab2List.get(i);
                tab2.getElementsByTag("caption").get(0).getElementsByTag("b").get(0).text("������� � " + (i + 1));
                
                tab2.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(artiklRec.getStr(eArtikl.name));
                tab2.getElementsByTag("tr").get(2).getElementsByTag("td").get(1).text(colorRec1.getStr(eColor.name)
                        + " / " + colorRec2.getStr(eColor.name) + " / " + colorRec3.getStr(eColor.name));
                tab2.getElementsByTag("tr").get(3).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 3)
                        + " x " + UCom.format(winc.height() / 1000, 3));
                tab2.getElementsByTag("tr").get(4).getElementsByTag("td").get(1).text(furnitureRec.getStr(eFurniture.name));
                tab2.getElementsByTag("tr").get(5).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.num));
                tab2.getElementsByTag("tr").get(6).getElementsByTag("td").get(1).text(UCom.format(square / 1000000, 2));

                //������� �3 ������� / �����������  
                Element tab3 = tab3List.get(i);
                List<RSpecific> spcList3 = new ArrayList<RSpecific>(), spcList3a = new ArrayList<RSpecific>();
                loadTab3(winc, tab3, template3Tr, spcList3, spcList3a); //������������ ��� ������� 
                spcList3.forEach(act -> tab3.append(template3Tr));
                tab3.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList3.size(); j++) { //�������� ������ 
                    Elements td = tab3.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RSpecific rs = spcList3.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.artikl());
                    td.get(2).text(rs.name());
                    td.get(3).text(rs.width());
                    td.get(4).text(rs.ang0());
                    td.get(5).text(rs.ang1());
                    td.get(6).text(rs.count());
                    td.get(7).text(rs.anglHor());
                    td.get(8).text("");
                    td.get(9).text(rs.width());
                    td.get(10).text(rs.artikl());
                }

                //������� �4 ����������� 
                Element tab4 = tab4List.get(i);
                List<RSpecific> spcList4 = loadTab4(winc, tab4, template4Tr);
                spcList4.forEach(act -> tab4.append(template4Tr));
                tab4.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList4.size(); j++) { //�������� ������ 
                    Elements td = tab4.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RSpecific rs = spcList4.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.artikl());
                    td.get(2).text(rs.name());
                    td.get(3).text("��");
                    td.get(4).text(rs.width());
                }

                //������� �5 ������  
                Element tab5 = tab5List.get(i);
                List<RSpecific> spcList5 = loadTab5(winc, tab5, template5Tr);
                spcList5.forEach(act -> tab5.append(template5Tr));
                tab5.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList5.size(); j++) { //�������� ������ 
                    Elements td = tab5.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RSpecific rs = spcList5.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.artikl());
                    td.get(2).text(rs.name());
                    td.get(3).text(rs.width());
                    td.get(4).text(rs.count());
                    td.get(5).text(rs.ang0());
                    td.get(6).text(rs.ang1());
                }

                //������� �6 ����������  
                Element tab6 = tab6List.get(i);
                List<RSpecific> spcList6 = loadTab6(winc, tab6, template6Rec);
                spcList6.forEach(act -> tab6.append(template6Rec));
                tab6.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList6.size(); j++) { //�������� ������ 
                    Elements td = tab6.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RSpecific rs = spcList6.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.name());
                    td.get(2).text(rs.width());
                    td.get(3).text(rs.height());
                    td.get(4).text(rs.weight());
                    td.get(5).text(rs.count());
                    td.get(6).text(rs.weight());
                }
            }

            //�������� �����������
            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }

        } catch (Exception e) {
            System.err.println("������:Target.loadDoc() " + e);
        }
    }

    //������� / �����������
    public static void loadTab3(Wincalc winc, Element tab, String templateRec, List<RSpecific> spcList2, List<RSpecific> spcList3) {

        winc.listSpec.forEach(spcRec -> { //�������
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(new RSpecific(spcRec));
            }
        });
        spcList2.forEach(spcRec1 -> { //�����������
            SpcRecord spcRec3 = new SpcRecord();
            for (SpcRecord spcRec2 : winc.listSpec) {
                if (TypeArt.isType(spcRec2.artiklRec(), TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id()) {
                    spcRec3 = spcRec2;
                }
            }
            spcList3.add(new RSpecific(spcRec3));
        });
    }

    //�����������
    public static List<RSpecific> loadTab4(Wincalc winc, Element tab, String templateRec) {

        List<RSpecific> spcList = new ArrayList<RSpecific>();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X135) == true) {
                spcList.add(new RSpecific(spcRec));
            }
        });
        return spcList;
    }

    //������
    public static List<RSpecific> loadTab5(Wincalc winc, Element tab, String templateRec) {

        List<RSpecific> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X108) == true) {
                spcList.add(new RSpecific(spcRec));
            }
        });
        return spcList;
    }

    //����������
    public static List<RSpecific> loadTab6(Wincalc winc, Element tab, String templateRec) {

        List<RSpecific> spcList = new ArrayList<RSpecific>();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X502) == true) {
                spcList.add(new RSpecific(spcRec));
            }
        });
        return spcList;
    }
}
