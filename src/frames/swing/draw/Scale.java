package frames.swing.draw;

import builder.model.AreaSimple;
import builder.script.GsonElem;
import enums.Type;
import java.awt.Color;

public class Scale {

    double X1, X2, Y1, Y2;
    public Color color = Color.black;  //цвет выделения линии 
    private AreaSimple area = null;

    public Scale(AreaSimple area) {
        this.area = area;
    }
    
    public void init() {
        this.area = area.winc().listArea.find(this.area.id());  
    }

    public AreaSimple area() {
        return area;
    }
    
    public GsonElem gson() {
        return area.gson();
    }

    public double width() {
        return (X2 - X1);
    }

    public double height() {
        return (Y2 - Y1);
    }
    
    public double widthGson() {
        return area.gson().width();
    }

    public double heightGson() {
        return area.gson().height();
    }
}
