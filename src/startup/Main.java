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
    
    //Конструктор
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
//                //закрываю и сохраняю проперти
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
        UIManager.put("AbstractButton.clickText", "клик");
        UIManager.put("AbstractDocument.additionText", "добавление");
        UIManager.put("AbstractDocument.deletionText", "удаление");
        UIManager.put("AbstractDocument.redoText", "Повторить");
        UIManager.put("AbstractDocument.styleChangeText", "смену стиля");
        UIManager.put("AbstractDocument.undoText", "Отменить");
        UIManager.put("AbstractUndoableEdit.redoText", "Повторить");
        UIManager.put("AbstractUndoableEdit.undoText", "Отменить");
        UIManager.put("ColorChooser.cancelText", "Отмена");
        UIManager.put("ColorChooser.hsbBlueText", "B");
        UIManager.put("ColorChooser.hsbBrightnessText", "Яркость");
        UIManager.put("ColorChooser.hsbDisplayedMnemonicIndex", "0");
        UIManager.put("ColorChooser.hsbGreenText", "G");
        UIManager.put("ColorChooser.hsbHueText", "Оттенок");
        UIManager.put("ColorChooser.hsbMnemonic", "72");
        UIManager.put("ColorChooser.hsbNameText", "HSB");
        UIManager.put("ColorChooser.hsbRedText", "R");
        UIManager.put("ColorChooser.hsbSaturationText", "Насыщенность");
        UIManager.put("ColorChooser.okText", "OK");
        UIManager.put("ColorChooser.previewText", "Просмотр");
        UIManager.put("ColorChooser.resetMnemonic", "82");
        UIManager.put("ColorChooser.resetText", "Сброс");
        UIManager.put("ColorChooser.rgbBlueMnemonic", "66");
        UIManager.put("ColorChooser.rgbBlueText", "Синий");
        UIManager.put("ColorChooser.rgbDisplayedMnemonicIndex", "1");
        UIManager.put("ColorChooser.rgbGreenMnemonic", "78");
        UIManager.put("ColorChooser.rgbGreenText", "Зеленый");
        UIManager.put("ColorChooser.rgbMnemonic", "71");
        UIManager.put("ColorChooser.rgbNameText", "RGB");
        UIManager.put("ColorChooser.rgbRedMnemonic", "68");
        UIManager.put("ColorChooser.rgbRedText", "Красный");
        UIManager.put("ColorChooser.sampleText", "Пример текста Пример текста");
        UIManager.put("ColorChooser.swatchesDisplayedMnemonicIndex", "0");
        UIManager.put("ColorChooser.swatchesMnemonic", "83");
        UIManager.put("ColorChooser.swatchesNameText", "Палитра");
        UIManager.put("ColorChooser.swatchesRecentText", "Последние:");
        UIManager.put("ComboBox.togglePopupText", "Раскрыть список");
        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
        UIManager.put("FileChooser.cancelButtonMnemonic", "67");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Закрыть диалог");
        UIManager.put("FileChooser.directoryDescriptionText", "Папка");
        UIManager.put("FileChooser.directoryOpenButtonMnemonic", "79");
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть выбранную директорию");
        UIManager.put("FileChooser.fileDescriptionText", "Все файлы");
        UIManager.put("FileChooser.helpButtonMnemonic", "72");
        UIManager.put("FileChooser.helpButtonText", "Помощь");
        UIManager.put("FileChooser.helpButtonToolTipText", "Показать справку");
        UIManager.put("FileChooser.newFolderErrorText", "Ошибка при создании каталога");
        UIManager.put("FileChooser.openButtonMnemonic", "79");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть выбранный файл");
        UIManager.put("FileChooser.openDialogTitleText", "Открыть");
        UIManager.put("FileChooser.saveButtonMnemonic", "83");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить выбранный файл");
        UIManager.put("FileChooser.saveDialogTitleText", "Сохранить");
        UIManager.put("FileChooser.updateButtonMnemonic", "85");
        UIManager.put("FileChooser.updateButtonText", "Обновить");
        UIManager.put("FileChooser.updateButtonToolTipText", "Обновить список директорий");
        UIManager.put("FormView.browseFileButtonText", "Просмотр...");
        UIManager.put("FormView.resetButtonText", "Сброс");
        UIManager.put("FormView.submitButtonText", "Отправить запрос");
        UIManager.put("InternalFrame.closeButtonToolTip", "Закрыть");
        UIManager.put("InternalFrame.iconButtonToolTip", "Свернуть");
        UIManager.put("InternalFrame.maxButtonToolTip", "Развернуть");
        UIManager.put("InternalFrame.restoreButtonToolTip", "Восстановить");
        UIManager.put("InternalFrameTitlePane.closeButtonAccessibleName", "Закрыть");
        UIManager.put("InternalFrameTitlePane.closeButtonText", "Закрыть");
        UIManager.put("InternalFrameTitlePane.iconifyButtonAccessibleName", "Свернуть");
        UIManager.put("InternalFrameTitlePane.maximizeButtonAccessibleName", "Развернуть");
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", "Развернуть");
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", "Свернуть");
        UIManager.put("InternalFrameTitlePane.moveButtonText", "Переместить");
        UIManager.put("InternalFrameTitlePane.restoreButtonText", "Восстановить");
        UIManager.put("InternalFrameTitlePane.sizeButtonText", "Размер");
        UIManager.put("OptionPane.cancelButtonMnemonic", "0");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.inputDialogTitle", "Ввод");
        UIManager.put("OptionPane.messageDialogTitle", "Сообщение");
        UIManager.put("OptionPane.noButtonMnemonic", "78");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.okButtonMnemonic", "0");
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.titleText", "Выберите значение");
        UIManager.put("OptionPane.yesButtonMnemonic", "89");
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("PrintingDialog.abortButtonDisplayedMnemonicIndex", "0");
        UIManager.put("PrintingDialog.abortButtonMnemonic", "65");
        UIManager.put("PrintingDialog.abortButtonText", "Прервать");
        UIManager.put("PrintingDialog.abortButtonToolTipText", "Прервать печать");
        UIManager.put("PrintingDialog.contentAbortingText", "Прерывание печати...");
        UIManager.put("PrintingDialog.contentInitialText", "Идет печать...");
        UIManager.put("PrintingDialog.titleProgressText", "Печать");
        UIManager.put("ProgressMonitor.progressText", "Процесс...");
        UIManager.put("SplitPane.leftButtonText", "левая кнопка");
        UIManager.put("SplitPane.rightButtonText", "правая кнопка");
        UIManager.put("FileChooser.detailsViewActionLabelText", "Таблица");
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Таблица");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");
        UIManager.put("FileChooser.fileDateHeaderText", "Modified");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
        UIManager.put("FileChooser.homeFolderAccessibleName", "Домой");
        UIManager.put("FileChooser.homeFolderToolTipText", "Домой");
        UIManager.put("FileChooser.listViewActionLabelText", "Список");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "Список");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.lookInLabelText", "Просмотр:");
        UIManager.put("FileChooser.newFolderAccessibleName", "Новая папка");
        UIManager.put("FileChooser.newFolderActionLabelText", "Новая папка");
        UIManager.put("FileChooser.newFolderToolTipText", "Создать новую папку");
        UIManager.put("FileChooser.refreshActionLabelText", "Обновить");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в:");
        UIManager.put("FileChooser.upFolderAccessibleName", "Вверх");
        UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
        UIManager.put("FileChooser.viewMenuLabelText", "Просмотр");
        UIManager.put("MetalTitlePane.closeMnemonic", "67");
        UIManager.put("MetalTitlePane.closeTitle", "Закрыть");
        UIManager.put("MetalTitlePane.iconifyMnemonic", "69");
        UIManager.put("MetalTitlePane.iconifyTitle", "Свернуть");
        UIManager.put("MetalTitlePane.maximizeMnemonic", "88");
        UIManager.put("MetalTitlePane.maximizeTitle", "Развернуть");
        UIManager.put("MetalTitlePane.restoreMnemonic", "82");
        UIManager.put("MetalTitlePane.restoreTitle", "Восстановить");
        /*
        UIManager.put("FileChooser.detailsViewActionLabelText", "Таблица");
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Таблица");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");
        UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
        UIManager.put("FileChooser.homeFolderAccessibleName", "Домой");
        UIManager.put("FileChooser.homeFolderToolTipText", "Домой");
        UIManager.put("FileChooser.listViewActionLabelText", "Список");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "Список");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.lookInLabelText", "Просмотр:");
        UIManager.put("FileChooser.newFolderAccessibleName", "Новая папка");
        UIManager.put("FileChooser.newFolderActionLabelText", "Новая папка");
        UIManager.put("FileChooser.newFolderToolTipText", "Создать новую папку");
        UIManager.put("FileChooser.refreshActionLabelText", "Обновить");
        UIManager.put("FileChooser.saveInLabelText", "Сохранить в:");
        UIManager.put("FileChooser.upFolderAccessibleName", "Вверх");
        UIManager.put("FileChooser.upFolderToolTipText", "Вверх на один уровень");
        UIManager.put("FileChooser.viewMenuLabelText", "Просмотр");
         */
    }
}
