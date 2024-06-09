package frames;

import builder.Wincalc;
import builder.model.AreaSimple;
import builder.model.Com5t;
import builder.model.ElemFrame;
import builder.model.ElemSimple;
import common.ArrayCom;
import enums.Layout;
import enums.Type;
import frames.swing.DefMutableTreeNode;

public class UTree {

    private static Wincalc winc = null;
    private static DefMutableTreeNode root = null;
    private static DefMutableTreeNode frm = null;

    /**
     * Заполнение JTree
     */
    public static DefMutableTreeNode loadWinTree(Wincalc w) {
        try {
            winc = w;
            root = new DefMutableTreeNode(winc.root);
            root.add(new DefMutableTreeNode(new Com5t(Type.PARAM)));
            //Рама
            frm = root.add(new DefMutableTreeNode(new Com5t(Type.FRAME)));
            frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.LEFT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.BOTT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.RIGHT)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.TOP)));
            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            loadWinTree(winc.root.childs);

        } catch (Exception e) {
            System.err.println("Ошибка:UTree.loadWinTree() " + e);
        }
        return root;
    }

    /**
     * Функция рекурсии
     */
    public static void loadWinTree(ArrayCom<Com5t> childs) {
        try {
            for (Com5t com : childs) {
                //Если это не створка
                if (com.type != Type.STVORKA) {
                    //Если это элемент
                    if (com instanceof ElemSimple) {
                        frm.add(new DefMutableTreeNode(com));
                        if (com.type != Type.GLASS) {
                            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                            }));
                        }
                        //Если это ареа                   
                    } else {
                        loadWinTree(((AreaSimple) com).childs);
                    }
                } else {
                    loadStvorka(com); //створка
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UTree.loadWinTree(2) " + e);
        }
    }

    /**
     * Створка
     */
    public static void loadStvorka(Com5t com) {
        try {
            DefMutableTreeNode nodeStv = root.add(new DefMutableTreeNode(com)); //створка всегда на верху, нет створка в створке
            AreaSimple stv = (AreaSimple) com;

            //Цыкл по стор. створки
            for (int i = 0; i < stv.frames.size(); i++) {
                ElemFrame frame = (ElemFrame) stv.frames.get(i);
                nodeStv.add(new DefMutableTreeNode(frame));
                nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
            }

            //Цикл по детям створки
            for (Com5t com2 : ((AreaSimple) com).childs) {
                //Если это элемент
                if (com2 instanceof ElemSimple) {
                    nodeStv.add(new DefMutableTreeNode(com2));
                    if (com2.type != Type.GLASS && com2.type != Type.MOSKITKA) { //В стекле нет соединений
                        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                    }
                    //Это ареа
                } else {
                    for (Com5t com3 : ((AreaSimple) com2).childs) {
                        if (com3 instanceof ElemSimple) {
                            nodeStv.add(new DefMutableTreeNode(com3));
                            if (com3.type != Type.GLASS && com3.type != Type.MOSKITKA) {
                                nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                            }
                        } else {
                            for (Com5t com4 : ((AreaSimple) com3).childs) {
                                if (com4 instanceof ElemSimple) {
                                    nodeStv.add(new DefMutableTreeNode(com4));
                                    if (com4.type != Type.GLASS && com4.type != Type.MOSKITKA) {
                                        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UTree.loadStvorka() " + e);
        }
    }
}
