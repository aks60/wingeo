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

    public void parseDoc(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Target.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

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
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, qPrjpart.get(0).getStr(ePrjpart.login));
            qSysuser.add(eSysuser.up.newRecord("SEL")); //если qSysuser.size() == 0

            //СЕКЦИЯ №1
            {
                doc.getElementById("h02").text("Заказ №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

                //Таблица №1 ЗАКАЗЧИК
                String ord = qPrjpart.get(0).getStr(ePrjpart.partner); //заказчик
                String kon = (qPrjpart.get(0).getInt(ePrjpart.flag2) == 1) //конт. лицо
                        ? qPrjpart.get(0).getStr(ePrjpart.org_contact)
                        : qSysuser.get(0).getStr(eSysuser.fio);
                Element tab1 = doc.getElementById("tab1");
                tab1.getElementsByTag("tr").get(0).getElementsByTag("td").get(1).text(ord);
                tab1.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(kon);
            }

            //СЕКЦИЯ № 2,3,4,5,6,7,8,9
            {
                //Заполним файл шаблонами заказов
                Element div2 = doc.getElementById("div2");
                String templateBody = div2.html();
                List<Wincalc> wincList = URep.wincList(prjprodList, 400);
                for (int i = 1; i < prjprodList.size(); i++) {
                    div2.append(templateBody);
                }

                Elements tab2List = doc.getElementsByClass("tab2"), tab3List = doc.getElementsByClass("tab3"),
                        tab4List = doc.getElementsByClass("tab4"), tab5List = doc.getElementsByClass("tab5"),
                        tab6List = doc.getElementsByClass("tab6"), tab7List = doc.getElementsByClass("tab7"),
                        tab8List = doc.getElementsByClass("tab8"), tab9List = doc.getElementsByClass("tab9");
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
                    int countNum = prjprodRec.getInt(ePrjprod.num);

                    List<TRecord> listSpc = new ArrayList<TRecord>();
                    listSpc.addAll(winc.listSpec);
                    listSpc.addAll(Kitcalc.tarifficProd(winc, prjprodRec, 0, true, true));

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
                    tab2.getElementsByTag("tr").get(2).getElementsByTag("td").get(1)
                            .text(colorRec1.getStr(eColor.name) + " / " + colorRec2.getStr(eColor.name) + " / " + colorRec3.getStr(eColor.name));
                    tab2.getElementsByTag("tr").get(3).getElementsByTag("td").get(1)
                            .text(UCom.format(winc.width() / 1000, 3) + " x " + UCom.format(winc.height() / 1000, 3));
                    tab2.getElementsByTag("tr").get(4).getElementsByTag("td").get(1).text(furnitureRec.getStr(eFurniture.name));
                    tab2.getElementsByTag("tr").get(5).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.num));
                    tab2.getElementsByTag("tr").get(6).getElementsByTag("td").get(1).text(UCom.format(square / 1000000, 2));

                    //Таблица №3 ПРОФИЛЬ / АРМИРОВАНИЕ  
                    Element tab3 = tab3List.get(i);
                    loadTab3(listSpc, tab3, template3Tr, countNum); //спецификация для изделия 

                    //Таблица №4 УПЛОТНИТЕЛИ 
                    Element tab4 = tab4List.get(i);
                    loadTab4(listSpc, tab4, template4Tr, countNum);

                    //Таблица №5 ШТАПИК  
                    Element tab5 = tab5List.get(i);
                    loadTab5(listSpc, tab5, template5Tr, countNum);

                    //Таблица №6 АКСЕССУАРЫ  
                    Element tab6 = tab6List.get(i);
                    loadTab6(listSpc, tab6, template6Tr, countNum);

                    //Таблица №7 ФУРНИТУРА   
                    Element tab7 = tab7List.get(i);
                    loadTab7(listSpc, tab7, template7Tr, countNum);

                    //Таблица №8 ЗАПОЛНЕНИЯ    
                    Element tab8 = tab8List.get(i);
                    loadTab8(listSpc, tab8, template8Tr, countNum);

                    //Таблица №9 ПРОЧЕЕ 
                    Element tab9 = tab9List.get(i);
                    loadTab9(listSpc, tab9, template9Tr, countNum);
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
    public static void loadTab3(List<TRecord> listSpec, Element tab, String templateTr, int numProd) {
        List<TRecord> spcListA = new ArrayList<TRecord>();
        List<TRecord> spcListB = new ArrayList<TRecord>();

        for (TRecord spcRecA : listSpec) { //профиля
            if (TypeArt.isType(spcRecA.artiklRec, TypeArt.X101, TypeArt.X102, TypeArt.X103) == true) {

                spcListA.add(spcRecA);
                int count = spcListB.size();
                for (TRecord spcRecB : listSpec) {
                    
                    if (spcRecB.elem5e != null) {
                        if (TypeArt.isType(spcRecB.artiklRec, TypeArt.X107) == true && spcRecB.elem5e.id == spcRecA.id) {
                            spcListB.add(spcRecB);
                        }
                    }
                }
                if (count == spcListB.size()) { //если добаления не было приписываем пустой хвост
                    spcListB.add(new TRecord());
                }
            } else if (TypeArt.isType(spcRecA.artiklRec, TypeArt.X104, TypeArt.X105) == true) {
                spcListA.add(spcRecA);
                spcListB.add(new TRecord());
            }
        }

        listSpec.removeAll(spcListA);
        listSpec.removeAll(spcListB);

        spcListA.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcListA.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rsA = new RRecord(spcListA.get(j));
            RRecord rsB = new RRecord(spcListB.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rsA.artikl());
            td.get(2).text(rsA.name());
            td.get(3).text(rsA.width());
            td.get(4).text(rsA.ang0());
            td.get(5).text(rsA.ang1());
            td.get(6).text(String.valueOf(rsA.spc().count * numProd));
            td.get(7).text(rsA.anglHor());
            td.get(8).text("");
            td.get(9).text(rsB.width());
            td.get(10).text(rsB.artikl());
        }
    }

    //УПЛОТНИТЕЛИ
    public static void loadTab4(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X135) == true) {
                spcList.add(spcRec);
            }
        });
        listSpec.removeAll(spcList);
        spcList.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.artikl());
            td.get(2).text(rs.name());
            td.get(3).text(rs.unit());
            td.get(4).text(String.valueOf(rs.spc().count * countNum));
            td.get(5).text(rs.width());
        }
    }

    //ШТАПИК
    public static void loadTab5(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X108) == true) {
                spcList.add(spcRec);
            }
        });

        listSpec.removeAll(spcList);
        spcList.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.artikl());
            td.get(2).text(rs.name());
            td.get(3).text(rs.width());
            td.get(4).text(String.valueOf(rs.spc().count * countNum));
            td.get(5).text(rs.ang0());
            td.get(6).text(rs.ang1());
        }
    }

    //АКСЕССУАРЫ
    public static void loadTab6(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X201, TypeArt.X202, TypeArt.X203,
                    TypeArt.X204, TypeArt.X205, TypeArt.X206, TypeArt.X215, TypeArt.X220,
                    TypeArt.X230, TypeArt.X231, TypeArt.X250, TypeArt.X290) == true) {

                spcList.add(spcRec);
            }
        });

        listSpec.removeAll(spcList);

        List<TRecord> spcList2 = RRecord.groups4T(spcList);
        spcList2.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList2.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList2.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.artikl());
            td.get(2).text(rs.name());
            td.get(3).text(String.valueOf(rs.spc().count * countNum));
        }
    }

    //ФУРНИТУРА 
    public static void loadTab7(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {
        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X209, TypeArt.X210,
                    TypeArt.X211, TypeArt.X212, TypeArt.X213, TypeArt.X214) == true) {

                spcList.add(spcRec);
            }
        });

        listSpec.removeAll(spcList);
        List<TRecord> spcList2 = RRecord.groups4T(spcList);
        spcList2.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList2.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList2.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.artikl());
            td.get(2).text(rs.name());
            td.get(3).text(rs.unit());
            td.get(4).text(String.valueOf(rs.spc().count * countNum));
        }
    }

    //ЗАПОЛНЕНИЯ
    public static void loadTab8(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec, TypeArt.X502) == true) {
                spcList.add(spcRec);
            }
        });

        listSpec.removeAll(spcList);
        spcList.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.name());
            td.get(2).text(rs.width());
            td.get(3).text(rs.height());
            td.get(4).text(UCom.format((rs.spc().width * rs.spc().height) / 1000000, 2));
            td.get(5).text(String.valueOf(rs.spc().count * countNum));
            td.get(6).text(UCom.format((rs.spc().width * rs.spc().height) / 1000000, 2));
        }
    }

    //ПРОЧЕЕ
    public static void loadTab9(List<TRecord> listSpec, Element tab, String templateTr, int countNum) {

        List<TRecord> spcList = new ArrayList<TRecord>();
        listSpec.forEach(spcRec -> {

            spcList.add(spcRec);
        });

        listSpec.removeAll(spcList);
        List<TRecord> spcList2 = RRecord.groups4T(spcList);
        spcList2.forEach(act -> tab.append(templateTr));
        tab.getElementsByTag("tr").remove(1);

        for (int j = 0; j < spcList2.size(); j++) { //заполним строки 
            Elements td = tab.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
            RRecord rs = new RRecord(spcList2.get(j));
            td.get(0).text(String.valueOf(j + 1));
            td.get(1).text(rs.artikl());
            td.get(2).text(rs.name());
            td.get(3).text(rs.color(1));
            td.get(4).text(rs.unit());
            td.get(5).text(String.valueOf(rs.spc().count * countNum));
        }
    }
}
