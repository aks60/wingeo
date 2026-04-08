package builder.making;

import builder.model.ElemSimple;
import dataset.Field;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eParmap;
import domain.eSyspar1;
import enums.UseColor;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class UColor {

    private static final int COLOR_US = 2;
    private static final int COLOR_FK = 3;
    private static final int ARTIKL_ID = 4;

    private static ImageIcon icon[] = {null, null, null, null, null, null};
    private static ImageIcon icon2[] = {null, null, null, null, null, null};
    private static int[] indexIcon = {10, 20, 30, 31, 40, 41};

    //Конструктор
    public UColor() {
        if (this.icon[0] == null) {
            ImageIcon icon[] = {
                new ImageIcon(getClass().getResource("/resource/img16/b000.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b001.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b002.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b003.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b004.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b005.gif"))};
            this.icon = icon;
        }
        if (this.icon2[0] == null) {
            ImageIcon icon[] = {
                new ImageIcon(getClass().getResource("/resource/img16/b070.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b071.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b072.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b073.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b074.gif")),
                new ImageIcon(getClass().getResource("/resource/img16/b075.gif"))};
            this.icon2 = icon;
        }
    }

    /**
     * Подбора по текстуре элем. или серии элементов
     *
     * @param spcAdd - спецификацм элемента
     */
    public static boolean choiceFromArtOrSeri(TRecord spcAdd) {  //см. http://help.profsegment.ru/?id=1107 

        TRecord spcClon = new TRecord(spcAdd);
        int typesUS = spcClon.detailRec.getInt(COLOR_US);

        //Серия артикулов
        if (UseColor.isSeries(typesUS)) {

            List<Record> artseriList = eArtikl.filter(spcClon.artiklRec.getInt(eArtikl.groups4_id));
            
            for (Record artseriRec : artseriList) { //цикл по сериям
                spcClon.artiklRec(artseriRec);
                if (UColor.colorFromSetting(spcClon, 1, true)
                        && UColor.colorFromSetting(spcClon, 2, true)
                        && UColor.colorFromSetting(spcClon, 3, true)) {

                    spcAdd.copy(spcClon);
                    return true;
                }
            }
            spcClon.colorID1 = colorFromTexturUse(spcClon, typesUS & 0x0000000f);
            spcClon.colorID2 = colorFromTexturUse(spcClon, (typesUS & 0x000000f0) >> 4);
            spcClon.colorID3 = colorFromTexturUse(spcClon, (typesUS & 0x00000f00) >> 8);

            //Не серия артикулов
        } else {
            if (UColor.colorFromSetting(spcAdd, 1, false)
                    && UColor.colorFromSetting(spcAdd, 2, false)
                    && UColor.colorFromSetting(spcAdd, 3, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Устанавл.цвет ВРУЧНУЮ, АВТОПОДБОР, ПАРАМЕТР
     *
     * @param spcAdd
     * @param side
     * @param seri
     * @return
     */
    private static boolean colorFromSetting(TRecord spcAdd, int side, boolean seri) {  //см. http://help.profsegment.ru/?id=1107        

        int srcNumberUS = spcAdd.detailRec.getInt(COLOR_US);
        int srcColorFk = spcAdd.detailRec.getInt(COLOR_FK);

        if (srcColorFk == -1) {
            colorFromMes(spcAdd);
            return false; //нет данных для поиска
        }
        int resultColorID = -1;
        try {
            int srcColorUS = (side == 1) ? srcNumberUS & 0x0000000f : (side == 2)
                    ? (srcNumberUS & 0x000000f0) >> 4 : (srcNumberUS & 0x00000f00) >> 8; //тип подбора                
            int elemArtID = spcAdd.artiklRec.getInt(eArtikl.id);

            //Цвет элемента по которому подбираю из варианта подбора
            int originColorID = colorFromTexturUse(spcAdd, srcColorUS);

            ////= ВРУЧНУЮ =////
            if (srcColorFk > 0 && srcColorFk != 100000) {

                //Явное указание текстуры
                if (srcColorUS == UseColor.MANUAL.id) {
                    if (seri == true) {
                        resultColorID = -1; //нельзя назначать на серию
                    } else {
                        resultColorID = scanFromProfSide(elemArtID, srcColorFk, side); //теоритически это должно железно работать!!!
                        if (resultColorID == -1) {
                            if (spcAdd.artiklRec.getInt(eArtikl.level1) == 2 && (spcAdd.artiklRec.getInt(eArtikl.level2) == 11 || spcAdd.artiklRec.getInt(eArtikl.level2) == 13)) {
                                return false;
                            }
                            resultColorID = scanFromColorFirst(spcAdd); //первая в списке и это неправильно
                        }
                    }

                    //Подбор по текстуре профиля и текстуре сторон профиля
                } else if (List.of(UseColor.PROF.id, UseColor.GLAS.id, UseColor.COL1.id, UseColor.COL2.id,
                        UseColor.COL3.id, UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {

                    resultColorID = scanFromProfSide(elemArtID, originColorID, side);
                    if (resultColorID == -1 && seri == false) {
                        resultColorID = srcColorFk;
                    }
                }

              ////= АВТОПОДБОР =////
            } else if (srcColorFk == 0 || srcColorFk == 100000) {
                //Для точн.подбора в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур"

                //Подбор по текстуре профиля и заполн.
                if (List.of(UseColor.PROF.id, UseColor.GLAS.id).contains(srcColorUS)) {
                    resultColorID = scanFromProfile(elemArtID, originColorID, side);
                    if (resultColorID == -1 && srcColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //если неудача подбора то первая в списке запись цвета
                    }
                    //Подбор по текстуре сторон профиля
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {
                    resultColorID = scanFromProfSide(elemArtID, originColorID, side);
                    if (resultColorID == -1 && srcColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //первая в списке запись цвета
                    }
                }

              ////= ПАРАМЕТР =////
            } else if (srcColorFk < 0) {  //если artdetColorFK == -1 в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур" 
                Record syspar1Rec = spcAdd.elem5e.winc.mapPardef.get(srcColorFk);

                //Подбор по текстуре профиля и заполн.
                if (srcColorUS == UseColor.PROF.id || srcColorUS == UseColor.GLAS.id) {
                    resultColorID = scanFromParams(elemArtID, syspar1Rec, originColorID, side);

                    //Подбор по текстуре сторон профиля
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {
                    resultColorID = scanFromParamSide(elemArtID, syspar1Rec, originColorID, side);

                    //Подбор по текстурному параметру
                } else if (List.of(UseColor.PARAM.id, UseColor.PARSER.id).contains(srcColorUS)) {
                    Record parmapRec = eParmap.find3(syspar1Rec.getStr(eSyspar1.text), syspar1Rec.getInt(eSyspar1.groups_id));
                    originColorID = parmapRec.getInt(eParmap.color_id1);
                    resultColorID = scanFromProfSide(elemArtID, originColorID, side);
                }
            }
            if (resultColorID != -1) {
                if (side == 1) {
                    spcAdd.colorID1 = resultColorID;
                } else if (side == 2) {
                    spcAdd.colorID2 = resultColorID;
                } else if (side == 3) {
                    spcAdd.colorID3 = resultColorID;
                }

            } else { //в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур" 
                return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UColor.colorFromProduct(3) " + e);
        }
        return true;
    }

    //Авто профиля или заполнения
    private static int scanFromProfile(int artiklID, int colorID, int side) {

        List<Record> artdetList = eArtdet.filter(artiklID);
        Field mark_c = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
        //Цикл по ARTDET определённого артикула
        for (Record artdetRec : artdetList) {

            if (side == 1) {
                if (artdetRec.getInt(eArtdet.mark_c1) == 1 && artdetRec.getInt(eArtdet.color_fk) == colorID) {
                    return colorID;
                }
            } else {
                if (artdetRec.getInt(mark_c) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == colorID) {
                        return colorID;
                    }
                } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == colorID) {
                        return colorID;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Авто сторон профиля
     *
     * @param detailArtiklID - артикул элемента детализации состава
     * @param originColorID - текстура стороны профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromProfSide(int detailArtiklID, int originColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(detailArtiklID);
            //Цикл по ARTDET определённого артикула
            for (Record artdetRec : artdetList) {
                //Сторона подлежит рассмотрению?
                if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1))) //cторона подлежит рассмотрению?
                        || (side == 2 && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))))
                        || (side == 3 && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))) {

                    //Группа текстур
                    if (artdetRec.getInt(eArtdet.color_fk) < 0) {
                        List<Record> colorList = eColor.filter(artdetRec.getInt(eArtdet.color_fk)); //фильтр списка определённой группы
                        //Цикл по COLOR определённой группы
                        for (Record colorRec : colorList) {
                            if (colorRec.getInt(eColor.id) == originColorID) {
                                return originColorID;
                            }
                        }

                        //Одна текстура
                    } else {
                        if (artdetRec.getInt(eArtdet.color_fk) == originColorID) { //если есть такая текстура в ARTDET
                            return originColorID;
                        }
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("Ошибка Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    /**
     * Параметр профиля или заполнения
     *
     * @param detailArtiklID - артикул элемента детализации состава
     * @param syspar1Rec - параметр подбора
     * @param originColorID - текстура профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromParams(int detailArtiklID, Record syspar1Rec, int originColorID, int side) {

        List<Record> artdetList = eArtdet.filter(detailArtiklID);
        List<Record> parmapList = eParmap.filter3(syspar1Rec.getInt(eSyspar1.groups_id));
        Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;

        //Цикл по параметрам соответствия текстур
        for (Record parmapRec : parmapList) {
            //Если текстура соответствия и текстура профиля совпали
            if (parmapRec.getInt(eParmap.color_id2) == originColorID) {
                //Цикл по списку текстур элемента
                for (Record artdetRec : artdetList) {

                    if (side == 1) {
                        if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                            if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                return parmapRec.getInt(eParmap.color_id1);
                            }
                        }
                    } else {
                        if (artdetRec.getInt(field) == 1) {
                            if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                return parmapRec.getInt(eParmap.color_id1);
                            }
                        } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                            if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                return parmapRec.getInt(eParmap.color_id1);
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Параметр сторон профиля
     *
     * @param detailArtiklID - артикул элемента детализации состава
     * @param syspar1Rec - параметр подбора
     * @param originColorID - текстура стороны профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromParamSide(int detailArtiklID, Record syspar1Rec, int originColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(detailArtiklID);
            Record parmapRec = eParmap.find3(syspar1Rec.getStr(eSyspar1.text), syspar1Rec.getInt(eSyspar1.groups_id));

            Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            //Если текстура соответствия и текстура профиля совпали
            if (parmapRec.getInt(eParmap.color_id2) == originColorID) {
                //Цикл по списку текстур элемента
                for (Record artdetRec : artdetList) {
                    if (artdetRec.getInt(eArtdet.color_fk) >= 0) {
                        if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1))) //cторона подлежит рассмотрению?
                                || (side == 2 && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))))
                                || (side == 3 && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))) {

                            if (side == 1) {
                                if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                    return parmapRec.getInt(eParmap.color_id1);
                                }
                            } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                                if (artdetRec.getInt(field) == 1) {
                                    if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                        return parmapRec.getInt(eParmap.color_id1);
                                    }
                                } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                                    if (artdetRec.getInt(eArtdet.color_fk) == parmapRec.getInt(eParmap.color_id1)) {
                                        return parmapRec.getInt(eParmap.color_id1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("Ошибка Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    //Первая запись цвета
    private static int scanFromColorFirst(TRecord spc) {
        Record artdetRec = eArtdet.find(spc.detailRec.getInt(ARTIKL_ID));
        if (artdetRec.getInt(1) != -1) {
            int colorFK2 = artdetRec.getInt(eArtdet.color_fk);

            if (colorFK2 < 0 && colorFK2 != -1) { //это группа
                List<Record> colorList = eColor.filter(colorFK2);
                if (colorList.isEmpty() == false) {
                    return colorList.get(0).getInt(eColor.id);
                }
            } else { //если это не группа цветов                               
                return colorFK2;

            }
        }
        JOptionPane.showMessageDialog(null, "Для артикула  " + spc.code + " не определена цена.", "ВНИМАНИЕ!", 1);
        return 1; //такого случая не должно быть
    }

    //Выдает цвет в соответствии с заданным вариантом подбора текстуры   
    private static int colorFromTexturUse(TRecord spcAdd, int srcColorUS) {
        try {
            switch (srcColorUS) {
                case 0:
                    return spcAdd.detailRec.getInt(COLOR_FK);  //указана вручную
                case 11: //По текстуре профиля
                    int artiklID = spcAdd.elem5e.root.artiklRec.getInt(eArtikl.id);
                    Record record = eArtdet.data().stream().filter(rec
                            -> rec.getInt(eArtdet.mark_c1) == 1
                            && rec.getInt(eArtdet.mark_c2) == 1
                            && rec.getInt(eArtdet.mark_c3) == 1
                            && rec.getInt(eArtdet.artikl_id) == artiklID
                            && rec.getInt(eArtdet.color_fk) > 0)
                            .findFirst().orElse(eArtdet.record());
                    if (record.getInt(eArtdet.id) == -3) {
                        record = eArtdet.data().stream().filter(rec
                                -> rec.getInt(eArtdet.mark_c1) == 1
                                && rec.getInt(eArtdet.artikl_id) == artiklID
                                && rec.getInt(eArtdet.color_fk) > 0)
                                .findFirst().orElse(eArtdet.record());
                    }
                    return record.getInt(eArtdet.color_fk);
                case 15: //По текстуре заполнения
                    if (spcAdd.elem5e.artiklRecAn.getInt(eArtikl.level1) == 5) {
                        return spcAdd.elem5e.colorID1;
                    }
                case 1: //По основе профиля
                    return spcAdd.elem5e.root.colorID1;
                case 2: //По внутр.профиля
                    return spcAdd.elem5e.root.colorID2;
                case 3: //По внешн.профиля
                    return spcAdd.elem5e.root.colorID3;
                case 6: //По основе профиля в серии
                    return spcAdd.elem5e.root.colorID1;
                case 7: //По внутр.профиля в серии
                    return spcAdd.elem5e.root.colorID2;
                case 8: //По внешн.профиля в серии
                    return spcAdd.elem5e.root.colorID3;
                default:
                    return -1;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Color.getID_colorUS() " + e);
            return -1;
        }
    }

    //Сообщение неудачи
    private static void colorFromMes(TRecord spc) {  //см. http://help.profsegment.ru/?id=1107        
        String place = "---";
        if ("ВСТ".equals(spc.place)) {
            place = "Составы";
        } else if ("СОЕД".equals(spc.place)) {
            place = "Соединения";
        } else if ("ФУРН".equals(spc.place)) {
            place = "Фурнитура";
        } else if ("КОМП".equals(spc.place)) {
            place = "Комплекты";
        }
        JOptionPane.showMessageDialog(null, "Проблема с заполнением базы данных.\nДля артикула  '" + spc.code + "' не определена текстура. \nСмотри форму 'Составы => " + place + "'.", "ВНИМАНИЕ!", 1);
    }

    public static void colorRuleFromParam(ElemSimple slem5e) {  //см. http://help.profsegment.ru/?id=1107        

        String ruleOfColor = slem5e.spcRec.getParam(-1, 31019);
        if ("-1".equals(ruleOfColor) == false) {
            if ("внутренняя по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID1;
            } else if ("внешняя по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID1;
            } else if ("внутрення по внешней".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID3;
            } else if ("внешняя по внутренней".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID2;
            } else if ("2 стороны по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID1;
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID1;
            }
        }
    }

    //Текстура профиля или текстура заполнения изделия (неокрашенные)
    public static int findColorFromArtdet(int artiklId) {
        try {
            List<Record> artdetList = eArtdet.filter(artiklId);
            //Цикл по ARTDET определённого артикула
            for (Record artdetRec : artdetList) {
                if (artdetRec.getInt(eArtdet.color_fk) >= 0) {
                    if ("1".equals(artdetRec.getStr(eArtdet.mark_c1))) {
                        return artdetRec.getInt(eArtdet.color_fk);
                    }
                }
            }
            return -3;

        } catch (Exception e) {
            System.err.println("Ошибна Paint.colorFromArtikl() " + e);
            return -1;
        }
    }

    //Поиск текстуры в артикуле
    public static int findColorFromArtdet(int artiklID, int side, int elemColorID) {
        try {
            List<Record> artdetList = eArtdet.filter(artiklID);
            //Цикл по ARTDET определённого артикула
            for (Record artdetRec : artdetList) {
                //Сторона подлежит рассмотрению?
                if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1)))
                        || (side == 2 && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))))
                        || (side == 3 && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))) {

                    //Группа текстур
                    if (artdetRec.getInt(eArtdet.color_fk) < 0) {
                        List<Record> colorList = eColor.filter(artdetRec.getInt(eArtdet.color_fk)); //фильтр списка определённой группы
                        //Цикл по COLOR определённой группы
                        for (Record colorRec : colorList) {
                            if (colorRec.getInt(eColor.id) == elemColorID) {
                                return elemColorID;
                            }
                        }

                        //Одна текстура
                    } else {
                        if (artdetRec.getInt(eArtdet.color_fk) == elemColorID) { //если есть такая текстура в ARTDET
                            return elemColorID;
                        }
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("Ошибка Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    //Иконка типа соединения
    public static ImageIcon iconFromTypeJoin(int typeJoin) {
        for (int i = 0; i < 6; i++) {
            if (typeJoin == indexIcon[i]) {
                return icon[i];
            }
        }
        return null;
    }

    //Иконка типа соединения
    public static ImageIcon iconFromTypeJoin2(int typeJoin) {
        for (int i = 0; i < 6; i++) {
            if (typeJoin == indexIcon[i]) {
                return icon2[i];
            }
        }
        return null;
    }
}
