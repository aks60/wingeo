package frames;

import builder.Wincalc;
import builder.model.Com5t;
import common.ArrayCom;
import enums.Layout;
import enums.Type;
import frames.swing.DefMutableTreeNode;

public class UTree {

    public static DefMutableTreeNode loadWinTree(Wincalc winc) {

        DefMutableTreeNode root = new DefMutableTreeNode(winc.gson);
        root.add(new DefMutableTreeNode(new Com5t(Type.PARAM)));
        //Рама
//        DefMutableTreeNode frm = root.add(new DefMutableTreeNode(new GSonElem(Type.FRAME)));
//        Object obj = winc.root.frames.get(Layout.LEFT);
//        frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.LEFT)));
//        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
//        frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.BOTT)));
//        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
//        frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.RIGHT)));
//        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
//        frm.add(new DefMutableTreeNode(winc.root.frames.get(Layout.TOP)));
//        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));

        return root;
    }

    public static void loadWinTree(Wincalc winc, ArrayCom<Com5t> childs) {

    }
    
    public static void loadWinTree(Wincalc winc, DefMutableTreeNode root, Com5t com) {
        
    }
}
