package frames.dialog;

import frames.swing.FrameToFile;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import frames.UGui;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import common.listener.ListenerObject;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Календарь
 */
public class DicDate extends javax.swing.JDialog {

    protected int rangeTopYear = 25;
    protected ListenerObject listener;
    protected GregorianCalendar appCalendar = new GregorianCalendar();
    protected int overDay[] = new int[]{6, 0, 1, 2, 3, 4, 5};

    public DicDate(java.awt.Window owner, ListenerObject listener, Integer dxYear) {
        super(owner, ModalityType.DOCUMENT_MODAL);
        this.listener = listener;
        initComponents();

        //Диапазон выбора
        listYear.setModel(new javax.swing.AbstractListModel() {//диапазон выбора

            public int getSize() {
                return 100;
            }

            public Object getElementAt(int i) {
                return UGui.getYearCur() + rangeTopYear - i;
            }
        });

        //Устанавливаем appCalendar дату
        int year = appCalendar.get(appCalendar.YEAR) + dxYear;
        appCalendar.set(appCalendar.YEAR, year);

        loadingTab();
        tabDay.requestFocus();

        FrameToFile.setFrameSize(this);
        setVisible(true);
    }

    //Заполнение модели данных
    public void loadingTab() {

        //Запоминаем текущую дату
        Date date = appCalendar.getTime();
        int month = appCalendar.get(Calendar.MONTH);

        //Очищаем DataModelDay    
        for (int col = 0; col < tabDay.getColumnCount(); col++) {
            for (int row = 0; row < tabDay.getRowCount(); row++) {
                tabDay.setValueAt(null, row, col);
            }
        }
        //Устанавливаем объект appCalendar на первый день текущего месяца
        appCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int dx = appCalendar.get(Calendar.WEEK_OF_MONTH);

        do { //Заполняем массив DataModelDay днями месяца
            int day_of_week = overDay[appCalendar.get(Calendar.DAY_OF_WEEK) - 1];
            int week_of_month = appCalendar.get(Calendar.WEEK_OF_MONTH) - dx;
            int day_of_month = appCalendar.get(Calendar.DAY_OF_MONTH);
            tabDay.setValueAt(day_of_month, week_of_month, day_of_week);

            appCalendar.add(Calendar.DAY_OF_MONTH, 1);  //передвигаю объект appCalendar на новый день
        } while (appCalendar.get(Calendar.MONTH) == month);

        appCalendar.setTime(date); //возвращаем текущую дату

        //Выделяем элементы даты
        int day_of_week = overDay[appCalendar.get(Calendar.DAY_OF_WEEK) - 1];
        int week_of_moth = appCalendar.get(Calendar.WEEK_OF_MONTH) - dx;
        tabDay.setRowSelectionInterval(week_of_moth, week_of_moth);
        tabDay.setColumnSelectionInterval(day_of_week, day_of_week);
        listMonth.setSelectedIndex(appCalendar.get(Calendar.MONTH));
    }

    public static void main(String[] args) throws IOException {

        String serverUrl = args.length > 0 ? args[0] : "https://www.google.co.in";
        System.out.println(getServerHttpDate(serverUrl));
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        System.out.println(ts);

        //formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)    Tue, 24 Jul 2018 13:25:37 GMT
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        Date date1 = null;
        try {
            date1 = sdf.parse(getServerHttpDate(serverUrl));
            date2 = sdf2.parse(ts.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("date1 : " + sdf2.format(date1));
        System.out.println("date2 : " + sdf2.format(date2));

        if (date1.compareTo(date2) > 0) {
            System.out.println("Date1 is after Date2");

//        List<String> cmd = new ArrayList<String>();
//        cmd.add("hg rollback");
//        ProcessBuilder pb = new ProcessBuilder(cmd);
//        //pb.directory(new File("C:\\Windows\\System32"));
//        pb.directory(new File("C:\\Windows\\SysWOW64\\"));
//        Process p = pb.start();
        } else if (date1.compareTo(date2) < 0) {
            System.out.println("Date1 is before Date2");
        } else if (date1.compareTo(date2) == 0) {
            System.out.println("Date1 is equal to Date2");
        } else {
            System.out.println("How to get here?");
        }

    }
    
    private static String getServerHttpDate(String serverUrl) throws IOException {
        URL url = new URL(serverUrl);
        URLConnection connection = url.openConnection();

        Map<String, List<String>> httpHeaders = connection.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            if (headerName != null && headerName.equalsIgnoreCase("date")) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listMonth = new javax.swing.JList();
        tabDay = new javax.swing.JTable();
        scrYear = new javax.swing.JScrollPane();
        listYear = new javax.swing.JList();
        tool = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        labl1 = new javax.swing.JLabel();
        lab2 = new javax.swing.JLabel();
        lab3 = new javax.swing.JLabel();
        lab4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Календарь");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formShown(evt);
            }
        });

        listMonth.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listMonth.setFont(UGui.getFont(-1,0));
        listMonth.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "  1  Январь", "  2  Февраль", "  3  Март ", "  4  Апрель", "  5  Май", "  6  Июнь", "  7  Июль", "  8  Август", "  9  Сентябрь", "10  Октябрь", "11  Ноябрь", "12  Декабрь" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listMonth.setMaximumSize(new java.awt.Dimension(32767, 32767));
        listMonth.setPreferredSize(new java.awt.Dimension(71, 190));
        listMonth.setVisibleRowCount(12);
        listMonth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DicDate.this.mouseClicked(evt);
            }
        });
        listMonth.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                monthChanged(evt);
            }
        });

        tabDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tabDay.setFont(UGui.getFont(1,0));
        tabDay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""},
                {"", "", "", "", "", "", ""}
            },
            new String [] {
                "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabDay.setCellSelectionEnabled(true);
        tabDay.setMaximumSize(new java.awt.Dimension(32767, 32767));
        tabDay.setPreferredSize(new java.awt.Dimension(525, 192));
        tabDay.setRowHeight(32);
        tabDay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabDay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DicDate.this.mouseClicked(evt);
            }
        });
        tabDay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DicDate.this.keyPressed(evt);
            }
        });
        tabDay.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                dayChanged(event);
            }
        });
        tabDay.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                dayChanged(event);
            }
        });

        scrYear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrYear.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrYear.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrYear.setMinimumSize(new java.awt.Dimension(0, 0));
        scrYear.setPreferredSize(new java.awt.Dimension(48, 190));

        listYear.setFont(UGui.getFont(-1,0));
        listYear.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002", "2001", "2000", "1999", "1998", "1997", "1996", "1995", "1994", "1993", "1992", "1991", "1990", "1989", "1988", "1987", "1986", "1985", "1984", "1983", "1982", "1981", "1980", "1979", "1978", "1977", "1976", "1975", "1974", "1973", "1972", "1971", "1970", "1969", "1968", "1967", "1966", "1965", "1964", "1963", "1962", "1961", "1960", "1959", "1958", "1957", "1956", "1955", "1954", "1953", "1952", "1951", "1950", "1949", "1948", "1947", "1946", "1945", "1944", "1943", "1942", "1941", "1940", "1939", "1938", "1937", "1936", "1935" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listYear.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listYear.setVisibleRowCount(22);
        listYear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DicDate.this.mouseClicked(evt);
            }
        });
        listYear.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                yearChanged(evt);
            }
        });
        scrYear.setViewportView(listYear);

        tool.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tool.setMaximumSize(new java.awt.Dimension(32767, 26));
        tool.setPreferredSize(new java.awt.Dimension(289, 26));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c037.gif"))); // NOI18N
        btnClose.setText("Отмена");
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setFocusable(false);
        btnClose.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClose.setMaximumSize(new java.awt.Dimension(80, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(0, 0));
        btnClose.setPreferredSize(new java.awt.Dimension(80, 25));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAction(evt);
            }
        });

        btnOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c020.gif"))); // NOI18N
        btnOk.setText("OK");
        btnOk.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnOk.setFocusable(false);
        btnOk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOk.setMaximumSize(new java.awt.Dimension(80, 25));
        btnOk.setMinimumSize(new java.awt.Dimension(0, 0));
        btnOk.setPreferredSize(new java.awt.Dimension(80, 25));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okAction(evt);
            }
        });

        javax.swing.GroupLayout toolLayout = new javax.swing.GroupLayout(tool);
        tool.setLayout(toolLayout);
        toolLayout.setHorizontalGroup(
            toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolLayout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        toolLayout.setVerticalGroup(
            toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolLayout.createSequentialGroup()
                .addGroup(toolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        labl1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        labl1.setForeground(new java.awt.Color(0, 0, 255));
        labl1.setText(" Год");
        labl1.setAlignmentY(6.0F);
        labl1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        labl1.setMaximumSize(new java.awt.Dimension(30, 19));
        labl1.setPreferredSize(new java.awt.Dimension(30, 19));

        lab2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lab2.setForeground(new java.awt.Color(0, 0, 255));
        lab2.setText("    Месяц");
        lab2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lab2.setMaximumSize(new java.awt.Dimension(54, 19));
        lab2.setPreferredSize(new java.awt.Dimension(54, 19));

        lab3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lab3.setForeground(new java.awt.Color(0, 0, 255));
        lab3.setText(" Пн    Вт   Ср   Чт     Пт");
        lab3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lab3.setMaximumSize(new java.awt.Dimension(54, 19));
        lab3.setPreferredSize(new java.awt.Dimension(54, 19));

        lab4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lab4.setForeground(new java.awt.Color(255, 0, 0));
        lab4.setText("Сб    Вс");
        lab4.setAlignmentY(6.0F);
        lab4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lab4.setMaximumSize(new java.awt.Dimension(30, 19));
        lab4.setPreferredSize(new java.awt.Dimension(30, 19));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(scrYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(listMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lab4, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                            .addComponent(tabDay, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)))
                    .addComponent(tool, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labl1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrYear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listMonth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabDay, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tool, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAction
        dispose();
}//GEN-LAST:event_closeAction

    private void okAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okAction
        listener.action(appCalendar);
        dispose();
}//GEN-LAST:event_okAction

    //Смена дня
    private void dayChanged(javax.swing.event.ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() == false) {
            int col = tabDay.getSelectedColumn();
            int row = tabDay.getSelectedRow();
            if (col != -1 && row != -1) {
                if (tabDay.getValueAt(row, col) instanceof Integer) {
                    int day = ((Integer) tabDay.getValueAt(row, col)).intValue();
                    if (day > 0) {
                        appCalendar.set(Calendar.DAY_OF_MONTH, day);
                    }
                }
            }
        }
    }

    //Смена года
    private void yearChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_yearChanged
        if (evt.getValueIsAdjusting() == false) {
            int index = listYear.getSelectedIndex();
            appCalendar.set(Calendar.YEAR, UGui.getYearCur() + 25 - index);
            loadingTab();
        }
    }//GEN-LAST:event_yearChanged

    //Смена месяца
    private void monthChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_monthChanged
        if (evt.getValueIsAdjusting() == false) {
            int index = listMonth.getSelectedIndex();
            appCalendar.set(Calendar.MONTH, index);
            loadingTab();
        }
    }//GEN-LAST:event_monthChanged

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getClickCount() == 2) {
            okAction(null);
        }
    }//GEN-LAST:event_mouseClicked

    private void keyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressed
        listener.action(appCalendar);
        dispose();
    }//GEN-LAST:event_keyPressed

    private void formShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formShown
        int year = appCalendar.get(Calendar.YEAR);
        listYear.setSelectedValue(year, true);
        int selectedIndex = listYear.getSelectedIndex();
        int firstVisibleIndex = listYear.getFirstVisibleIndex();
        int dx = firstVisibleIndex - selectedIndex + 6;
        if (year + dx < UGui.getYearCur() + rangeTopYear) {
            listYear.setSelectedValue(year + dx, true);
            listYear.setSelectedValue(year, true);
        }
    }//GEN-LAST:event_formShown

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab3;
    private javax.swing.JLabel lab4;
    private javax.swing.JLabel labl1;
    private javax.swing.JList listMonth;
    private javax.swing.JList listYear;
    private javax.swing.JScrollPane scrYear;
    private javax.swing.JTable tabDay;
    private javax.swing.JPanel tool;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
