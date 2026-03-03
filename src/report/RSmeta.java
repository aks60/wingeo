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
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.eSystree;
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

    private static Record projectRec;
    
    public void parseDoc1(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Smeta1.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile, "windows-1251");

            Record prjprodRec = prjprodList.get(0);
            projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc1(prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"), "windows-1251");
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
            Document doc = Jsoup.parse(tempFile, "windows-1251");
            Record prjprodRec = prjprodList.get(0);
            projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc2(prjprodList, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"), "windows-1251");
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Нет доступа к файлу. Процесс не может получить доступ к файлу, так как этот файл занят другим процессом.", "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка1:HtmlOfSmeta.smeta2()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:HtmlOfSmeta.smeta2()" + e);
        }
    }

    private static void loadDoc1(List<Record> prjprodList, Document doc) {
        double square = 0f;
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Record sysuserRec = eSysuser.find2(prjpartRec.getStr(ePrjpart.login));
            double discKit = projectRec.getDbl(eProject.disc_kit, 0) +  projectRec.getDbl(eProject.disc_all, 0);
            double discWin = projectRec.getDbl(eProject.disc_win, 0) +  projectRec.getDbl(eProject.disc_all, 0);

            doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord) + " от '" + UGui.convert2Date(projectRec.get(eProject.date4)) + "'");

            //Заполним файл шаблонами заказов
            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = wincList(prjprodList, 400);

            //СЕКЦИЯ №1
            {
                for (int i = 1; i < prjprodList.size(); i++) {
                    div2.append(template2);
                }
                Elements tabList = doc.getElementById("div2").getElementsByClass("tab2");

                //Цикл по изделиям
                for (int i = 0; i < prjprodList.size(); i++) {

                    Wincalc winc = wincList.get(i);
                    Record prjprodRec = prjprodList.get(i);

                    int numProd = prjprodRec.getInt(ePrjprod.num);
                    square += numProd * winc.root.area.getGeometryN(0).getArea();

                    Elements captions2 = tabList.get(i).getElementsByTag("caption");
                    captions2.get(0).text("Изделие № " + (i + 1));
                    Elements trList = tabList.get(i).getElementsByTag("tr");

                    trList.get(1).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.name));
                    trList.get(2).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 2) + " x " + UCom.format(winc.height() / 1000, 2));
                    trList.get(3).getElementsByTag("td").get(1).text(eColor.find(winc.root.colorID1).getStr(eColor.name) 
                            + " / " + eColor.find(winc.root.colorID2).getStr(eColor.name) 
                            + " / " + eColor.find(winc.root.colorID3).getStr(eColor.name));
                    trList.get(4).getElementsByTag("td").get(1).text(String.valueOf(numProd));
                    trList.get(5).getElementsByTag("td").get(1).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
                    trList.get(6).getElementsByTag("td").get(1).text(UCom.format(winc.weight, 2));
                    trList.get(7).getElementsByTag("td").get(1).text(UCom.format(numProd * winc.cost1, 9));
                    trList.get(8).getElementsByTag("td").get(1).text(UCom.format(numProd * (winc.cost2 - discWin * winc.cost2 / 100), 9));
                }                
            }
            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab6").getElementsByTag("tr");
                Kitcalc.tarifficProj(new Wincalc(), projectRec, discKit, true, true);
                trList.get(0).getElementsByTag("td").get(1).text(UCom.format(projectRec.getDbl(eProject.cost2_win, 0), 9) + " руб."); //всего за изделия
                trList.get(1).getElementsByTag("td").get(1).text(UCom.format(projectRec.getDbl(eProject.cost2_kit, 0), 9) + " руб.+"); //всего за комплекты
                trList.get(2).getElementsByTag("td").get(1)
                        .text(UCom.format(projectRec.getDbl(eProject.cost2_win, 0) + projectRec.getDbl(eProject.cost2_kit, 0), 9) + " руб."); //ИТОГО ПО ЗАКАЗУ 
                trList.get(3).getElementsByTag("td").get(0)
                        .text("Сумма прописью : " + UMon.inwords(projectRec.getDbl(eProject.cost2_win, 0) + projectRec.getDbl(eProject.cost2_kit, 0)));
                trList.get(4).getElementsByTag("td").get(0)
                        .text("включая НДС 20% : " + UCom.format((projectRec.getDbl(eProject.cost2_win, 0) + projectRec.getDbl(eProject.cost2_kit, 0)) * 20 / 120, 9) + " руб.");
                trList.get(5).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square / 1000000, 2) + " кв.м.");

                Elements imgList = doc.getElementById("div2").getElementsByTag("img");
                for (int i = 0; i < imgList.size(); i++) {
                    Element get = imgList.get(i);
                    get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.loadDoc1()" + e);
        }
    }

    private static void loadDoc2(List<Record> prjprodList, Document doc) {
        try {
            double totalTab4 = 0, totalTab5 = 0, square = 0f;
            double discWin = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);            
            double discKit = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);            
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.login, prjpartRec.getStr(ePrjpart.login));
            qSysuser.add(eSysuser.up.newRecord("SEL")); //если qSysuser.size() == 0                       
            Record sysuserRec = qSysuser.get(0);
            doc.getElementById("h01").text("Смета №" + projectRec.getStr(eProject.num_ord) + " от '" 
                    + UGui.convert2Date(projectRec.get(eProject.date4)) + "'");
            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);

            //СЕКЦИЯ №1
            {
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
            }
            //СЕКЦИЯ №2
            {
                for (int i = 1; i < prjprodList.size(); i++) {
                    div2.append(template2);
                }
                Elements tab2List = doc.getElementById("div2").getElementsByClass("tab2");
                Elements tab3List = doc.getElementById("div2").getElementsByClass("tab3");

                //Цикл по изделиям
                for (int i = 0; i < prjprodList.size(); i++) {

                    Record prjprodRec = prjprodList.get(i);
                    Wincalc winc = wincList.get(i);
                    int numProd = prjprodRec.getInt(ePrjprod.num);                    
                    Kitcalc.tarifficProd(winc, prjprodRec, discKit, true, true);

                    //Изделие
                    loadTab2(prjprodList, winc, tab2List, i, numProd);
                    Elements td = tab2List.get(i).getElementsByTag("td");

                    //Комплектация к изделию
                    if (Kitcalc.kitList.isEmpty()) {
                        tab3List.get(i).html("");
                    } else {
                        Elements captions3 = tab3List.get(i).getElementsByTag("caption");
                        captions3.get(0).text("Комплектация к изделию № " + (i + 1));
                        String template3 = tab3List.get(i).getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                        for (int k = 1; k < Kitcalc.kitList.size(); k++) {
                            tab3List.get(i).getElementsByTag("tbody").get(0).append(template3);
                        }
                        loadTab3(Kitcalc.kitList, winc, tab3List, i);
                    }

                    square += numProd * winc.root.area.getGeometryN(0).getArea();
                }
            }
            //СЕКЦИЯ №3
            {
                Element tab4Elem = doc.getElementById("tab4"),
                        tab5Elem = doc.getElementById("tab5");

                //Изделия все
                String template4 = tab4Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                for (int i = 1; i < prjprodList.size(); i++) {
                    tab4Elem.getElementsByTag("tbody").append(template4);
                }
                totalTab4 = loadTab4(prjprodList, wincList, tab4Elem);

                //Комплектация все
                ArrayList<TRecord> prjkitAllList = Kitcalc.tarifficProj(new Wincalc(), projectRec, 0, true, true);
                String template5 = tab5Elem.getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                for (int i = 1; i < prjkitAllList.size(); i++) {
                    tab5Elem.getElementsByTag("tbody").append(template5);
                }
                totalTab5 = loadTab5(prjkitAllList, tab5Elem);
            }
            //СЕКЦИЯ №4
            {
                Elements trList = doc.getElementById("tab6").getElementsByTag("tr");
                double totalDiscSum = (totalTab4 + totalTab5); //с общей скидкой менеджера 
                
                trList.get(0).getElementsByTag("td").get(1).text(UCom.format(totalDiscSum, 9) + " руб."); //ИТОГО ПО ЗАКАЗУ
                trList.get(1).getElementsByTag("td").get(0).text("Сумма прописью : " + UMon.inwords(totalDiscSum));
                trList.get(2).getElementsByTag("td").get(0).text("включая НДС 20% : " + UCom.format(totalDiscSum * 20 / 120, 9) + " руб.");
                trList.get(3).getElementsByTag("td").get(0).text("Площадь изделий в заказе : " + UCom.format(square / 1000000, 2) + " кв.м.");

                Elements imgList = doc.getElementById("div2").getElementsByTag("img");
                for (int i = 0; i < imgList.size(); i++) {
                    Element get = imgList.get(i);
                    get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.loadDoc2() " + e);
        }
    }

    //Свойства изделия
    public static void loadTab2(List<Record> prjprodList, Wincalc winc, Elements tabList, int indexProd, int numProd) {
        try {
            Record prjprodRec = prjprodList.get(indexProd);
            double discWin = projectRec.getDbl(eProject.disc_win, 0) + projectRec.getDbl(eProject.disc_all, 0);
            ArrayList<ElemSimple> glassList = UCom.filter(winc.listElem, Type.GLASS);
            String color3Name = eColor.find(winc.root.colorID1).getStr(eColor.name) 
                    + " / " + eColor.find(winc.root.colorID2).getStr(eColor.name) + " / " + eColor.find(winc.root.colorID3).getStr(eColor.name);
            Elements captions = tabList.get(indexProd).getElementsByTag("caption");
            captions.get(0).text("Изделие № " + (indexProd + 1));
            Elements trRec = tabList.get(indexProd).getElementsByTag("tbody").get(0).getElementsByTag("tr");

            trRec.get(1).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.name));
            trRec.get(2).getElementsByTag("td").get(1).text(eSystree.nameSysprof(prjprodRec.getInt(ePrjprod.systree_id)));
            trRec.get(3).getElementsByTag("td").get(1).text(UCom.format(winc.width() / 1000, 3) + " x " + UCom.format(winc.height() / 1000, 3));
            trRec.get(4).getElementsByTag("td").get(1).text(glassList.get(0).artiklRecAn.getStr(eArtikl.code));
            trRec.get(5).getElementsByTag("td").get(1).text("");
            trRec.get(6).getElementsByTag("td").get(1).text(color3Name);
            trRec.get(7).getElementsByTag("td").get(1).text(String.valueOf(numProd));
            trRec.get(8).getElementsByTag("td").get(1).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
            trRec.get(9).getElementsByTag("td").get(1).text(UCom.format(winc.weight, 2));
            trRec.get(10).getElementsByTag("td").get(1).text(UCom.format(winc.cost1 * 1000000 / winc.root.area.getGeometryN(0).getArea(), 9));            
            trRec.get(11).getElementsByTag("td").get(1).text(UCom.format(numProd * winc.cost1, 9));
            trRec.get(12).getElementsByTag("td").get(1).text(UCom.format(numProd * (winc.cost2 - discWin * winc.cost2 / 100), 9)); //со скидкой менеджера

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab2() " + e);
        }
    }

    //Комплектация изделия
    public static void loadTab3(List<TRecord> kitList, Wincalc winc, Elements tabList, int index) {
        try {
            double discKit = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);
            //Цикл по строкам комплектации
            for (int k = 0; k < kitList.size(); k++) {

                TRecord prjkitRec = kitList.get(k);
                Record artiklRec = prjkitRec.artiklRec;                               
                Elements trList = tabList.get(index).getElementsByTag("tr");
                Elements tdRec = trList.get(k + 1).getElementsByTag("td");

                tdRec.get(0).text(String.valueOf(k + 1));
                tdRec.get(1).text(artiklRec.getStr(eArtikl.code));
                tdRec.get(2).text(artiklRec.getStr(eArtikl.name));
                tdRec.get(3).text(eColor.find(prjkitRec.colorID1).getStr(eColor.name));
                tdRec.get(4).text(UCom.dimension(prjkitRec.width, prjkitRec.height, prjkitRec.unit));
                tdRec.get(5).text(UCom.format(prjkitRec.quant2, 2));
                tdRec.get(6).text(UCom.format(prjkitRec.price, 2));
                tdRec.get(7).text(UCom.format(prjkitRec.cost2, 2));
            }

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab3() " + e);
        }
    }

    //Изделия все
    public static double loadTab4(List<Record> prjprodList, List<Wincalc> wincList, Element tabElem) {
        double total = 0;
        double discWin = projectRec.getDbl(eProject.disc_win, 0) + projectRec.getDbl(eProject.disc_all, 0);
        try {
            Elements trList = tabElem.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (int i = 0; i < prjprodList.size(); i++) {
                
                Record prjprodRec = prjprodList.get(i);
                Wincalc winc = wincList.get(i);
                int numProd = prjprodRec.getInt(ePrjprod.num);
                //double priceMan = winc.cost2 - discWin * winc.cost2 / 100; //со скидкой менеджера
                Elements tdRec = trList.get(i).getElementsByTag("td");
                
                tdRec.get(0).text(String.valueOf(i + 1));
                tdRec.get(1).text(prjprodRec.getStr(ePrjprod.name));
                tdRec.get(2).text(eColor.find(winc.root.colorID1).getStr(eColor.name));
                tdRec.get(3).text(UCom.format(winc.width(), 2));
                tdRec.get(4).text(UCom.format(winc.height(), 2));
                tdRec.get(5).text(String.valueOf(numProd));
                tdRec.get(6).text(UCom.format(winc.cost2, 2));                
                tdRec.get(7).text(UCom.format(numProd * winc.cost2, 2));
                
                total += numProd * winc.cost2;
            }
            Elements tdFoot = tabElem.getElementsByTag("tfoot").get(0).getElementsByTag("td");
            tdFoot.get(1).text(UCom.format(projectRec.getDbl(eProject.cost2_win, 0), 2));

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab4() " + e);
        }
        return total;
    }

    //Комплектация все
    public static double loadTab5(ArrayList<TRecord> prjkitList, Element tabElem) {
        double total = 0;
        try {
            double discKit = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);
            Elements trList = tabElem.getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for (int i = 0; i < prjkitList.size(); i++) {

                TRecord prjkitRec = prjkitList.get(i);
                Record artiklRec = prjkitRec.artiklRec;
                Elements tdRec = trList.get(i).getElementsByTag("td");
                
                tdRec.get(0).text(String.valueOf(i + 1));
                tdRec.get(1).text(artiklRec.getStr(eArtikl.code));
                tdRec.get(2).text(artiklRec.getStr(eArtikl.name));
                tdRec.get(3).text(eColor.find(prjkitRec.colorID1).getStr(eColor.name));
                tdRec.get(4).text(UCom.dimension(prjkitRec.width, prjkitRec.height, prjkitRec.unit));
                tdRec.get(5).text(UCom.format(prjkitRec.quant2, 2));
                tdRec.get(6).text(UCom.format(prjkitRec.price, 2));
                tdRec.get(7).text(UCom.format(prjkitRec.cost2, 2));
                
                total += prjkitRec.cost2;
            }
            Elements tdFoot = tabElem.getElementsByTag("tfoot").get(0).getElementsByTag("td");
            tdFoot.get(1).text(UCom.format(projectRec.getDbl(eProject.cost2_kit), 2));  //всего за комплекты

        } catch (Exception e) {
            System.err.println("Ошибка: RSmeta.loadTab5() " + e);
        }
        return total;
    }
}
