package report;

import builder.Wincalc;
import common.eProp;
import dataset.Record;
import domain.ePrjprod;
import frames.swing.Canvas;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class URep {
    
    public static List<Wincalc> wincList(List<Record> prjprodList, int length) {
        List<Wincalc> list = new ArrayList<Wincalc>();
        try {
            for (int index = 0; index < prjprodList.size(); ++index) {
                Record prjprodRec = prjprodList.get(index);
                String script = prjprodRec.getStr(ePrjprod.script);
                Wincalc winc = new Wincalc(script);
                winc.specific(true);
                winc.imageIcon = Canvas.createIcon(winc, length);
                winc.bufferImg = new BufferedImage(length, length, BufferedImage.TYPE_INT_RGB);
                winc.gc2d = winc.bufferImg.createGraphics();
                winc.gc2d.fillRect(0, 0, length, length);
                double height = winc.height();
                double width = winc.width();
                winc.scale = (length / width > length / height) ? length / (height + 80) : length / (width + 80);
                winc.gc2d.scale(winc.scale, winc.scale);
                winc.draw(); //рисую конструкцию
                File outputfile = new File(eProp.path_prop.read(), "img" + (index + 1) + ".gif");
                ImageIO.write(winc.bufferImg, "gif", outputfile);
                list.add(winc);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.wincList()" + e);
        }
        return list;
    }    
}
