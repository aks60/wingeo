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

    //�����������
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
     * ����� ������� �� �������� �������� ��� ����� ���������
     *
     * @param spcAdd - ����������� ��������
     * @param side - ������ �������� �� ������� ������ ������ ��������
     */
    public static boolean colorFromElemOrSeri(SpcRecord spcAdd) {  //��. http://help.profsegment.ru/?id=1107 

        SpcRecord spcClon = new SpcRecord(spcAdd);
        int typesUS = spcClon.detailRec.getInt(COLOR_US);
        if (UseColor.isSeries(typesUS)) { //���� �����

            List<Record> artseriList = eArtikl.filter(spcClon.artiklRec().getInt(eArtikl.groups4_id));
            for (Record artseriRec : artseriList) {
                spcClon.artiklRec(artseriRec);
                if (UColor.colorFromProduct(spcClon, 1, true)
                        && UColor.colorFromProduct(spcClon, 2, true)
                        && UColor.colorFromProduct(spcClon, 3, true)) {
                    spcAdd.copy(spcClon);
                    return true;
                }
            }
            spcClon.colorID1 = getID_colorUS(spcClon, typesUS & 0x0000000f);
            spcClon.colorID2 = getID_colorUS(spcClon, (typesUS & 0x000000f0) >> 4);
            spcClon.colorID3 = getID_colorUS(spcClon, (typesUS & 0x00000f00) >> 8);

        } else {
            if (UColor.colorFromProduct(spcAdd, 1, false)
                    && UColor.colorFromProduct(spcAdd, 2, false)
                    && UColor.colorFromProduct(spcAdd, 3, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * �������, ����������, ��������
     *
     * @param spcAdd
     * @param side
     * @param seri
     * @return
     */
    private static boolean colorFromProduct(SpcRecord spcAdd, int side, boolean seri) {  //��. http://help.profsegment.ru/?id=1107        

        int srcNumberUS = spcAdd.detailRec.getInt(COLOR_US);
        int srcColorFk = spcAdd.detailRec.getInt(COLOR_FK);

        if (srcColorFk == -1) {
            colorFromMes(spcAdd);
            return false; //��� ������ ��� ������
        }
        int resultColorID = -1;
        try {
            int srcColorUS = (side == 1) ? srcNumberUS & 0x0000000f : (side == 2)
                    ? (srcNumberUS & 0x000000f0) >> 4 : (srcNumberUS & 0x00000f00) >> 8; //��� �������                
            int elemArtID = spcAdd.artiklRec().getInt(eArtikl.id);

            //���� �������� �� �������� �������� �� �������� �������
            int originColorID = getID_colorUS(spcAdd, srcColorUS);

            
            ////= ������� =////
            if (srcColorFk > 0 && srcColorFk != 100000) {

                //����� �������� ��������
                if (srcColorUS == UseColor.MANUAL.id) {
                    if (seri == true) {
                        resultColorID = -1; //������ ��������� �� �����
                    } else {
                        resultColorID = scanFromProfSide(elemArtID, srcColorFk, side); //������������ ��� ������ ������� ��������!!!
                        if (resultColorID == -1) {
                            if (spcAdd.artiklRec().getInt(eArtikl.level1) == 2 && (spcAdd.artiklRec().getInt(eArtikl.level2) == 11 || spcAdd.artiklRec().getInt(eArtikl.level2) == 13)) {
                                return false;
                            }
                            resultColorID = scanFromColorFirst(spcAdd); //������ � ������ � ��� �����������
                        }
                    }

                    //������ �� �������� ������� � �������� ������ �������
                } else if (List.of(UseColor.PROF.id, UseColor.GLAS.id, UseColor.COL1.id, UseColor.COL2.id,
                        UseColor.COL3.id, UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {

                    resultColorID = scanFromProfSide(elemArtID, originColorID, side);
                    if (resultColorID == -1 && seri == false) {
                        resultColorID = srcColorFk;
                    }
                }

                
                ////= ���������� =////
            } else if (srcColorFk == 0 || srcColorFk == 100000) {
                //��� ����.������� � ������������ �� ������. ��. HELP "�����������=>������ �������"

                //������ �� �������� ������� � ������.
                if (List.of(UseColor.PROF.id, UseColor.GLAS.id).contains(srcColorUS)) {
                    resultColorID = scanFromProfile(elemArtID, originColorID, side);
                    if (resultColorID == -1 && srcColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //���� ������� ������� �� ������ � ������ ������ �����
                    }
                    //������ �� �������� ������ �������
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {
                    resultColorID = scanFromProfSide(elemArtID, originColorID, side);
                    if (resultColorID == -1 && srcColorFk == 0) {
                        resultColorID = scanFromColorFirst(spcAdd); //������ � ������ ������ �����
                    }
                }

                
                ////= �������� =////
            } else if (srcColorFk < 0) {  //���� artdetColorFK == -1 � ������������ �� ������. ��. HELP "�����������=>������ �������" 
                Record syspar1Rec = spcAdd.elem5e.winc.mapPardef.get(srcColorFk);

                //������ �� �������� ������� � ������.
                if (srcColorUS == UseColor.PROF.id || srcColorUS == UseColor.GLAS.id) {
                    resultColorID = scanFromParams(elemArtID, syspar1Rec, originColorID, side);

                    //������ �� �������� ������ �������
                } else if (List.of(UseColor.COL1.id, UseColor.COL2.id, UseColor.COL3.id,
                        UseColor.C1SER.id, UseColor.C2SER.id, UseColor.C3SER.id).contains(srcColorUS)) {
                    resultColorID = scanFromParamSide(elemArtID, syspar1Rec, originColorID, side);

                    //������ �� ����������� ���������
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

            } else { //� ������������ �� ������. ��. HELP "�����������=>������ �������" 
                return false;
            }
        } catch (Exception e) {
            System.err.println("������:UColor.colorFromProduct(3) " + e);
        }
        return true;
    }

    /**
     * ���� ������� ��� ����������
     *
     * @param detailArtiklID - ������� �������� �����������
     * �������originColorID@param profFullColorID - �������� �������
     * @param side - ������� �������� ����������� �������
     */
    private static int scanFromProfile(int detailArtiklID, int originColorID, int side) {

        List<Record> artdetList = eArtdet.filter(detailArtiklID);
        Field mark_c = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
        //���� �� ARTDET ������������ ��������
        for (Record artdetRec : artdetList) {

            if (side == 1) {
                if (artdetRec.getInt(eArtdet.mark_c1) == 1 && artdetRec.getInt(eArtdet.color_fk) == originColorID) {
                    return originColorID;
                }
            } else {
                if (artdetRec.getInt(mark_c) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == originColorID) {
                        return originColorID;
                    }
                } else if (artdetRec.getInt(eArtdet.mark_c1) == 1) {
                    if (artdetRec.getInt(eArtdet.color_fk) == originColorID) {
                        return originColorID;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * ���� ������ �������
     *
     * @param detailArtiklID - ������� �������� ����������� �������
     * @param originColorID - �������� ������� �������
     * @param side - ������� �������� ����������� �������
     */
    private static int scanFromProfSide(int detailArtiklID, int originColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(detailArtiklID);
            //���� �� ARTDET ������������ ��������
            for (Record artdetRec : artdetList) {
                //������� �������� ������������?
                if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1))) //c������ �������� ������������?
                        || (side == 2 && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))))
                        || (side == 3 && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))) {

                    //������ �������
                    if (artdetRec.getInt(eArtdet.color_fk) < 0) {
                        List<Record> colorList = eColor.filter(artdetRec.getInt(eArtdet.color_fk)); //������ ������ ����������� ������
                        //���� �� COLOR ����������� ������
                        for (Record colorRec : colorList) {
                            if (colorRec.getInt(eColor.id) == originColorID) {
                                return originColorID;
                            }
                        }

                        //���� ��������
                    } else {
                        if (artdetRec.getInt(eArtdet.color_fk) == originColorID) { //���� ���� ����� �������� � ARTDET
                            return originColorID;
                        }
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("������ Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    /**
     * �������� ������� ��� ����������
     *
     * @param detailArtiklID - ������� �������� ����������� �������
     * @param syspar1Rec - �������� �������
     * @param originColorID - �������� �������
     * @param side - ������� �������� ����������� �������
     */
    private static int scanFromParams(int detailArtiklID, Record syspar1Rec, int originColorID, int side) {

        List<Record> artdetList = eArtdet.filter(detailArtiklID);
        List<Record> parmapList = eParmap.filter3(syspar1Rec.getInt(eSyspar1.groups_id));
        Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;

        //���� �� ���������� ������������ �������
        for (Record parmapRec : parmapList) {
            //���� �������� ������������ � �������� ������� �������
            if (parmapRec.getInt(eParmap.color_id2) == originColorID) {
                //���� �� ������ ������� ��������
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
     * �������� ������ �������
     *
     * @param detailArtiklID - ������� �������� ����������� �������
     * @param syspar1Rec - �������� �������
     * @param originColorID - �������� ������� �������
     * @param side - ������� �������� ����������� �������
     */
    private static int scanFromParamSide(int detailArtiklID, Record syspar1Rec, int originColorID, int side) {
        try {
            List<Record> artdetList = eArtdet.filter(detailArtiklID);
            Record parmapRec = eParmap.find3(syspar1Rec.getStr(eSyspar1.text), syspar1Rec.getInt(eSyspar1.groups_id));

            Field field = (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            //���� �������� ������������ � �������� ������� �������
            if (parmapRec.getInt(eParmap.color_id2) == originColorID) {
                //���� �� ������ ������� ��������
                for (Record artdetRec : artdetList) {
                    if (artdetRec.getInt(eArtdet.color_fk) >= 0) {
                        if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1))) //c������ �������� ������������?
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
            System.err.println("������ Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    //������ ������ �����
    private static int scanFromColorFirst(SpcRecord spc) {
        Record artdetRec = eArtdet.find(spc.detailRec.getInt(ARTIKL_ID));
        if (artdetRec.getInt(1) != -1) {
            int colorFK2 = artdetRec.getInt(eArtdet.color_fk);

            if (colorFK2 < 0 && colorFK2 != -1) { //��� ������
                List<Record> colorList = eColor.filter(colorFK2);
                if (colorList.isEmpty() == false) {
                    return colorList.get(0).getInt(eColor.id);
                }
            } else { //���� ��� �� ������ ������                               
                return colorFK2;

            }
        }
        JOptionPane.showMessageDialog(null, "��� ��������  " + spc.artikl + " �� ���������� ����.", "��������!", 1);
        return 1; //������ ������ �� ������ ����
    }

    //������ ���� � ������������ � �������� ��������� ������� ��������   
    private static int getID_colorUS(SpcRecord spcAdd, int srcColorUS) {
        try {
            switch (srcColorUS) {
                case 0:
                    return spcAdd.detailRec.getInt(COLOR_FK);  //������� �������
                case 11: //�� �������� �������
                    ElemSimple firstElem = spcAdd.elem5e.root.frames.get(0);
                    int artiklID = firstElem.artiklRec.getInt(eArtikl.id);
                    //int artiklID = spcAdd.elem5e.artiklRecAn.getInt(eArtikl.id);
                    return eArtdet.data().stream().filter(rec
                            -> rec.getInt(eArtdet.mark_c1) == 1
                            && rec.getInt(eArtdet.mark_c2) == 1
                            && rec.getInt(eArtdet.mark_c3) == 1
                            && rec.getInt(eArtdet.artikl_id) == artiklID
                            && rec.getInt(eArtdet.color_fk) > 0)
                            .findFirst().orElse(eArtdet.record()).getInt(eArtdet.color_fk);
                case 15: //�� �������� ����������
                    if (spcAdd.elem5e.artiklRecAn.getInt(eArtikl.level1) == 5) {
                        return spcAdd.elem5e.colorID1;
                    }
                case 1: //�� ������ �������
                    return spcAdd.elem5e.winc.colorID1;
//                    return spcAdd.elem5e.colorID1;
                case 2: //�� �����.�������
                    return spcAdd.elem5e.winc.colorID2;
//                    return spcAdd.elem5e.colorID2;
                case 3: //�� �����.�������
                    return spcAdd.elem5e.winc.colorID3;
//                    return spcAdd.elem5e.colorID3;
                case 6: //�� ������ ������� � �����
                    return spcAdd.elem5e.winc.colorID1;
//                    return spcAdd.elem5e.colorID1;
                case 7: //�� �����.������� � �����
                    return spcAdd.elem5e.winc.colorID2;
//                    return spcAdd.elem5e.colorID2;
                case 8: //�� �����.������� � �����
                    return spcAdd.elem5e.winc.colorID3;
//                    return spcAdd.elem5e.colorID3;
                default:
                    return -1;
            }
        } catch (Exception e) {
            System.err.println("������:Color.colorFromTypes() " + e);
            return -1;
        }
    }

    //��������� �������
    private static void colorFromMes(SpcRecord spc) {  //��. http://help.profsegment.ru/?id=1107        
        String place = "---";
        if ("���".equals(spc.place)) {
            place = "�������";
        } else if ("����".equals(spc.place)) {
            place = "����������";
        } else if ("����".equals(spc.place)) {
            place = "���������";
        } else if ("����".equals(spc.place)) {
            place = "���������";
        }
        JOptionPane.showMessageDialog(null, "�������� � ����������� ���� ������.\n��� ��������  '" + spc.artikl + "' �� ���������� ��������. \n������ ����� '������� => " + place + "'.", "��������!", 1);
    }

    public static void colorRuleFromParam(ElemSimple slem5e) {  //��. http://help.profsegment.ru/?id=1107        

        String ruleOfColor = slem5e.spcRec.getParam(-1, 31019);
        if ("-1".equals(ruleOfColor) == false) {
            if ("���������� �� ��������".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID1;
            } else if ("������� �� ��������".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID1;
            } else if ("��������� �� �������".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID3;
            } else if ("������� �� ����������".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID2;
            } else if ("2 ������� �� ��������".equalsIgnoreCase(ruleOfColor)) {
                slem5e.spcRec.colorID2 = slem5e.spcRec.colorID1;
                slem5e.spcRec.colorID3 = slem5e.spcRec.colorID1;
            }
        }
    }

    //�������� ������� ��� �������� ���������� ������� (������������)
    public static int colorFromArtikl(int artiklId) {
        try {
            List<Record> artdetList = eArtdet.filter(artiklId);
            //���� �� ARTDET ������������ ��������
            for (Record artdetRec : artdetList) {
                if (artdetRec.getInt(eArtdet.color_fk) >= 0) {
                    if ("1".equals(artdetRec.getStr(eArtdet.mark_c1))
                            && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))
                            && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))) {

                        return artdetRec.getInt(eArtdet.color_fk);
                    }
                }
            }
            return -3;

        } catch (Exception e) {
            System.err.println("������ Paint.colorFromArtikl() " + e);
            return -1;
        }
    }

    //����� �������� � ��������
    public static int colorFromArtikl(int artiklID, int side, int elemColorID) {
        try {
            List<Record> artdetList = eArtdet.filter(artiklID);
            //���� �� ARTDET ������������ ��������
            for (Record artdetRec : artdetList) {
                //������� �������� ������������?
                if ((side == 1 && "1".equals(artdetRec.getStr(eArtdet.mark_c1)))
                        || (side == 2 && ("1".equals(artdetRec.getStr(eArtdet.mark_c2)) || "1".equals(artdetRec.getStr(eArtdet.mark_c1))))
                        || (side == 3 && ("1".equals(artdetRec.getStr(eArtdet.mark_c3))) || "1".equals(artdetRec.getStr(eArtdet.mark_c1)))) {

                    //������ �������
                    if (artdetRec.getInt(eArtdet.color_fk) < 0) {
                        List<Record> colorList = eColor.filter(artdetRec.getInt(eArtdet.color_fk)); //������ ������ ����������� ������
                        //���� �� COLOR ����������� ������
                        for (Record colorRec : colorList) {
                            if (colorRec.getInt(eColor.id) == elemColorID) {
                                return elemColorID;
                            }
                        }

                        //���� ��������
                    } else {
                        if (artdetRec.getInt(eArtdet.color_fk) == elemColorID) { //���� ���� ����� �������� � ARTDET
                            return elemColorID;
                        }
                    }
                }
            }
            return -1;

        } catch (Exception e) {
            System.err.println("������ Color.colorFromArtdet() " + e);
            return -1;
        }
    }

    //������ ���� ����������
    public static ImageIcon iconFromTypeJoin(int typeJoin) {
        for (int i = 0; i < 6; i++) {
            if (typeJoin == indexIcon[i]) {
                return icon[i];
            }
        }
        return null;
    }

    //������ ���� ����������
    public static ImageIcon iconFromTypeJoin2(int typeJoin) {
        for (int i = 0; i < 6; i++) {
            if (typeJoin == indexIcon[i]) {
                return icon2[i];
            }
        }
        return null;
    }
}
