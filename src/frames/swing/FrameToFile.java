package frames.swing;

import common.ePref;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import startup.Main;

/**
 * <p>
 * Сохронение размера фрейма в файле </p>
 */
public class FrameToFile extends javax.swing.Timer implements ActionListener {

    private static Dimension screenSize;
    private static Dimension frameSize;
    private static Window frameUp;
    //private boolean eventPerformed = false;
    private static Window frame;
    private static AbstractButton btn;
    private static Window frame2;

    public FrameToFile(Window frame, AbstractButton btn) {
        super(1000, null);
        this.frame = frame;
        this.btn = btn;
        initListener();
    }

    public void initListener() {
        super.addActionListener(this);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(java.awt.event.MouseEvent evt) {
                System.out.println(".mousePressed()");
                start();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                System.out.println(".mouseReleased()");
                stop();
                btn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif")));
            }
        });
    }

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        System.out.println("frames.swing.FrameToFile.actionPerformed()");
        Dimension frameSize = frame.getSize();
        try {
            btn.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c036.gif")));
            String dy = String.valueOf(frame.getSize().height);
            String dx = String.valueOf(frame.getSize().width);
            ePref.load().setProperty(frame.getClass().getName() + "_height", dy);
            ePref.load().setProperty(frame.getClass().getName() + "_width", dx);
        } finally {
            stop();
        }
    }

    /**
     * Устанавливаем размеры и координаты window
     */
    public static void setFrameSize(Window frame) {

//        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        frameSize = frame.getSize();
//        frameUp = frame;
//        String dy = ePref.load().getProperty(frame.getClass().getName() + "_height", null);
//        String dx = ePref.load().getProperty(frame.getClass().getName() + "_width", null);
//
//        if (ePref.locate == true) {
//            frameSize.height = (dy == null) ? frameSize.height : Integer.parseInt(dy); //размеры окна
//            frameSize.width = (dx == null) ? frameSize.width : Integer.parseInt(dx);  //размеры окна
//        }
//        if (frameSize.height > screenSize.height) {
//            frameSize.height = screenSize.height;
//        }
//        if (frameSize.width > screenSize.width) {
//            frameSize.width = screenSize.width;
//        }
//        if (frame.getName().equals("Setting")) {
//            frame.setLocation(20, 100);
//        } else {
//            //frame.setLocationRelativeTo(null);
//            frame.setLocation((screenSize.width - frameSize.width) / 2,
//                    (screenSize.height - frameSize.height - 48) / 2 + 48);
//        }
//        frame.setPreferredSize(frameSize);
//        frame.pack();
    }

    /**
     * Устанавливаем координаты window
     */
    public static void setFrameSize(Window frame, int x, int y, int dx, int dy) {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frameSize = frame.getSize();
        frameUp = frame;
        if (dy != 0) {
            frameSize.height = dy;
        } else {
            String dy2 = ePref.load().getProperty(frame.getClass().getName() + "_height", "nul");
            if (!dy2.equals("nul")) {
                frameSize.height = Integer.valueOf(dy2);
            }
        }
        if (dx != 0) {
            frameSize.width = dx;
        } else {
            String dx2 = ePref.load().getProperty(frame.getClass().getName() + "_width", "nul");
            if (!dx2.equals("nul")) {
                frameSize.width = Integer.valueOf(dx2);
            }
        }
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        if (x == 0 && y == 0) {
            frame.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height - 48) / 2 + 48);
        } else if (x == 0 && y != 0) {
            frame.setLocation((screenSize.width - frameSize.width) / 2, y);
        } else if (x != 0 && y == 0) {
            frame.setLocation(x, (screenSize.height - frameSize.height - 48) / 2 + 48);
        } else {
            frame.setLocation(x, y);
        }
        frame.setPreferredSize(frameSize);
        frame.pack();
    }
}
