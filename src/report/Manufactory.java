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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//Задание в цех
public class Manufactory {

    private static DecimalFormat df1 = new DecimalFormat("#0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public void parseDoc(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Manufactory.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            loadDoc(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfManufactory.manufactory() " + e);
        }
    }

    private static void loadDoc(Record projectRec, Document doc) {
        Double square = 0.0; //площадь
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
            

            doc.getElementById("h02").text("Заказ №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //Таблица №1 ЗАКАЗЧИК
            String ord = qPrjpart.get(0).getStr(ePrjpart.partner); //заказчик
            String kon = (qPrjpart.get(0).getInt(ePrjpart.flag2) == 1) //конт. лицо
                    ? qPrjpart.get(0).getStr(ePrjpart.org_contact)
                    : qSysuser.get(0).getStr(eSysuser.fio); 
            Element tab1 =  doc.getElementById("tab1");
            tab1.getElementsByTag("tr").get(0).getElementsByTag("td").get(1).text(ord);
            tab1.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(kon);

            //Заполним файл шаблонами заказов
            Element div2 = doc.getElementById("div2");
            String templateBody = div2.html();
            List<Wincalc> wincList = URep.wincList(qPrjprod, 400);
            for (int i = 1; i < qPrjprod.size(); i++) {
                div2.append(templateBody);
            }  
            
            //Цикл по изделиям
            for (int i = 0; i < qPrjprod.size(); i++) {
                Record prjprodRec = qPrjprod.get(i);
                Wincalc winc = wincList.get(i);
                
                //Таблица №2 ИЗДЕЛИЕ РЕКВИЗИТЫ  
                square += winc.root.area.getGeometryN(0).getArea();
                Record artiklRec = winc.root.frames.get(0).artiklRecAn;
                Record colorRec1 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID1).get(0);
                Record colorRec2 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID2).get(0);
                Record colorRec3 = new Query(eColor.values()).sql(eColor.data(), eColor.id, winc.colorID3).get(0);
                AreaStvorka areaStvorka = (AreaStvorka) UCom.filter(winc.listArea, Type.STVORKA).get(0);
                Record furnitureRec = new Query(eFurniture.values()).sql(eFurniture.data(), eFurniture.id, areaStvorka.sysfurnRec.getInt(eSysfurn.furniture_id)).get(0);
                
                Element tab2 = tab2List.get(i);                                
                tab2.getElementsByTag("tr").get(1).getElementsByTag("td").get(1).text(artiklRec.getStr(eArtikl.name));
                tab2.getElementsByTag("tr").get(2).getElementsByTag("td").get(1).text(colorRec1.getStr(eColor.name) 
                        + " / " + colorRec2.getStr(eColor.name)+ " / " + colorRec3.getStr(eColor.name));
                tab2.getElementsByTag("tr").get(3).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 3) 
                        + " x " + UCom.format(winc.height() / 1000, 3));
                tab2.getElementsByTag("tr").get(4).getElementsByTag("td").get(1).text(furnitureRec.getStr(eFurniture.name));
                tab2.getElementsByTag("tr").get(5).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.num));
                tab2.getElementsByTag("tr").get(6).getElementsByTag("td").get(1).text(UCom.format(square / 1000000, 2));
                
                
                //Таблица №3 ПРОФИЛЬ / АРМИРОВАНИЕ  
                Element tab3 = tab3List.get(i);
                List<SpcRecord> spcList3 = new ArrayList(), spcList3a = new ArrayList();
                loadTab3(winc, tab3, template3Rec, spcList3, spcList3a); //спецификация для изделия 
                spcList3.forEach(act -> tab3.append(template3Rec));
                tab3.getElementsByTag("tr").remove(1);
                
                for (int j = 0; j < spcList3.size(); j++) { //заполним строки 
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

                //Таблица №4 УПЛОТНИТЕЛИ 
                Element tab4 = tab4List.get(i);
                List<SpcRecord> spcList4 = loadTab4(winc, tab4, template4Rec);
                spcList4.forEach(act -> tab4.append(template4Rec));
                tab4.getElementsByTag("tr").remove(1);

                for (int j = 0; j < spcList4.size(); j++) { //заполним строки 
                    Elements tdList4 = tab4.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList4.get(0).text(String.valueOf(j + 1));
                    tdList4.get(1).text(str(spcList4.get(j).artikl));
                    tdList4.get(2).text(str(spcList4.get(j).name));
                    tdList4.get(3).text("мм");
                    tdList4.get(4).text(str(spcList4.get(j).width));
                }

                //Таблица №5 ШТАПИК  
                Element tab5 = tab5List.get(i);
                List<SpcRecord> spcList5 = loadTab5(winc, tab5, template5Rec);
                spcList5.forEach(act -> tab5.append(template5Rec));
                tab5.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList5.size(); j++) { //заполним строки 
                    Elements tdList5 = tab5.getElementsByTag("tr").get(j + 1).getElementsByTag("td");
                    tdList5.get(0).text(String.valueOf(j + 1));
                    tdList5.get(1).text(str(spcList5.get(j).artikl));
                    tdList5.get(2).text(str(spcList5.get(j).name));
                    tdList5.get(3).text(str(spcList5.get(j).width));
                    tdList5.get(4).text(str(spcList5.get(j).count));
                    tdList5.get(5).text(str(spcList5.get(j).anglCut0));
                    tdList5.get(6).text(str(spcList5.get(j).anglCut1));
                }

                //Таблица №6 ЗАПОЛНЕНИЯ  
                Element tab6 = tab6List.get(i);
                List<SpcRecord> spcList6 = loadTab6(winc, tab6, template6Rec);
                spcList6.forEach(act -> tab6.append(template6Rec));
                tab6.getElementsByTag("tr").remove(1);
                for (int j = 0; j < spcList6.size(); j++) { //заполним строки 
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

            //Загрузим изображения
            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfManufactory.load() " + e);
        }
    }

    //ПРОФИЛЬ / АРМИРОВАНИЕ
    public static void loadTab3(Wincalc winc, Element tab, String templateRec, List<SpcRecord> spcList2, List<SpcRecord> spcList3) {

        winc.listSpec.forEach(spcRec -> { //профиля
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X100, TypeArt.X101, TypeArt.X102, TypeArt.X103, TypeArt.X104, TypeArt.X105) == true) {
                spcList2.add(new SpcRecord(spcRec));
            }
        });
        spcList2.forEach(spcRec1 -> { //армирование
            SpcRecord spcRec3 = new SpcRecord();
            for (SpcRecord spcRec2 : winc.listSpec) {
                if (TypeArt.isType(spcRec2.artiklRec(), TypeArt.X107) == true && spcRec2.elem5e.id == spcRec1.id) {
                    spcRec3 = spcRec2;
                }
            }
            spcList3.add(new SpcRecord(spcRec3));
        });
    }

    //УПЛОТНИТЕЛИ
    public static List<SpcRecord> loadTab4(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X135) == true) {
                spcList.add(new SpcRecord(spcRec));
            }
        });
        return spcList;
    }

    //ШТАПИК
    public static List<SpcRecord> loadTab5(Wincalc winc, Element tab, String templateRec) {

        List<SpcRecord> spcList = new ArrayList();
        winc.listSpec.forEach(spcRec -> {
            if (TypeArt.isType(spcRec.artiklRec(), TypeArt.X108) == true) {
                spcList.add(new SpcRecord(spcRec));
            }
        });
        return spcList;
    }

    //ЗАПОЛНЕНИЯ
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
