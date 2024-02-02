package builder.script;

import builder.Wincalc;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import enums.Layout;
import enums.Type;
import java.util.LinkedList;

public class GsonElem {

    public static transient double genId = 0;  //идентификатор    
    public double id = 0;  //идентификатор
    public transient GsonElem owner = null;  //владелец
    public LinkedList<GsonElem> childs = null; //список детей
    public JsonObject param = new JsonObject(); //параметры элемента
    public Type type = null; //тип элемента
    public Double x1, y1, h, x2, y2;
    public Double length = null; //ширина или высота добавляемой area (зависит от напрвления расположения) 

    public GsonElem() {
        ++genId;
    }

    public GsonElem(Type type) {
        this.id = ++genId;
        this.type = type;
    }

    public GsonElem(Type type, String paramJson) {
        this.id = ++genId;
        this.type = type;
        this.param = new Gson().fromJson(paramJson, JsonObject.class);
    }

    public GsonElem(Type type, Integer x1, Integer y1) {
        this(type, x1, y1, null, null, null, null);
    }

    public GsonElem(Type type, Integer x1, Integer y1, Integer h) {
        this(type, x1, y1, null, null, h, null);
    }

    public GsonElem(Type type, Integer x1, Integer y1, String param) {
        this(type, x1, y1, null, null, null, param);
    }

    public GsonElem(Type type, Integer x1, Integer y1, Integer x2, Integer y2) {
        this(type, x1, y1, x2, y2, null, null);
    }

    public GsonElem(Type type, Integer x1, Integer y1, Integer h, String param) {
        this(type, x1, y1, null, null, h, param);
    }

    public GsonElem(Type type, Integer x1, Integer y1, Integer x2, Integer y2, String param) {
        this(type, x1, y1, x2, y2, null, param);
    }

    public GsonElem(Type type, Integer x1, Integer y1, Integer x2, Integer y2, Integer h, String param) {
        this.id = ++genId;
        this.type = type;
        this.x1 = (x1 == null) ? null : (double) x1;
        this.y1 = (y1 == null) ? null : (double) y1;
        this.x2 = (x2 == null) ? null : (double) x2;
        this.y2 = (y2 == null) ? null : (double) y2;
        this.h = (h == null) ? null : (double) h;
        this.param = new Gson().fromJson(param, JsonObject.class); //параметры элемента
    }

    public GsonElem addArea(GsonElem area) {
        area.owner = this;
        childs = (childs == null) ? new LinkedList() : childs;
        this.childs.add(area);
        return area;
    }

    public GsonElem addElem(GsonElem elem) {
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

    public void notSerialize(GsonElem gsonElem) {
        if (gsonElem == this && this.param != null && this.param.size() == 0) {
            this.param = null;
        }
        if (this.childs != null) {
            for (GsonElem el : this.childs) {
                if (el.param != null && el.param.size() == 0) {
                    el.param = null;
                }
                el.notSerialize(this); //рекурсия  
            }
        }
    }
}
