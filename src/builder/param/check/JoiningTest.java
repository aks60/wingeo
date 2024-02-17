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


public class JoiningTest {

    public JoiningTest() {
        super();
    }

    public void joiningVar() {

        grup = 1005; //2005, 3005, 4005 //Контейнер имеет тип Артикула1/Артикула2
        assert true == joiningVar2.check(iwin2.listJoin.join(frame2_1, 0), param("1/1", grup)) : grup;
        assert true == joiningVar2.check(iwin2.listJoin.join(imp2_horiz, 0), param("3/1", grup)) : grup;

        grup = 1008; //Эффективное заполнение изд., мм
        assert true == joiningVar2.check(iwin2.listJoin.join(frame2_1, 1), param("30", grup)) : grup;
        assert false == joiningVar2.check(iwin2.listJoin.join(frame2_1, 1), param("32", grup)) : grup;

        grup = 1010; //4010 //Внешнее соединение
        //assert true == joiningVar4.check(iwin_4.listJoin.get(iwin_4.rootArea.x1() + ":" + iwin_4.rootArea.y1()), param("Да", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(frame4_left, 0), param("Да", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 0), param("Нет", grup)) : grup;
        assert false == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 0), param("Да", grup)) : grup;

        grup = 1020; //Ограничение угла к горизонту, °
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("30;270", grup)) : grup;
        assert false == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("90", grup)) : grup;

        grup = 1039; //Для типа открывания 
        assert true == joiningVar2.check(iwin2.listJoin.join(stv2_left_3, 1), param("поворотное", grup)) : grup;
        assert false == joiningVar2.check(iwin2.listJoin.join(stv2_left_3, 1), param("поворотно-откидное", grup)) : grup;

        grup = 1043; //Ограничение габарита контура, мм 
        assert true == joiningVar2.check(iwin2.listJoin.join(frame3_left, 1), param("2,45", grup)) : grup;
        assert true == joiningVar2.check(iwin2.listJoin.join(frame3_left, 1), param("2,0-3,0", grup)) : grup;
        assert false == joiningVar2.check(iwin2.listJoin.join(frame3_left, 1), param("2,0", grup)) : grup;

        grup = 1095; //2095, 3095, 4095; //Если признак системы конструкции
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("1;2;", grup)) : grup;
        assert false == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("2;9", grup)) : grup;

//        grup = 2003; //3003; //Угол варианта
//        assert true == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 0), param("левый", grup)) : grup;
//        assert true == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 1), param("левый", grup)) : grup;
//        assert false == joiningVar4.check(iwin_4.listJoin.get(stv_right_4, 1), param("правый", grup)) : grup;

        grup = 4020; //Угол варианта
        assert false == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("30-89,98;90,11-179,9;180,1-269,9;270,1-359,9;", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("89,98-90,1", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("90", grup)) : grup;

        grup = 2012; //3012; //Для Артикулов указан состав
        assert true == joiningVar4.check(iwin4.listJoin.join(frame4_right, 1), param("Армирование", grup)) : grup;
        assert false == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("_Армирование", grup)) : grup;

        grup = 2015; //3015, 4015 //Ориентация Артикула1/Артикула2, °
        assert false == joiningVar4.check(iwin4.listJoin.join(frame4_right, 1), param("0;180/*", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("90/*", grup)) : grup;

        grup = 2020; //3020, 4030 //Ограничение угла, °
        assert false == joiningVar4.check(iwin4.listJoin.join(frame4_right, 1), param("29-89,99;90,01-360", grup)) : grup;
        assert true == joiningVar4.check(iwin4.listJoin.join(stv4_right_3, 1), param("90", grup)) : grup;

        grup = 3002; //4002 //Вид L-образного варианта или Вид Т-образного варианта
        assert true == joiningVar4.check(iwin4.listJoin.join(frame4_right, 1), param("Простое L-обр", grup)) : grup;
        assert false == joiningVar4.check(iwin4.listJoin.join(frame4_right, 1), param("null", grup)) : grup;
    }

    /**
     * select d.id, c.name, b.id, a.text from joinpar2 a left join joindet b on
     * b.id = a.joindet_id left join joinvar c on c.id = b.joinvar_id left join
     * joining d on d.id = c.joining_id where a.groups_id = 11009
     */
    public void joiningDet() {
        HashMap<Integer, String> mapParam = new HashMap();

        grup = 11000; //12000 //Для технологического кода контейнера
        //Object o22 = iwin_2.listJoin.get(frame_right_2, 1);
        assert true == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("KBE 58;/KBE 58;", grup)) : grup;
        assert true == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("KBE 58;/KBE 58", grup)) : grup;
        assert false == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("KBE 58;/KBE 5", grup)) : grup;

//        grup = 11001; //12001 //Если признак состава Арт.1 
//        assert true == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("KBE 58", grup)) : grup;
//        assert false == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("null", grup)) : grup;
//
//        grup = 11002; //12002 //Если признак состава Арт.2 
//        assert true == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("KBE 58", grup)) : grup;
//        assert false == joiningDet2.check(mapParam, iwin_2.listJoin.get(frame_right_2, 1), param("null", grup)) : grup;

        grup = 11005;  //12005 //Контейнер типа
        assert true == joiningDet3.check(mapParam, iwin3.listJoin.join(frame3_right, 1), param("1-3", grup)) : grup;
        assert false == joiningDet3.check(mapParam, iwin3.listJoin.join(frame3_right, 1), param("4", grup)) : grup;

        grup = 11008; //Эффективное заполнение изделия, мм
        assert true == joiningDet3.check(mapParam, iwin2.listJoin.join(frame3_right, 1), param("32", grup)) : grup;
        assert false == joiningDet3.check(mapParam, iwin2.listJoin.join(frame3_right, 1), param("30", grup)) : grup;

        grup = 11028; //12028 //Диапазон веса заполнения, кг
        assert true == joiningDet3.check(mapParam, iwin2.listJoin.join(frame3_right, 1), param("30-140;", grup)) : grup;
        assert true == joiningDet3.check(mapParam, iwin2.listJoin.join(frame3_right, 1), param("140;", grup)) : grup;
        assert false == joiningDet3.check(mapParam, iwin2.listJoin.join(frame3_right, 1), param("30;", grup)) : grup;

        grup = 11066; //Если текстура профиля Арт.1 
        assert true == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("1009-1010", grup)) : grup;
        assert false == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("1006", grup)) : grup;

        grup = 11095; //12095 //Если признак системы конструкции
        assert true == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("1;5;4;", grup)) : grup;
        assert false == joiningDet2.check(mapParam, iwin2.listJoin.join(frame2_3, 1), param("5;4;", grup)) : grup;
    }
}
