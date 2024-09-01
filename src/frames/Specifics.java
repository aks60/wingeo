package frames;

import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import common.eProp;
import dataset.Record;
import domain.eElemdet;
import domain.eFurndet;
import domain.eGlasdet;
import domain.eJoindet;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import builder.Wincalc;
import builder.making.SpcRecord;
import builder.making.SpcTariffic;
import common.ArraySpc;
import common.UCom;
import domain.eSysprod;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import static java.util.stream.Collectors.toList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import startup.App;
import common.listener.ListenerFrame;
import domain.eArtikl;
import domain.ePrjprod;
import frames.swing.DefCellRendererNumb;
import frames.swing.TableFieldFilter;
import frames.swing.col.ColumnGroup;
import frames.swing.col.GroupableTableHeader;
import java.util.Iterator;
import java.util.stream.IntStream;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import report.ExecuteCmd;
import report.HtmlOfTable;

public class Specifics extends javax.swing.JFrame {

    private int manager = 0;
    private builder.Wincalc winc = new Wincalc();
    private TableFieldFilter filterTable = null;
    private ArraySpc<SpcRecord> listSpc = new ArraySpc<SpcRecord>();

    ImageIcon[] image = {new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b063.gif")),
        new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b076.gif")),
        new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b077.gif")),
        new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))
    };

    public Specifics(int manager) {
        initComponents();
        initElements();
        this.manager = manager;
        createMenu();
        createIwin();
        loadingData();
        loadingTab1(this.listSpc);
        UGui.setSelectedRow(tab1);
    }

    public void loadingData() {

        this.listSpc.addAll(winc.listSpec); //добавим спецификацию

        //Если открыл менеджер добавим комплекты
        if (manager == 1) {
            int prjprodID = Integer.valueOf(eProp.prjprodID.read());
            Record prjprodRec = ePrjprod.find(prjprodID);
            ArraySpc<SpcRecord> listKit = SpcTariffic.kits(prjprodRec, winc, true); //комплекты
            this.listSpc.addAll(listKit);
        }
    }

    public void loadingTab1(List<SpcRecord> listSpc) {

        DefaultTableModel dtm = ((DefaultTableModel) tab1.getModel());
        dtm.getDataVector().clear();
        dtm.fireTableDataChanged();
        int vSize = new SpcRecord().getVector(0).size();

        double sum1 = 0, sum2 = 0, sum9 = 0, sum13 = 0;
        for (int i = 0; i < listSpc.size(); i++) {
            Vector v = listSpc.get(i).getVector(i + 1);
            dtm.addRow(v);
            sum1 = sum1 + (Double) v.get(v.size() - 1);
            sum2 = sum2 + (Double) v.get(v.size() - 2);
            sum9 = sum9 + (Double) v.get(v.size() - 9);
            sum13 = sum13 + (Double) v.get(v.size() - 13);
        }
        Vector v2 = new Vector();
        v2.add(listSpc.size() + 1);
        IntStream.range(1, vSize).forEach(action -> v2.add(null));
        v2.set(v2.size() - 1, sum1); //стоимость без скидки
        v2.set(v2.size() - 2, sum2); //стоимость со скидклй
        v2.set(v2.size() - 9, sum9);
        v2.set(v2.size() - 13, sum13);
        dtm.addRow(v2);
        labSum.setText("Итого: " + UCom.format(sum1, "#,##0.##"));
    }

    public void createIwin() {
        if (manager == 1) {
            int prjprodID = Integer.valueOf(eProp.prjprodID.read());
            Record prjprodRec = ePrjprod.find(prjprodID);
            if (prjprodRec == null) {
                JOptionPane.showMessageDialog(this, "Выберите конструкцию в списке заказов", "Предупреждение", JOptionPane.OK_OPTION);
            } else {
                String script = prjprodRec.getStr(ePrjprod.script);
                JsonElement je = new Gson().fromJson(script, JsonElement.class);
                je.getAsJsonObject().addProperty("nuni", prjprodRec.getInt(ePrjprod.systree_id));
                winc.build(je.toString());
                winc.specification(true);
            }
        } else {
            int sysprodID = Integer.valueOf(eProp.sysprodID.read());
            Record sysprodRec = eSysprod.find(sysprodID);
            if (sysprodRec == null) {
                JOptionPane.showMessageDialog(this, "Выберите конструкцию в системе профилей", "Предупреждение", JOptionPane.OK_OPTION);
            } else {
                String script = sysprodRec.getStr(eSysprod.script);
                JsonElement je = new Gson().fromJson(script, JsonElement.class);
                je.getAsJsonObject().addProperty("nuni", sysprodRec.getInt(eSysprod.systree_id));
                winc.build(je.toString());
                winc.specification(cbx2.getSelectedIndex() == 0);
            }
        }
    }

    public void createMenu() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Мат. ценности");
        mnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadingTab1(listSpc);
            }
        });
        UTree.loadArtTree(root);
        Iterator<TreeNode> ir = root.children().asIterator();
        while (ir.hasNext()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ir.next();
            enums.TypeArt type = (enums.TypeArt) node.getUserObject();
            JMenu jm = new JMenu(type.name);
            jm.setFont(frames.UGui.getFont(0, 0));
            jm.setIcon(image[3]);
            DefaultMutableTreeNode nod2 = new DefaultMutableTreeNode(type.name);
            JMenuItem mn1 = new JMenuItem(type.name, image[3]); //1 элемент списка
            mn1.setFont(frames.UGui.getFont(0, 1));
            mn1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    List<SpcRecord> listSpec = listSpc.stream().filter(rec
                            -> rec.artiklRec().getInt(eArtikl.level1) == type.id1).collect(toList());
                    loadingTab1(listSpec);
                }
            });
            jm.add(mn1);
            ppmTree.add(jm);

            Iterator<TreeNode> in = node.children().asIterator();
            while (in.hasNext()) {
                DefaultMutableTreeNode nod3 = (DefaultMutableTreeNode) in.next();
                enums.TypeArt typ2 = (enums.TypeArt) nod3.getUserObject();
                JMenuItem mn2 = new JMenuItem(typ2.name, image[3]); //следующте элемены списка
                mn2.setFont(frames.UGui.getFont(0, 0));
                mn2.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        List<SpcRecord> listSpec = listSpc.stream().filter(rec
                                -> rec.artiklRec().getInt(eArtikl.level1) == typ2.id1
                                && rec.artiklRec().getInt(eArtikl.level2) == typ2.id2).collect(toList());
                        loadingTab1(listSpec);
                    }
                });
                jm.add(mn2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmTree = new javax.swing.JPopupMenu();
        mnAll = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
        cbx2 = new javax.swing.JComboBox<>();
        btnTest = new javax.swing.JButton();
        btn21 = new javax.swing.JButton();
        btn23 = new javax.swing.JButton();
        btn24 = new javax.swing.JButton();
        btn25 = new javax.swing.JButton();
        btn26 = new javax.swing.JButton();
        btn27 = new javax.swing.JButton();
        btn22 = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable() {
            protected JTableHeader createDefaultTableHeader() {
                return new GroupableTableHeader(columnModel);
            }
        };
        south = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        pan1 = new javax.swing.JPanel();
        labSum = new javax.swing.JLabel();

        mnAll.setFont(frames.UGui.getFont(0,1));
        mnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mnAll.setText("Показать всё");
        ppmTree.add(mnAll);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Спецификация");
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

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

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnReport.setToolTipText(bundle.getString("Печать")); // NOI18N
        btnReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport.setFocusable(false);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReport.setMaximumSize(new java.awt.Dimension(25, 25));
        btnReport.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport.setPreferredSize(new java.awt.Dimension(25, 25));
        btnReport.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReport(evt);
            }
        });

        btnFind1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c088.gif"))); // NOI18N
        btnFind1.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind1.setFocusable(false);
        btnFind1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind1(evt);
            }
        });

        btnFind2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c089.gif"))); // NOI18N
        btnFind2.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind2.setFocusable(false);
        btnFind2.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind2.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind2.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind2(evt);
            }
        });

        cbx2.setBackground(new java.awt.Color(212, 208, 200));
        cbx2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbx2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Учитывать норму отх.", "Без нормы отхода " }));
        cbx2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        cbx2.setPreferredSize(new java.awt.Dimension(180, 25));
        cbx2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxCalcType(evt);
            }
        });

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTest(evt);
            }
        });

        btn21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c056.gif"))); // NOI18N
        btn21.setToolTipText(bundle.getString("Меню.Спецификация")); // NOI18N
        btn21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn21.setFocusable(false);
        btn21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn21.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn21mnSpecif(evt);
            }
        });

        btn23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c023.gif"))); // NOI18N
        btn23.setToolTipText(bundle.getString("Меню.Комплекты")); // NOI18N
        btn23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn23.setFocusable(false);
        btn23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn23.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn23mnKits(evt);
            }
        });

        btn24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c024.gif"))); // NOI18N
        btn24.setToolTipText(bundle.getString("Меню.Соединения")); // NOI18N
        btn24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn24.setFocusable(false);
        btn24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn24.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn24mnJoining(evt);
            }
        });

        btn25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c025.gif"))); // NOI18N
        btn25.setToolTipText(bundle.getString("Меню.Вставки")); // NOI18N
        btn25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn25.setFocusable(false);
        btn25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn25.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn25mnElement(evt);
            }
        });

        btn26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c026.gif"))); // NOI18N
        btn26.setToolTipText(bundle.getString("Меню.Заполнения")); // NOI18N
        btn26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn26.setFocusable(false);
        btn26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn26.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn26mnGlass(evt);
            }
        });

        btn27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c027.gif"))); // NOI18N
        btn27.setToolTipText(bundle.getString("Меню.Фурнитура")); // NOI18N
        btn27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn27.setFocusable(false);
        btn27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn27.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn27mnFurnityra(evt);
            }
        });

        btn22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c041.gif"))); // NOI18N
        btn22.setToolTipText(bundle.getString("Пересчитать")); // NOI18N
        btn22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn22.setFocusable(false);
        btn22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn22.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn22)
                .addGap(18, 18, 18)
                .addComponent(btn21)
                .addGap(0, 0, 0)
                .addComponent(btn24)
                .addGap(0, 0, 0)
                .addComponent(btn25)
                .addGap(0, 0, 0)
                .addComponent(btn26)
                .addGap(0, 0, 0)
                .addComponent(btn27)
                .addGap(0, 0, 0)
                .addComponent(btn23)
                .addGap(18, 18, 18)
                .addComponent(cbx2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 334, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbx2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn26, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn27, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(900, 500));
        centr.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, "", "", "", "", "", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, "", "", "", "", "", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Nпп", "PK", "FK", "Расположенние", "Артикул", "Наименование", "Текстура", "Внутр..", "Внешн...", "Длина", "Ширина", "Масса", "реза1", "реза2", "гориз.", "<html>Кол.<br/>единиц", "<html>Един.<br/>изм.", "<html>Процент<br/> отхода", "<html>Кол.без<br/>отхода", "<html>Кол. с <br/>отходом", "за ед. изм.", "с отх.", "без ск.", "со ск."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tab1.setFillsViewportHeight(true);
        tab1.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(32);
            tab1.getColumnModel().getColumn(0).setMaxWidth(40);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(34);
            tab1.getColumnModel().getColumn(1).setMaxWidth(40);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(22);
            tab1.getColumnModel().getColumn(2).setMaxWidth(40);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(44);
            tab1.getColumnModel().getColumn(3).setMaxWidth(60);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(100);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(240);
            tab1.getColumnModel().getColumn(9).setPreferredWidth(48);
            tab1.getColumnModel().getColumn(10).setPreferredWidth(48);
            tab1.getColumnModel().getColumn(11).setPreferredWidth(46);
            tab1.getColumnModel().getColumn(12).setPreferredWidth(36);
            tab1.getColumnModel().getColumn(13).setPreferredWidth(36);
            tab1.getColumnModel().getColumn(14).setPreferredWidth(38);
            tab1.getColumnModel().getColumn(15).setPreferredWidth(30);
            tab1.getColumnModel().getColumn(16).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(17).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(18).setPreferredWidth(48);
            tab1.getColumnModel().getColumn(19).setPreferredWidth(46);
            tab1.getColumnModel().getColumn(20).setPreferredWidth(44);
            tab1.getColumnModel().getColumn(21).setPreferredWidth(44);
            tab1.getColumnModel().getColumn(22).setPreferredWidth(58);
            tab1.getColumnModel().getColumn(23).setPreferredWidth(58);
        }

        centr.add(scr1, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        filler1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler1);

        pan1.setLayout(new java.awt.BorderLayout());

        labSum.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labSum.setText("sum:0");
        labSum.setMaximumSize(new java.awt.Dimension(200, 14));
        labSum.setMinimumSize(new java.awt.Dimension(200, 14));
        labSum.setPreferredSize(new java.awt.Dimension(120, 14));
        pan1.add(labSum, java.awt.BorderLayout.EAST);

        south.add(pan1);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        HtmlOfTable.load("Отчёт по спецификации", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void btnFind1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind1
        double id = UCom.getDbl(tab1.getValueAt(tab1.getSelectedRow(), 1).toString());
        SpcRecord recordSpc = this.listSpc.find(id);
        ProgressBar.create(this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Artikles.createFrame(Specifics.this, recordSpc.artiklRec());
            }
        });
    }//GEN-LAST:event_btnFind1

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        String str = tab1.getValueAt(tab1.getSelectedRow(), 3).toString().substring(0, 3);
        if ("КОМ".equals(str) == false) {

            double id = UCom.getDbl(tab1.getValueAt(tab1.getSelectedRow(), 1).toString());
            SpcRecord specificRec = this.listSpc.find(id);
            Record variantRec = specificRec.variantRec;
            Record detailRec = specificRec.detailRec;
            if (detailRec != null) {
                ProgressBar.create(Specifics.this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        if (str.equals("ВСТ")) {
                            App.Element.createFrame(Specifics.this, detailRec.getInt(eElemdet.id));

                        } else if (str.equals("СОЕ")) {
                            App.Joining.createFrame(Specifics.this, detailRec.getInt(eJoindet.id));

                        } else if (str.equals("ЗАП")) {
                            App.Filling.createFrame(Specifics.this, detailRec.getInt(eGlasdet.id));

                        } else if (str.equals("ФУР")) {
                            App.Furniture.createFrame(Specifics.this, detailRec.getInt(eFurndet.id));
                        }
                    }
                });
            } else {
                ProgressBar.create(Specifics.this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        if (str.equals("ВСТ") || str.equals("ЗАП")) {
                            App.Systree.createFrame(Specifics.this);
                        }
                    }
                });
            }
        }
    }//GEN-LAST:event_btnFind2

    private void cbxCalcType(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxCalcType
        ProgressBar.create(this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                createIwin();
                loadingTab1(listSpc);
                UGui.setSelectedRow(tab1);
            }
        });

    }//GEN-LAST:event_cbxCalcType

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest

    }//GEN-LAST:event_btnTest

    private void btn23mnKits(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn23mnKits
        List<SpcRecord> spc = this.listSpc.stream().filter(rec -> "КОМ".equals(rec.place.substring(0, 3))).collect(toList());
        loadingTab1(spc);
    }//GEN-LAST:event_btn23mnKits

    private void btn24mnJoining(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn24mnJoining
        List<SpcRecord> spc = this.listSpc.stream().filter(rec -> "СОЕ".equals(rec.place.substring(0, 3))).collect(toList());
        loadingTab1(spc);
    }//GEN-LAST:event_btn24mnJoining

    private void btn25mnElement(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn25mnElement
        List<SpcRecord> spc = this.listSpc.stream().filter(rec -> "ВСТ".equals(rec.place.substring(0, 3))).collect(toList());
        loadingTab1(spc);
    }//GEN-LAST:event_btn25mnElement

    private void btn26mnGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn26mnGlass
        List<SpcRecord> spc = this.listSpc.stream().filter(rec -> "ЗАП".equals(rec.place.substring(0, 3))).collect(toList());
        loadingTab1(spc);
    }//GEN-LAST:event_btn26mnGlass

    private void btn27mnFurnityra(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn27mnFurnityra
        List<SpcRecord> spc = this.listSpc.stream().filter(rec -> "ФУР".equals(rec.place.substring(0, 3))).collect(toList());
        loadingTab1(spc);
    }//GEN-LAST:event_btn27mnFurnityra

    private void btn21mnSpecif(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn21mnSpecif
        ppmTree.show(north, btn21.getX(), btn21.getY() + 18);
    }//GEN-LAST:event_btn21mnSpecif

    private void btn22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn22ActionPerformed
        int index = UGui.getIndexRec(tab1);
        createIwin();
        createIwin();
        loadingTab1(this.listSpc);
        UGui.setSelectedIndex(tab1, index);
    }//GEN-LAST:event_btn22ActionPerformed

// <editor-fold defaultstate="collapsed" desc="Generated Code">     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn21;
    private javax.swing.JButton btn22;
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTest;
    private javax.swing.JComboBox<String> cbx2;
    private javax.swing.JPanel centr;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel labSum;
    private javax.swing.JMenuItem mnAll;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPopupMenu ppmTree;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    public void initElements() {
        new FrameToFile(this, btnClose);

        filterTable = new TableFieldFilter(4, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tab1.getModel());
        tab1.setRowSorter(sorter);
        tab1.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lab.setBackground(new java.awt.Color(212, 208, 200));
                return lab;
            }
        });
        tab1.getTableHeader().setPreferredSize(new Dimension(0, 46));
        tab1.getColumnModel().getColumn(1).setCellRenderer(new DefCellRendererNumb("#0.#"));
        tab1.getColumnModel().getColumn(2).setCellRenderer(new DefCellRendererNumb("#0.#"));
        tab1.getColumnModel().getColumn(9).setCellRenderer(new DefCellRendererNumb(1));
        tab1.getColumnModel().getColumn(10).setCellRenderer(new DefCellRendererNumb(1));
        tab1.getColumnModel().getColumn(11).setCellRenderer(new DefCellRendererNumb(3));
        tab1.getColumnModel().getColumn(12).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(13).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(14).setCellRenderer(new DefCellRendererNumb(1));
        tab1.getColumnModel().getColumn(15).setCellRenderer(new DefCellRendererNumb(1));
        tab1.getColumnModel().getColumn(18).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(19).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(20).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(21).setCellRenderer(new DefCellRendererNumb(2));
        tab1.getColumnModel().getColumn(22).setCellRenderer(new DefCellRendererNumb(9));
        tab1.getColumnModel().getColumn(23).setCellRenderer(new DefCellRendererNumb(9));
        for (int i = 8; i < tab1.getColumnCount(); i++) {
            tab1.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                    JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    if ("-1".equals(lab.getText()) || "-1.0".equals(lab.getText()) || "0.0".equals(lab.getText())) {
                        lab.setText("");
                    }
                    return lab;
                }
            });
        }        
        if ("Nimbus".equals(eProp.lookandfeel.read())) {
            for (int i = 15; i < 22; i++) {
                tab1.getColumnModel().getColumn(i).setPreferredWidth(tab1.getColumnModel().getColumn(i).getPreferredWidth() + tab1.getColumnModel().getColumn(i).getPreferredWidth() / 3);
            }
        }
        cbx2.setRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(image[2]);
                return comp;
            }

        });
        tab1.getTableHeader().setFont(frames.UGui.getFont(0, 0));
        TableColumnModel cm = tab1.getColumnModel();
        ColumnGroup angl = new ColumnGroup("Угол");
        angl.add(cm.getColumn(12));
        angl.add(cm.getColumn(13));
        angl.add(cm.getColumn(14));
        ColumnGroup sebe = new ColumnGroup("Себестоимость");
        sebe.add(cm.getColumn(20));
        sebe.add(cm.getColumn(21));
        ColumnGroup cost = new ColumnGroup("Стоимость");
        cost.add(cm.getColumn(22));
        cost.add(cm.getColumn(23));
        GroupableTableHeader header = (GroupableTableHeader) tab1.getTableHeader();
        header.addColumnGroup(sebe);
        header.addColumnGroup(angl);
        header.addColumnGroup(cost);
    }
}
