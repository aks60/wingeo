package report;

import builder.Wincalc;
import common.ePrefs;
import common.listener.ListenerFrame;
import dataset.Record;
import domain.ePrjprod;
import frames.UGui;
import frames.swing.cmp.Canvas;
import frames.swing.cmp.ProgressBar;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import static startup.Test.frame;

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
                File outputfile = new File(ePrefs.genl.getProp(), "img" + (index + 1) + ".gif");
                ImageIO.write(winc.bufferImg, "gif", outputfile);
                list.add(winc);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:HtmlOfSmeta.wincList()" + e);
        }
        return list;
    }    
    
//    public static void specificRep(int n, Window frame) {
//        ProgressBar.create(frame.this, new ListenerFrame() {
//            public void actionRequest(Object obj) {
//                //Спецификация
//                List<Record> prjprodList =  List.of(qPrjprod.get(UGui.getIndexRec(tab2)));
//                new RSpecific().parseDoc(prjprodList);
//            }
//        });     
//    }
//    
//    public static void materialRep(int n) {
//        
//    }
//    
//    public static void targetRep(int n) {
//        
//    }
//    public static void smetaRep(int n) {
//        
//    }
}
