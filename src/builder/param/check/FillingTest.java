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
import static builder.param.check.WincalcTest.frame_left2;
import static builder.param.check.WincalcTest.frame_right2;
import static builder.param.check.WincalcTest.stv_right2;
import static builder.param.check.WincalcTest.imp_horiz2;
import static builder.param.check.WincalcTest.imp_vert2;
import static builder.param.check.WincalcTest.glass_top2;
import static builder.param.check.WincalcTest.glass_left2;
import static builder.param.check.WincalcTest.glass_right2;
import static builder.param.check.WincalcTest.frame_left3;
import static builder.param.check.WincalcTest.frame_right3;
import static builder.param.check.WincalcTest.stv_right3;
import static builder.param.check.WincalcTest.imp_vert3;
import static builder.param.check.WincalcTest.glass_top3;
import static builder.param.check.WincalcTest.glass_left3;
import static builder.param.check.WincalcTest.frame_left4;
import static builder.param.check.WincalcTest.frame_right4;
import static builder.param.check.WincalcTest.stv_left4;
import static builder.param.check.WincalcTest.stv_right4;
import static builder.param.check.WincalcTest.glass_right4;
import static builder.param.check.WincalcTest.glass_left4;

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

        grup = 13001; //Если признак состава 
        //assert true == fillingVar2.check(frame_left_2, param("KBE 58", grup)) : grup;
        //assert false == fillingVar2.check(frame_left_2, param("KBE 5X", grup)) : grup;

        grup = 13003; //Тип проема
        assert false == fillingVar2.check(stv_right2, param("глухой", grup)) : grup;
        assert true == fillingVar2.check(stv_right2, param("не глухой", grup)) : grup;

        grup = 13005; //Заполнение типа
        assert true == fillingVar3.check(glass_top3, param("Стеклопакет", grup)) : grup;
        assert false == fillingVar3.check(glass_left3, param("Стекло", grup)) : grup;

        grup = 13014; //Углы ориентации стороны, ° 
        assert true == fillingVar3.check(glass_left3, param("0;90;180;270", grup));
        assert false == fillingVar3.check(glass_left3, param("10;20;30", grup));

        grup = 13015;  //Форма заполнения
        assert true == fillingVar3.check(glass_left3, param("Прямоугольное", grup)) : grup;
        assert true == fillingVar3.check(glass_top3, param("Арочное", grup)) : grup;
        assert false == fillingVar3.check(glass_left3, param("Не прямоугольное", grup)) : grup;

        grup = 13017; //Код системы содержит строку
        assert true == fillingVar4.check(glass_left4, param("me-1", grup)) : grup;
        assert false == fillingVar4.check(glass_left3, param("КП-40", grup)) : grup;

        grup = 13095; //Если признак системы конструкции
        assert true == fillingVar4.check(stv_right4, param("1;2;", grup)) : grup;
        assert false == fillingVar4.check(stv_right4, param("2;9", grup)) : grup;
    }

    /**
     * select c.name, d.name, a.text from glaspar2 a left join glasdet b on b.id
     * = a.glasdet_id left join glasgrp c on c.id = b.glasgrp_id left join
     * artikl d on b.artikl_id = d.id where a.groups_id in (14065, 15055)
     */
    public void fillingDet() {
        HashMap<Integer, String> mapParam = new HashMap();

        grup = 14000; //15000 //Для технологического кода контейнера
        assert true == fillingDet2.check(mapParam, glass_left2, param("KBE 58", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_left2, param("KBE;58;", grup)) : grup;

        grup = 14001; //15001 //Если признак состава
        //assert true == fillingDet2.check(mapParam, glass_left_2, param("KBE 58", grup)) : grup;
        //assert false == fillingDet2.check(mapParam, glass_left_2, param("null", grup)) : grup;

        grup = 14005;  //Тип проема
        assert false == fillingDet2.check(mapParam, glass_left2, param("глухой", grup)) : grup;
        assert true == fillingDet2.check(mapParam, glass_left2, param("не глухой", grup)) : grup;

        grup = 14008; //15008 //Эффективное заполнение изд., мм
        assert true == fillingDet2.check(mapParam, glass_left2, param("30", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_left2, param("32", grup)) : grup;

        grup = 14009;  //Арочное заполнение
        assert true == fillingDet2.check(mapParam, glass_left2, param("Нет", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_left2, param("Да", grup)) : grup;

        grup = 14017; //15017 //Код системы содержит строку 
        assert true == fillingDet2.check(mapParam, glass_left2, param("et-1", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_left2, param("КП-40", grup)) : grup;

        grup = 14065; //15055 //Ограничение угла, ° или Точный угол
        assert true == fillingDet2.check(mapParam, glass_left2, param("270", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_left2, param("270,1", grup)) : grup;

        grup = 14067; //15067 //Коды основной текстуры изделия 
        assert true == fillingDet2.check(mapParam, glass_right2, param("1009", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_right2, param("109", grup)) : grup;

        grup = 14068; //15068 //Коды внутр. текстуры изделия
        assert true == fillingDet2.check(mapParam, glass_right2, param("1009", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_right2, param("109", grup)) : grup;

        grup = 14069; //15069 //Коды внешн. текстуры изделия
        assert true == fillingDet2.check(mapParam, glass_right2, param("1009", grup)) : grup;
        assert false == fillingDet2.check(mapParam, glass_right2, param("109", grup)) : grup;

        grup = 14081; //15081 //Если артикул профиля контура
        assert true == fillingDet3.check(mapParam, glass_left3, param("21316-05000", grup)) : grup;
        assert false == fillingDet3.check(mapParam, glass_left3, param("21316=05000", grup)) : grup;

        grup = 15027; //12027  //Рассчитывать для профиля ++++
        assert true == fillingDet4.check(mapParam, glass_left4, param("без уплотнителя", grup)) : grup;
        assert false == fillingDet4.check(mapParam, glass_left4, param("с уплотнителем", grup)) : grup;
    }
}
