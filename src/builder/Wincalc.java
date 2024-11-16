package builder;

import builder.making.TJoining;
import builder.model.AreaRectangl;
import builder.model.AreaSimple;
import builder.model.AreaStvorka;
import builder.model.Com5t;
import builder.model.ElemCross;
import builder.model.ElemFrame;
import builder.model.ElemGlass;
import builder.model.ElemSimple;
import builder.making.TRecord;
import builder.making.UColor;
import builder.model.AreaArch;
import builder.model.AreaDoor;
import builder.model.AreaTrapeze;
import builder.model.ElemBlinds;
import builder.model.ElemJoining;
import builder.model.ElemMosquit;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import common.UCom;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import domain.eSyspar1;
import domain.eSysprof;
import domain.eSyssize;
import enums.Type;
import enums.UseArtiklTo;
import frames.swing.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class Wincalc {

    public Integer nuni = 0; //��� �������  
    public double nppID = 0; //��� ��������� ����� � ������������
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //�������,�����,������� 
    public Record syssizRec = null; //������� ���������
    private double price1 = 0; //��������� ��� ������
    private double price2 = 0; //��������� � ��������������� �������
    public double weight = 0; //����� �����������  
    public BufferedImage bufferImg = null;  //����� �������
    public ImageIcon imageIcon = null; //������� �����������
    public Graphics2D gc2d = null; //����������� ������� �������  
    public double scale = 1; //����������� ������
    public Canvas canvas = null;
    public GsonRoot gson = null; //��������� ������ ����������� 1-�� ������
    public AreaSimple root = null; //��������� ������ ����������� 2-�� ������    

    public ArrayList<ListenerKey> keyboardPressed = new ArrayList<ListenerKey>();
    public ArrayList<ListenerMouse> mousePressed = new ArrayList<ListenerMouse>();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();

    public HashMap<Integer, Record> mapPardef = new HashMap<>(); //���. �� ��������� + ���������� ���. �������
    public ArrayList<AreaSimple> listArea = new ArrayList<AreaSimple>(); //������ ����.
    public ArrayList<ElemSimple> listElem = new ArrayList<ElemSimple>(); //������ ����.
    public ArrayList<ElemJoining> listJoin = new ArrayList<ElemJoining>(); //������ ����.
    public ArrayList<Com5t> listAll = new ArrayList<Com5t>(); //������ ���� ����������� (area + elem)
    public ArrayList<TRecord> listSpec = new ArrayList<TRecord>(); //������������

    public Wincalc() {
    }

    public Wincalc(String script) {
        build(script);
    }

    public void build(String script) {
        //System.out.println(new GsonBuilder().create().toJson(new com.google.gson.JsonParser().parse(script))); //��� ������������
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new com.google.gson.JsonParser().parse(script)));

        //���� �������
        nppID = 0;
        mapPardef.clear();
        List.of((List) listArea, (List) listElem, (List) listSpec, (List) listAll, (List) listJoin).forEach(el -> el.clear());

        //�������� Gson ������
        gson = new GsonBuilder().create().fromJson(script, GsonRoot.class);

        JsonParser parser = new JsonParser();
        JsonElement rootNode = parser.parse(script);

        gson.setOwner(this);

        //���� ������������
        nuni = (gson.nuni == null) ? -3 : gson.nuni;
        Record sysprofRec = eSysprof.find2(nuni, UseArtiklTo.FRAME); //������.������ �������
        Record artiklRec = eArtikl.find(sysprofRec.getInt(eSysprof.artikl_id), false); //�������
        syssizRec = eSyssize.find(artiklRec); //��������� ���������
        colorID1 = (gson.color1 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color1;
        colorID2 = (gson.color2 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color2;
        colorID3 = (gson.color3 == -3) ? UColor.colorFromArtikl(sysprofRec.getInt(eSysprof.artikl_id)) : gson.color3;

        //��������� �� ���������
        eSyspar1.filter(nuni).forEach(syspar1Rec -> mapPardef.put(syspar1Rec.getInt(eSyspar1.groups_id), syspar1Rec));

        //������� ����
        if (Type.RECTANGL == gson.type) {
            root = new AreaRectangl(this, gson);

        } else if (Type.TRAPEZE == gson.type) {
            root = new AreaTrapeze(this, gson);

        } else if (Type.ARCH == gson.type) {
            root = new AreaArch(this, gson);

        } else if (Type.DOOR == gson.type) {
            root = new AreaDoor(this, gson);
        }

        //�������� �����������
        creator(root, gson);

        //���������� �����������
        location();
    }

    private void creator(AreaSimple owner, GsonElem gson) {
        try {
            if (gson.childs != null) {
                LinkedHashMap<AreaSimple, GsonElem> hm = new LinkedHashMap();
                for (GsonElem js : gson.childs) {

                    if (Type.STVORKA == js.type) {
                        AreaSimple area5e = new AreaStvorka(this, js, owner);
                        owner.childs.add(area5e); //������� ������ ��������
                        hm.put(area5e, js); //���������� ����

                    } else if (Type.AREA == js.type) {
                        AreaSimple area5e = new AreaSimple(this, js, owner);
                        owner.childs.add(area5e); //������� ������ ��������
                        hm.put(area5e, js); //���������� ����

                    } else if (Type.FRAME_SIDE == js.type) {
                        ElemFrame elem5e = new ElemFrame(this, js.id, js, owner);
                        root.frames.add(elem5e);

                    } else if (List.of(Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).contains(js.type)) {
                        ElemCross elem5e = new ElemCross(this, js, owner);
                        owner.childs.add(elem5e); //������� ������ ��������
                        //UGeo.normalizeElem(elem5e);

                    } else if (Type.GLASS == js.type) {
                        ElemGlass elem5e = new ElemGlass(this, js, owner);
                        owner.childs.add(elem5e); //������� ������ ��������

                    } else if (Type.MOSQUIT == js.type) {
                        ElemMosquit elem5e = new ElemMosquit(this, js, owner);
                        owner.childs.add(elem5e); //������� ������ ��������

                    } else if (Type.BLINDS == js.type) {
                        ElemBlinds elem5e = new ElemBlinds(this, js, owner);
                        owner.childs.add(elem5e); //������� ������ ��������
                    }
                }
                //������ ��������� ��������
                for (Map.Entry<AreaSimple, GsonElem> entry : hm.entrySet()) {
                    creator(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            System.err.println("������:Wincalc.elements(*) " + e);
        }
    }

    //�����.�����. ��������� �����������
    public void location() {
        try {
            //������� ���� ������������ ��������� ����
            root.setLocation();

            //����. ��������� ��������� �����������
            listElem.forEach(e -> e.initArtikle());

            //�������� � ��������� ������ �������
            if (root.type == Type.DOOR) {
                UCom.filter(listArea, Type.STVORKA).forEach(e -> e.setLocation());
            }

            //����� �������� �� ���� � ������� �������� ��������
            UCom.filter(listElem, Type.IMPOST, Type.STOIKA, Type.ERKER, Type.SHTULP).forEach(e -> e.setLocation());

            //�������� � ��������� ������ �������
            if (root.type != Type.DOOR) {
                UCom.filter(listArea, Type.STVORKA).forEach(e -> e.setLocation());
            }

            //����. ��������� �������
            UCom.filter(listArea, Type.STVORKA).forEach(a -> a.frames.forEach(e -> e.initArtikle()));

            //������� ��������� ������ ����
            if (root.type == Type.DOOR) {
                for (ElemSimple elemSimple : UCom.filter(listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.GLASS)) {
                    elemSimple.setLocation();
                }
            } else {
                UCom.filter(listElem, Type.FRAME_SIDE, Type.STVORKA_SIDE, Type.GLASS).forEach(e -> e.setLocation());
            }

            //���������� ���, �������� � �������             
            root.addJoining();  //L � T ����������
            UCom.filter(listArea, Type.STVORKA).forEach(e -> e.addJoining()); //����. ����.

        } catch (Exception s) {
            System.err.println("������:Wincalc.location() " + s);
        }
    }

    //������������ � ����������� 
    public void specific(boolean norm_otx) {
        weight = 0;
        price1 = 0;
        price2 = 0;
        try {
            //������������ ������� ��������� �����������
            listElem.forEach(elem -> elem.setSpecific());

            //������ �������� ����� ����������� �������� � ������������ ����� ������� addSpecific();
            new builder.making.TJoining(this).join(); //����������
            new builder.making.TElement(this).elem(); //�������
            new builder.making.TFilling(this).fill(); //����������
            new builder.making.TFurniture(this).furn(); //��������� 
            new builder.making.TTariffic(this, norm_otx).calc(); //�����������

            //�������� ������ ������������
            for (ElemSimple elem5e : listElem) {
                if (elem5e.spcRec.artikl.isEmpty() || elem5e.spcRec.artikl.trim().charAt(0) != '@') {
                    this.listSpec.add(elem5e.spcRec);
                }
                for (TRecord spc : elem5e.spcRec.spcList) {
                    if (spc.artikl.isEmpty() || spc.artikl.trim().charAt(0) != '@') {
                        this.listSpec.add(spc);
                    }
                }
            }

            //�������� ���������
            for (TRecord spc : listSpec) {
                this.price1 = (this.price1 + spc.price1); //����� ��������� ��� ������
                this.price2 = (this.price2 + spc.price2); //����� ��������� �� �������                                   
            }

            //��� �������
            ArrayList<ElemSimple> glassList = UCom.filter(listElem, Type.GLASS);
            for (ElemSimple el : glassList) {
                this.weight += el.artiklRecAn.getDbl(eArtikl.density) * el.width() * el.height() / 1000000; //��.��� * ������� = ���
            }

            Collections.sort(listSpec, (o1, o2) -> (o1.place.subSequence(0, 3) + o1.name + o1.width).compareTo(o2.place.subSequence(0, 3) + o2.name + o2.width));

        } catch (Exception e) {
            System.err.println("������:Wincalc.constructiv() " + e);
        }
    }
    
    //������ �����������
    public void draw() {
        try {

            //���������� �������������
            UCom.filter(this.listElem, Type.GLASS).stream().forEach(el -> el.paint());

            //���������� ���������
            UCom.filter(this.listElem, Type.GLASS).stream().forEach(el -> ((ElemGlass) el).rascladkaPaint());

            //���������� ��������
            UCom.filter(this.listElem, Type.MOSQUIT).stream().forEach(el -> ((ElemMosquit) el).paint());

            //���������� ��������
            UCom.filter(this.listElem, Type.IMPOST, Type.SHTULP, Type.STOIKA).stream().forEach(el -> el.paint());

            //���������� ���
            UCom.filter(this.listElem, Type.FRAME_SIDE).stream().forEach(el -> el.paint());

            //���������� �������� �������
            UCom.filter(this.listElem, Type.STVORKA_SIDE).stream().forEach(el -> el.paint());

            //��������� ��������� �������
            UCom.filter(this.listArea, Type.STVORKA).stream().forEach(el -> el.paint());

            //��������� �����
            if (this.scale > .1) {
                this.root.paint();
            }

// <editor-fold defaultstate="collapsed" desc="���������"> 
/*            
            //���������� ���������
            winc.listElem.filter(Type.GLASS).stream().forEach(el -> el.rascladkaPaint());
            ���������� ��������
            this.listElem.filter(Type.MOSKITKA).stream().forEach(el -> el.paint());
            ������� � ������
            if (winc.bufferImg != null) {
                ByteArrayOutputStream byteArrOutStream = new ByteArrayOutputStream();
                ImageIO.write(winc.bufferImg, "png", byteArrOutStream);
                if (eProp.dev == true) {
                    File outputfile = new File("CanvasImage.png");
                    ImageIO.write(winc.bufferImg, "png", outputfile);
                }
            }
             */
// </editor-fold> 
        } catch (Exception s) {
            System.err.println("������:Wincalc.draw() " + s);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="GET AND SET"> 
    public double width() {
        return root.area.getGeometryN(0).getEnvelopeInternal().getWidth();
    }

    public double height() {
        return root.area.getGeometryN(0).getEnvelopeInternal().getHeight();
    }

    public double price(int index) {
        return (index == 1) ? price1 : price2;
    }
    // </editor-fold>  
}
