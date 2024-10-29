package builder.param.check;

import static builder.param.check.WincalcTest.param;
import dataset.Record;
import domain.eElement;
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
import static builder.param.check.WincalcTest.frame2_1;
import static builder.param.check.WincalcTest.frame2_3;
import static builder.param.check.WincalcTest.stv2_left_3;
import static builder.param.check.WincalcTest.stv3_right_3;
import static builder.param.check.WincalcTest.stv4_left_1;
import static builder.param.check.WincalcTest.stv4_right_3;
import common.UCom;

public class JoiningTest {

    public JoiningTest() {
        super();
    }

    public void joiningVar() {
        try {
            grup = 1005; //2005, 3005, 4005 //��������� ����� ��� ��������1/��������2
            assert true == joiningVar2.check(UCom.join(iwin2.listJoin, frame2_1, 0), param("1/1", grup)) : grup;
            assert true == joiningVar2.check(UCom.join(iwin2.listJoin, imp2_horiz, 0), param("1/3", grup)) : grup;

            grup = 1008; //����������� ���������� ���., ��
            assert true == joiningVar2.check(UCom.join(iwin2.listJoin, frame2_1, 1), param("30", grup)) : grup;
            assert false == joiningVar2.check(UCom.join(iwin2.listJoin, frame2_1, 1), param("32", grup)) : grup;

            grup = 1010; //4010 //������� ����������
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_left, 0), param("��", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 0), param("���", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 0), param("��", grup)) : grup;
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 0), param("Xx", grup)) : grup;

            grup = 1020; //����������� ���� � ���������, �
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("30;270", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("90", grup)) : grup;

            grup = 1039; //��� ���� ���������� 
            assert true == joiningVar2.check(UCom.join(iwin2.listJoin, stv2_left_3, 1), param("����������", grup)) : grup;
            assert false == joiningVar2.check(UCom.join(iwin2.listJoin, stv2_left_3, 1), param("���������-��������", grup)) : grup;

            grup = 1043; //����������� �������� �������, �� 
            assert false == joiningVar3.check(UCom.join(iwin3.listJoin, frame3_left, 1), param("1,81", grup)) : grup;
            assert true == joiningVar3.check(UCom.join(iwin3.listJoin, frame3_left, 1), param("1,0-3,0", grup)) : grup;
            assert true == joiningVar3.check(UCom.join(iwin3.listJoin, frame3_left, 1), param("2,0", grup)) : grup;

            grup = 1095; //2095, 3095, 4095; //���� ������� ������� �����������
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("1;2;", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("2;9", grup)) : grup;

//        grup = 2003; //3003; //���� ��������
//        assert true == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 0), param("�����", grup)) : grup;
//        assert true == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 1), param("�����", grup)) : grup;
//        assert false == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 1), param("������", grup)) : grup;
            grup = 2012; //3012; //��� ��������� ������ ������
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_right, 1), param("�����������", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("_�����������", grup)) : grup;

            grup = 2015; //3015, 4015 //���������� ��������1/��������2, �
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_right, 1), param("0;180/*", grup)) : grup;
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("90/*", grup)) : grup;

            grup = 2020; //3020, 4030 //����������� ����, �
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_right, 1), param("29-89,99;90,01-360", grup)) : grup;
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("90", grup)) : grup;

            grup = 3002; //4002 //��� L-��������� �������� ��� ��� �-��������� ��������
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_right, 1), param("������� L-���", grup)) : grup;
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, frame4_right, 1), param("null", grup)) : grup;

            grup = 4020; //���� ��������
            assert false == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("30-89,98;90,11-179,9;180,1-269,9;270,1-359,9;", grup)) : grup;
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("89,98-90,1", grup)) : grup;
            assert true == joiningVar4.check(UCom.join(iwin4.listJoin, stv4_right_3, 1), param("90", grup)) : grup;
            
            System.err.println("builder.param.check.JoiningTest.joiningVar() - ���������");
        } catch (Exception e) {
            System.err.println("������:JoiningTest.joiningVar() " + e);
        }
    }

    /**
     * select d.id, c.name, b.id, a.text from joinpar2 a left join joindet b on
     * b.id = a.joindet_id left join joinvar c on c.id = b.joinvar_id left join
     * joining d on d.id = c.joining_id where a.groups_id = 11009
     */
    public void joiningDet() {
        HashMap<Integer, String> mapParam = new HashMap<Integer, String>();
        try {
            grup = 11000; //12000 //��� ���������������� ���� ����������
            //Object o22 = iwin_2.listJoin.get(frame_right_2, 1);
            assert true == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("KBE 58;/KBE 58;", grup)) : grup;
            assert true == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("KBE 58;/KBE 58", grup)) : grup;
            assert false == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("KBE 58;/KBE 5", grup)) : grup;

//        grup = 11001; //12001 //���� ������� ������� ���.1 
//        assert true == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("KBE 58", grup)) : grup;
//        assert false == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("null", grup)) : grup;
//
//        grup = 11002; //12002 //���� ������� ������� ���.2 
//        assert true == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("KBE 58", grup)) : grup;
//        assert false == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("null", grup)) : grup;
            grup = 11005;  //12005 //��������� ����
            assert true == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("1-3", grup)) : grup;
            assert false == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("4", grup)) : grup;

            grup = 11008; //����������� ���������� �������, ��
            assert true == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("32", grup)) : grup;
            assert false == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("30", grup)) : grup;

            grup = 11028; //12028 //�������� ���� ����������, ��
            assert true == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("30-140;", grup)) : grup;
            assert true == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("140;", grup)) : grup;
            assert false == joiningDet3.check(mapParam, UCom.join(iwin3.listJoin, frame3_right, 1), param("30;", grup)) : grup;

            grup = 11066; //���� �������� ������� ���.1 
            assert true == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("1009-1010", grup)) : grup;
            assert false == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("1006", grup)) : grup;

            grup = 11095; //12095 //���� ������� ������� �����������
            assert true == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("1;5;4;", grup)) : grup;
            assert false == joiningDet2.check(mapParam, UCom.join(iwin2.listJoin, frame2_3, 1), param("5;4;", grup)) : grup;
            
            System.err.println("builder.param.check.JoiningTest.joiningDet() - ���������");
        } catch (Exception e) {
            System.err.println("������:JoiningTest.joiningDet() " + e);
        }
    }
}
