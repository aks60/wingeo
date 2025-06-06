package builder.param.check;

import java.util.HashMap;

import static builder.param.check.WincalcTest.param;
import static builder.param.check.WincalcTest.hmParam;
import static builder.param.check.WincalcTest.grup;
import static builder.param.check.WincalcTest.record;

import static builder.param.check.WincalcTest.elementVar2;
import static builder.param.check.WincalcTest.elementDet2;
import static builder.param.check.WincalcTest.joiningVar2;
import static builder.param.check.WincalcTest.joiningDet2;
import static builder.param.check.WincalcTest.fillingVar2;
import static builder.param.check.WincalcTest.fillingDet2;
import static builder.param.check.WincalcTest.furnitureVar2;
import static builder.param.check.WincalcTest.furnitureDet2;

import static builder.param.check.WincalcTest.elementVar3;
import static builder.param.check.WincalcTest.elementDet3;
import static builder.param.check.WincalcTest.joiningVar3;
import static builder.param.check.WincalcTest.joiningDet3;
import static builder.param.check.WincalcTest.fillingVar3;
import static builder.param.check.WincalcTest.fillingDet3;
import static builder.param.check.WincalcTest.furnitureVar3;
import static builder.param.check.WincalcTest.furnitureDet3;

import static builder.param.check.WincalcTest.elementVar4;
import static builder.param.check.WincalcTest.elementDet4;
import static builder.param.check.WincalcTest.joiningVar4;
import static builder.param.check.WincalcTest.joiningDet4;
import static builder.param.check.WincalcTest.fillingVar4;
import static builder.param.check.WincalcTest.fillingDet4;
import static builder.param.check.WincalcTest.furnitureVar4;
import static builder.param.check.WincalcTest.furnitureDet4;
import static builder.param.check.WincalcTest.iwin2;
import static builder.param.check.WincalcTest.iwin3;
import static builder.param.check.WincalcTest.iwin4;
import static builder.param.check.WincalcTest.imp2_horiz;
import static builder.param.check.WincalcTest.imp2_vert;
import static builder.param.check.WincalcTest.glass2_top;
import static builder.param.check.WincalcTest.glass2_left;
import static builder.param.check.WincalcTest.glass2_right;
import static builder.param.check.WincalcTest.frame3_left;
import static builder.param.check.WincalcTest.frame3_right;
import static builder.param.check.WincalcTest.imp3_vert;
import static builder.param.check.WincalcTest.glass3_top;
import static builder.param.check.WincalcTest.glass3_left;
import static builder.param.check.WincalcTest.frame4_left;
import static builder.param.check.WincalcTest.frame4_right;
import static builder.param.check.WincalcTest.glass4_right;
import static builder.param.check.WincalcTest.glass4_left;
import static builder.param.check.WincalcTest.stv2_left_3;
import static builder.param.check.WincalcTest.stv3_right_3;
import static builder.param.check.WincalcTest.stv4_left_1;
import static builder.param.check.WincalcTest.stv4_right_3;
import static builder.param.check.WincalcTest.frame2_left;
import static builder.param.check.WincalcTest.frame2_right;

public class FillingTest {

    public FillingTest() {
        super();
    }

    /**
     * select distinct e.id, e.name, b.id, b.name, a.text from glaspar1 a left
     * join glasgrp b on b.id = a.glasgrp_id left join glasprof c on
     * c.glasgrp_id = b.id left join sysprof d on d.artikl_id = c.artikl_id left
     * join systree e on e.id = d.systree_id where a.groups_id = 13014
     */
    public void fillingVar() {
        try {
//        grup = 13001; //���� ������� ������� 
//        assert true == fillingVar2.check(glass2_left, param("KBE 58", grup)) : grup;
//        assert false == fillingVar2.check(glass2_left, param("KBE 5X", grup)) : grup;

            grup = 13003; //��� ������
            assert false == fillingVar2.check(stv2_left_3, param("������", grup)) : grup;
            assert true == fillingVar2.check(stv2_left_3, param("�� ������", grup)) : grup;

            grup = 13005; //���������� ����
            assert true == fillingVar3.check(glass3_top, param("�����������", grup)) : grup;
            assert false == fillingVar3.check(glass3_left, param("������", grup)) : grup;

            grup = 13014; //���� ���������� �������, � 
            assert true == fillingVar3.check(glass3_left, param("0;90;180;270", grup));
            assert false == fillingVar3.check(glass3_left, param("0;9.1;180;270", grup));

            grup = 13015;  //����� ����������
            assert true == fillingVar3.check(glass3_left, param("�������������", grup)) : grup;
            assert false == fillingVar3.check(glass3_left, param("�� �������������", grup)) : grup;
            assert true == fillingVar3.check(glass3_top, param("�������", grup)) : grup;
            assert false == fillingVar3.check(glass3_top, param("�� �������", grup)) : grup;

            grup = 13017; //��� ������� �������� ������
            assert true == fillingVar4.check(glass4_left, param("me-1", grup)) : grup;
            assert false == fillingVar4.check(glass3_left, param("��-40", grup)) : grup;

            grup = 13095; //���� ������� ������� ����������� (��. Systree->���.��������)
            assert true == fillingVar4.check(stv4_right_3, param("1;2;", grup)) : grup;
            assert false == fillingVar4.check(stv4_right_3, param("2;9", grup)) : grup;
            
            System.err.println("builder.param.check.FillingTest.fillingVar() - ���������");
        } catch (Exception e) {
            System.err.println("������:FillingtTest.fillingVar() " + e);
        }
    }

    /**
     * select c.name, d.name, a.text from glaspar2 a left join glasdet b on b.id
     * = a.glasdet_id left join glasgrp c on c.id = b.glasgrp_id left join
     * artikl d on b.artikl_id = d.id where a.groups_id in (14065, 15055)
     */
    public void fillingDet() {
        HashMap<Integer, String> mapParam = new HashMap<Integer, String>();
        try {
            grup = 14000; //15000 //��� ���������������� ���� ����������
            assert true == fillingDet2.check(mapParam, null, param("KBE 58", grup)) : grup;
            assert false == fillingDet2.check(mapParam, null, param("KBE;58;", grup)) : grup;

            grup = 14001; //15001 //���� ������� ������� (��� ����� ���������� ������� ������� �������-KBE 58 )
            //assert true == fillingDet2.check(mapParam, glass2_left, param("KBE 58", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_left, param("null", grup)) : grup;

            grup = 14005;  //��� ������
            assert false == fillingDet2.check(mapParam, glass2_left, param("������", grup)) : grup;
            assert true == fillingDet2.check(mapParam, glass2_left, param("�� ������", grup)) : grup;

            grup = 14008; //15008 //����������� ���������� ���., ��
            assert true == fillingDet2.check(mapParam, glass2_left, param("30", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_left, param("32", grup)) : grup;

            grup = 14009;  //������� ����������
            assert true == fillingDet2.check(mapParam, glass2_left, param("���", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_left, param("��", grup)) : grup;

            grup = 14017; //15017 //��� ������� �������� ������ 
            assert true == fillingDet2.check(mapParam, glass2_left, param("et-1", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_left, param("��-40", grup)) : grup;

            grup = 14065; //15055 //����������� ����, � ��� ������ ����
            assert true == fillingDet2.check(mapParam, glass2_left, param("0", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_left, param("270,1", grup)) : grup;

            grup = 14067; //15067 //���� �������� �������� ������� 
            assert true == fillingDet2.check(mapParam, glass2_right, param("1009", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_right, param("109", grup)) : grup;

            grup = 14068; //15068 //���� �����. �������� �������
            assert true == fillingDet2.check(mapParam, glass2_right, param("100-1009", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_right, param("109", grup)) : grup;

            grup = 14069; //15069 //���� �����. �������� �������
            assert true == fillingDet2.check(mapParam, glass2_right, param("1009", grup)) : grup;
            assert false == fillingDet2.check(mapParam, glass2_right, param("109", grup)) : grup;

            grup = 14081; //15081 //���� ������� ������� �������
            assert true == fillingDet3.check(mapParam, glass3_left, param("21316-05000", grup)) : grup;
            assert false == fillingDet3.check(mapParam, glass3_left, param("21316=05000", grup)) : grup;

            grup = 15027; //12027  //������������ ��� �������
            assert true == fillingDet4.check(mapParam, glass4_left, param("��� �����������", grup)) : grup;
            assert false == fillingDet4.check(mapParam, glass4_left, param("� ������������", grup)) : grup;
            
            System.err.println("builder.param.check.FillingTest.fillingDet() - ���������");
        } catch (Exception e) {
            System.err.println("������:FillingTest.fillingDet() " + e);
        }
    }
}
