package report;

import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.model.ElemSimple;
import common.MoneyInWords;
import common.UCom;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static report.URep.wincList;

public class Smeta {

    public void parseDoc1(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Smeta1.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            loadDoc1(projectRec, doc);

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

    public void parseDoc2(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Smeta2.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            loadDoc2(projectRec, doc);

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

    private static void loadDoc1(Record projectRec, Document doc) {
        double total = 0f;
        double square = 0f; //площадь
        double square2 = 0f; //площадь
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Record sysuserRec = eSysuser.find2(prjpartRec.getStr(ePrjpart.login));
            List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
            List<Record> kitList = new ArrayList<Record>();

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
                
                int count = prjprodRec.getInt(ePrjprod.num);
                square += winc.root.area.getGeometryN(0).getArea();
                square2 += count * square;
                
                List<Record> list = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                kitList.addAll(list);

                //СЕКЦИЯ №2
                ArrayList<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
                Elements captions2 = tab2List.get(i).getElementsByTag("caption");
                captions2.get(0).text("Изделие № " + (i + 1));
                
                Elements td = tab2List.get(i).getElementsByTag("td");
                td.get(2).text(prjprodRec.getStr(ePrjprod.name));
                td.get(4).text(UCom.format(winc.width() / 1000, 2) + " x " + UCom.format(winc.height() / 1000, 2));
                td.get(6).text(eColor.find(winc.colorID1).getStr(eColor.name) + " / "
                        + eColor.find(winc.colorID2).getStr(eColor.name) + " / "
                        + eColor.find(winc.colorID3).getStr(eColor.name));
                td.get(8).text(String.valueOf(count));
                td.get(10).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
                td.get(12).text(UCom.format(winc.weight, 2));
                td.get(14).text(UCom.format(winc.price1, 9));
                //td.get(14).text(UCom.format(projectRec.getDbl(eProject.cost2), 9));
                td.get(16).text(UCom.format(winc.price2, 9));
                //td.get(16).text(UCom.format(projectRec.getDbl(eProject.cost4), 9));
                total += count * projectRec.getDbl(eProject.cost4);
            }

            //СЕКЦИЯ №3
            Elements trList = doc.getElementById("tab6").getElementsByTag("tr");
            trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total, 9) + " руб.");
            trList.get(1).getElementsByTag("td").get(0).text(MoneyInWords.inwords(total));
            trList.get(2).getElementsByTag("td").get(1).text("333,33" + " руб.");            
            trList.get(3).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square2 / 1000000, 2) + " кв.м.");

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.load1()" + e);
        }
    }

    private static void loadDoc2(Record projectRec, Document doc) {
        double total = 0f;
        double square = 0f; //площадь
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Record sysRec = eSysuser.find2(prjpartRec.getStr(ePrjpart.login));
            Record sysuserRec = (sysRec == null) ? eSysuser.virtualRec() : sysRec;
            List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
            List<Record> prjkitAll = new ArrayList<Record>();

            doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //СЕКЦИЯ №1
            Elements attr = doc.getElementById("tab1").getElementsByTag("td");
            if (prjpartRec.getInt(ePrjpart.flag2) == 0) {
                //Част.лицо
                attr.get(1).text(prjpartRec.getStr(ePrjpart.partner));
                attr.get(5).text(prjpartRec.getStr(ePrjpart.addr_phone));
                attr.get(9).text(prjpartRec.getStr(ePrjpart.addr_email));

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

                Elements tdList = tab2List.get(i).getElementsByTag("td");
                Wincalc winc = wincList.get(i);
                square = square + winc.width() * winc.height();
                Record prjprodRec = prjprodList.get(i);
                int count = prjprodRec.getInt(ePrjprod.num);
                List<Record> prjkitList = ePrjkit.filter3(prjprodRec.getInt(ePrjprod.id));
                prjkitAll.addAll(prjkitList);

                ArrayList<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
                Elements captions2 = tab2List.get(i).getElementsByTag("caption");
                captions2.get(0).text("Изделие № " + (i + 1));
                tdList.get(2).text(prjprodRec.getStr(ePrjprod.name));
                tdList.get(4).text(prjprodRec.getStr(ePrjprod.name));
                tdList.get(6).text(UCom.format(winc.width() / 1000, 3) + " x " + UCom.format(winc.height() / 1000, 3));
                tdList.get(8).text(glassList.get(0).artiklRecAn.getStr(eArtikl.code));
                tdList.get(10).text("");
                tdList.get(12).text(eColor.find(winc.colorID1).getStr(eColor.name) + " / "
                        + eColor.find(winc.colorID2).getStr(eColor.name) + " / "
                        + eColor.find(winc.colorID3).getStr(eColor.name));
                tdList.get(14).text(String.valueOf(count));
                tdList.get(16).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
                tdList.get(18).text(UCom.format(winc.weight, 2));
                tdList.get(20).text(UCom.format(count * winc.price1, 9));
                tdList.get(22).text(UCom.format(winc.price1 * 1000000 / winc.root.area.getGeometryN(0).getArea(), 9));
                tdList.get(24).text(UCom.format(count * winc.price2, 9));

                total += prjprodRec.getInt(ePrjprod.num) * winc.price2;

                if (prjkitList.size() == 0) {
                    tab3List.get(i).html("");
                } else {
                    Elements captions3 = tab3List.get(i).getElementsByTag("caption");
                    captions3.get(0).text("Комплектация к изделию № " + (i + 1));
                    String template3 = tab3List.get(i).getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                    for (int k = 1; k < prjkitList.size(); k++) {
                        tab3List.get(i).getElementsByTag("tbody").get(0).append(template3);
                    }

                    //Цикл по строкам комплектации
                    for (int k = 0; k < prjkitList.size(); k++) {

                        Record prjkitRec = prjkitList.get(k);
                        Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);

                        Elements tr3List = tab3List.get(i).getElementsByTag("tr");
                        Elements td3List = tr3List.get(k + 1).getElementsByTag("td");

                        td3List.get(0).text(String.valueOf(k + 1));
                        td3List.get(1).text(artiklRec.getStr(eArtikl.code));
                        td3List.get(2).text(artiklRec.getStr(eArtikl.name));
                        td3List.get(3).text(eColor.find(winc.colorID1).getStr(eColor.name));
                        td3List.get(4).text(UCom.format(prjkitRec.getDbl(ePrjkit.width), 2)
                                + "x" + UCom.format(prjkitRec.getDbl(ePrjkit.height), 2));
                        td3List.get(5).text(prjkitRec.getStr(ePrjkit.numb));
                        td3List.get(6).text(UCom.format(0, 1));
                        td3List.get(7).text(UCom.format(0, 1));
                    }
                }
            }

            //СЕКЦИЯ №3
            Element tab4Elem = doc.getElementById("tab4");
            Element tab5Elem = doc.getElementById("tab5");
            Element tab6Elem = doc.getElementById("tab6");
            String template4 = tab4Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjprodList.size(); i++) {
                tab4Elem.getElementsByTag("tbody").append(template4);
            }
            Elements tr4List = tab4Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (int i = 0; i < prjprodList.size(); i++) {
                Record prjprodRec = prjprodList.get(i);
                Wincalc winc = wincList.get(i);
                Elements td4List = tr4List.get(i).getElementsByTag("td");
                td4List.get(0).text(String.valueOf(i + 1));
                td4List.get(1).text(prjprodRec.getStr(ePrjprod.name));
                td4List.get(2).text(eColor.find(winc.colorID1).getStr(eColor.name));
                td4List.get(3).text(UCom.format(winc.width(), 2));
                td4List.get(4).text(UCom.format(winc.height(), 2));
                td4List.get(5).text(prjprodRec.getStr(ePrjprod.num));
                td4List.get(6).text(UCom.format(0, 2));
                td4List.get(7).text(UCom.format(0, 2));
            }

            String template5 = tab5Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjkitAll.size(); i++) {
                tab5Elem.getElementsByTag("tbody").append(template5);
            }
            Elements tr5List = tab5Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (int i = 1; i < prjkitAll.size(); i++) {
                Record prjkitRec = prjkitAll.get(i);
                Elements td5List = tr5List.get(i).getElementsByTag("td");
                Record artiklRec = eArtikl.find(prjkitRec.getInt(ePrjkit.artikl_id), true);
                td5List.get(0).text(String.valueOf(i + 1));
                td5List.get(1).text(artiklRec.getStr(eArtikl.code));
                td5List.get(2).text(artiklRec.getStr(eArtikl.name));
                td5List.get(3).text(eColor.find(prjkitRec.getInt(ePrjkit.color1_id)).getStr(eColor.name));
                td5List.get(4).text(UCom.format(prjkitRec.getDbl(ePrjkit.width), 2)
                        + " x " + UCom.format(prjkitRec.getDbl(ePrjkit.height), 2));
                td5List.get(5).text(prjkitRec.getStr(ePrjkit.numb));
                td5List.get(6).text(UCom.format(330, 2));
                td5List.get(7).text(UCom.format(440, 2));

            }
            //СЕКЦИЯ №4
            Elements trList = doc.getElementById("tab6").getElementsByTag("tr");
            trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total, 2));
            trList.get(1).getElementsByTag("td").get(0).text(MoneyInWords.inwords(total));
            trList.get(3).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square / 1000000, 2) + " кв.м.");

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.load2() " + e);
        }
    }
}
