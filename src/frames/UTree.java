package frames;

import builder.Wincalc;
import builder.model.AreaSimple;
import builder.model.Com5t;
import builder.model.ElemFrame;
import builder.model.ElemSimple;
import builder.script.GsonElem;
import common.UCom;
import enums.Layout;
import enums.Type;
import enums.TypeArt;
import frames.swing.DefMutableTreeNode;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

public class UTree {

    private static Wincalc winc = null;
    private static DefMutableTreeNode root = null;
    private static DefMutableTreeNode frm = null;

    //�������� tree �����������
    public static DefMutableTreeNode loadWinTree(Wincalc w) {
        try {
            winc = w;
            root = new DefMutableTreeNode(winc.root);
            root.add(new DefMutableTreeNode(new Com5t(Type.PARAM)));
            //����
            frm = root.add(new DefMutableTreeNode(new Com5t(Type.FRAME)));
            frm.add(new DefMutableTreeNode(UCom.layout(winc.root.frames, Layout.LEFT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(UCom.layout(winc.root.frames, Layout.BOTT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(UCom.layout(winc.root.frames, Layout.RIGHT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(UCom.layout(winc.root.frames, Layout.TOP)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            loadWinTree(winc.root.childs);

        } catch (Exception e) {
            System.err.println("������:UTree.loadWinTree() " + e);
        }
        return root;
    }

    //������� ��������
    public static void loadWinTree(ArrayList<Com5t> childs) {
        try {
            for (Com5t com : childs) {
                //���� ��� �� �������
                if (com.type != Type.STVORKA) {
                    //���� ��� �������
                    if (com instanceof ElemSimple) {
                        frm.add(new DefMutableTreeNode(com));
                        if (com.type != Type.GLASS) {
                            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                            }));
                        }
                        //���� ��� ����                   
                    } else {
                        loadWinTree(((AreaSimple) com).childs);
                    }
                } else {
                    loadStvorka(com); //�������
                }
            }
        } catch (Exception e) {
            System.err.println("������:UTree.loadWinTree(2) " + e);
        }
    }

    //�������
    public static void loadStvorka(Com5t com) {
        try {
            DefMutableTreeNode nodeStv = root.add(new DefMutableTreeNode(com)); //������� ������ �� �����, ��� ������� � �������
            AreaSimple stv = (AreaSimple) com;

            //���� �� ����. �������
            for (int i = 0; i < stv.frames.size(); i++) {
                ElemFrame frame = (ElemFrame) stv.frames.get(i);
                nodeStv.add(new DefMutableTreeNode(frame));
                nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            }

            //���� �� ����� �������
            for (Com5t com2 : ((AreaSimple) com).childs) {
                //���� ��� �������
                if (com2 instanceof ElemSimple) {
                    nodeStv.add(new DefMutableTreeNode(com2));
                    if (com2.type != Type.GLASS && com2.type != Type.MOSQUIT) { //� ������ ��� ����������
                        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                    }
                    //��� ����
                } else {
                    for (Com5t com3 : ((AreaSimple) com2).childs) {
                        if (com3 instanceof ElemSimple) {
                            nodeStv.add(new DefMutableTreeNode(com3));
                            if (com3.type != Type.GLASS && com3.type != Type.MOSQUIT) {
                                nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                            }
                        } else {
                            for (Com5t com4 : ((AreaSimple) com3).childs) {
                                if (com4 instanceof ElemSimple) {
                                    nodeStv.add(new DefMutableTreeNode(com4));
                                    if (com4.type != Type.GLASS && com4.type != Type.MOSQUIT) {
                                        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:UTree.loadStvorka() " + e);
        }
    }

    //�������� tree ���������
    public static void loadArtTree(DefaultMutableTreeNode root) {
        DefaultMutableTreeNode node = null;

        for (TypeArt it : TypeArt.values()) {
            if (it.id1 == 1 && it.id2 == 0) {
                node = new DefaultMutableTreeNode(TypeArt.X100); //"�������"

            } else if (it.id1 == 2 && it.id2 == 0) {
                root.add(node);
                node = new DefaultMutableTreeNode(TypeArt.X200); //"����������"

            } else if (it.id1 == 3 && it.id2 == 0) {
                root.add(node);
                node = new DefaultMutableTreeNode(TypeArt.X300); //"�������"

            } else if (it.id1 == 4 && it.id2 == 0) {
                root.add(node);
                node = new DefaultMutableTreeNode(TypeArt.X400); //"����������"

            } else if (it.id1 == 5 && it.id2 == 0) {
                root.add(node);
                node = new DefaultMutableTreeNode(TypeArt.X500); //"����������"

//            } else if (it.id1 == 6 && it.id2 == 0) {
//                root.add(node);
//                node = new DefaultMutableTreeNode(TypeArt.X600); //"������"                
            } else if (it.id2 > 0) {   //���������       
                root.add(node);
                node.add(new javax.swing.tree.DefaultMutableTreeNode(it));
            }
        }
        root.add(node);
    }

//    //����� � ������ GsonElem �� ID
//    public static GsonElem findTreeID(GsonElem gs, double ID) {
//            for (GsonElem gson: gs.childs) {
//                System.out.println("gson.id = " + gson.id);
//                if (ID == gson.id) {
//                    return gson;
//                } else {
//                   findTreeID(gson, ID); 
//                }
//            }
//        return null;
//    }
}
