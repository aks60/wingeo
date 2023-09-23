package frames;

import frames.swing.FrameToFile;
import builder.Wincalc;
import builder.making.Specific;
import common.UCom;
import dataset.Record;
import domain.eArtikl;
import frames.swing.TableFieldFilter;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import startup.Test;

public class PSCompare extends javax.swing.JFrame {

    enum Fld {
        ATYPM("уров1"), ATYPP("уров2"), ANUMB("артикул"), CLNUM("color1"), CLNU1("color2"), CLNU2("color3"),
        ALENG("длина"), ARADI("ширина"), AUG01("уг1"), AUG02("уг2"), AQTYP("кол"), AQTYA("погонаж"),
        APERC("норм.отх"), ASEB1("себест"), APRC1("стоим.без.ск.за ед.изм"), APRCD("стоим.со.ск.за.ед.изм");

        Fld(Object o) {
        }
    }
    private Connection cn = null;
    private DatabaseMetaData mdb = null;
    ResultSet rsm = null;
    private Graphics2D gc2d = null;
    private DecimalFormat df1 = new DecimalFormat("#0.0");
    private DecimalFormat df2 = new DecimalFormat("#0.00");
    private HashMap<Integer, String> hmColor = new HashMap();
    private JPanel paintPanel = new JPanel() {

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D gc2d = (Graphics2D) g;
            double max_w = 0;
            double max_h = 0;
            for (int i = 0; i < tab4.getRowCount(); i++) {
                double w = (double) tab4.getValueAt(i, 6);
                max_w = (w >= max_w) ? w : max_w;
                double h = (double) tab4.getValueAt(i, 7);
                max_h = (h >= max_h) ? h : max_h;
            }
            double k = (getWidth() / max_w > getHeight() / max_h) ? getHeight() / (max_h + 180) : getWidth() / (max_w + 40);
            double h2 = (this.getHeight()) / k;
            gc2d.scale(k, k);
            gc2d.setStroke(new BasicStroke(6)); //толщина линии
            gc2d.translate(10, -10);

            for (int i = 0; i < tab4.getRowCount(); i++) {
                double x1 = (double) tab4.getValueAt(i, 4);
                double y1 = (double) tab4.getValueAt(i, 5);
                double x2 = (double) tab4.getValueAt(i, 6);
                double y2 = (double) tab4.getValueAt(i, 7);

                if (tab4.getValueAt(i, 12).equals(1) || tab4.getValueAt(i, 12).equals(3)) {
                    g.setColor(java.awt.Color.BLACK);
                } else if (tab4.getValueAt(i, 12).equals(2)) {
                    g.setColor(java.awt.Color.BLUE);
                } else {
                    g.setColor(java.awt.Color.RED);
                }
                gc2d.drawLine((int) Math.round(x1), (int) Math.round(this.getHeight() / k - y1), (int) Math.round(x2), (int) Math.round(this.getHeight() / k - y2));

                if (Double.valueOf(tab4.getValueAt(i, 11).toString()) > 0) {
                    gc2d.drawArc((int) Math.round(x2), (int) Math.round(h2 - y2 - 100), (int) (x1 - x2), 180, 0, 180);
                }
            }
        }
    };


    /**
     * Конструктор из главного меню
     */
    public PSCompare() {
        initComponents();
        initElements();
        cn = Test.connect1();
        loadingData();
        loadingTabGroup1();
        pan7.add(paintPanel, java.awt.BorderLayout.CENTER);
        tabb.setSelectedIndex(4);
        tab1.setColumnSelectionInterval(3, 3);
    }
    
    /**
     * Конструктор из спецификации
     */
    public PSCompare(Wincalc winc) {
        initComponents();
        initElements();
        cn = Test.connect1();
        loadingData();
        loadingTabGroup2(winc);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tab1.getModel());
        tab1.setRowSorter(sorter);
        pan7.add(paintPanel, java.awt.BorderLayout.CENTER);
        tab1.setColumnSelectionInterval(3, 3);
    }

    public void loadingData() {
        try {
            mdb = cn.getMetaData();
            rsm = mdb.getTables(null, null, null, new String[]{"TABLE"});
            Statement st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("select CNUMB, CNAME from COLSLST");
            while (rs.next()) {
                hmColor.put(rs.getInt("CNUMB"), rs.getString("CNAME"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка: DBCompare.loadingData().  " + e);
        }
    }

    //select distinct a.punic, a.onumb from saveelm a, specpau b where a.punic = b.punic and a.onumb = b.onumb
    public void loadingTabGroup1() {
        try {
            cn = Test.connect1();
            Statement st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //=== Таблица 4 === 
            if (txt19.getText().isEmpty() == false) {
                int npp = 0;
                ((DefaultTableModel) tab4.getModel()).getDataVector().clear();
                ResultSet rs = st.executeQuery("select * from SAVEELM where TYPP != 0 and PUNIC = " + txt19.getText() + " and ONUMB = " + txt20.getText() + " order by TYPP");
                if (rs.isLast() == false) {
                    while (rs.next()) {
                        Vector vectorRec = new Vector();
                        vectorRec.add(++npp);
                        vectorRec.add(rs.getObject("PUNIC"));
                        vectorRec.add(rs.getObject("ONUMB"));
                        vectorRec.add(rs.getObject("ANUMB"));
                        vectorRec.add(rs.getObject("C1X"));
                        vectorRec.add(rs.getObject("C1Y"));
                        vectorRec.add(rs.getObject("C2X"));
                        vectorRec.add(rs.getObject("C2Y"));
                        vectorRec.add(rs.getObject("ALENG"));
                        vectorRec.add(rs.getObject("ALEN1"));
                        vectorRec.add(rs.getObject("ALEN2"));
                        vectorRec.add(rs.getObject("RAD"));
                        vectorRec.add(rs.getObject("TYPP"));
                        ((DefaultTableModel) tab4.getModel()).getDataVector().add(vectorRec);
                    }
                }
                npp = 0;
                rs.close();
                rs = st.executeQuery("select b.fname from savefur a, furnlst b where a.punic = " + txt19.getText() + " and a.onumb = " + txt20.getText() + " and a.funic = b.funic");
                while (rs.next()) {
                    ((DefaultTableModel) tab2.getModel()).addRow(new Object[]{rs.getString("FNAME")});
                }
                ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
                paintPanel.repaint();

                //=== Таблица 1 ===
                ((DefaultTableModel) tab1.getModel()).getDataVector().clear();
                rs = st.executeQuery("select a.* from SPECPAU a where a.PUNIC = " + txt19.getText() + "and a.ONUMB = " + txt20.getText() + "  and clke != -1 order by a.anumb");
                if (rs.isLast() == false) {
                    npp = 0;
                    while (rs.next()) {
                        Vector vectorRec = new Vector();
                        vectorRec.add(++npp);
                        for (int i = 0; i < Fld.values().length; i++) {
                            vectorRec.add(rs.getObject(Fld.values()[i].name()));
                        }
                        vectorRec.set(4, hmColor.get(vectorRec.get(4)));  //цвет
                        vectorRec.set(5, hmColor.get(vectorRec.get(5)));  //цвет
                        vectorRec.set(6, hmColor.get(vectorRec.get(6)));  //цвет
                        String artikl = rs.getString("ANUMB"); //артикул                              
                        Record artiklRec = eArtikl.query().stream().filter(r -> artikl.equals(r.get(eArtikl.code))).findFirst().orElse(eArtikl.up.newRecord());
                        vectorRec.add(4, artiklRec.get(eArtikl.name)); //имя артикула                 
                        vectorRec.add(null); //стоим. элемента без скидки
                        vectorRec.add(null); //стоим. элемента со скидкой                
                        ((DefaultTableModel) tab1.getModel()).getDataVector().add(vectorRec);
                    }
                }
                rs.close();
            }
            //=== Таблица 6 ===
            Vector vectorData = new Vector();
            Vector vectorColumn = new Vector(List.of("PUNIC", "PNUMB", "ONUMB", "ONAME", "PDATE", "BPICT"));
//            ResultSet rs = st.executeQuery("select b.punic, b.pnumb, a.onumb, a.oname, b.pdate, a.bpict from listord a, listprj b "
//                    + "where a.punic = b.punic and b.pdate > '01.06.2015' and b.pdate < '01.01.2023' order by b.pdate");
            ResultSet rs = st.executeQuery("select b.punic, b.pnumb, a.onumb, a.oname, b.pdate, a.bpict from listord a, listprj b where a.punic = b.punic and b.punic in "
                    + "(412463, 427595, 427597, 427761, 427817, 427818 ,427819, 427820, 427840, 427838, 427842, 427848, 427851, 427852, 427858, 427872, 427422, 427565, "
                    + "427833, 427832, 427831, 427830, 427825, 427826, 427779, 425392, 425392, 427850, 427708, 427737, 427629, 427847, 427856,"
                    + "425688) order by b.pnumb");          
            if (rs.isLast() == false) {
                while (rs.next()) {
                    Vector vectorRec = new Vector();
                    vectorRec.add(rs.getObject("PUNIC"));
                    vectorRec.add(rs.getObject("PNUMB"));
                    vectorRec.add(rs.getObject("ONUMB"));
                    vectorRec.add(rs.getObject("ONAME"));
                    vectorRec.add(rs.getObject("PDATE"));
                    try {
                        Blob blob = rs.getBlob("BPICT");
                        int blobLength = (int) blob.length();
                        byte[] bytes = blob.getBytes(1, blobLength);
                        blob.free();
                        BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                        ImageIcon icon = new ImageIcon(img);
                        vectorRec.add(icon);
                        //ImageIO.write(img, "jpg", new File("img.jpg"));

                    } catch (Exception e) {
                        vectorRec.add(null);
                    }
                    vectorData.add(vectorRec);
                }
            }
            DefaultTableModel model = new DefaultTableModel(vectorData, vectorColumn) {
                public Class getColumnClass(int column) {
                    return (column == 5) ? ImageIcon.class : Object.class;
                    //return Object.class;
                }
            };
            tab6.setModel(model);
            tab6.getColumnModel().getColumn(0).setMaxWidth(80);
            tab6.getColumnModel().getColumn(1).setMaxWidth(80);
            tab6.getColumnModel().getColumn(2).setMaxWidth(80);
            rs.close();
        } catch (SQLException e) {
            System.err.println("Ошибка: DBCompare.loadingTab4().  " + e);
        }
    }
    
    public void loadingTabGroup2(Wincalc winc) {
        try {
            Map<String, Vector> hmSpc = new HashMap();
            Set<String> setSpcSa = new HashSet();
            Set<String> setSpcPs = new HashSet();
            winc.listSpec.forEach(specRec -> setSpcSa.add(specRec.artikl));
            if (winc.rootGson.project() != null) {
                txt21.setText(String.valueOf(winc.rootGson.project()));
            }
            if (winc.rootGson.order() != null) {
                txt20.setText(String.valueOf(winc.rootGson.order()));
            }
            //Заполним на будушее hmSpc из SA
            for (String art_code : setSpcSa) {
                Record artiklRec = eArtikl.find2(art_code);
                hmSpc.put(art_code, new Vector(List.of(art_code, artiklRec.get(eArtikl.name), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)));
            }
            //setSpcSa.forEach(el -> hmSpc.put(el, new Vector(List.of(el + "*", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))));
            winc.listSpec.forEach(specRec -> {
                List<Double> val = hmSpc.get(specRec.artikl);
                val.set(2, val.get(2) + specRec.count); //колич. в SA
                val.set(4, val.get(4) + specRec.quant1); //погонаж в SA
            });

            //=== Таблица 1 ===
            ((DefaultTableModel) tab1.getModel()).getDataVector().clear();
            Statement st = cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("select PUNIC from LISTPRJ where PNUMB = " + txt21.getText());
            rs.next();
            int punic = rs.getInt("PUNIC");
            rs = st.executeQuery("select a.* from SPECPAU a where a.PUNIC = " + punic + " and a.ONUMB = " + txt20.getText() + " and clke != -1 order by a.anumb");
            int npp = 0;
            double sum1 = 0, sum2 = 0;
            if (rs.isLast() == false) {
                while (rs.next()) {
                    Vector vectorRec = new Vector();
                    vectorRec.add(++npp);
                    for (int i = 0; i < Fld.values().length; i++) {
                        vectorRec.add(rs.getObject(Fld.values()[i].name()));
                    }
                    vectorRec.set(4, hmColor.get(vectorRec.get(4)));  //цвет
                    vectorRec.set(5, hmColor.get(vectorRec.get(5)));  //цвет
                    vectorRec.set(6, hmColor.get(vectorRec.get(6)));  //цвет
                    String artikl = rs.getString("ANUMB"); //артикул
                    double leng = rs.getDouble("ALENG"); //длина
                    double count = rs.getDouble("AQTYP"); //колич
                    double pogonag = rs.getDouble("AQTYA"); //погонаж
                    double perc = rs.getDouble("APERC"); //отход
                    double cost = rs.getDouble("APRC1"); //стоим.без.ск.за ед.изм
                    double costdec = rs.getDouble("APRCD"); //стоим.со.ск.за.ед.изм                                
                    double value1 = (perc * pogonag / 100 + pogonag) * cost;
                    double value2 = (perc * pogonag / 100 + pogonag) * costdec;
                    Record artiklRec = eArtikl.query().stream().filter(r -> artikl.equals(r.get(eArtikl.code))).findFirst().orElse(eArtikl.up.newRecord());
                    sum1 = sum1 + value1;
                    sum2 = sum2 + value2;
                    vectorRec.add(4, artiklRec.get(eArtikl.name)); //имя артикула                
                    vectorRec.add(value1); //стоим. элемента без скидки
                    vectorRec.add(value2); //стоим. элемента со скидкой

                    ((DefaultTableModel) tab1.getModel()).getDataVector().add(vectorRec);

                    //Заполним на будушее hmSpc из PS
                    setSpcPs.add(artikl);
                    List<Double> val = hmSpc.getOrDefault(artikl, new Vector(List.of(artikl, "=*=", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)));
                    val.set(3, val.get(3) + count); //колич в PS
                    val.set(5, val.get(5) + pogonag); //погонаж в PS               
                }
            }
            rs.close();
            lab1.setText("Изд: punic=" + punic + "    Проект: pnumb=" + winc.rootGson.project() + "  Заказ: onumb="
                    + winc.rootGson.order() + "   Стоим.без.ск = " + df2.format(sum1) + "   Стоим.со.ск = " + df2.format(sum2));

            //=== Таблица 2 ===
            ((DefaultTableModel) tab2.getModel()).getDataVector().clear();
            Set<String> setSpc1x = new HashSet(setSpcSa);
            Set<String> setSpc2x = new HashSet(setSpcPs);
            setSpc1x.removeAll(setSpcPs);
            setSpc2x.removeAll(setSpcSa);
            ((DefaultTableModel) tab2.getModel()).getDataVector().add(new Vector(List.of("--- ЛИШНИЕ SAOkna  за.выч.Профстрой ---")));
            setSpc1x.forEach(e -> ((DefaultTableModel) tab2.getModel()).getDataVector().add(new Vector(List.of(e))));
            ((DefaultTableModel) tab2.getModel()).getDataVector().add(new Vector(List.of("--- НЕДОСТАЮЩИЕ ПрофСтрой  за.выч.SAOkna ---")));
            setSpc2x.forEach(e -> ((DefaultTableModel) tab2.getModel()).getDataVector().add(new Vector(List.of(e))));
            ((DefaultTableModel) tab2.getModel()).addRow(new Object[]{""});
            ((DefaultTableModel) tab2.getModel()).addRow(new Object[]{"Установленая фурнитура"});
            rs = st.executeQuery("select b.fname from savefur a, furnlst b where a.punic = " + punic + " and a.onumb = " + txt20.getText() + " and a.funic = b.funic");
            if (rs.isLast() == false) {
                while (rs.next()) {
                    ((DefaultTableModel) tab2.getModel()).addRow(new Object[]{rs.getString("FNAME")});
                }
            }
            rs.close();

            //=== Таблица 3 ===
            ((DefaultTableModel) tab3.getModel()).getDataVector().clear();
            for (Map.Entry<String, Vector> entry : hmSpc.entrySet()) {
                Vector vec = entry.getValue();
                vec.set(5, (double) vec.get(3) - (double) vec.get(4));
                ((DefaultTableModel) tab3.getModel()).getDataVector().add(vec);
            }

            //=== Таблица 4 ===
            npp = 0;
            ((DefaultTableModel) tab4.getModel()).getDataVector().clear();
            rs = st.executeQuery("select * from SAVEELM where TYPP != 0 and PUNIC = " + punic + "and ONUMB =" + txt20.getText() + "order by TYPP");
            if (rs.isLast() == false) {
                while (rs.next()) {
                    Vector vectorRec = new Vector();
                    vectorRec.add(++npp);
                    vectorRec.add(rs.getObject("PUNIC"));
                    vectorRec.add(rs.getObject("ONUMB"));
                    vectorRec.add(rs.getObject("ANUMB"));
                    vectorRec.add(rs.getObject("C1X"));
                    vectorRec.add(rs.getObject("C1Y"));
                    vectorRec.add(rs.getObject("C2X"));
                    vectorRec.add(rs.getObject("C2Y"));
                    vectorRec.add(rs.getObject("ALENG"));
                    vectorRec.add(rs.getObject("ALEN1"));
                    vectorRec.add(rs.getObject("ALEN2"));
                    vectorRec.add(rs.getObject("RAD"));
                    vectorRec.add(rs.getObject("TYPP"));
                    ((DefaultTableModel) tab4.getModel()).getDataVector().add(vectorRec);
                }
            }
            rs.close();

            //=== Таблица 5 ===
            try {
                if (rsm.isLast() == false) {
                    while (rsm.next()) {
                        if ("CONNLST".equals(rsm.getString("TABLE_NAME")) == true) {
                            ((DefaultTableModel) tab5.getModel()).getDataVector().clear();
                            rs = st.executeQuery("select b.anumb, c.anumb, a.typ, d.anum1, d.anum2, d.cname, e.cname from SAVECON a"
                                    + " left join SAVEELM b on a.punic = b.punic and a.onumb = b.onumb and a.ne1 = b.nel left join SAVEELM c on a.punic = c.punic and a.onumb = c.onumb and a.ne2 = c.nel"
                                    + " left join connlst d on a.ncon = d.cconn left join connvar e on a.nvar = e.cunic"
                                    + " where a.punic = " + punic + " and a.onumb = " + txt20.getText() + " order by a.typ, d.cname");
                            if (rs.isLast() == false) {
                                while (rs.next()) {
                                    Vector vectorRec = new Vector();
                                    vectorRec.add(++npp);
                                    vectorRec.add(rs.getObject(1));
                                    vectorRec.add(rs.getObject(2));
                                    vectorRec.add(rs.getObject(3));
                                    vectorRec.add(rs.getObject(4));
                                    vectorRec.add(rs.getObject(5));
                                    vectorRec.add(rs.getObject(6));
                                    vectorRec.add(rs.getObject(7));
                                    ((DefaultTableModel) tab5.getModel()).getDataVector().add(vectorRec);
                                }
                            }
                            rs.close();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка:DBCompare.loadingTabGroup1() Тавлица CONNLST отсуствует!");
            }

            //=== Таблица 6 ===
            Vector vectorData = new Vector();
            Vector vectorColumn = new Vector(List.of("PUNIC", "PNUMB", "ONUMB", "ONAME", "PDATE", "BPICT"));
            rs = st.executeQuery("select b.punic, b.pnumb, a.onumb, a.oname, b.pdate, a.bpict from listord a, listprj b "
                    + "where  a.punic = b.punic and a.punic = " + punic + " and a.onumb = " + txt20.getText() + " order by b.pnumb");
            if (rs.isLast() == false) {
                while (rs.next()) {
                    Vector vectorRec = new Vector();
                    vectorRec.add(rs.getObject("PUNIC"));
                    vectorRec.add(rs.getObject("PNUMB"));
                    vectorRec.add(rs.getObject("ONUMB"));
                    vectorRec.add(rs.getObject("ONAME"));
                    vectorRec.add(rs.getObject("PDATE"));
                    try {
                        Blob blob = rs.getBlob("BPICT");
                        int blobLength = (int) blob.length();
                        byte[] bytes = blob.getBytes(1, blobLength);
                        blob.free();
                        BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                        ImageIcon icon = new ImageIcon(img);
                        vectorRec.add(icon);
                        //ImageIO.write(img, "jpg", new File("img.jpg"));

                    } catch (Exception e) {
                        vectorRec.add(null);
                    }
                    vectorData.add(vectorRec);
                }
            }
            DefaultTableModel model = new DefaultTableModel(vectorData, vectorColumn) {
                public Class getColumnClass(int column) {
                    return (column == 5) ? ImageIcon.class : Object.class;
                    //return Object.class;
                }
            };
            tab6.setModel(model);
            tab6.getColumnModel().getColumn(0).setMaxWidth(80);
            tab6.getColumnModel().getColumn(1).setMaxWidth(80);
            tab6.getColumnModel().getColumn(2).setMaxWidth(80);
            rs.close();
         
            //=== Таблица 7 ===
            ((DefaultTableModel) tab7.getModel()).getDataVector().clear();
//            rs = st.executeQuery("select c.pname, b.pname from savefup a "
//                    + " left join parlist b on a.pnumb = b.pnumb and a.znumb = b.znumb"
//                    + " left join parlist c on a.pnumb = c.pnumb and c.znumb = 0 "
//                    + " where a.nel = 8 and a.punic = " + punic + " and a.onumb = " + txt20.getText());
            rs = st.executeQuery("select c.pname, b.pname, e.znumb from savefup a "
                    + "left join parlist b on a.pnumb = b.pnumb and a.znumb = b.znumb "
                    + "left join parlist c on a.pnumb = c.pnumb and c.znumb = 0 "
                    + "left join listord d on a.punic = d.punic and a.onumb = d.onumb "
                    + "left join parsysp e on d.osysp = e.psss and a.pnumb = e.pnumb and a.znumb = e.znumb "
                    + "where a.nel = 0 and a.punic = " + punic + " and a.onumb = " + txt20.getText());
            if (rs.isLast() == false) {
                while (rs.next()) {
                    Vector vectorRec = new Vector();
                    vectorRec.add(rs.getObject(1));
                    vectorRec.add(rs.getObject(2));
                    vectorRec.add(rs.getObject(3));
                    ((DefaultTableModel) tab7.getModel()).getDataVector().add(vectorRec);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Ошибка: DBCompare.loadingTab().  " + e);
        }
    }

    //Сравнение спецификации с профстроем
    public static void iwinPs4(Wincalc winc, boolean detail) {
        System.out.println();
        System.out.println("Prj=" + winc.rootGson.project() + " Ord=" + winc.rootGson.order());
        Double iwinTotal = 0.0, jarTotal = 0.0;
        Map<String, Double> hmDbPs = new LinkedHashMap();
        Map<String, Double> hmDbSa = new LinkedHashMap();
        try {
            Connection conn = Test.connect1();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery("select PUNIC from LISTPRJ where PNUMB = " + winc.rootGson.project());
            rs.next();
            int punic = rs.getInt("PUNIC");
            rs = st.executeQuery("select a.* from SPECPAU a where a.PUNIC = " + punic + " and a.ONUMB = " + winc.rootGson.order() + "  and clke != -1 order by a.anumb");
            while (rs.next()) {
                //double leng = rs.getDbl("ALENG"); //длина
                //double count = rs.getDbl("AQTYP"); //колич
                double pogonag = rs.getDouble("AQTYA"); //погонаж
                double perc = rs.getDouble("APERC"); //отход
                double cost = rs.getDouble("APRC1"); //стоим.без.ск.за ед.изм
                double value1 = (perc * pogonag / 100 + pogonag) * cost;
                double value2 = (hmDbPs.get(rs.getString("ANUMB")) == null) ? value1 : value1 + hmDbPs.get(rs.getString("ANUMB"));
                String key = rs.getString("ANUMB");
                hmDbPs.put(key, value2); //стоимость без скидки
            }
            conn.close();

            for (Specific spc : winc.listSpec) {
                String key = spc.artikl;
                Double val = hmDbSa.getOrDefault(key, 0.0);
                hmDbSa.put(key, val + spc.price); //стоимость без скидки
            }

            if (detail == true) {
                System.out.printf("%-64s%-24s%-16s%-16s%-16s", new Object[]{"Name", "Artikl", "PS", "SA", "Delta"});
                System.out.println();
                for (Map.Entry<String, Double> entry : hmDbPs.entrySet()) {
                    String key = entry.getKey();
                    Double val1 = entry.getValue();
                    Double val2 = hmDbSa.getOrDefault(key, 0.0);
                    hmDbSa.remove(key);
                    Record rec = eArtikl.query().stream().filter(r -> key.equals(r.get(eArtikl.code))).findFirst().orElse(eArtikl.up.newRecord());
                    System.out.printf("%-64s%-24s%-16.2f%-16.2f%-16.2f", new Object[]{rec.get(eArtikl.name), key, val1, val2, Math.abs(val1 - val2)});
                    System.out.println();
                    jarTotal = jarTotal + val2;
                    iwinTotal = iwinTotal + val1;
                }
                if (hmDbSa.isEmpty() == false) {
                    System.out.printf("%-32s%-20s", new Object[]{"Artikl", "Value"});
                }
                System.out.println();
                for (Map.Entry<String, Double> entry : hmDbSa.entrySet()) {
                    String key = entry.getKey();
                    Double value3 = entry.getValue();
                    System.out.printf("%-32s%-16.2f", "Лишние: " + key, value3);
                    System.out.println();
                    jarTotal = jarTotal + value3;
                }
            } else {
                for (Map.Entry<String, Double> entry : hmDbPs.entrySet()) {
                    String key = entry.getKey();
                    Double val1 = entry.getValue();
                    Double val2 = hmDbSa.getOrDefault(key, 0.0);
                    hmDbSa.remove(key);
                    jarTotal = jarTotal + val2;
                    iwinTotal = iwinTotal + val1;
                }
                for (Map.Entry<String, Double> entry : hmDbSa.entrySet()) {
                    String key = entry.getKey();
                    Double value3 = entry.getValue();
                    jarTotal = jarTotal + value3;
                }
            }
            System.out.printf("%-18s%-18s%-18s%-12s", "Prj=" + winc.rootGson.project(), "PS=" + String.format("%.2f", iwinTotal), "SA="
                    + String.format("%.2f", jarTotal), "dx=" + String.format("%.2f", Math.abs(iwinTotal - jarTotal)));
            System.out.println();

        } catch (SQLException e) {
            System.err.println("Ошибка: DBCompare.iwinPs4()  " + e);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        north = new javax.swing.JPanel();
        pan2 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        pan1 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        center = new javax.swing.JPanel();
        tabb = new javax.swing.JTabbedPane();
        pan4 = new javax.swing.JPanel();
        scr = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan12 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        scr7 = new javax.swing.JScrollPane();
        tab7 = new javax.swing.JTable();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        pan5 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan6 = new javax.swing.JPanel();
        pan7 = new javax.swing.JPanel();
        pan8 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan10 = new javax.swing.JPanel();
        lab19 = new javax.swing.JLabel();
        lab20 = new javax.swing.JLabel();
        txt19 = new javax.swing.JTextField();
        txt20 = new javax.swing.JTextField();
        btn1 = new javax.swing.JButton();
        labFurn = new javax.swing.JLabel();
        txt21 = new javax.swing.JTextField();
        lab21 = new javax.swing.JLabel();
        pan11 = new javax.swing.JPanel();
        scr6 = new javax.swing.JScrollPane();
        tab6 = new javax.swing.JTable();
        south = new javax.swing.JPanel();
        labSum = new javax.swing.JLabel();

        jPanel1.setPreferredSize(new java.awt.Dimension(895, 60));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 895, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DBCompare");

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));
        north.setLayout(new java.awt.BorderLayout());

        pan2.setName(""); // NOI18N
        pan2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lab1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lab1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pan2.add(lab1);

        north.add(pan2, java.awt.BorderLayout.CENTER);

        pan1.setPreferredSize(new java.awt.Dimension(40, 25));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMaximumSize(new java.awt.Dimension(25, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(25, 25));
        btnClose.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose(evt);
            }
        });

        javax.swing.GroupLayout pan1Layout = new javax.swing.GroupLayout(pan1);
        pan1.setLayout(pan1Layout);
        pan1Layout.setHorizontalGroup(
            pan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan1Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pan1Layout.setVerticalGroup(
            pan1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan1Layout.createSequentialGroup()
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        north.add(pan1, java.awt.BorderLayout.EAST);

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        center.setPreferredSize(new java.awt.Dimension(800, 550));
        center.setLayout(new java.awt.BorderLayout());

        pan4.setLayout(new java.awt.BorderLayout());

        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "№пп", "ATYPM", "ATYPP", "Артикул", "Название", "Текстура", "Внутренняя", "Внешняя", "Длина", "Ширина", "Угол 1", "Угол 2", "Количество", "Погонаж", "<html>Норма<br/>отхода", "<html>Себестоимость<br/> за ед.изм.", "<html>Ст.без.ск<br/> за ед.изм.", "<html>Ст.со.ск<br/> за ед.изм.", "<html>Стоим.<br/>без.скид.", "<html>Стоим.<br/>со.скидк."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tabMousePressed(evt);
            }
        });
        scr.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(0).setMaxWidth(60);
            tab1.getColumnModel().getColumn(1).setMinWidth(0);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(1).setMaxWidth(0);
            tab1.getColumnModel().getColumn(2).setMinWidth(0);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(2).setMaxWidth(0);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(140);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(240);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(6).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(8).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(9).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(10).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(11).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(13).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(14).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(15).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(16).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(17).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(18).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(19).setPreferredWidth(80);
        }

        pan4.add(scr, java.awt.BorderLayout.CENTER);

        tabb.addTab("Спецификация\n", pan4);

        pan3.setLayout(new javax.swing.BoxLayout(pan3, javax.swing.BoxLayout.PAGE_AXIS));

        pan12.setLayout(new javax.swing.BoxLayout(pan12, javax.swing.BoxLayout.LINE_AXIS));

        scr2.setPreferredSize(new java.awt.Dimension(454, 400));

        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"select distinct b.punic, b.pnumb, a.onumb, c.oname, b.pdate from specpau a, listprj b, listord c where a.punic = b.punic and a.punic = c.punic and a.onumb = c.onumb  and clke != -1  order by b.pdate, b.pnumb, a.onumb"},
                {""}
            },
            new String [] {
                "."
            }
        ));
        tab2.setFillsViewportHeight(true);
        scr2.setViewportView(tab2);

        pan12.add(scr2);

        tab7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Имя параметра", "Значение параметра", "Совпадение"
            }
        ));
        tab7.setFillsViewportHeight(true);
        scr7.setViewportView(tab7);

        pan12.add(scr7);

        pan3.add(pan12);

        scr5.setPreferredSize(new java.awt.Dimension(454, 204));

        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "№пп", "Артикл1", "Артикл2", "Тип", "Арт. аналог1", "Арт. аналог2", "Название", "Вариант"
            }
        ));
        tab5.setFillsViewportHeight(true);
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(0).setMaxWidth(40);
            tab5.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab5.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab5.getColumnModel().getColumn(3).setMaxWidth(40);
            tab5.getColumnModel().getColumn(4).setPreferredWidth(20);
            tab5.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab5.getColumnModel().getColumn(6).setPreferredWidth(200);
            tab5.getColumnModel().getColumn(7).setPreferredWidth(200);
        }

        pan3.add(scr5);

        jLabel1.setText("10 - Прилегающее, 20 - Угловое на ус, 30(31) - Угловое левое(правое), 40 - Т - образное (импост,ригель)");
        pan3.add(jLabel1);

        tabb.addTab("Сравнение 1", pan3);

        pan5.setLayout(new java.awt.BorderLayout());

        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Артикул", "Наименование", "Колич.  SA", "Колич.  PS", "Погонаж  SA", "Погонаж  PS", "Дельта"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab3.setFillsViewportHeight(true);
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab3.getColumnModel().getColumn(2).setPreferredWidth(50);
            tab3.getColumnModel().getColumn(3).setPreferredWidth(50);
            tab3.getColumnModel().getColumn(4).setPreferredWidth(50);
            tab3.getColumnModel().getColumn(5).setPreferredWidth(50);
            tab3.getColumnModel().getColumn(6).setPreferredWidth(50);
        }

        pan5.add(scr3, java.awt.BorderLayout.CENTER);

        tabb.addTab("Сравнение 2", pan5);

        pan6.setLayout(new java.awt.BorderLayout());

        pan7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pan7.setPreferredSize(new java.awt.Dimension(400, 4));
        pan7.setLayout(new java.awt.BorderLayout());
        pan6.add(pan7, java.awt.BorderLayout.WEST);

        pan8.setLayout(new java.awt.BorderLayout());

        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№пп", "Проект", "Заказ", "Артикл", "X1", "Y1", "X2", "Y2", "Длина", "Ширина", "Высота", "Радиус", "Тип"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab4.setFillsViewportHeight(true);
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(20);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(20);
            tab4.getColumnModel().getColumn(2).setPreferredWidth(20);
        }

        pan8.add(scr4, java.awt.BorderLayout.CENTER);

        pan10.setPreferredSize(new java.awt.Dimension(100, 60));

        lab19.setFont(frames.UGui.getFont(0,0));
        lab19.setText("PUNIC");
        lab19.setToolTipText("");
        lab19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab19.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab19.setMinimumSize(new java.awt.Dimension(34, 14));
        lab19.setPreferredSize(new java.awt.Dimension(60, 18));

        lab20.setFont(frames.UGui.getFont(0,0));
        lab20.setText("ONUMB");
        lab20.setToolTipText("");
        lab20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab20.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab20.setMinimumSize(new java.awt.Dimension(34, 14));
        lab20.setPreferredSize(new java.awt.Dimension(60, 20));

        txt19.setFont(frames.UGui.getFont(0,0));
        txt19.setText("425688");
        txt19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt19.setPreferredSize(new java.awt.Dimension(80, 18));

        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setText("1");
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(40, 18));

        btn1.setText("Пересчитать");
        btn1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn1.setMaximumSize(new java.awt.Dimension(18, 18));
        btn1.setMinimumSize(new java.awt.Dimension(18, 18));
        btn1.setPreferredSize(new java.awt.Dimension(18, 18));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1(evt);
            }
        });

        labFurn.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        labFurn.setPreferredSize(new java.awt.Dimension(200, 19));

        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setText("506929");
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setPreferredSize(new java.awt.Dimension(80, 18));

        lab21.setFont(frames.UGui.getFont(0,0));
        lab21.setText("PNUMB");
        lab21.setToolTipText("");
        lab21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab21.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab21.setMinimumSize(new java.awt.Dimension(34, 14));
        lab21.setPreferredSize(new java.awt.Dimension(60, 18));

        javax.swing.GroupLayout pan10Layout = new javax.swing.GroupLayout(pan10);
        pan10.setLayout(pan10Layout);
        pan10Layout.setHorizontalGroup(
            pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan10Layout.createSequentialGroup()
                        .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pan10Layout.createSequentialGroup()
                        .addComponent(lab20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labFurn, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        pan10Layout.setVerticalGroup(
            pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labFurn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pan8.add(pan10, java.awt.BorderLayout.PAGE_START);

        pan6.add(pan8, java.awt.BorderLayout.CENTER);

        tabb.addTab("Рисунок конструкции", pan6);

        pan11.setLayout(new java.awt.BorderLayout());

        tab6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Номер проекта", "Номер заказа", "Имя заказа", "Рисунок"
            }
        ));
        tab6.setFillsViewportHeight(true);
        tab6.setRowHeight(80);
        scr6.setViewportView(tab6);
        if (tab6.getColumnModel().getColumnCount() > 0) {
            tab6.getColumnModel().getColumn(0).setPreferredWidth(140);
            tab6.getColumnModel().getColumn(0).setMaxWidth(140);
            tab6.getColumnModel().getColumn(1).setPreferredWidth(140);
            tab6.getColumnModel().getColumn(1).setMaxWidth(140);
            tab6.getColumnModel().getColumn(2).setPreferredWidth(140);
            tab6.getColumnModel().getColumn(2).setMaxWidth(140);
        }

        pan11.add(scr6, java.awt.BorderLayout.CENTER);

        tabb.addTab("Список заказов", pan11);

        center.add(tabb, java.awt.BorderLayout.CENTER);

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        labSum.setText("sum:0");
        labSum.setMaximumSize(new java.awt.Dimension(200, 14));
        labSum.setMinimumSize(new java.awt.Dimension(200, 14));
        labSum.setPreferredSize(new java.awt.Dimension(200, 14));
        south.add(labSum);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void tabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMousePressed

    }//GEN-LAST:event_tabMousePressed

    private void btn1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1
        loadingTabGroup1();
    }//GEN-LAST:event_btn1

    // <editor-fold defaultstate="collapsed" desc="Generated Code">   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btnClose;
    private javax.swing.JPanel center;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab19;
    private javax.swing.JLabel lab20;
    private javax.swing.JLabel lab21;
    private javax.swing.JLabel labFurn;
    private javax.swing.JLabel labSum;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel pan8;
    private javax.swing.JScrollPane scr;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JScrollPane scr7;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTable tab6;
    private javax.swing.JTable tab7;
    private javax.swing.JTabbedPane tabb;
    private javax.swing.JTextField txt19;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt21;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {

        FrameToFile.setFrameSize(this);
        new FrameToFile(this, btnClose);

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        DefaultTableCellRenderer cellRenderer3 = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value != null) {
                    value = (UCom.getDbl(value.toString()) > 0) ? df2.format(value) : null;
                }
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                return label;
            }
        };
        tab1.getTableHeader().setPreferredSize(new Dimension(0, 32));
    }
}
