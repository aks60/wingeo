package builder;

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
import com.google.gson.JsonSyntaxException;
import common.UCom;
import common.ePrefs;
import common.listener.ListenerAction;
import common.listener.ListenerKey;
import common.listener.ListenerMouse;
import dataset.Record;
import domain.eArtikl;
import domain.ePrjprod;
import domain.eProject;
import domain.eSyspar1;
import domain.eSysprof;
import domain.eSyssize;
import enums.Type;
import enums.UseArtiklTo;
import frames.swing.cmp.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import static startup.App.Top;

// ��. �������� git -> 804d27409d
public class Wincalc {

    public Integer nuni = 0; //��� �������  
    public double nppID = 0; //��� ��������� ����� � ������������
    public int colorID1 = -1, colorID2 = 1, colorID3 = -1; //�������,�����,������� 
    public Record syssizRec = null; //������� ���������
    public double cost1 = 0; //��������� ��� ������
    public double cost2 = 0; //��������� �� �������
    public double weight = 0; //����� �����������  
    public BufferedImage bufferImg = null;  //����� �������
    public ImageIcon imageIcon = null; //������� �����������
    public Graphics2D gc2d = null; //����������� �������� �������  
    public double scale = 1; //����������� ������
    public Canvas canvas = null;
    public GsonRoot gson = null; //��������� ������ ����������� 1-�� ������
    public AreaSimple root = null; //��������� ������ ����������� 2-�� ������    

    public ListenerAction actionEvent = new ListenerAction() {
        public void action() {
        }
    };
    public ArrayList<ListenerKey> keyboardPressed = new ArrayList<ListenerKey>();
    public ArrayList<ListenerMouse> mousePressed = new ArrayList<ListenerMouse>();
    public ArrayList<ListenerMouse> mouseDragged = new ArrayList<ListenerMouse>();

    public HashMap<Integer, Record> mapPardef = new HashMap<>(); //���. �� ��������� + ���������� ���. �������
    public ArrayList<AreaSimple> listArea = new ArrayList<AreaSimple>(); //������ ����.
    public ArrayList<ElemSimple> listElem = new ArrayList<ElemSimple>(); //������ ����.
    public ArrayList<ElemJoining> listJoin = new ArrayList<ElemJoining>(); //������ ����.
    public ArrayList<Com5t> listAll = new ArrayList<Com5t>(); //������ ���� ����������� (area + elem)
    public ArrayList<TRecord> listSpec = new ArrayList<TRecord>(); //������������
    public ArrayList<TRecord> listKit = new ArrayList<TRecord>(); //������������

    public Wincalc() {
    }

    public Wincalc(String script) {
        build(script);
    }

    public void build(String script) {
        try {
            //System.out.println(new GsonBuilder().create().toJson(JsonParser.parseString(script))); //��� ������������
            //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(script)));

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
            
        } catch (JsonSyntaxException e) {
            System.out.println("������: Wincalc.build()");
        }
    }

    //����������� ���������� root �� ����������� gson 
    private void creator(AreaSimple owner, GsonElem gson) {
        try {
            if (gson.childs != null) {
                LinkedHashMap<AreaSimple, GsonElem> hm = new LinkedHashMap();
                for (GsonElem js : gson.childs) {

                    if (Type.STVORKA == js.type) {
                        AreaStvorka area5e = new AreaStvorka(this, js, owner);
                        owner.childs.add(area5e); //������� ������ ��������
                        hm.put(area5e, js); //���������� ����

                    } else if (Type.AREA == js.type) {
                        AreaSimple area5e = new AreaSimple(this, js, owner);
                        owner.childs.add(area5e); //������� ������ ��������
                        hm.put(area5e, js); //���������� ����

                    } else if (Type.BOX_SIDE == js.type) {
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
            System.err.println("������:Wincalc.creator() " + e);
        }
    }

    //�����.�����. ��������� �����������
    public void location() {
        try {
            listElem.forEach(e -> e.initArtikle());
            root.setLocation();
            UCom.filterNo(listElem, Type.GLASS).forEach(e -> e.setLocation());
            UCom.filterNo(listArea, Type.STVORKA).forEach(e -> e.setLocation());
            
            UCom.filter(listArea, Type.STVORKA).forEach(e -> ((AreaStvorka) e).newStvside());
            UCom.filter(listArea, Type.STVORKA).forEach(a -> a.frames.forEach(e -> e.initArtikle()));
            UCom.filter(listArea, Type.STVORKA).forEach(e -> e.setLocation());
    
            UCom.filter(listElem, Type.STV_SIDE, Type.GLASS).forEach(e -> e.setLocation());

            //���������� ���, �������� � �������             
            root.addJoining();  //L � T ����������
            UCom.filter(listArea, Type.STVORKA).forEach(e -> e.addJoining()); //����. ����.

        } catch (Exception s) {
            System.err.println("������:Wincalc.location() " + s);
        }
    }

    //������������ � ����������� 
    public void specific(boolean norm_otx) {
        specific(norm_otx, false);
    }

    public void specific(boolean norm_otx, boolean man) {
        this.weight = 0;
        this.cost1 = 0;
        this.cost2 = 0;
        this.listSpec.clear();
        try {
            //������������ ������� ��������� �����������
            listElem.forEach(elem -> elem.setSpecific());

            //������ �������� ����� ����������� �������� � ������������ ����� ������� addSpecific();
            new builder.making.TJoining(this).join(); //����������
            new builder.making.TElement(this).elem(); //�������
            new builder.making.TFilling(this).fill(); //����������
            new builder.making.TFurniture(this).furn(); //��������� 
            new builder.making.TTariffic(this, norm_otx).calculate(); //�����������

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
            //���� ������������ �� ������� ���������
            if (man == true) {
                int prjprodID = Integer.valueOf(ePrefs.prjprodID.getProp());
                Record prjprodRec = ePrjprod.find(prjprodID);
                Record projectRec = eProject.find(prjprodRec.getInt(ePrjprod.project_id));
                if (prjprodRec != null) {
                    //������ ���������
                    double disc = projectRec.getDbl(eProject.disc_all, 0) + projectRec.getDbl(eProject.disc_win, 0);
                    for (TRecord tRecord : this.listSpec) {
                        tRecord.cost2 = tRecord.cost2 - disc * tRecord.cost2 / 100; //������ ���������
                    }
                } else {
                    JOptionPane.showMessageDialog(Top.frame, "�������� ����������� � ������ �������", "��������������", JOptionPane.OK_OPTION);
                }
            }

            //�������� ���������
            for (TRecord spc : this.listSpec) {
                this.cost1 += spc.cost1; //����� ��������� ��� ������
                this.cost2 += spc.cost2; //����� ��������� �� �������                                   
            }

            //��� �������
            ArrayList<ElemSimple> glassList = UCom.filter(listElem, Type.GLASS);
            for (ElemSimple el : glassList) {
                this.weight += el.artiklRecAn.getDbl(eArtikl.density) * el.width() * el.height() / 1000000; //��.��� * ������� = ���
            }

            Collections.sort(this.listSpec, (o1, o2) -> (o1.place.subSequence(0, 3) + o1.name + o1.width).compareTo(o2.place.subSequence(0, 3) + o2.name + o2.width));

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
            UCom.filter(this.listElem, Type.BOX_SIDE).stream().forEach(el -> el.paint());

            //���������� �������� �������
            UCom.filter(this.listElem, Type.STV_SIDE).stream().forEach(el -> el.paint());

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
    // </editor-fold>  
}
