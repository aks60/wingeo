package startup;

import builder.Wincalc;
import builder.model1.ElemJoining;
import frames.swing.FrameToFile;
import common.eProfile;
import common.listener.ListenerRecord;
import dataset.Conn;
import dataset.Field;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eParmap;
import domain.eColor;
import domain.eCurrenc;
import domain.eElemdet;
import domain.eElement;
import domain.eElempar1;
import domain.eElempar2;
import domain.eFurndet;
import domain.eFurniture;
import domain.eFurnpar1;
import domain.eFurnpar2;
import domain.eFurnside1;
import domain.eFurnside2;
import domain.eGlasdet;
import domain.eGlasgrp;
import domain.eGlaspar1;
import domain.eGlaspar2;
import domain.eGlasprof;
import domain.eGroups;
import domain.eJoindet;
import domain.eJoining;
import domain.eJoinpar1;
import domain.eJoinpar2;
import domain.eJoinvar;
import domain.eKitdet;
import domain.eKitpar2;
import domain.eKits;
import domain.eParams;
import domain.ePrjpart;
import domain.ePrjprod;
import domain.eProject;
import domain.ePrjkit;
import domain.eRulecalc;
import domain.eSetting;
import domain.eSysfurn;
import domain.eSysmodel;
import domain.eSyspar1;
import domain.eSysprod;
import domain.eSysprof;
import domain.eSyssize;
import domain.eSystree;
import domain.eSysuser;
import frames.AboutBox;
import frames.Artikles;
import frames.Colors;
import frames.dialog.DicCurrenc;
import frames.PSCompare;
import frames.Elements;
import frames.Fillings;
import frames.Furniturs;
import frames.Groups;
import frames.Joinings;
import frames.Kits;
import frames.Models;
import frames.Orders;
import frames.Param;
import frames.Partner;
import frames.Rulecalc;
import frames.Setting;
import frames.Specifics;
import frames.Syssize;
import frames.Systree;
import frames.PSFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.Set;
import javax.swing.JFrame;

public enum App {

    Top, Setting, Groups, Colors, Artikles, Joining, Element, Param,
    Filling, Furniture, Kits, Systree, Partner, Order, AboutBox, Models,
    Specification, Syssize, RuleCalc, PSFrame, PSCompare;
    public javax.swing.JFrame frame;
    public static javax.swing.JFrame active;

    public void createFrame(java.awt.Window parent, Object... param) {
        if (frame != null) {
            frame.dispose();
        }
        try {
            switch (this) {

                case PSFrame:
                    frame = new PSFrame();
                    break;
                case Setting:
                    frame = new Setting(parent);
                    break;
                case RuleCalc:
                    frame = new Rulecalc();
                    break;
                case AboutBox:
                    frame = new AboutBox();
                    break;
                case Artikles:
                    if (param.length == 1) {
                        if (param[0] instanceof Record) {
                            frame = new Artikles(parent, (Record) param[0]);
                        } else if (param[0] instanceof ListenerRecord) {
                            frame = new Artikles(parent, (ListenerRecord) param[0]);
                        }
                    } else {
                        frame = new Artikles();
                    }
                    break;
                case Groups:
                    if (param.length == 2) {
                        frame = new Groups((int) param[0], (ListenerRecord) param[1]);
                    } else {
                        frame = new Groups((int) param[0]);
                    }
                    break;
                case Colors:
                    frame = new Colors();
                    break;
                case Joining:
                    if (param.length == 0) {
                        frame = new Joinings();
                    } else if (param.length == 1) {
                        if (param[0] instanceof ElemJoining) {
                            frame = new Joinings((ElemJoining) param[0]);
                        } else {
                            frame = new Joinings((int) param[1]);
                        }
                    }
                    break;
                case Element:
                    if (param.length == 0) {
                        frame = new Elements();
                    } else if (param.length == 1) {
                        frame = new Elements((int) param[0]);
                    }
                    break;
                case Param:
                    if (param.length == 0) {
                        frame = new Param();
                    } else {
                        frame = new Param(parent, (ListenerRecord) param[0]);
                    }
                    break;
                case Filling:
                    if (param.length == 0) {
                        frame = new Fillings();
                    } else if (param.length == 1) {
                        frame = new Fillings((int) param[0]);
                    }
                    break;
                case Furniture:
                    if (param.length == 0) {
                        frame = new Furniturs();
                    } else if (param.length == 1) {
                        frame = new Furniturs((int) param[0]);
                    }
                    break;
                case Kits:
                    frame = new Kits();
                    break;
                case Systree:
                    if (param.length == 0) {
                        frame = new Systree();
                    } else {
                        frame = new Systree((int) param[0], (int) param[1]);
                    }
                    break;
                case Partner:
                    if (param.length == 0) {
                        frame = new Partner();
                    } else {
                        frame = new Partner((int) param[0]);
                    }
                    break;
                case Order:
                    frame = new Orders();
                    break;
                case Models:
                    frame = new Models();
                    break;
                case Specification:
                    frame = new Specifics();
                    break;
                case Syssize:
                    frame = new Syssize();
                    break;
                case PSCompare:
                    if (param.length == 0) {
                        frame = new PSCompare();
                    } else {
                        frame = new PSCompare((Wincalc) param[0]);
                    }
                    break;
            }
            //eProfile.appframe = frame;
            active = frame;
            frame.setName(this.name());
            FrameToFile.setFrameSize(frame); //размеры окна
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowDeiconified(java.awt.event.WindowEvent evt) {
                    Top.frame.setExtendedState(JFrame.NORMAL);
                }
            });
            frame.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage());
            frame.setVisible(true);
        } catch (Exception e) {
            //frame.dispose();
            //frame = null;
            System.err.println("ОШИБКА:startup.App.createFrame() " + e);
        }
    }

    public static void createApp(eProfile profile) {

        try {
            ResultSet rs = Conn.connection().createStatement().executeQuery("select current_user from rdb$database");
            rs.next();
            eProfile.user = rs.getString(1);
            eProfile.profile = profile; //профиль приложения
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            if (profile.equals(eProfile.P01)) {
                Top.frame = new Adm();

            } else if (profile.equals(eProfile.P02)) {
                Top.frame = new Tex();
                if (System.getProperty("os.name").equals("Windows 10") == true
                        || System.getProperty("os.name").equals("Windows 11") == true) {
                    Top.frame.setPreferredSize(new java.awt.Dimension(800, 94));
                    Top.frame.setMinimumSize(new java.awt.Dimension(800, 94));
                } else if (System.getProperty("os.name").equals("Windows 7") == true) {
                    Top.frame.setPreferredSize(new java.awt.Dimension(800, 80));
                    Top.frame.setMinimumSize(new java.awt.Dimension(800, 80));
                }

            } else {
                Top.frame = new Man();
                if (System.getProperty("os.name").equals("Windows 10") == true
                        || System.getProperty("os.name").equals("Windows 11") == true) {
                    Top.frame.setPreferredSize(new java.awt.Dimension(800, 98));
                    Top.frame.setMinimumSize(new java.awt.Dimension(800, 98));
                } else if (System.getProperty("os.name").equals("Windows 7") == true) {
                    Top.frame.setPreferredSize(new java.awt.Dimension(800, 84));
                    Top.frame.setMinimumSize(new java.awt.Dimension(800, 84));
                }
            }
            Top.frame.setName(profile.name());
            if (profile.equals(eProfile.P01)) {
                FrameToFile.setFrameSize(Top.frame); //размеры окна
            } else {
                Top.frame.setLocation(0, 0);
                Top.frame.setSize(screenSize.width, Top.frame.getHeight()); //размеры гл. окна
            }

            Top.frame.setVisible(true);
            //Top.frame.setExtendedState(JFrame.ICONIFIED);
            //Top.frame.setState(Frame.ICONIFIED);

        } catch (Exception e) {
            System.err.println("Ошибка: App.createApp()");
        }
    }

    //Список таблиц базы данных
    public static Field[] db = { //в порядке удаления при конвертирования из базы приёмника
        //eSetting.up, 
        eSyspar1.up, eSysprof.up, eSysfurn.up, eSysprod.up, eSysmodel.up,
        eKitpar2.up, eKitdet.up, eKits.up,
        eJoinpar2.up, eJoinpar1.up, eJoindet.up, eJoinvar.up, eJoining.up,
        eElempar1.up, eElempar2.up, eElemdet.up, eElement.up,
        eGlaspar1.up, eGlaspar2.up, eGlasdet.up, eGlasprof.up, eGlasgrp.up,
        eFurnpar1.up, eFurnpar2.up, eFurnside1.up, eFurnside2.up, eFurndet.up, eFurniture.up,
        eParmap.up, eParams.up, eColor.up,
        ePrjkit.up, ePrjprod.up, eProject.up, ePrjpart.up,
        eRulecalc.up, eSystree.up,
        eArtdet.up, eArtikl.up,
        eSyssize.up, eGroups.up, eCurrenc.up, eSysuser.up
    };
}
