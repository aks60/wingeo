package report;

import builder.Wincalc;
import common.MoneyInWords;
import dataset.Record;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
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

public class HtmlOfInvoice {

    private static DecimalFormat df0 = new DecimalFormat("0");
    private static DecimalFormat df1 = new DecimalFormat("0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public void invoice1(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Invoice1.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            load1(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("UTF-8"));
            HtmlOfTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Нет доступа к файлу. Процесс не может получить доступ к файлу, так как этот файл занят другим процессом.", "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка1:HtmlOfInvoice.smeta1()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:HtmlOfInvoice.smeta1()" + e);
        }
    }

    public void invoice2(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Invoice2.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

            //Заполним отчёт
            load2(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("UTF-8"));
            HtmlOfTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Нет доступа к файлу. Процесс не может получить доступ к файлу, так как этот файл занят другим процессом.", "ВНИМАНИЕ!", 1);
            System.err.println("Ошибка1:HtmlOfInvoice.smeta2()" + e);
        } catch (Exception e) {
            System.err.println("Ошибка2:HtmlOfInvoice.smeta2()" + e);
        }
    }

    private static void load1(Record projectRec, Document doc) {
        double total = 0f;
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
            List<Wincalc> wincList = wincList(prjprodList);

            doc.getElementById("h01").text("Счёт №" + projectRec.getStr(eProject.num_acc) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab2").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner));
                if (prjpartRec.getInt(ePrjpart.flag2) == 1) {
                    trList.get(1).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.org_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_phone));

                } else {
                    trList.get(1).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.addr_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.addr_leve2));
                    trList.get(2).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.addr_phone));
                }
                trList.get(3).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.bank_inn));
            }
            //СЕКЦИЯ №3
            String templateRow = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjprodList.size(); i++) {
                doc.getElementById("tab3").getElementsByTag("tbody").append(templateRow);
            }
            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                Elements trList = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                Elements tdList = trList.get(i).getElementsByTag("td");
                Wincalc winc = wincList.get(i);
                Record prjprodRec = prjprodList.get(i);
                tdList.get(1).text(prjprodRec.getStr(ePrjprod.name));
                tdList.get(3).text(prjprodRec.getStr(ePrjprod.num));
                tdList.get(4).text(df1.format(winc.cost2));
                tdList.get(5).text(df1.format(prjprodRec.getInt(ePrjprod.num) * winc.cost2));
                total += prjprodRec.getInt(ePrjprod.num) * winc.cost2;
            }
            {
                Elements trList = doc.getElementById("tab4").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(df2.format(total));
                trList.get(1).getElementsByTag("td").get(0).text(MoneyInWords.inwords(total));
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.load1()" + e);
        }
    }

    private static void load2(Record projectRec, Document doc) {
        double total = 0f;
        try {
            Record prjpartRec = ePrjpart.find(projectRec.getInt(eProject.prjpart_id));
            List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
            List<Wincalc> wincList = wincList(prjprodList);

            doc.getElementById("h01").text("Счёт №" + projectRec.getStr(eProject.num_acc) + " от '" + UGui.DateToStr(projectRec.get(eProject.date4)) + "'");

            //СЕКЦИЯ №2
            {
                Elements trList = doc.getElementById("tab1").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                if (prjpartRec.getInt(ePrjpart.flag2) == 1) {
                    trList.get(4).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner)
                            + " " + prjpartRec.getStr(ePrjpart.org_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.org_leve2));
                    trList.get(5).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner));
                    trList.get(6).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.org_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.org_leve2));

                } else {
                    trList.get(4).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner)
                            + " " + prjpartRec.getStr(ePrjpart.addr_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.addr_leve2));
                    trList.get(5).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.partner));
                    trList.get(6).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.addr_leve1)
                            + " " + prjpartRec.getStr(ePrjpart.addr_leve2));
                }
                trList.get(7).getElementsByTag("td").get(1).text(prjpartRec.getStr(ePrjpart.bank_inn)
                        + " / " + prjpartRec.getStr(ePrjpart.bank_kpp));
            }
            //СЕКЦИЯ №3
            String templateRow = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr").get(0).html();
            for (int i = 1; i < prjprodList.size(); i++) {
                doc.getElementById("tab3").getElementsByTag("tbody").append(templateRow);
            }
            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                Elements trList = doc.getElementById("tab3").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                Elements tdList = trList.get(i).getElementsByTag("td");
                Wincalc winc = wincList.get(i);
                Record prjprodRec = prjprodList.get(i);
                tdList.get(1).text(prjprodRec.getStr(ePrjprod.name));
                tdList.get(3).text(prjprodRec.getStr(ePrjprod.num));
                tdList.get(4).text(df1.format(winc.cost2));
                tdList.get(5).text(df1.format(prjprodRec.getInt(ePrjprod.num) * winc.cost2));
                double cost2 = prjprodRec.getInt(ePrjprod.num) * winc.cost2;
                tdList.get(7).text(df1.format(cost2 / 100 * 18));
                tdList.get(8).text(df1.format(cost2 + cost2 / 100 * 18));
                total += cost2 + cost2 / 100 * 18;
            }
            {
                Elements trList = doc.getElementById("tab4").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(df2.format(total));
                trList.get(1).getElementsByTag("td").get(0).text(MoneyInWords.inwords(total));
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.load1()" + e);
        }
    }

    private static List<Wincalc> wincList(List<Record> prjprodList) {
        List<Wincalc> list = new ArrayList<Wincalc>();
        try {
            for (int index = 0; index < prjprodList.size(); ++index) {
                Record prjprodRec = prjprodList.get(index);
                String script = prjprodRec.getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specification(true);
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
