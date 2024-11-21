package report;

import builder.Kitcalc;
import builder.model.AreaStvorka;
import common.UCom;
import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.TRecord;
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
import report.sup.RRecord;

//Задание в цех
public class RTarget {

    public void parseDoc1(Record prjprodRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Target.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));
            List<Record> prjprodList = new ArrayList();
            prjprodList.add(prjprodRec);

            //Заполним отчёт
            loadDoc(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:Target.parseDoc() " + e);
        }
    }

    public void parseDoc2(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Target.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);
            List<Record> prjprodList = new Query(ePrjprod.values()).sql(ePrjprod.data(), ePrjprod.project_id, projectRec.getInt(eProject.id));
            //List<Record> prjprodList2 = new Query(ePrjprod.values()).sql(ePrjprod.data(), ePrjprod.project_id, projectRec.getInt(eProject.id));

            //Заполним отчёт
            loadDoc(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:Target.parseDoc() " + e);
        }
    }

    private static void loadDoc(Record projectRec, List<Record> prjprodList, Document doc) {
        Double square = 0.0; //площадь
        try {
            Query qPrjpart = new Query(ePrjpart.values()).sql(ePrjpart.data(), ePrjpart.id, projectRec.getInt(eProject.prjpart_id));
            qPrjpart.add(ePrjpart.up.newRecord("SEL"));
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, qPrjpart.get(0).getInt(ePrjpart.login));
            qSysuser.add(eSysuser.up.newRecord("SEL"));

            doc.getElementById("h02").text("Заказ №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //Таблица №1 ЗАКАЗЧИК
            String ord = qPrjpart.get(0).getStr(ePrjpart.partner); //заказчик
            String kon = (qPrjpart.get(0).getInt(ePrjpart.flag2) == 1) //конт. лицо
                    ? qPrjpart.get(0).getStr(ePrjpart.org_contact)
                    : qSysuser.get(0).getStr(eSysuser.fio);
            Element tab1 = doc.getElementById("tab1");
            tab1.getElementsByTag("tr").get(0).getElementsByTag("td").get(1).text(ord);
            tab1.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(kon);

            //Заполним файл шаблонами заказов
            Element div2 = doc.getElementById("div2");
            String templateBody = div2.html();
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);
            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(templateBody);
            }

            Elements tab2List = doc.getElementsByClass("tab2"),
                    tab3List = doc.getElementsByClass("tab3"), tab4List = doc.getElementsByClass("tab4"),
                    tab5List = doc.getElementsByClass("tab5"), tab6List = doc.getElementsByClass("tab6"),
                    tab7List = doc.getElementsByClass("tab7"), tab8List = doc.getElementsByClass("tab8"),
                    tab9List = doc.getElementsByClass("tab9");
            String template3Tr = tab3List.get(0).getElementsByTag("tr").get(1).html(),
                    template4Tr = tab4List.get(0).getElementsByTag("tr").get(1).html(),
                    template5Tr = tab5List.get(0).getElementsByTag("tr").get(1).html(),
                    template6Tr = tab6List.get(0).getElementsByTag("tr").get(1).html(),
                    template7Tr = tab7List.get(0).getElementsByTag("tr").get(1).html(),
                    template8Tr = tab8List.get(0).getElementsByTag("tr").get(1).html(),
                    template9Tr = tab9List.get(0).getElementsByTag("tr").get(1).html();

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {
                Record prjprodRec = prjprodList.get(i);
                Wincalc winc = wincList.get(i);

                List<TRecord> listSpc = new ArrayList<TRecord>();
                listSpc.addAll(winc.listSpec);
                listSpc.addAll(Kitcalc.tarifficProd(prjprodRec, winc, true));

                //Таблица №2 ИЗДЕЛИЕ РЕКВИЗИТЫ  
                square += winc.root.area.getGeometryN(0).getArea();
                Record artiklRec = winc.root.frames.get(0).artiklRecAn;
                Record colorRec1 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID1).get(0);
                Record colorRec2 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID2).get(0);
                Record colorRec3 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID3).get(0);
                AreaStvorka areaStvorka = (AreaStvorka) UCom.filter(winc.listArea, Type.STVORKA).get(0);
                Record furnitureRec = new Query(eFurniture.values()).sql(eFurniture.data(), eFurniture.id, areaStvorka.sysfurnRec.getInt(eSysfurn.furniture_id)).get(0);

                Element tab2 = tab2List.get(i);
                tab2.getElementsByTag("caption").get(0).getElementsByTag("b").get(0).text("Изделие № " + (i + 1));

                tab2.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(artiklRec.getStr(eArtikl.name));
                tab2.getElementsByTag("tr").get(2).getElementsByTag("td").get(1).text(colorRec1.getStr(eColor.name)
                        + " / " + colorRec2.getStr(eColor.name) + " / " + colorRec3.getStr(eColor.name));
                tab2.getElementsByTag("tr").get(3).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 3)
                        + " x " + UCom.format(winc.height() / 1000, 3));
                tab2.getElementsByTag("tr").get(4).getElementsByTag("td").get(1).text(furnitureRec.getStr(eFurniture.name));
                tab2.getElementsByTag("tr").get(5).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.num));
                tab2.getElementsByTag("tr").get(6).getElementsByTag("td").get(1).text(UCom.format(square / 1000000, 2));

                //Таблица №3 ПРОФИЛЬ / АРМИРОВАНИЕ  
                Element tab3 = tab3List.get(i);
                List<TRecord> spcList3 = new ArrayList<TRecord>(), spcList3a = new ArrayList<TRecord>();
                loadTab3(listSpc, tab3, template3Tr, spcList3, spcList3a); //спецификация для изделия 
                spcList3.forEach(act -> tab3.append(template3Tr));
                tab3.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList3.size(); j++) { //заполним строки 
                    Elements td = tab3.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RRecord rs1 = new RRecord(spcList3.get(j));
                    RRecord rs2 = new RRecord(spcList3a.get(j));
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs1.artikl());
                    td.get(2).text(rs1.name());
                    td.get(3).text(rs1.width());
                    td.get(4).text(rs1.ang0());
                    td.get(5).text(rs1.ang1());
                    td.get(6).text(rs1.count());
                    td.get(7).text(rs1.anglHor());
                    td.get(8).text("");
                    td.get(9).text(rs2.width());
                    td.get(10).text(rs2.artikl());
                }

                //Таблица №4 УПЛОТНИТЕЛИ 
                Element tab4 = tab4List.get(i);
                List<RRecord> spcList4 = loadTab4(listSpc, tab4, template4Tr);
                spcList4.forEach(act -> tab4.append(template4Tr));
                tab4.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList4.size(); j++) { //заполним строки 
                    Elements td = tab4.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RRecord rs = spcList4.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.artikl());
                    td.get(2).text(rs.name());
                    td.get(3).text("мм");
                    td.get(4).text(rs.width());
                }

                //Таблица №5 ШТАПИК  
                Element tab5 = tab5List.get(i);
                List<RRecord> spcList5 = loadTab5(listSpc, tab5, template5Tr);
                spcList5.forEach(act -> tab5.append(template5Tr));
                tab5.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList5.size(); j++) { //заполним строки 
                    Elements td = tab5.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RRecord rs = spcList5.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.artikl());
                    td.get(2).text(rs.name());
                    td.get(3).text(rs.width());
                    td.get(4).text(rs.count());
                    td.get(5).text(rs.ang0());
                    td.get(6).text(rs.ang1());
                }

                //Таблица №6 АКСЕССУАРЫ  
                Element tab6 = tab5List.get(i);
                List<RRecord> spcList6 = loadTab6(listSpc, tab6, template5Tr);
                spcList6.forEach(act -> tab5.append(template6Tr));
                tab6.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList5.size(); j++) { //заполним строки 

                }

                //Таблица №9 ЗАПОЛНЕНИЯ  
                Element tab9 = tab9List.get(i);
                List<RRecord> spcList9 = loadTab9(listSpc, tab9, template9Tr);
                spcList9.forEach(act -> tab9.append(template9Tr));
                tab9.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList9.size(); j++) { //заполним строки 
                    Elements td = tab9.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    RRecord rs = spcList9.get(j);
                    td.get(0).text(String.valueOf(j + 1));
                    td.get(1).text(rs.name());
                    td.get(2).text(rs.width());
                    td.get(3).text(rs.height());
                    td.get(4).text(rs.weight());
                    td.get(5).text(rs.count());
                    td.get(6).text(rs.weight());
                }
            }

            //Загрузим изображения
            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Target.loadDoc() " + e);
        }
    }

    //ПРОФИЛЬ / АРМИРОВАНИЕ
    public static void loadTab3(List<TRecord> listSpec, Element tab, String templateRec, List<TRecord> spcList2, List<TRecord> spcList3) {

        listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(spcRec);
            }
        });
        spcList2.forEach(spcRec1 -> { //армирование
            int count = spcList3.size();
            for (TRecord spcRec2 : listSpec) {
                if (TypeArt.isType(spcRec2.artiklRec(), TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id) {
                    spcList3.add(spcRec2);
                }
            }
            if (count == spcList3.size()) {
                spcList3.add(new TRecord());
            }
        });
        listSpec.removeAll(spcList2);
        listSpec.removeAll(spcList3);
    }

    //УПЛОТНИТЕЛИ
    public static List<RRecord> loadTab4(List<TRecord> listSpec, Element tab, String templateRec) {

        List<RRecord> spcList = new ArrayList<RRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X135) == true) {
                spcList.add(new RRecord(spcRec));
            }
        });
        listSpec.removeAll(spcList);
        return spcList;
    }

    //ШТАПИК
    public static List<RRecord> loadTab5(List<TRecord> listSpec, Element tab, String templateRec) {

        List<RRecord> spcList = new ArrayList();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X108) == true) {
                spcList.add(new RRecord(spcRec));
            }
        });
        listSpec.removeAll(spcList);
        return spcList;
    }

    //АКСЕССУАРЫ
    public static List<RRecord> loadTab6(List<TRecord> listSpec, Element tab, String templateRec) {

        List<RRecord> spcList = new ArrayList();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X200) == true) {
                spcList.add(new RRecord(spcRec));
            }
        });
        listSpec.removeAll(spcList);
        return spcList;
    }

    //ЗАПОЛНЕНИЯ
    public static List<RRecord> loadTab9(List<TRecord> listSpec, Element tab, String templateRec) {

        List<RRecord> spcList = new ArrayList<RRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X502) == true) {
                spcList.add(new RRecord(spcRec));
            }
        });
        listSpec.removeAll(spcList);
        return spcList;
    }
}
