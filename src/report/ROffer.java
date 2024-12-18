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

    private static DecimalFormat df0 = new DecimalFormat("0");
    private static DecimalFormat df1 = new DecimalFormat("0.0");
    private static DecimalFormat df2 = new DecimalFormat("#0.00");

    public  void parseDoc(Record projectRec) {
        try {
            InputStream in = getClass().getResourceAsStream("/resource/report/Offer.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile);

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

            doc.getElementById("h01").text("Коммерческое предложение от " + UGui.DateToStr(projectRec.get(eProject.date4)));

            Element div2 = doc.getElementById("div2");
            String template2 = div2.html();
            List<Wincalc> wincList = URep.wincList(prjprodList, 400);; //wincList(prjprodList, length);
            for (int i = 1; i < prjprodList.size(); i++) {
                div2.append(template2);
            }
            Elements tab4List = doc.getElementById("div2").getElementsByClass("tab4");
            //Цикл по изделиям
            for (int i = 0; i < prjprodList.size(); i++) {

                Elements tdList = tab4List.get(i).getElementsByTag("td");
                Elements trRec = tab4List.get(i).getElementsByTag("tbody").get(0).getElementsByTag("tr");
                Wincalc winc = wincList.get(i);
                square = square + winc.root.area.getGeometryN(0).getArea();
                Record prjprodRec = prjprodList.get(i);

                //tdList.get(0).text("Изделие № " + (i + 1));
                trRec.get(0).getElementsByTag("td").get(0).text("ИзделиеXXX № " + (i + 1));
                tdList.get(3).text(eSystree.systemProfile(6));
                AreaStvorka area5e = (AreaStvorka) UCom.filter(winc.listArea, Type.STVORKA).get(0);
                AreaStvorka stv = (area5e != null) ? ((AreaStvorka) area5e) : null;
                int furniture_id = stv.sysfurnRec.getInt(eSysfurn.furniture_id);
                String fname = (furniture_id != -1) ? eFurniture.find(furniture_id).getStr(eFurniture.name) : "";
                ElemSimple elemGlass = (ElemSimple) UCom.filter(winc.listElem, Type.GLASS).get(0);
                String gname = (elemGlass != null) ? elemGlass.artiklRec.getStr(eArtikl.code) + " - " + elemGlass.artiklRec.getStr(eArtikl.name) : "";
                
                tdList.get(5).text(fname);
                tdList.get(7).text(gname);
                tdList.get(9).text(eColor.find(winc.colorID1).getStr(eColor.name));
                tdList.get(11).text(eColor.find(winc.colorID2).getStr(eColor.name));
                tdList.get(13).text(eColor.find(winc.colorID3).getStr(eColor.name));
                tdList.get(15).text(winc.width() + "x" + winc.height());
                tdList.get(17).text(prjprodRec.getStr(ePrjprod.num));
                tdList.get(19).text(df2.format(winc.root.area.getGeometryN(0).getArea()));
                tdList.get(21).text(df2.format(winc.weight));
                tdList.get(23).text(df1.format(winc.price1()));
            }
            {
                Elements trList = doc.getElementById("tab2").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(1).text(df2.format(projectRec.getDbl(eProject.price2a)));
                trList.get(1).getElementsByTag("td").get(1).text(df2.format(projectRec.getDbl(eProject.price2b)));
                trList.get(2).getElementsByTag("td").get(1).text(df2.format(projectRec.getDbl(eProject.price2c)));
            }
            {
                Elements trList = doc.getElementById("tab5").getElementsByTag("tr");
                trList.get(0).getElementsByTag("td").get(2).text(df1.format(square / 1000000) + " кв.м.");
                trList.get(1).getElementsByTag("td").get(2).text(df2.format(projectRec.getDbl(eProject.price1a)));
                trList.get(2).getElementsByTag("td").get(2).text(df2.format(projectRec.getDbl(eProject.price2a)));
                trList.get(3).getElementsByTag("td").get(2).text(df2.format(projectRec.getDbl(eProject.price2c)));
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

//    private static byte[] toByteArray(BufferedImage bi) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(bi, "png", outputStream);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return outputStream.toByteArray();
//    }
}
