package report;

import builder.Kitcalc;
import report.sup.RTable;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import common.UCom;
import common.UMon;
import dataset.Record;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import enums.UseUnit;
import frames.UGui;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//Счёт
public class RCheck {

    private static Record projectRec;
    
    public void parseDoc1(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Check1.html");
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
            System.err.println("Ошибка1:HtmlOfInvoice.smeta1()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:Check.parseDoc1()" + e);
        }
    }

    public void parseDoc2(List<Record> prjprodList) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Check2.html");
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
            System.err.println("Ошибка1:Check.parseDoc2()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:Check.parseDoc2()" + e);
        }
    }

    private static void loadDoc1(List<Record> prjprodList, Document doc) {
        double total = 0f;
        try {
            Record prjpart1Rec = ePrjpart.find(projectRec.getInt(eProject.vendor_id));
            Record prjpart2Rec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            List<Wincalc> wincList = wincList(prjprodList);

            doc.getElementById("h01").text("Счёт №" + projectRec.getStr(eProject.num_acc) + " от '" + UGui.convert2Date(projectRec.get(eProject.date4)) + "'");
            //СЕКЦИЯ №1
            {
                Elements trList = doc.getElementById("tab1").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.partner));

                if (prjpart1Rec.getInt(ePrjpart.flag2) == 0) {
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart1Rec.getStr(ePrjpart.addr_leve1) + " " + prjpart1Rec.getStr(ePrjpart.addr_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.addr_phone));
                } else {
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart1Rec.getStr(ePrjpart.org_leve1) + " " + prjpart1Rec.getStr(ePrjpart.org_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.org_phone));
                }
                trList.get(4).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.bank_inn));
                trList.get(5).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.bank_rs));
                trList.get(6).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.bank_bik));
                trList.get(7).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.bank_ks));
                trList.get(8).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.bank_kpp));
            }
            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab2").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.partner));

                if (prjpart2Rec.getInt(ePrjpart.flag2) == 0) {
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart2Rec.getStr(ePrjpart.addr_leve1) + " " + prjpart2Rec.getStr(ePrjpart.addr_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.addr_phone));
                } else {
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart2Rec.getStr(ePrjpart.org_leve1) + " " + prjpart2Rec.getStr(ePrjpart.org_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.org_phone));
                }
                trList.get(3).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.bank_inn));
            }
            //СЕКЦИЯ №3
            {
                double discWin = projectRec.getDbl(eProject.disc_win, 0) + projectRec.getDbl(eProject.disc_all, 0);
                double discKit = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);
                String templateRow = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                for (int i = 0; i < prjprodList.size(); i++) {
                    doc.getElementById("tab3").getElementsByTag("tbody").append(templateRow);
                }
                Elements trList = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                //Цикл по изделиям
                for (int i = 0; i < prjprodList.size(); i++) {

                    Elements tdList = trList.get(i).getElementsByTag("td");
                    Wincalc winc = wincList.get(i);
                    Record prjprodRec = prjprodList.get(i);
                    double numProd = prjprodRec.getInt(ePrjprod.num);

                    tdList.get(0).text(String.valueOf(i + 1));
                    tdList.get(1).text(prjprodRec.getStr(ePrjprod.name));
                    tdList.get(2).text("шт.");
                    tdList.get(3).text(String.valueOf(numProd));
                    tdList.get(4).text(UCom.format(winc.cost2 - discWin * winc.cost2 / 100, 9));
                    tdList.get(5).text(UCom.format(numProd * winc.cost2 * 20 / 120, 9));
                    tdList.get(6).text(UCom.format(numProd * (winc.cost2 - discWin * winc.cost2 / 100), 9)); //со скидкой менеджера

                    total += numProd * (winc.cost2 - discWin * winc.cost2 / 100);
                }
                Kitcalc.tarifficProj(new Wincalc(), projectRec, discKit, true, true);
                int index = prjprodList.size();
                Elements tdList = trList.get(index).getElementsByTag("td");
                tdList.get(0).text(String.valueOf(index + 1));
                tdList.get(1).text("Комплекты заказа");
                tdList.get(2).text("");
                tdList.get(3).text("");
                tdList.get(4).text("");
                tdList.get(5).text(UCom.format(Kitcalc.cost2 * 20 / 120, 9));
                tdList.get(6).text(UCom.format(Kitcalc.cost2, 9));
            }
            {
                Elements trList = doc.getElementById("tab5").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total + Kitcalc.cost2, 9) + " руб.");
                trList.get(1).getElementsByTag("td").get(0).text(UMon.inwords(total + Kitcalc.cost2));
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Check.loadDoc1()" + e);
        }
    }

    private static void loadDoc2(List<Record> prjprodList, Document doc) {
        double total = 0f;
        try {
            Record prjpart1Rec = ePrjpart.find(projectRec.getInt(eProject.vendor_id));
            Record prjpart2Rec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            List<Wincalc> wincList = wincList(prjprodList);
            double discKit = projectRec.getDbl(eProject.disc_kit, 0) + projectRec.getDbl(eProject.disc_all, 0);
            double discWin = projectRec.getDbl(eProject.disc_win, 0) + projectRec.getDbl(eProject.disc_all, 0);

            doc.getElementById("h01").text("Счёт-фактура №" + projectRec.getStr(eProject.num_acc) + " от '" + UGui.convert2Date(projectRec.get(eProject.date4)) + "'");
            //СЕКЦИЯ №1
            {
                Elements trList = doc.getElementById("tab1").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                if (prjpart1Rec.getInt(ePrjpart.flag2) == 1) {
                    trList.get(0).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.partner));
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart1Rec.getStr(ePrjpart.org_leve1) + " " + prjpart1Rec.getStr(ePrjpart.org_leve2));
                } else {
                    trList.get(0).getElementsByTag("td").get(1).text(prjpart1Rec.getStr(ePrjpart.partner));
                    trList.get(1).getElementsByTag("td").get(1)
                            .text(prjpart1Rec.getStr(ePrjpart.addr_leve1) + " " + prjpart1Rec.getStr(ePrjpart.addr_leve2));
                }
                trList.get(2).getElementsByTag("td").get(1)
                        .text(prjpart1Rec.getStr(ePrjpart.bank_inn) + " / " + prjpart1Rec.getStr(ePrjpart.bank_kpp));
            }
            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab1").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                if (prjpart2Rec.getInt(ePrjpart.flag2) == 1) {
                    trList.get(4).getElementsByTag("td").get(1)
                            .text(prjpart2Rec.getStr(ePrjpart.org_leve1) + " " + prjpart2Rec.getStr(ePrjpart.org_leve2));
                    trList.get(5).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.partner));
                    trList.get(6).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.org_leve1)
                            + " " + prjpart2Rec.getStr(ePrjpart.org_leve2));
                } else {
                    trList.get(4).getElementsByTag("td").get(1)
                            .text(prjpart2Rec.getStr(ePrjpart.addr_leve1) + " " + prjpart2Rec.getStr(ePrjpart.addr_leve2));
                    trList.get(5).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.partner));
                    trList.get(6).getElementsByTag("td").get(1).text(prjpart2Rec.getStr(ePrjpart.addr_leve1)
                            + " " + prjpart2Rec.getStr(ePrjpart.addr_leve2));
                }
                trList.get(7).getElementsByTag("td").get(1)
                        .text(prjpart2Rec.getStr(ePrjpart.bank_inn) + " / " + prjpart2Rec.getStr(ePrjpart.bank_kpp));
            }
            //СЕКЦИЯ №3
            {
                String templateRow = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
                for (int i = 0; i < prjprodList.size(); i++) {
                    doc.getElementById("tab3").getElementsByTag("tbody").append(templateRow);
                }
                Elements trList = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                //Цикл по изделиям
                for (int i = 0; i < prjprodList.size(); i++) {

                    Elements tdList = trList.get(i).getElementsByTag("td");
                    Wincalc winc = wincList.get(i);
                    Record prjprodRec = prjprodList.get(i);
                    Kitcalc.tarifficProd(new Wincalc(), prjprodRec, 0, true, true);
                    double numProd = prjprodRec.getInt(ePrjprod.num);
                    double nds = winc.cost2 * 20 / 120;

                    tdList.get(0).text(String.valueOf(1 + i));
                    tdList.get(1).text(prjprodRec.getStr(ePrjprod.name));
                    tdList.get(2).text("шт.");
                    tdList.get(3).text(String.valueOf(numProd));
                    tdList.get(4).text(UCom.format(winc.cost2 - nds - discWin * winc.cost2 / 100, 9));
                    tdList.get(5).text(UCom.format(numProd * (winc.cost2 - nds - discWin * winc.cost2 / 100), 9));
                    tdList.get(6).text("");
                    tdList.get(7).text("20%");
                    tdList.get(8).text(UCom.format(numProd * nds, 9));
                    tdList.get(9).text(UCom.format(numProd * (winc.cost2 - discWin * winc.cost2 / 100), 9));

                    total += numProd * (winc.cost2 - discWin * winc.cost2 / 100);
                }
                Kitcalc.tarifficProj(new Wincalc(), projectRec, discKit, true, true);
                int index = prjprodList.size();
                double nds = Kitcalc.cost2 * 20 / 120;
                Elements tdList = trList.get(index).getElementsByTag("td");
                tdList.get(0).text(String.valueOf(index + 1));
                tdList.get(1).text("Комплекты заказа");
                tdList.get(2).text("");
                tdList.get(3).text("");
                tdList.get(4).text("");
                tdList.get(5).text(UCom.format(Kitcalc.cost2 - discKit * Kitcalc.cost2 / 100 - nds, 9));
                tdList.get(6).text("");
                tdList.get(6).text("20%");
                tdList.get(8).text(UCom.format(nds, 9));
                tdList.get(9).text(UCom.format(Kitcalc.cost2, 9));
            }
            //СЕКЦИЯ №4
            {
                Elements trList = doc.getElementById("tab5").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(UCom.format(total + Kitcalc.cost2, 9) + " руб.");
                trList.get(1).getElementsByTag("td").get(0).text(UMon.inwords(total + Kitcalc.cost2));
            }

        } catch (Exception e) {
            System.err.println("Ошибка:Check.loadDoc2()" + e);
        }
    }

    private static List<Wincalc> wincList(List<Record> prjprodList) {
        List<Wincalc> list = new ArrayList<Wincalc>();
        try {
            for (int index = 0; index < prjprodList.size(); ++index) {
                Record prjprodRec = prjprodList.get(index);
                String script = prjprodRec.getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specific(true);
                list.add(winc);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.wincList()" + e);
        }
        return list;
    }

    private static byte[] toByteArray(BufferedImage bi) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }
}
