package builder.param.check;

import builder.model.AreaSimple;
import java.util.HashMap;

import static builder.param.check.WincalcTest.param;
import static builder.param.check.WincalcTest.grup;
import static builder.param.check.WincalcTest.furnitureVar2;
import static builder.param.check.WincalcTest.furnitureDet2;

import static builder.param.check.WincalcTest.furnitureVar3;
import static builder.param.check.WincalcTest.furnitureDet3;

import static builder.param.check.WincalcTest.furnitureDet4;
import static builder.param.check.WincalcTest.frame3_left;
import static builder.param.check.WincalcTest.stv2_left_3;
import static builder.param.check.WincalcTest.stv2_left_4;
import static builder.param.check.WincalcTest.stv3_right_3;
import static builder.param.check.WincalcTest.stv4_right_3;

public class FurnitureTest {

    public FurnitureTest() {
        super();
    }

    public void furnitureVar() {
        try {
            //TODO Test - ����� �������
            grup = 21001; //����� ������� //eElement.find4(212)
            assert true == furnitureVar2.check(null, param("�������������", grup)) : grup;
            assert false == furnitureVar3.check(null, param("�������������", grup)) : grup;
            assert true == furnitureVar3.check(null, param("�������", grup)) : grup;
            assert false == furnitureVar3.check(null, param("�� �������", grup)) : grup;

            grup = 21004;  //������� ������� 
            assert true == furnitureVar2.check(null, param("917", grup)) : grup;
            assert true == furnitureVar3.check(null, param("21316-05000", grup)) : grup;
            assert false == furnitureVar3.check(null, param("21316*05000", grup)) : grup;

            grup = 21005;  //������� ���������� �� ���������  
            assert true == furnitureVar3.check(null, param("4x10x4x10x4", grup)) : grup;
            assert false == furnitureVar3.check(null, param("xxx", grup)) : grup;

            grup = 21010; //����������� ����� �������, �� 
            assert true == furnitureVar3.check(frame3_left, param("0-6000", grup)) : grup;
            assert false == furnitureVar3.check(frame3_left, param("5000-6000", grup)) : grup;

            grup = 21013; //����������� ������ ����� �� ��������, ��
            assert true == furnitureVar3.check(stv3_right_3, param("940-1200", grup)) : grup;
            assert false == furnitureVar3.check(stv3_right_3, param("500-501", grup)) : grup;

            grup = 21016; //���������� ����������� ��������� (�/�)
            assert true == furnitureVar2.check(stv2_left_3, param("0,5-1;1-2,0", grup)) : grup;
            assert false == furnitureVar2.check(stv2_left_3, param("1-1,09", grup)) : grup;

            grup = 21040;  //����������� ����
            assert true == furnitureVar2.check(stv2_left_4, param("74-360", grup)) : grup;
            assert false == furnitureVar2.check(stv2_left_4, param("12-55", grup)) : grup;
            
            System.out.println("builder.param.check.FurnitureTest.furnitureVar() - ���������");
        } catch (Exception e) {
            System.err.println("������:furnitureTest.furnitureVar() " + e);
        }
    }

    /**
     * ������ ������ ������� select distinct c.name, d.name, a.text from
     * furnpar2 a left join furndet b on b.id = a.furndet_id left join furniture
     * c on c.id = b.furndet_id left join artikl d on b.artikl_id = d.id where
     * a.groups_id in (24003)
     */
    public void furnitureDet() {
        try {
            HashMap<Integer, String> mapParam = new HashMap<Integer, String>();
            AreaSimple area3_stv_right = (AreaSimple) stv3_right_3.owner;
            AreaSimple area2_stv_left = (AreaSimple) stv2_left_3.owner;
            AreaSimple area4_stv_right = (AreaSimple) stv4_right_3.owner;

            grup = 24001; //25001 //����� �������
            assert false == furnitureDet3.check(mapParam, area2_stv_left, param("�������", grup)) : grup;
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("�������������", grup)) : grup;
            assert true == furnitureDet3.check(mapParam, area2_stv_left, param("�� �������", grup)) : grup;

            grup = 24002;  //���� ������� �������
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("917.07", grup)) : grup;
            assert false == furnitureDet2.check(mapParam, area2_stv_left, param("91X.07", grup)) : grup;

            grup = 24004; //���� ������� ��������� � ��������
            assert true == furnitureDet3.check(mapParam, area3_stv_right, param("21315-01000", grup)) : grup;
            assert false == furnitureDet3.check(mapParam, area3_stv_right, param("xxxxxxxxxxx", grup)) : grup;

            grup = 24005; //25005  //���� �������� ������� 
            assert true == furnitureDet3.check(mapParam, area3_stv_right, param("1009;10000-10999;17000-29999;", grup)) : grup;
            assert true == furnitureDet3.check(mapParam, area3_stv_right, param("0-1008;1009", grup)) : grup;
            assert false == furnitureDet3.check(mapParam, area3_stv_right, param("0-1008;1010", grup)) : grup;

            grup = 24007; //25007  //���� �������� ����� 
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("����������", grup)) : grup;
            assert false == furnitureDet2.check(mapParam, area2_stv_left, param("�����", grup)) : grup;

            grup = 24008; //25008  //���� ����� �������
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("����� �������", grup)) : grup;
            assert false == furnitureDet2.check(mapParam, area2_stv_left, param("�����", grup)) : grup;

//        grup = 24009; //25009  //���� �������� �������  
//        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("����������", grup)) : grup;
//        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("�����", grup)) : grup; 
            grup = 24012;  //����������� ����������
            assert true == furnitureDet3.check(mapParam, area3_stv_right, param("������", grup)) : grup;
            assert false == furnitureDet3.check(mapParam, area3_stv_right, param("�����", grup)) : grup;

            grup = 24017; //25017 //��� ������� �������� ������
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("et-1", grup)) : grup;
            assert false == furnitureDet2.check(mapParam, area2_stv_left, param("��-40", grup)) : grup;

//        grup = 24032; //25032 //���������� ��������
//        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("��", grup)) : grup;
            grup = 24033; //24033 //��������� ���������� 
            assert true == furnitureDet4.check(mapParam, area4_stv_right, param("��", grup)) : grup;
            assert false == furnitureDet3.check(mapParam, area3_stv_right, param("��", grup)) : grup;

            grup = 24063; //25063 //�������� ����, ��
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("3-40", grup)) : grup;
            assert true == furnitureDet2.check(mapParam, area2_stv_left, param("40", grup)) : grup;
            assert false == furnitureDet2.check(mapParam, area2_stv_left, param("1-12", grup)) : grup;

            grup = 24064; //25064 //����������� ������ �����, ��
            assert true == furnitureDet4.check(mapParam, area4_stv_right, param("870", grup)) : grup;
            assert false == furnitureDet3.check(mapParam, area3_stv_right, param("40", grup)) : grup;

            grup = 24067; //25067 //���� �������� �������� �������
            assert true == furnitureDet4.check(mapParam, area4_stv_right, param("1000-1010;", grup)) : grup;
            assert false == furnitureDet4.check(mapParam, area4_stv_right, param("900-990;", grup)) : grup;

            grup = 24070; //25070 //���� ������ �����
            assert true == furnitureDet4.check(mapParam, area4_stv_right, param("�� ��������", grup)) : grup;
            assert false == furnitureDet4.check(mapParam, area4_stv_right, param("�����������", grup)) : grup;
            
            System.out.println("builder.param.check.FurnitureTest.furnitureDet() - ���������");
        } catch (Exception e) {
            System.err.println("������:furnitureTest.furnitureDet() " + e);
        }
    }
}
