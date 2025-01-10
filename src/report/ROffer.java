package report;

import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.model.AreaStvorka;
import builder.Wincalc;
import builder.model.ElemSimple;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eFurniture;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.eSysfurn;
import domain.eSystree;
import domain.eSysuser;
import enums.Type;
import frames.UGui;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//Коммерческое предложение
public class ROffer {

    public void parseDoc(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Offer.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            Record prjprodRec = prjprodList.get(0);
            Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));

            //Заполним отчёт
            loadDoc(projectRec, doc);

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

    private static void loadDoc(Record projectRec, Document doc) {
        int length = 400;
        double square = 0f; //площадь
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            Record sysuserRec = eSysuser.find2(prjpartRec.getStr(ePrjpart.login));
            List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
            double discWin = projectRec.getDbl(eProject.disc_win, 0);
            double discPrj = projectRec.getDbl(eProject.disc_all, 0);
            double price2a = projectRec.getDbl(eProject.cost2_win, 0) - discPrj * projectRec.getDbl(eProject.cost2_win, 0) / 100;
            double price2b = projectRec.getDbl(eProject.cost2_kit, 0) - discPrj * projectRec.getDbl(eProject.cost2_kit, 0) / 100;

            doc.getElementById("h01").text("Коммерческое предложение от " + UGui.DateToStr(projectRec.get(eProject.date4)));

            //СЕКЦИЯ №1
            {
                Elements trList = doc.getElementById("tab1").getElementsByTag("tbody").get(0).getElementsByTag("tr");

                if (prjpartRec.getInt(ePrjpart.flag2) == 0) {
                    trList.get(0).getElementsByTag("td").get(1)
                            .text(prjpartRec.getStr(ePrjpart.addr_leve1) + " " + prjpartRec.getStr(ePrjpart.addr_leve2));
                    trList.get(1).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.addr_phone));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.addr_email));
                    trList.get(3).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner));

                } else {
                    trList.get(0).getElementsByTag("td").get(1)
                            .text(prjpartRec.getStr(ePrjpart.org_leve1) + " " + prjpartRec.getStr(ePrjpart.org_leve2));
                    trList.get(1).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_phone));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_email));
                    trList.get(3).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_contact));
                }
            }
            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab2").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(UCom.format(price2a - discPrj * price2a / 100, 9));
                trList.get(1).getElementsByTag("td").get(1).text(UCom.format(price2b - discPrj * price2b / 100, 9));
                trList.get(2).getElementsByTag("td").get(1)
                        .text(UCom.format(projectRec.getDbl(eProject.cost2_win) + projectRec.getDbl(eProject.cost2_kit), 9));
            }
            //СЕКЦИЯ №3
            {
                Element div2 = doc.getElementById("div2");
                String template2 = div2.html();
                List<Wincalc> wincList = URep.wincList(prjprodList, 400);; //wincList(prjprodList, length);
                for (int i = 1; i < prjprodList.size(); i++) {
                    div2.append(template2);
                }
                Elements imgList = doc.getElementById("div2").getElementsByTag("img");
                Elements tabList = doc.getElementById("div2").getElementsByClass("tab4");
                //Цикл по изделиям
                for (int i = 0; i < prjprodList.size(); i++) {

                    Record prjprodRec = prjprodList.get(i);
                    int numProd = prjprodRec.getInt(ePrjprod.num);

                    Elements trList = tabList.get(i).getElementsByTag("tr");
                    Wincalc winc = wincList.get(i);
                    square = square + winc.root.area.getGeometryN(0).getArea();

                    String fname = "";
                    List stvList = UCom.filter(winc.listArea, Type.STVORKA);
                    if (stvList.isEmpty() == false) {
                        AreaStvorka stv = ((AreaStvorka) stvList.get(0));
                        int furniture_id = stv.sysfurnRec.getInt(eSysfurn.furniture_id);
                        fname = (furniture_id != -1) ? eFurniture.find(furniture_id).getStr(eFurniture.name) : "";
                    }
                    ElemSimple elemGlass = (ElemSimple) UCom.filter(winc.listElem, Type.GLASS).get(0);
                    String gname = (elemGlass != null) ? elemGlass.artiklRec.getStr(eArtikl.code) + " - " + elemGlass.artiklRec.getStr(eArtikl.name) : "";

                    imgList.get(i).attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");

                    trList.get(0).getElementsByTag("td").get(0).text("Изделие № " + (i + 1));
                    trList.get(2).getElementsByTag("td").get(1).text(eSystree.nameSysprof(prjprodRec.getInt(ePrjprod.systree_id)));
                    trList.get(3).getElementsByTag("td").get(1).text(fname);
                    trList.get(4).getElementsByTag("td").get(1).text(gname);
                    trList.get(5).getElementsByTag("td").get(1).text(eColor.find(winc.colorID1).getStr(eColor.name));
                    trList.get(6).getElementsByTag("td").get(1).text(eColor.find(winc.colorID2).getStr(eColor.name));
                    trList.get(7).getElementsByTag("td").get(1).text(eColor.find(winc.colorID3).getStr(eColor.name));
                    trList.get(8).getElementsByTag("td").get(1).text(winc.width() + "x" + winc.height());
                    trList.get(9).getElementsByTag("td").get(1).text(prjprodRec.getStr(ePrjprod.num));
                    trList.get(10).getElementsByTag("td").get(1).text(UCom.format(winc.root.area.getGeometryN(0).getArea() / 1000000, 2));
                    trList.get(11).getElementsByTag("td").get(1).text(UCom.format(winc.weight, 2));
                    trList.get(12).getElementsByTag("td").get(1).text(UCom.format(numProd * winc.cost1, 9));
                    trList.get(13).getElementsByTag("td").get(1).text(UCom.format(numProd * (winc.cost2 - (discWin + discPrj) * winc.cost2 / 100), 9));
                }
            }
            //СЕКЦИЯ №4
            {
                Elements trList = doc.getElementById("tab5").getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(2).text(UCom.format(square / 1000000, 9) + " кв.м.");
                trList.get(1).getElementsByTag("td").get(2).text(UCom.format(projectRec.getDbl(eProject.cost1_win, 0), 9));
                trList.get(2).getElementsByTag("td").get(2).text(UCom.format(projectRec.getDbl(eProject.cost2_win, 0), 9));
                trList.get(3).getElementsByTag("td").get(2).text(UCom.format(projectRec.getDbl(eProject.cost2_win, 0)
                        + projectRec.getDbl(eProject.cost2_kit, 0), 9));
            }

            Elements imgList = doc.getElementById("div2").getElementsByTag("img");
            for (int i = 0; i < imgList.size(); i++) {
                Element get = imgList.get(i);
                get.attr("src", "C:\\Users\\All Users\\Avers\\Okna\\img" + (i + 1) + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfOffer.load()" + e);
        }
    }
}
