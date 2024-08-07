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

        grup = 21001; //Форма контура //eElement.find4(212)
        assert true == furnitureVar2.check(null, param("прямоугольная", grup)) : grup;
        assert false == furnitureVar3.check(null, param("прямоугольная", grup)) : grup;
        assert true == furnitureVar3.check(null, param("арочная", grup)) : grup;
        assert false == furnitureVar3.check(null, param("не арочная", grup)) : grup;

        grup = 21004;  //Артикул створки 
        assert true == furnitureVar2.check(null, param("917", grup)) : grup;
        assert true == furnitureVar3.check(null, param("21316-05000", grup)) : grup;
        assert false == furnitureVar3.check(null, param("21316*05000", grup)) : grup;

        grup = 21005;  //Артикул заполнения по умолчанию  
        assert true == furnitureVar3.check(null, param("4x10x4x10x4", grup)) : grup;
        assert false == furnitureVar3.check(null, param("xxx", grup)) : grup;

        grup = 21010; //Ограничение длины стороны, мм 
        assert true == furnitureVar3.check(frame3_left, param("0-6000", grup)) : grup;
        assert false == furnitureVar3.check(frame3_left, param("5000-6000", grup)) : grup;

        grup = 21013; //Ограничение высоты ручки по середине, мм
        assert true == furnitureVar3.check(stv3_right_3, param("940-1200", grup)) : grup;
        assert false == furnitureVar3.check(stv3_right_3, param("500-501", grup)) : grup;

        grup = 21016; //Допустимое соотношение габаритов (б/м)
        assert true == furnitureVar2.check(stv2_left_3, param("0,5-1;1-2,0", grup)) : grup;
        assert false == furnitureVar2.check(stv2_left_3, param("1-1,09", grup)) : grup;

        grup = 21040;  //Ограничение угла
        assert true == furnitureVar2.check(stv2_left_4, param("74-360", grup)) : grup;
        assert false == furnitureVar2.check(stv2_left_4, param("12-55", grup)) : grup;
    }

    /**
     * Только первый уровень select distinct c.name, d.name, a.text from
     * furnpar2 a left join furndet b on b.id = a.furndet_id left join furniture
     * c on c.id = b.furndet_id left join artikl d on b.artikl_id = d.id where
     * a.groups_id in (24003)
     */
    public void furnitureDet() {
        HashMap<Integer, String> mapParam = new HashMap<Integer, String>();
        AreaSimple area3_stv_right = (AreaSimple) stv3_right_3.owner;
        AreaSimple area2_stv_left = (AreaSimple) stv2_left_3.owner;
        AreaSimple area4_stv_right = (AreaSimple) stv4_right_3.owner;

        grup = 24001; //25001 //Форма контура
        assert false == furnitureDet3.check(mapParam, area2_stv_left, param("арочная", grup)) : grup;
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("прямоугольная", grup)) : grup;
        assert true == furnitureDet3.check(mapParam, area2_stv_left, param("не арочная", grup)) : grup;

        grup = 24002;  //Если артикул створки
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("917.07", grup)) : grup;
        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("91X.07", grup)) : grup;

        grup = 24004; //Если створка прилегает к артикулу
        assert true == furnitureDet3.check(mapParam, area3_stv_right, param("21315-01000", grup)) : grup;
        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("xxxxxxxxxxx", grup)) : grup;

        grup = 24005; //25005  //Коды текстуры створки 
        assert true == furnitureDet3.check(mapParam, area3_stv_right, param("1009;10000-10999;17000-29999;", grup)) : grup;
        assert true == furnitureDet3.check(mapParam, area3_stv_right, param("0-1008;1009", grup)) : grup;
        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("0-1008;1010", grup)) : grup;

        grup = 24007; //25007  //Коды текстуры ручки 
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("Коричневый", grup)) : grup;
        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("ххххх", grup)) : grup;

        grup = 24008; //25008  //Если серия створки
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("Общий створка", grup)) : grup;
        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("ххххх", grup)) : grup;

//        grup = 24009; //25009  //Коды текстуры подвеса  
//        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("Коричневый", grup)) : grup;
//        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("ххххх", grup)) : grup; 
        
        grup = 24012;  //Направление открывания
        assert true == furnitureDet3.check(mapParam, area3_stv_right, param("правое", grup)) : grup;
        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("левое", grup)) : grup;

        grup = 24017; //25017 //Код системы содержит строку
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("et-1", grup)) : grup;
        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("КП-40", grup)) : grup;

//        grup = 24032; //25032 //Правильная полуарка
//        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("Да", grup)) : grup;

        grup = 24033; //24033 //Фурнитура штульповая 
        assert true == furnitureDet4.check(mapParam, area4_stv_right, param("Да", grup)) : grup;
        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("Да", grup)) : grup;

        grup = 24063; //25063 //Диапазон веса, кг
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("3-40", grup)) : grup;
        assert true == furnitureDet2.check(mapParam, area2_stv_left, param("40", grup)) : grup;
        assert false == furnitureDet2.check(mapParam, area2_stv_left, param("1-12", grup)) : grup;

        grup = 24064; //25064 //Ограничение высоты ручки, мм
        assert true == furnitureDet4.check(mapParam, area4_stv_right, param("870", grup)) : grup;
        assert false == furnitureDet3.check(mapParam, area3_stv_right, param("40", grup)) : grup;

        grup = 24067; //25067 //Коды основной текстуры изделия
        assert true == furnitureDet4.check(mapParam, area4_stv_right, param("1000-1010;", grup)) : grup;
        assert false == furnitureDet4.check(mapParam, area4_stv_right, param("900-990;", grup)) : grup;

        grup = 24070; //25070 //Если высота ручки
        assert true == furnitureDet4.check(mapParam, area4_stv_right, param("по середине", grup)) : grup;
        assert false == furnitureDet4.check(mapParam, area4_stv_right, param("константная", grup)) : grup;
    }
}
