package builder.making;

import builder.model.ElemSimple;
import dataset.Field;
import dataset.Record;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eParmap;
import domain.eSetting;
import domain.eSyspar1;
import enums.Layout;
import enums.UseColor;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class UColor {

    private static final int COLOR_US = 2;
    private static final int COLOR_FK = 3;
    private static final int ARTIKL_ID = 4;

    private static ImageIcon icon[] = {null, null, null, null, null, null};
    private static ImageIcon icon2[] = {null, null, null, null, null, null};
    private static int[] indexIcon = {10, 20, 30, 31, 40, 41};

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
     * Подбор в текстуре сторон элемента (серии) МЦ
     *
     * @param spcAdd - спецификацм элемента
     * @param side - строна элемента по которой ведётся подбор текстуры
     */
    public static boolean colorFromProduct(Specific spcAdd) {  //см. http://help.profsegment.ru/?id=1107 

        Specific spcClon = new Specific().clon(spcAdd);
        int typesUS = spcClon.detailRec.getInt(COLOR_US);
        if (UseColor.isSeries(typesUS)) { //если серия

            List<Record> artseriList = eArtikl.find3(spcClon.artiklRec.getInt(eArtikl.groups4_id));
            for (Record artseriRec : artseriList) {
                spcClon.setArtikl(artseriRec);
                if (UColor.colorFromProduct(spcClon, 1, true)
                        && UColor.colorFromProduct(spcClon, 2, true)
                        && UColor.colorFromProduct(spcClon, 3, true)) {
                    spcAdd.clon(spcClon);
                    return true;
                }
            }
            spcClon.setColor(1, getColorFromProfile(spcClon, typesUS & 0x0000000f));
            spcClon.setColor(2, getColorFromProfile(spcClon, (typesUS & 0x000000f0) >> 4));
            spcClon.setColor(3, getColorFromProfile(spcClon, (typesUS & 0x00000f00) >> 8));

        } else {
            if (UColor.colorFromProduct(spcAdd, 1, false)
                    && UColor.colorFromProduct(spcAdd, 2, false)
                    && UColor.colorFromProduct(spcAdd, 3, false)) {
                return true;
            }
        }
        return false;
    }

    private static boolean colorFromProduct(Specific spcAdd, int side, boolean seri) {  //см. http://help.profsegment.ru/?id=1107        

        int elemColorFk = spcAdd.detailRec.getInt(COLOR_FK);
        int typesUS = spcAdd.detailRec.getInt(COLOR_US);

        if (elemColorFk == -1) {
            colorFromMes(spcAdd);
            return false; //нет данных для поиска
        }
        int resultColorID = -1;
        try {
            int elemColorUS = (side == 1) ? typesUS & 0x0000000f : (side == 2) ? (typesUS & 0x000000f0) >> 4 : (typesUS & 0x00000f00) >> 8; //тип подбора                
            int elemArtID = spcAdd.artiklRec.getInt(eArtikl.id);
            int profSideColorID = getColorFromProfile(spcAdd, elemColorUS); //цвет из варианта подбора 

            ////= ВРУЧНУЮ =////
            if (elemColorFk > 0 && elemColorFk != 100000) {

                //Явное указание текстуры
                if (elemColorUS == UseColor.MANUAL.id) {
                    if (seri == true) {
                        resultColorID = -1; //нельзя назначать на серию
                    } else {
                        resultColorID = scanFromProfSide(elemArtID, elemColorFk, side); //теоритически это должно железно работать!!!
                        if (resultColorID == -1) {
                            //System.err.println("Коллизия определения явного указания цвета");
                            if ("ps3".equals(eSetting.val(2))) {
                                return false;
                            }
                            if (spcAdd.artiklRec.getInt(eArtikl.level1) == 2 && (spcAdd.artiklRec.getInt(eArtikl.level2) == 11 || spcAdd.artiklRec.getInt(eArtikl.level2) == 13)) {
                                return false;
                            }
                            resultColorID = scanFromColorFirst(spcAdd); //первая в списке и это неправильно
                        }
                    }

                    //Подбор по текстуре профиля и текстуре сторон профиля
                } else if (List.of(UseColor.PROF.id, UseColor.GLAS.id, UseColor.COL1.id, UseColor.COL2.id,
                        UseColor.COL3.id, UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(elemColorUS)) {

                    resultColorID = scanFromProfSide(elemArtID, profSideColorID, side);
                    if (resultColorID == -1 && seri == false) {
                        resultColorID = elemColorFk;
                    }
                }

                ////= АВТОПОДБОР =////
            } else if (elemColorFk == 0 || elemColorFk == 100000) {
                //Для spcColorFk == 100000 если artdetColorFK == -1 в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур"

                //Подбор по текстуре профиля и заполн.
                if (List.of(UseColor.PROF.id, UseColor.GLAS.id).contains(elemColorUS)) {
                    resultColorID = scanFromProfile(elemArtID, profSideColorID, side);
                    if (resultColorID == -1 && elemColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //если неудача подбора то первая в списке запись цвета
                    }
                    //Подбор по текстуре сторон профиля
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(elemColorUS)) {
                    resultColorID = scanFromProfSide(elemArtID, profSideColorID, side);
                    if (resultColorID == -1 && elemColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //первая в списке запись цвета
                    }
                }

                ////= ПАРАМЕТР =////
            } else if (elemColorFk < 0) {  //если artdetColorFK == -1 в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур" 
                Record syspar1Rec = spcAdd.elem5e.winc().mapPardef().get(elemColorFk);

                //Подбор по текстуре профиля и заполн.
                if (elemColorUS == UseColor.PROF.id || elemColorUS == UseColor.GLAS.id) {
                    resultColorID = scanFromParams(elemArtID, syspar1Rec, profSideColorID, side);

                    //Подбор по текстуре сторон профиля
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(elemColorUS)) {
                    resultColorID = scanFromParamSide(elemArtID, syspar1Rec, profSideColorID, side);

                    //Подбор по текстурному параметру
                } else if (List.of(UseColor.PARAM.id, UseColor.PARSER.id).contains(elemColorUS)) {
                    Record parmapRec = eParmap.find3(syspar1Rec.getStr(eSyspar1.text), syspar1Rec.getInt(eSyspar1.groups_id));
                    profSideColorID = parmapRec.getInt(eParmap.color_id1);
                    resultColorID = scanFromProfSide(elemArtID, profSideColorID, side);
                }
            }
            if (resultColorID != -1) {
                spcAdd.setColor(side, resultColorID);

            } else { //в спецификпцию не попадёт. См. HELP "Конструктив=>Подбор текстур" 
                return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UColor.colorFromProduct() " + e);
        }
        return true;
    }

    /**
     * Подбор по текстуре профиля или заполнения в элементе МЦ
     *
     * @param elemArtiklID - артикул элемента детализации состава
     * @param profFullColorID - текстура профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromProfile(int elemArtiklID, int profFullColorID, int side) {

        List<Record> artdetList = eArtdet.filter(elemArtiklID);
        Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
        //Цикл по ARTDET определённого артикула
        for (Record artdetRec : artdetList) {

            if (side == 1) {
                if (artdetRec.getInt(eArtdet.mark_c1) == 1 && artdetRec.getInt(eArtdet.color_fk) == profFullColorID) {
                    return profFullColorID;
                }
            } else {
                if (artdetRec.getInt(field) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == profFullColorID) {
                        return profFullColorID;
                    }
                } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == profFullColorID) {
                        return profFullColorID;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Подбор по текстуре сторон профиля в элементе МЦ
     *
     * @param elemArtiklID - артикул элемента детализации состава
     * @param profSideColorID - текстура стороны профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromProfSide(int elemArtiklID, int profSideColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(elemArtiklID);
            //Цикл по ARTDET определённого артикула
            for (Record artdetRec : artdetList) {
                //Сторона подлежит рассмотрению?
                if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1)))
                        || (side == 2 && (artdetRec.getInt(eArtdet.mark_c2) == 1 || (artdetRec.getInt(eArtdet.mark_c2) != 1 && artdetRec.getInt(eArtdet.mark_c1) == 1)))
                        || (side == 3 && (artdetRec.getInt(eArtdet.mark_c3) == 1 || (artdetRec.getInt(eArtdet.mark_c3) != 1 && artdetRec.getInt(eArtdet.mark_c1) == 1)))) {

                    //Группа текстур
                    if (artdetRec.getInt(eArtdet.color_fk) < 0) {
                        List<Record> colorList = eColor.find2(artdetRec.getInt(eArtdet.color_fk)); //фильтр списка определённой группы
                        //Цикл по COLOR определённой группы
                        for (Record colorRec : colorList) {
                            if (colorRec.getInt(eColor.id) == profSideColorID) {
                                return profSideColorID;
                            }
                        }

                        //Одна текстура
                    } else {
                        if (artdetRec.getInt(eArtdet.color_fk) == profSideColorID) { //если есть такая текстура в ARTDET
                            return profSideColorID;
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
     * Подбор по параметру текстур профиля или заполнения в элементе МЦ
     *
     * @param elemArtiklID - артикул элемента детализации состава
     * @param syspar1Rec - параметр подбора
     * @param profFullColorID - текстура профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromParams(int elemArtiklID, Record syspar1Rec, int profFullColorID, int side) {

        List<Record> artdetList = eArtdet.filter(elemArtiklID);
        List<Record> parmapList = eParmap.find2(syspar1Rec.getInt(eSyspar1.groups_id));
        Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;

        //Цикл по параметрам соответствия текстур
        for (Record parmapRec : parmapList) {
            //Если текстура соответствия и текстура профиля совпали
            if (parmapRec.getInt(eParmap.color_id2) == profFullColorID) {
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
     * Подбор по параметру текстур сторон профиля в элементе МЦ
     *
     * @param elemArtiklID - артикул элемента детализации состава
     * @param syspar1Rec - параметр подбора
     * @param profSideColorID - текстура стороны профиля
     * @param side - сторона элемента детализации состава
     */
    private static int scanFromParamSide(int elemArtiklID, Record syspar1Rec, int profSideColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(elemArtiklID);
            Record parmapRec = eParmap.find3(syspar1Rec.getStr(eSyspar1.text), syspar1Rec.getInt(eSyspar1.groups_id));
            
            Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            //Если текстура соответствия и текстура профиля совпали
            if (parmapRec.getInt(eParmap.color_id2) == profSideColorID) {
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

    //Первая в списке запись цвета элемента
    private static int scanFromColorFirst(Specific spc) {
        Record artdetRec = eArtdet.find(spc.detailRec.getInt(ARTIKL_ID));
        if (artdetRec != null) {
            int colorFK2 = artdetRec.getInt(eArtdet.color_fk);

            if (colorFK2 < 0 && colorFK2 != -1) { //это группа
                List<Record> colorList = eColor.find2(colorFK2);
                if (colorList.isEmpty() == false) {
                    return colorList.get(0).getInt(eColor.id);
                }
            } else { //если это не группа цветов                               
                return colorFK2;

            }
        }
        JOptionPane.showMessageDialog(null, "Для артикула  " + spc.artikl + " не определена цена.", "ВНИМАНИЕ!", 1);
        return 1; //такого случая не должно быть
    }

    //Выдает цвет проф. в соответствии с заданным вариантом подбора текстуры   
    private static int getColorFromProfile(Specific spcAdd, int elemColorUS) {
        try {
            switch (elemColorUS) {
                case 0:
                    return spcAdd.detailRec.getInt(COLOR_FK);  //указана вручную
                case 11: //По текстуре профиля
                    HashMap.Entry<Layout, ElemSimple> firstEntry = (Entry<Layout, ElemSimple>) spcAdd.elem5e.root().frames().entrySet().iterator().next();
                    int artiklID = firstEntry.getValue().artiklRec().getInt(eArtikl.id);
                    return eArtdet.query().stream().filter(rec -> rec.getInt(eArtdet.mark_c1) == 1
                            && rec.getInt(eArtdet.mark_c2) == 1 && rec.getInt(eArtdet.mark_c3) == 1
                            && rec.getInt(eArtdet.artikl_id) == artiklID && rec.getInt(eArtdet.color_fk) > 0)
                            .findFirst().orElse(eArtdet.record()).getInt(eArtdet.color_fk);
                case 15: //По текстуре заполнения
                    //System.out.println("builder.making.UColor.getColorProfile()");
                    return -1;
                case 1: //По основе профиля
                    return spcAdd.elem5e.winc().colorID1;
                case 2: //По внутр.профиля
                    return spcAdd.elem5e.winc().colorID2;
                case 3: //По внешн.профиля
                    return spcAdd.elem5e.winc().colorID3;
                case 6: //По основе профиля в серии
                    return spcAdd.elem5e.winc().colorID1;
                case 7: //По внутр.профиля в серии
                    return spcAdd.elem5e.winc().colorID2;
                case 8: //По внешн.профиля в серии
                    return spcAdd.elem5e.winc().colorID3;
                default:
                    return -1;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Color.colorFromTypes() " + e);
            return -1;
        }
    }

    //Сообщение неудачи
    private static void colorFromMes(Specific spc) {  //см. http://help.profsegment.ru/?id=1107        
        String place = "---";
        if ("ВСТ".equals(spc.place)) {
            place = "Вставки";
        } else if ("СОЕД".equals(spc.place)) {
            place = "Соединения";
        } else if ("ФУРН".equals(spc.place)) {
            place = "Фурнитура";
        } else if ("КОМП".equals(spc.place)) {
            place = "Комплекты";
        }
        JOptionPane.showMessageDialog(null, "Проблема с заполнением базы данных.\nДля артикула  '" + spc.artikl + "' не определена текстура. \nСмотри форму 'Составы => " + place + "'.", "ВНИМАНИЕ!", 1);
    }

    public static void colorRuleFromParam(ElemSimple slem5e) {  //см. http://help.profsegment.ru/?id=1107        

        String ruleOfColor = slem5e.spcRec().getParam(-1, 31019);
        if ("-1".equals(ruleOfColor) == false) {
            if ("внутренняя по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec().colorID2 = slem5e.spcRec().colorID1;
            } else if ("внешняя по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec().colorID3 = slem5e.spcRec().colorID1;
            } else if ("внутрення по внешней".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec().colorID2 = slem5e.spcRec().colorID3;
            } else if ("внешняя по внутренней".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec().colorID3 = slem5e.spcRec().colorID2;
            } else if ("2 стороны по основной".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec().colorID2 = slem5e.spcRec().colorID1;
                slem5e.spcRec().colorID3 = slem5e.spcRec().colorID1;
            }
        }
    }

    //Текстура профиля или текстура заполнения изделия (неокрашенные)
    public static int colorFromArtikl(int artiklId) {
        try {
            List<Record> artdetList = eArtdet.filter(artiklId);
            //Цикл по ARTDET определённого артикула
            for (Record artdetRec : artdetList) {
                if (artdetRec.getInt(eArtdet.color_fk) >= 0) {
                    if ("1".equals(artdetRec.getStr(eArtdet.mark_c1))
                            && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))
                            && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))) {

                        return artdetRec.getInt(eArtdet.color_fk);
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("Ошибна Paint.colorFromArtikl() " + e);
            return -1;
        }
    }
    
    //Поиск текстуры в артикуле
    public static int colorFromArtikl(int artiklID, int side, int elemColorID) {
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
                        List<Record> colorList = eColor.find2(artdetRec.getInt(eArtdet.color_fk)); //фильтр списка определённой группы
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
