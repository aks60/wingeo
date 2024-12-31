package frames.swing;

import common.eProp;
import domain.ePrjprod;
import domain.eProject;
import java.util.List;
import javax.swing.*;


public class MainMenu {

    
    private static int prjprodID;
    private static dataset.Record prjprodRec;
    //private static List<dataset.Record> prjprodListOne;

    private static int progectID;
    private static dataset.Record projectRec;
    //private static List<dataset.Record> prjprodListAll;
    
    public static void init(javax.swing.JFrame frame, javax.swing.JMenu mn08) {

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale);
        javax.swing.Icon icon = new javax.swing.ImageIcon(frame.getClass().getResource("/resource/img16/b058.gif"));
        
        prjprodID = Integer.valueOf(eProp.prjprodID.read());
        prjprodRec = ePrjprod.find(prjprodID);
        //prjprodListOne = List.of(prjprodRec);

        progectID = Integer.valueOf(eProp.orderID.read());
        projectRec = eProject.find(progectID);
        //prjprodListAll = ePrjprod.filter(progectID);

        javax.swing.JMenu jmenu01 = new javax.swing.JMenu();
        mn08.add(jmenu01);
        jmenu01.setIcon(icon); // NOI18N
        jmenu01.setText(bundle.getString("Меню.Изделие")); // NOI18N
        jmenu01.setFont(frames.UGui.getFont(0, 1));     
        
        javax.swing.JMenuItem menuItem01 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem02 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem03 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem04 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem05 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem06 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem07 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem08 = new javax.swing.JMenuItem();
        
        javax.swing.JMenu jmenu02 = new javax.swing.JMenu();
        mn08.add(jmenu02);
        jmenu02.setIcon(icon); // NOI18N
        jmenu02.setText(bundle.getString("Меню.Заказ")); // NOI18N
        jmenu02.setFont(frames.UGui.getFont(0, 1));
        
        javax.swing.JMenuItem menuItem21 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem22 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem23 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem24 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem25 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem26 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem27 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem menuItem28 = new javax.swing.JMenuItem();
        
        javax.swing.JPopupMenu.Separator sep1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JPopupMenu.Separator sep2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JPopupMenu.Separator sep3 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JPopupMenu.Separator sep4 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JPopupMenu.Separator sep5 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JPopupMenu.Separator sep6 = new javax.swing.JPopupMenu.Separator();

        menuItem01.setFont(frames.UGui.getFont(0, 1));
        menuItem01.setIcon(icon); // NOI18N
        menuItem01.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        menuItem01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem19(evt);
            }
        });
        jmenu01.add(menuItem01);

        menuItem02.setFont(frames.UGui.getFont(0, 1));
        menuItem02.setIcon(icon); // NOI18N
        menuItem02.setText(bundle.getString("Меню.Расход материалов")); // NOI18N
        menuItem02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem20(evt);
            }
        });
        jmenu01.add(menuItem02);

        menuItem03.setFont(frames.UGui.getFont(0, 1));
        menuItem03.setIcon(icon); // NOI18N
        menuItem03.setText(bundle.getString("Меню.Задание в цех")); // NOI18N
        menuItem03.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem21(evt);
            }
        });
        jmenu01.add(menuItem03);
        jmenu01.add(sep4);

        menuItem04.setFont(frames.UGui.getFont(0, 1));
        menuItem04.setIcon(icon); // NOI18N
        menuItem04.setText(bundle.getString("Меню.Смета")); // NOI18N
        menuItem04.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem22(evt);
            }
        });
        jmenu01.add(menuItem04);

        menuItem05.setFont(frames.UGui.getFont(0, 1));
        menuItem05.setIcon(icon); // NOI18N
        menuItem05.setText(bundle.getString("Меню.Смета подробная")); // NOI18N
        menuItem05.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem23(evt);
            }
        });
        jmenu01.add(menuItem05);
        jmenu01.add(sep5);

        menuItem06.setFont(frames.UGui.getFont(0, 1));
        menuItem06.setIcon(icon); // NOI18N
        menuItem06.setText(bundle.getString("Меню.Счёт")); // NOI18N
        menuItem06.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem24(evt);
            }
        });
        jmenu01.add(menuItem06);

        menuItem07.setFont(frames.UGui.getFont(0, 1));
        menuItem07.setIcon(icon); // NOI18N
        menuItem07.setText(bundle.getString("Меню.Счёт-фактура")); // NOI18N
        menuItem07.setToolTipText("");
        menuItem07.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem25(evt);
            }
        });
        jmenu01.add(menuItem07);
        jmenu01.add(sep6);

        menuItem06.setFont(frames.UGui.getFont(0, 1));
        menuItem06.setIcon(icon); // NOI18N
        menuItem06.setText(bundle.getString("Меню.Ком-ое предл...")); // NOI18N
        menuItem06.setToolTipText("");
        menuItem06.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem26(evt);
            }
        });
        jmenu01.add(menuItem06);


        menuItem21.setFont(frames.UGui.getFont(0, 1));
        menuItem21.setIcon(icon); // NOI18N
        menuItem21.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        menuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem1(evt);
            }
        });
        jmenu02.add(menuItem21);

        menuItem22.setFont(frames.UGui.getFont(0, 1));
        menuItem22.setIcon(icon); // NOI18N
        menuItem22.setText(bundle.getString("Меню.Расход материалов")); // NOI18N
        menuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem11(evt);
            }
        });
        jmenu02.add(menuItem22);

        menuItem23.setFont(frames.UGui.getFont(0, 1));
        menuItem23.setIcon(icon); // NOI18N
        menuItem23.setText(bundle.getString("Меню.Задание в цех")); // NOI18N
        menuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem18(evt);
            }
        });
        jmenu02.add(menuItem23);
        jmenu02.add(sep1);

        menuItem24.setFont(frames.UGui.getFont(0, 1));
        menuItem24.setIcon(icon); // NOI18N
        menuItem24.setText(bundle.getString("Меню.Смета")); // NOI18N
        menuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem14(evt);
            }
        });
        jmenu02.add(menuItem24);

        menuItem25.setFont(frames.UGui.getFont(0, 1));
        menuItem25.setIcon(icon); // NOI18N
        menuItem25.setText(bundle.getString("Меню.Смета подробная")); // NOI18N
        menuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem13(evt);
            }
        });
        jmenu02.add(menuItem25);
        jmenu02.add(sep2);

        menuItem26.setFont(frames.UGui.getFont(0, 1));
        menuItem26.setIcon(icon); // NOI18N
        menuItem26.setText(bundle.getString("Меню.Счёт")); // NOI18N
        menuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem15(evt);
            }
        });
        jmenu02.add(menuItem26);

        menuItem27.setFont(frames.UGui.getFont(0, 1));
        menuItem27.setIcon(icon); // NOI18N
        menuItem27.setText(bundle.getString("Меню.Счёт-фактура")); // NOI18N
        menuItem27.setToolTipText("");
        menuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem16(evt);
            }
        });
        jmenu02.add(menuItem27);
        jmenu02.add(sep3);

        menuItem28.setFont(frames.UGui.getFont(0, 1));
        menuItem28.setIcon(icon); // NOI18N
        menuItem28.setText(bundle.getString("Меню.Ком-ое предл...")); // NOI18N
        menuItem28.setToolTipText("");
        menuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //menuItem17(evt);
            }
        });
        jmenu02.add(menuItem28);
    }
}
