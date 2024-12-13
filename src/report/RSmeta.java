package report;

import builder.Kitcalc;
import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.TRecord;
import builder.model.ElemSimple;
import common.UMon;
import common.UCom;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.ePrjkit;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.eSysuser;
import enums.Type;
import frames.UGui;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static report.URep.wincList;

public class RSmeta {

    public void parseDoc1(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Smeta1.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc1(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Нет доступа к файлу. Процесс не может получить доступ к файлу, так как этот файл занят другим процессом.", "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка1:HtmlOfSmeta.smeta1()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:HtmlOfSmeta.smeta1()" + e);
        }
    }

    public void parseDoc2(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Smeta2.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);
            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc2(projectRec, prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Нет доступа к файлу. Процесс не может получить доступ к файлу, так как этот файл занят другим процессом.", "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка1:HtmlOfSmeta.smeta2()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:HtmlOfSmeta.smeta2()" + e);
        }
    }

    private static void loadDoc1(Record projectRec, List<Record> prjprodList, Document doc) {
        double total = 0, square = 0f;
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Record sysuserRec = eSysuser.find2(prjpartRec.getStr(ePrjpart.login));

            doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //Заполним файл шаблонами заказов
            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = wincList(prjprodList, 400);
            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(template2);
            }
            Elements tab2List = doc.getElementById("div2").getElementsByClass("tab2");

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                Wincalc winc = wincList.get(i);
                Record prjprodRec = prjprodList.get(i);

                int numProd = prjprodRec.getInt(ePrjprod.num);
                square += numProd * winc.root.area.getGeometryN(0).getArea();

                //СЕКЦИЯ №2
                Elements captions2 = tab2List.get(i).getElementsByTag("caption");
                captions2.get(0).text("Изделие № " + (i + 1));
                Elements tr2List = tab2List.get(i).getElementsByTag("tr");

                tr2List.get(1).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.name));
                tr2List.get(2).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 2) + " x " + UCom.format(winc.height() / 1000, 2));
                tr2List.get(3).getElementsByTag("td").get(1).text(eColor.find(winc.colorID1).getStr(eColor.name) + " / "
                        + eColor.find(winc.colorID2).getStr(eColor.name) + " / " + eColor.find(winc.colorID3).getStr(eColor.name));
                tr2List.get(4).getElementsByTag("td").get(1).text(String.valueOf(numProd));
                tr2List.get(5).getElementsByTag("td").get(1).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
                tr2List.get(6).getElementsByTag("td").get(1).text(UCom.format(winc.weight, 2));
                tr2List.get(7).getElementsByTag("td").get(1).text(UCom.format(winc.price1() * numProd, 9));
                double price2 = winc.price2() - projectRec.getDbl(eProject.disc2) * winc.price2() / 100;
                tr2List.get(8).getElementsByTag("td").get(1).text(UCom.format(price2 * numProd, 9));

                total += numProd * price2;
            }

            //СЕКЦИЯ №3
            Kitcalc.tarifficProj(projectRec, new Wincalc(), true, true);
            double nds = 18 * (total + Kitcalc.price2()) / 100; //НДС
            Elements trList = doc.getElementById("tab6").getElementsByTag("tr");

            trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total, 9) + " руб."); //всего за изделия
            trList.get(1).getElementsByTag("td").get(1).text(UCom.format(Kitcalc.price2(), 9) + " руб."); //всего за комплекты
            trList.get(2).getElementsByTag("td").get(1).text(UCom.format(total + Kitcalc.price2() + nds, 9) + " руб."); //ИТОГО ПО ЗАКАЗУ 
            trList.get(3).getElementsByTag("td").get(0).text("Сумма прописью : " + UMon.inwords(total + nds));
            trList.get(4).getElementsByTag("td").get(0).text("включая НДС 18% : " + UCom.format(nds, 9) + " руб.");
            trList.get(5).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square / 1000000, 2) + " кв.м.");

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.loadDoc1()" + e);
        }
    }

    private static void loadDoc2(Record projectRec, List<Record> prjprodList, Document doc) {
        double total1 = 0f, total2 = 0f;
        double square = 0f; //площадь
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, prjpartRec.getStr(ePrjpart.login));
            qSysuser.add(eSysuser.up.newRecord("SEL")); //если qSysuser.size() == 0                       
            Record sysuserRec = qSysuser.get(0);
            doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //СЕКЦИЯ №1
            Elements attr = doc.getElementById("tab1").getElementsByTag("td");
            if (prjpartRec.getInt(ePrjpart.flag2) == 0) {
                //Част.лицо
                attr.get(1).text(prjpartRec.getStr(ePrjpart.partner));
                attr.get(5).text(prjpartRec.getStr(ePrjpart.addr_phone));
                attr.get(9).text(prjpartRec.getStr(ePrjpart.addr_email));
                attr.get(13).text("");

            } else {//Организация
                attr.get(1).text(prjpartRec.getStr(ePrjpart.partner));
                attr.get(5).text(prjpartRec.getStr(ePrjpart.org_phone));
                attr.get(9).text(prjpartRec.getStr(ePrjpart.org_email));
                attr.get(13).text(prjpartRec.getStr(ePrjpart.org_contact));
            }

            attr.get(3).text(sysuserRec.getStr(eSysuser.fio));
            attr.get(7).text(sysuserRec.getStr(eSysuser.phone));
            attr.get(11).text(sysuserRec.getStr(eSysuser.email));

            //СЕКЦИЯ №2
            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);

            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(template2);
            }
            Elements tab2List = doc.getElementById("div2").getElementsByClass("tab2");
            Elements tab3List = doc.getElementById("div2").getElementsByClass("tab3");

            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                //Изделие
                Record prjprodRec = prjprodList.get(i);
                Wincalc winc = wincList.get(i);
                int countProd = prjprodRec.getInt(ePrjprod.num);
                Kitcalc.tarifficProd(prjprodRec, winc, true, true);

                loadTab2(projectRec, prjprodList, winc, tab2List, i, countProd);

                //int count = prjprodRec.getInt(ePrjprod.num);
                Elements td = tab2List.get(i).getElementsByTag("td");
                total1 += prjprodRec.getInt(ePrjprod.num) * winc.price2();

                //Комплектация к изделию
                if (Kitcalc.kits().isEmpty()) {
                    tab3List.get(i).html("");
                } else {
                    Elements captions3 = tab3List.get(i).getElementsByTag("caption");
                    captions3.get(0).text("Комплектация к изделию № " + (i + 1));
                    String template3 = tab3List.get(i).getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                    for (int k = 1; k < Kitcalc.kits().size(); k++) {
                        tab3List.get(i).getElementsByTag("tbody").get(0).append(template3);
                    }
                    loadTab3(Kitcalc.kits(), winc, tab3List, i, countProd);
                }
                
                square += countProd * winc.root.area.getGeometryN(0).getArea();
                double price3 = winc.price2() - projectRec.getDbl(eProject.disc2) * winc.price2() / 100; //со скидкой менеджера
                total1 += price3;
                total2 += countProd * price3;
            }

            //СЕКЦИЯ №3
            Element tab4Elem = doc.getElementById("tab4"),
                    tab5Elem = doc.getElementById("tab5");

            //Изделия все
            String template4 = tab4Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjprodList.size(); i++) {
                tab4Elem.getElementsByTag("tbody").append(template4);
            }
            loadTab4(prjprodList, wincList, tab4Elem);

            //Комплектация все
            ArrayList<TRecord> prjkitAllList = Kitcalc.tarifficProj(projectRec, new Wincalc(), true, true);
            String template5 = tab5Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjkitAllList.size(); i++) {
                tab5Elem.getElementsByTag("tbody").append(template5);
            }
            loadTab5(prjkitAllList, tab5Elem);

            //СЕКЦИЯ №4
            double nds = 18 * total2 / 100; //НДС
            Elements trList = doc.getElementById("tab6").getElementsByTag("tr");

            trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total2 + nds, 9) + " руб.");
            trList.get(1).getElementsByTag("td").get(0).text("Сумма прописью : " + UMon.inwords(total2 + nds));
            trList.get(2).getElementsByTag("td").get(0).text("включая НДС 18% : " + UCom.format(nds, 9) + " руб.");

            trList.get(3).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square / 1000000, 2) + " кв.м.");

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.loadDoc2() " + e);
        }
    }

    //Изделие
    public static void loadTab2(Record projectRec, List<Record> prjprodList, Wincalc winc, Elements tab2List, int indexProd, int countProd) {
        try {
            Elements td = tab2List.get(indexProd).getElementsByTag("td");
            Record prjprodRec = prjprodList.get(indexProd);

            ArrayList<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
            Elements captions2 = tab2List.get(indexProd).getElementsByTag("caption");
            captions2.get(0).text("Изделие № " + (indexProd + 1));
            Elements tr2Rec = tab2List.get(indexProd).getElementsByTag("tbody").get(0).getElementsByTag("tr");

            tr2Rec.get(1).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.name));
            tr2Rec.get(2).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.name));
            tr2Rec.get(3).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 3) + " x " + UCom.format(winc.height() / 1000, 3));
            tr2Rec.get(4).getElementsByTag("td").get(1).text(glassList.get(0).artiklRecAn.getStr(eArtikl.code));
            tr2Rec.get(5).getElementsByTag("td").get(1).text("");
            tr2Rec.get(6).getElementsByTag("td").get(1).text(eColor.find(winc.colorID1).getStr(eColor.name) + " / "
                    + eColor.find(winc.colorID2).getStr(eColor.name) + " / " + eColor.find(winc.colorID3).getStr(eColor.name));
            tr2Rec.get(7).getElementsByTag("td").get(1).text(String.valueOf(countProd));         
            tr2Rec.get(8).getElementsByTag("td").get(1).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
            tr2Rec.get(9).getElementsByTag("td").get(1).text(UCom.format(winc.weight, 2));
            tr2Rec.get(10).getElementsByTag("td").get(1).text(UCom.format(winc.price1() * 1000000 / winc.root.area.getGeometryN(0).getArea(), 9));
            tr2Rec.get(11).getElementsByTag("td").get(1).text(UCom.format(countProd * winc.price1(), 9));
            double price3 = winc.price2() - projectRec.getDbl(eProject.disc2) * winc.price2() / 100; //со скидкой менеджера
            tr2Rec.get(12).getElementsByTag("td").get(1).text(UCom.format(countProd * price3, 9));

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab2() " + e);
        }
    }

    //Комплектация к изделию
    public static void loadTab3(List<TRecord> kitList, Wincalc winc, Elements tab3List, int index, int countProd) {
        try {
            //Цикл по строкам комплектации
            for (int k = 0; k < kitList.size(); k++) {

                TRecord prjkitRec = kitList.get(k);
                Record artiklRec = prjkitRec.artiklRec;

                Elements tr3List = tab3List.get(index).getElementsByTag("tr");
                Elements td3Rec = tr3List.get(k + 1).getElementsByTag("td");

                td3Rec.get(0).text(String.valueOf(k + 1));
                td3Rec.get(1).text(artiklRec.getStr(eArtikl.code));
                td3Rec.get(2).text(artiklRec.getStr(eArtikl.name));
                td3Rec.get(3).text(eColor.find(winc.colorID1).getStr(eColor.name));
                td3Rec.get(4).text(UCom.dimension(prjkitRec.width, prjkitRec.height, prjkitRec.unit));
                td3Rec.get(5).text(UCom.format(prjkitRec.quant2 * countProd, 2));
                td3Rec.get(6).text(UCom.format(prjkitRec.sebes2, 2));
                td3Rec.get(7).text(UCom.format(prjkitRec.price2 * countProd, 2));
            }

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab3() " + e);
        }
    }

    //Изделия все
    public static void loadTab4(List<Record> prjprodList, List<Wincalc> wincList, Element tab4Elem) {
        try {
            double total = 0;
            Elements tr4List = tab4Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr");

            for (int i = 0; i < prjprodList.size(); i++) {
                Record prjprodRec = prjprodList.get(i);
                Wincalc winc = wincList.get(i);
                int countProd = prjprodRec.getInt(ePrjprod.num);
                Elements td4Rec = tr4List.get(i).getElementsByTag("td");
                td4Rec.get(0).text(String.valueOf(i + 1));
                td4Rec.get(1).text(prjprodRec.getStr(ePrjprod.name));
                td4Rec.get(2).text(eColor.find(winc.colorID1).getStr(eColor.name));
                td4Rec.get(3).text(UCom.format(winc.width(), 2));
                td4Rec.get(4).text(UCom.format(winc.height(), 2));
                td4Rec.get(5).text(String.valueOf(countProd));
                td4Rec.get(6).text(UCom.format(winc.price2(), 2));
                td4Rec.get(7).text(UCom.format(countProd * winc.price2(), 2));
                total += UCom.getDbl(td4Rec.get(7).text());
            }
            Elements tdFoot = tab4Elem.getElementsByTag("tfoot").get(0).getElementsByTag("td");
            tdFoot.get(1).text(UCom.format(total, 2));            

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab4() " + e);
        }
    }

    //Комплектация все
    public static void loadTab5(ArrayList<TRecord> prjkitList, Element tab5Elem) {
        try {
            double total = 0;
            Elements tr5List = tab5Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (int i = 0; i < prjkitList.size(); i++) {

                Record prjkitDb = prjkitList.get(i).detailRec;
                Record prjprodDb = ePrjprod.find(prjkitDb.getInt(ePrjkit.prjprod_id));
                int countProd = (prjprodDb == null) ? 1 : Integer.valueOf(prjprodDb.getStr(ePrjprod.num));

                TRecord prjkitRec = prjkitList.get(i);
                Record artiklRec = prjkitRec.artiklRec;
                Elements td5Rec = tr5List.get(i).getElementsByTag("td");
                td5Rec.get(0).text(String.valueOf(i + 1));
                td5Rec.get(1).text(artiklRec.getStr(eArtikl.code));
                td5Rec.get(2).text(artiklRec.getStr(eArtikl.name));
                td5Rec.get(3).text(eColor.find(prjkitRec.colorID1).getStr(eColor.name));
                td5Rec.get(4).text(UCom.dimension(prjkitRec.width, prjkitRec.height, prjkitRec.unit));
                td5Rec.get(5).text(UCom.format(prjkitRec.quant2 * countProd, 2));
                td5Rec.get(6).text(UCom.format(prjkitRec.sebes2, 2));
                td5Rec.get(7).text(UCom.format(prjkitRec.price2 * countProd, 2));
                total += UCom.getDbl(td5Rec.get(7).text());
            }
            Elements tdFoot = tab5Elem.getElementsByTag("tfoot").get(0).getElementsByTag("td");
            tdFoot.get(1).text(UCom.format(total, 2));
            
        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab5() " + e);
        }
    }
}
