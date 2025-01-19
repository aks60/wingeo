package builder.script;

import builder.Wincalc;
import builder.model.Com5t;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import enums.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonElem {

    public static transient double gsonId = 0;  //�������������    
    public double id = 0;  //�������������
    public transient GsonElem owner = null;  //��������
    public List<GsonElem> childs = new ArrayList<GsonElem>(); //������ �����
    public JsonObject param = new JsonObject(); //��������� ��������
    public Type type = null; //��� ��������
    public Double x1 = null, y1 = null, h = null, x2 = null, y2 = null;

    //Use GsonRoot
    public GsonElem() {
    }

    //Use class Bimax, GsonScript, GsonElem, ��������
    public GsonElem(Type type) {
        this.id = ++gsonId;
        this.type = type;
    }

    //Use class Bimax
    public GsonElem(Type type, String paramJson) {
        this.id = ++gsonId;
        this.type = type;
        this.param = new Gson().fromJson(paramJson, JsonObject.class);
    }

    //Use class Bimax, GsonScript, Test
    public GsonElem(Type type, Double x1, Double y1) {
        this(type, x1, y1, null, null, null, null);
    }

    //Use class Bimax, GsonScript, Test
    public GsonElem(Type type, Double x1, Double y1, Double h) {
        this(type, x1, y1, null, null, h, null);
    }

    //Use Bimax
    public GsonElem(Type type, Double x1, Double y1, String param) {
        this(type, x1, y1, null, null, null, param);
    }

    //Use class Bimax, GsonScript, GsonElem 
    public GsonElem(Type type, Double x1, Double y1, Double x2, Double y2) {
        this(type, x1, y1, x2, y2, null, null);
    }

    public GsonElem(Type type, Double x1, Double y1, Double h, String param) {
        this(type, x1, y1, null, null, h, param);
    }

    //Use class Bimax
    public GsonElem(Type type, Double x1, Double y1, Double x2, Double y2, String param) {
        this(type, x1, y1, x2, y2, null, param);
    }

    public GsonElem(Type type, Double x1, Double y1, Double x2, Double y2, Double h, String param) {
        this.id = ++gsonId;
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.h = h;
        this.param = new Gson().fromJson(param, JsonObject.class); //��������� ��������
    }

    public GsonElem addArea(GsonElem area) {
        area.owner = this;
        childs = (childs == null) ? new ArrayList<GsonElem>() : childs;
        this.childs.add(area);
        return area;
    }

    public GsonElem addElem(GsonElem elem) {
        elem.owner = this;
        childs = (childs == null) ? new ArrayList<GsonElem>() : childs;
        this.childs.add(elem);
        return this;
    }
    
    /**
     * ��������� ��������� ���� �����
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
            System.err.println("������:GeoElem.setOwnerAndForm() " + e);
        }
    }

    public static void setMaxID(Wincalc winc) {
        double max = 0;
        for (Com5t e : winc.listAll) {
            max = e.id > max ? e.id : max;
        }
        GsonElem.gsonId = max;
    }
    
    public void serialize(GsonElem gsonElem) {
        if (gsonElem == this && this.param != null && this.param.size() == 0) {
            this.param = null;
        }
        if (this.childs != null) {
            for (GsonElem el : this.childs) {
                if (el.param != null && el.param.size() == 0) {
                    el.param = null;
                }
                el.serialize(this); //��������  
            }
        }
    }
    
    @Override
    public String toString() {
        return "id= " + id + ", type= " + type;
    }    
}
