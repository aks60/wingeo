package builder.script;

import builder.Wincalc;
import com.google.gson.JsonObject;
import enums.Layout;
import enums.Type;
import java.util.LinkedList;

public class GeoElem {

    private static transient double genId = 0;  //идентификатор    
    public double id = 0;  //идентификатор
    public transient GeoElem owner = null;  //владелец
    public LinkedList<GeoElem> childs = null; //список детей
    public JsonObject param = null; //параметры элемента
    public Type type = null; //тип элемента
    public Double x1, y1, x2, y2;

    public GeoElem() {
        ++genId;
    }

    public GeoElem(Type type) {
        this.id = ++genId;
        this.type = type;
    }

    public GeoElem(Type type, Double x1, Double y1) {
        this(type, x1, y1, null, null);
    }

    public GeoElem(Type type, Double x1, Double y1, Double x2, Double y2) {
        this.id = ++genId;
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public GeoElem(Layout layout, Type type, String paramJson) {
        
    }
    
    public GeoElem addArea(GeoElem area) {
        area.owner = this;
        childs = (childs == null) ? new LinkedList() : childs;
        this.childs.add(area);
        return area;
    }

    public GeoElem addElem(GeoElem elem) {
        elem.owner = this;
        childs = (childs == null) ? new LinkedList() : childs;
        this.childs.add(elem);
        return this;
    }

    /**
     * Назначить родителей всем детям
     */
    public void setOwner(Wincalc winc) {
        try {
            this.childs.forEach(el -> {
                el.owner = this;
                if (el.childs != null) {
                    el.setOwner(winc);
                }
            });
        } catch (Exception e) {
            System.err.println("Ошибка:GeoElem.setOwnerAndForm() " + e);
        }
    }
}
