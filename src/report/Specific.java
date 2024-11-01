package report;

import report.sup.RTable;
import report.sup.RSpecific;
import report.sup.ExecuteCmd;
import builder.Wincalc;
import builder.making.SpcRecord;
import builder.making.SpcTariffic;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import domain.ePrjprod;
import domain.eProject;
import frames.UGui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//������������
public class Specific {

    private static int npp = 0;

    public  void parseDoc(Record projectRec) {
        try {
            npp = 0;
            InputStream in = getClass().getResourceAsStream("/resource/report/Specific.html");
            File tempFile = File.createTempFile("report", "html");
            in.transferTo(new FileOutputStream(tempFile));
            Document doc = Jsoup.parse(tempFile, "windows-1251");

            //�������� �����
            loadDoc1(projectRec, doc);

            String str = doc.html();
            str = new String(str.getBytes("windows-1251"));
            RTable.write(str);
            ExecuteCmd.documentType(null);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR*2 " + e, "��������!", 1);
            System.err.println("������:HtmlOfSpecific.specific()" + e);
        }
    }

    private static void loadDoc1(Record projectRec, Document doc) {
        
        //Elements tdList = tab2List.get(i).getElementsByTag("td");
        List<SpcRecord> spcList = new ArrayList<SpcRecord>();
        List<RSpecific> kitList = new ArrayList<RSpecific>();
        List<Record> prjprodList = ePrjprod.filter(projectRec.getInt(eProject.id));
        Wincalc winc = new builder.Wincalc();

        //���� �� ������������ ������
        for (Record prjprodRec : prjprodList) {
            String script = prjprodRec.getStr(ePrjprod.script);
            winc.build(script);
            winc.specification(true);
            spcList.addAll(winc.listSpec); //������� ������������
            
            List<SpcRecord> list = SpcTariffic.kits(prjprodRec, winc, true); //������� ���������
            list.forEach(rec -> kitList.add(new RSpecific(rec)));
        }
        
        List<RSpecific> listSpc = new ArrayList<RSpecific>();
        spcList.forEach(rec -> listSpc.add(new RSpecific(rec)));
        String num = projectRec.getStr(eProject.num_ord);
        String date = UGui.simpleFormat.format(projectRec.get(eProject.date5));

        List<RSpecific> listSpc1 = listSpc.stream().filter(rec -> rec.spc().artiklRec().getInt(eArtikl.level1) == 1).collect(toList());
        List<RSpecific> listSpc2 = RSpecific.groups(listSpc.stream().filter(rec -> rec.spc().artiklRec().getInt(eArtikl.level1) == 2).collect(toList()));
        List<RSpecific> listSpc3 = RSpecific.groups(listSpc.stream().filter(rec -> rec.spc().artiklRec().getInt(eArtikl.level1) == 3).collect(toList()));
        List<RSpecific> listSpc5 = listSpc.stream().filter(rec -> rec.spc().artiklRec().getInt(eArtikl.level1) == 5).collect(toList());

        doc.getElementById("h01").text("����� �" + projectRec.getStr(eProject.num_ord));   
        doc.getElementsByTag("thead").get(0).getElementsByTag("tr").get(0).getElementsByTag("th").get(0).html("����: " + date + " �.");
        
        Elements templateRec = doc.getElementsByTag("tbody").get(0).getElementsByTag("tr");
        doc.getElementsByTag("tbody").get(0).html("");

        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("�������");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc1.forEach(rec -> recordAdd(templateRec, rec, doc));
        
        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("����������");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc2.forEach(rec -> recordAdd(templateRec, rec, doc));
        
        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("����������");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc3.forEach(rec -> recordAdd(templateRec, rec, doc));
        
        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("����������");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        listSpc5.forEach(rec -> recordAdd(templateRec, rec, doc));
        
        templateRec.get(0).getElementsByTag("td").get(0).selectFirst("b").text("���������");
        doc.getElementsByTag("tbody").append(templateRec.get(0).html());
        kitList.forEach(rec -> recordAdd(templateRec, rec, doc));

        double total = listSpc1.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + listSpc2.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + listSpc3.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + listSpc5.stream().mapToDouble(spc -> spc.getCost1()).sum()
                + kitList.stream().mapToDouble(spc -> spc.getCost1()).sum();
        doc.getElementsByTag("tfoot").get(0).selectFirst("tr:eq(0)")
                .selectFirst("td:eq(1)").text(UCom.format(total, 9));
    }

    private static void recordAdd(Elements template, RSpecific rec, Document doc) {
        Elements tdList = template.get(1).getElementsByTag("td");
        tdList.get(0).text(String.valueOf(++npp));
        tdList.get(1).text(rec.getArtikl());
        tdList.get(2).text(rec.getName());
        tdList.get(3).text(rec.getColorID1());
        tdList.get(4).text(rec.getWidth());
        tdList.get(5).text(rec.getAngl());
        tdList.get(6).text(rec.getUnit());        
        tdList.get(7).text(rec.getCount());
        tdList.get(8).text(rec.getWeight());        
        tdList.get(9).text(rec.getPrice());
        tdList.get(10).text(rec.getCost());
        doc.getElementsByTag("tbody").append(template.get(1).html());
    }
}
