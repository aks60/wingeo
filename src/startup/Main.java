package startup;

import common.UCom;
import frames.LogoToDb;
import common.ePref;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main {
    
    ///////////////////////////////////////////
    public final static String versionApp = "2.0";
    ///////////////////////////////////////////
    
    //�����������
    public Main() {
        Locale.setDefault(ePref.locale);
        LogoToDb frame = new LogoToDb(null);
        frame.setVisible(true);
    }

    //java -jar C:\\Okna\\winapp\\dist\\winapp.jar tex
    public static void main(String[] args) {

        for (int index = 0; index < args.length; index++) {
            
            ePref.dev = true;
            
            if (index == 0 && args[0].equals("adm")) {
                ePref.profile = args[0];

            } else if (index == 0 && args[0].equals("tex")) {
                ePref.profile = args[0];

            } else if (index == 0 && args[0].equals("man")) {
                ePref.profile = args[0];
            }
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    runRussifier();
                    String lafname = ePref.lookandfeel.getProp();
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                        if (lafname.equals(laf.getName())) {
                            UIManager.setLookAndFeel(laf.getClassName());
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
                new Main();
//                //�������� � �������� ��������
//                Runtime.getRuntime().addShutdownHook(new Thread() {
//                    @Override
//                    public void run() {
////                        try {
////                            ePref.save();
////                        } catch (Exception e) {
////                            System.err.println(e);
////                        }
//                    }
//                });
            }
        });
    }

    public static final void runRussifier() {
        //UIManager.put("ComboBox.selectionBackground", java.awt.Color.yellow);
        UIManager.put("AbstractButton.clickText", "����");
        UIManager.put("AbstractDocument.additionText", "����������");
        UIManager.put("AbstractDocument.deletionText", "��������");
        UIManager.put("AbstractDocument.redoText", "���������");
        UIManager.put("AbstractDocument.styleChangeText", "����� �����");
        UIManager.put("AbstractDocument.undoText", "��������");
        UIManager.put("AbstractUndoableEdit.redoText", "���������");
        UIManager.put("AbstractUndoableEdit.undoText", "��������");
        UIManager.put("ColorChooser.cancelText", "������");
        UIManager.put("ColorChooser.hsbBlueText", "B");
        UIManager.put("ColorChooser.hsbBrightnessText", "�������");
        UIManager.put("ColorChooser.hsbDisplayedMnemonicIndex", "0");
        UIManager.put("ColorChooser.hsbGreenText", "G");
        UIManager.put("ColorChooser.hsbHueText", "�������");
        UIManager.put("ColorChooser.hsbMnemonic", "72");
        UIManager.put("ColorChooser.hsbNameText", "HSB");
        UIManager.put("ColorChooser.hsbRedText", "R");
        UIManager.put("ColorChooser.hsbSaturationText", "������������");
        UIManager.put("ColorChooser.okText", "OK");
        UIManager.put("ColorChooser.previewText", "��������");
        UIManager.put("ColorChooser.resetMnemonic", "82");
        UIManager.put("ColorChooser.resetText", "�����");
        UIManager.put("ColorChooser.rgbBlueMnemonic", "66");
        UIManager.put("ColorChooser.rgbBlueText", "�����");
        UIManager.put("ColorChooser.rgbDisplayedMnemonicIndex", "1");
        UIManager.put("ColorChooser.rgbGreenMnemonic", "78");
        UIManager.put("ColorChooser.rgbGreenText", "�������");
        UIManager.put("ColorChooser.rgbMnemonic", "71");
        UIManager.put("ColorChooser.rgbNameText", "RGB");
        UIManager.put("ColorChooser.rgbRedMnemonic", "68");
        UIManager.put("ColorChooser.rgbRedText", "�������");
        UIManager.put("ColorChooser.sampleText", "������ ������ ������ ������");
        UIManager.put("ColorChooser.swatchesDisplayedMnemonicIndex", "0");
        UIManager.put("ColorChooser.swatchesMnemonic", "83");
        UIManager.put("ColorChooser.swatchesNameText", "�������");
        UIManager.put("ColorChooser.swatchesRecentText", "���������:");
        UIManager.put("ComboBox.togglePopupText", "�������� ������");
        UIManager.put("FileChooser.acceptAllFileFilterText", "��� �����");
        UIManager.put("FileChooser.cancelButtonMnemonic", "67");
        UIManager.put("FileChooser.cancelButtonText", "������");
        UIManager.put("FileChooser.cancelButtonToolTipText", "������� ������");
        UIManager.put("FileChooser.directoryDescriptionText", "�����");
        UIManager.put("FileChooser.directoryOpenButtonMnemonic", "79");
        UIManager.put("FileChooser.directoryOpenButtonText", "�������");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "������� ��������� ����������");
        UIManager.put("FileChooser.fileDescriptionText", "��� �����");
        UIManager.put("FileChooser.helpButtonMnemonic", "72");
        UIManager.put("FileChooser.helpButtonText", "������");
        UIManager.put("FileChooser.helpButtonToolTipText", "�������� �������");
        UIManager.put("FileChooser.newFolderErrorText", "������ ��� �������� ��������");
        UIManager.put("FileChooser.openButtonMnemonic", "79");
        UIManager.put("FileChooser.openButtonText", "�������");
        UIManager.put("FileChooser.openButtonToolTipText", "������� ��������� ����");
        UIManager.put("FileChooser.openDialogTitleText", "�������");
        UIManager.put("FileChooser.saveButtonMnemonic", "83");
        UIManager.put("FileChooser.saveButtonText", "���������");
        UIManager.put("FileChooser.saveButtonToolTipText", "��������� ��������� ����");
        UIManager.put("FileChooser.saveDialogTitleText", "���������");
        UIManager.put("FileChooser.updateButtonMnemonic", "85");
        UIManager.put("FileChooser.updateButtonText", "��������");
        UIManager.put("FileChooser.updateButtonToolTipText", "�������� ������ ����������");
        UIManager.put("FormView.browseFileButtonText", "��������...");
        UIManager.put("FormView.resetButtonText", "�����");
        UIManager.put("FormView.submitButtonText", "��������� ������");
        UIManager.put("InternalFrame.closeButtonToolTip", "�������");
        UIManager.put("InternalFrame.iconButtonToolTip", "��������");
        UIManager.put("InternalFrame.maxButtonToolTip", "����������");
        UIManager.put("InternalFrame.restoreButtonToolTip", "������������");
        UIManager.put("InternalFrameTitlePane.closeButtonAccessibleName", "�������");
        UIManager.put("InternalFrameTitlePane.closeButtonText", "�������");
        UIManager.put("InternalFrameTitlePane.iconifyButtonAccessibleName", "��������");
        UIManager.put("InternalFrameTitlePane.maximizeButtonAccessibleName", "����������");
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", "����������");
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", "��������");
        UIManager.put("InternalFrameTitlePane.moveButtonText", "�����������");
        UIManager.put("InternalFrameTitlePane.restoreButtonText", "������������");
        UIManager.put("InternalFrameTitlePane.sizeButtonText", "������");
        UIManager.put("OptionPane.cancelButtonMnemonic", "0");
        UIManager.put("OptionPane.cancelButtonText", "������");
        UIManager.put("OptionPane.inputDialogTitle", "����");
        UIManager.put("OptionPane.messageDialogTitle", "���������");
        UIManager.put("OptionPane.noButtonMnemonic", "78");
        UIManager.put("OptionPane.noButtonText", "���");
        UIManager.put("OptionPane.okButtonMnemonic", "0");
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.titleText", "�������� ��������");
        UIManager.put("OptionPane.yesButtonMnemonic", "89");
        UIManager.put("OptionPane.yesButtonText", "��");
        UIManager.put("PrintingDialog.abortButtonDisplayedMnemonicIndex", "0");
        UIManager.put("PrintingDialog.abortButtonMnemonic", "65");
        UIManager.put("PrintingDialog.abortButtonText", "��������");
        UIManager.put("PrintingDialog.abortButtonToolTipText", "�������� ������");
        UIManager.put("PrintingDialog.contentAbortingText", "���������� ������...");
        UIManager.put("PrintingDialog.contentInitialText", "���� ������...");
        UIManager.put("PrintingDialog.titleProgressText", "������");
        UIManager.put("ProgressMonitor.progressText", "�������...");
        UIManager.put("SplitPane.leftButtonText", "����� ������");
        UIManager.put("SplitPane.rightButtonText", "������ ������");
        UIManager.put("FileChooser.detailsViewActionLabelText", "�������");
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "�������");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "�������");
        UIManager.put("FileChooser.fileAttrHeaderText", "��������");
        UIManager.put("FileChooser.fileDateHeaderText", "Modified");
        UIManager.put("FileChooser.fileNameHeaderText", "���");
        UIManager.put("FileChooser.fileNameLabelText", "��� �����:");
        UIManager.put("FileChooser.fileSizeHeaderText", "������");
        UIManager.put("FileChooser.fileTypeHeaderText", "���");
        UIManager.put("FileChooser.filesOfTypeLabelText", "��� ������:");
        UIManager.put("FileChooser.homeFolderAccessibleName", "�����");
        UIManager.put("FileChooser.homeFolderToolTipText", "�����");
        UIManager.put("FileChooser.listViewActionLabelText", "������");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "������");
        UIManager.put("FileChooser.listViewButtonToolTipText", "������");
        UIManager.put("FileChooser.lookInLabelText", "��������:");
        UIManager.put("FileChooser.newFolderAccessibleName", "����� �����");
        UIManager.put("FileChooser.newFolderActionLabelText", "����� �����");
        UIManager.put("FileChooser.newFolderToolTipText", "������� ����� �����");
        UIManager.put("FileChooser.refreshActionLabelText", "��������");
        UIManager.put("FileChooser.saveInLabelText", "��������� �:");
        UIManager.put("FileChooser.upFolderAccessibleName", "�����");
        UIManager.put("FileChooser.upFolderToolTipText", "�� ���� ������� �����");
        UIManager.put("FileChooser.viewMenuLabelText", "��������");
        UIManager.put("MetalTitlePane.closeMnemonic", "67");
        UIManager.put("MetalTitlePane.closeTitle", "�������");
        UIManager.put("MetalTitlePane.iconifyMnemonic", "69");
        UIManager.put("MetalTitlePane.iconifyTitle", "��������");
        UIManager.put("MetalTitlePane.maximizeMnemonic", "88");
        UIManager.put("MetalTitlePane.maximizeTitle", "����������");
        UIManager.put("MetalTitlePane.restoreMnemonic", "82");
        UIManager.put("MetalTitlePane.restoreTitle", "������������");
        /*
        UIManager.put("FileChooser.detailsViewActionLabelText", "�������");
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "�������");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "�������");
        UIManager.put("FileChooser.fileAttrHeaderText", "��������");
        UIManager.put("FileChooser.fileDateHeaderText", "�������");
        UIManager.put("FileChooser.fileNameHeaderText", "���");
        UIManager.put("FileChooser.fileNameLabelText", "��� �����:");
        UIManager.put("FileChooser.fileSizeHeaderText", "������");
        UIManager.put("FileChooser.fileTypeHeaderText", "���");
        UIManager.put("FileChooser.filesOfTypeLabelText", "��� ������:");
        UIManager.put("FileChooser.homeFolderAccessibleName", "�����");
        UIManager.put("FileChooser.homeFolderToolTipText", "�����");
        UIManager.put("FileChooser.listViewActionLabelText", "������");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "������");
        UIManager.put("FileChooser.listViewButtonToolTipText", "������");
        UIManager.put("FileChooser.lookInLabelText", "��������:");
        UIManager.put("FileChooser.newFolderAccessibleName", "����� �����");
        UIManager.put("FileChooser.newFolderActionLabelText", "����� �����");
        UIManager.put("FileChooser.newFolderToolTipText", "������� ����� �����");
        UIManager.put("FileChooser.refreshActionLabelText", "��������");
        UIManager.put("FileChooser.saveInLabelText", "��������� �:");
        UIManager.put("FileChooser.upFolderAccessibleName", "�����");
        UIManager.put("FileChooser.upFolderToolTipText", "����� �� ���� �������");
        UIManager.put("FileChooser.viewMenuLabelText", "��������");
         */
    }
}
