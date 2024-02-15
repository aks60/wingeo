package builder.param.check;

import builder.making.UColor;
import java.util.HashMap;

import static builder.param.check.WincalcTest.param;
import static builder.param.check.WincalcTest.hmParam;
import static builder.param.check.WincalcTest.grup;
import static builder.param.check.WincalcTest.record;

import static builder.param.check.WincalcTest.frame_left_2;
import static builder.param.check.WincalcTest.frame_right_2;
import static builder.param.check.WincalcTest.stv_right_2;
import static builder.param.check.WincalcTest.imp_horiz_2;
import static builder.param.check.WincalcTest.imp_vert_2;
import static builder.param.check.WincalcTest.glass_top_2;
import static builder.param.check.WincalcTest.glass_left_2;
import static builder.param.check.WincalcTest.glass_right_2;

import static builder.param.check.WincalcTest.frame_left_3;
import static builder.param.check.WincalcTest.frame_right_3;
import static builder.param.check.WincalcTest.stv_right_3;
import static builder.param.check.WincalcTest.imp_vert_3;
import static builder.param.check.WincalcTest.glass_top_3;
import static builder.param.check.WincalcTest.glass_left_3;

import static builder.param.check.WincalcTest.frame_left_4;
import static builder.param.check.WincalcTest.frame_right_4;
import static builder.param.check.WincalcTest.stv_left_4;
import static builder.param.check.WincalcTest.stv_right_4;
import static builder.param.check.WincalcTest.glass_right_4;
import static builder.param.check.WincalcTest.glass_left_4;

import static builder.param.check.WincalcTest.iwin_2; //601004
import static builder.param.check.WincalcTest.elementVar2;
import static builder.param.check.WincalcTest.elementDet2;
import static builder.param.check.WincalcTest.joiningVar2;
import static builder.param.check.WincalcTest.joiningDet2;
import static builder.param.check.WincalcTest.fillingVar2;
import static builder.param.check.WincalcTest.fillingDet2;
import static builder.param.check.WincalcTest.furnitureVar2;
import static builder.param.check.WincalcTest.furnitureDet2;

import static builder.param.check.WincalcTest.iwin_3; //604005
import static builder.param.check.WincalcTest.elementVar3;
import static builder.param.check.WincalcTest.elementDet3;
import static builder.param.check.WincalcTest.joiningVar3;
import static builder.param.check.WincalcTest.joiningDet3;
import static builder.param.check.WincalcTest.fillingVar3;
import static builder.param.check.WincalcTest.fillingDet3;
import static builder.param.check.WincalcTest.furnitureVar3;
import static builder.param.check.WincalcTest.furnitureDet3;

import static builder.param.check.WincalcTest.iwin_4; //700027
import static builder.param.check.WincalcTest.elementVar4;
import static builder.param.check.WincalcTest.elementDet4;
import static builder.param.check.WincalcTest.joiningVar4;
import static builder.param.check.WincalcTest.joiningDet4;
import static builder.param.check.WincalcTest.fillingVar4;
import static builder.param.check.WincalcTest.fillingDet4;
import static builder.param.check.WincalcTest.furnitureVar4;
import static builder.param.check.WincalcTest.furnitureDet4;

public class ElementTest {

    public ElementTest() {
    }

    /**
     * select c.name, d.name, a.text from elempar1 a left join element c on c.id
     * = a.element_id left join artikl d on c.artikl_id = d.id where a.groups_id
     * in (31017, 37017)
     */
    public void elementVar() {

        grup = 31000; //Для технологического кода контейнера
        assert true == elementVar2.check(frame_left_2, param("KBE 58;XXX 58;", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("KBE58;", grup)) : grup;

        grup = 31001; //Максимальное заполнение изделия, мм
        assert true == elementVar2.check(frame_left_2, param("30", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("12", grup)) : grup;

        grup = 31002; //Если профиль
        assert true == elementVar2.check(frame_left_2, param("прямой", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("арочный", grup)) : grup;

        grup = 31003; //Если соединенный артикул  T-обр.
        assert true == elementVar2.check(imp_vert_2, param("937", grup)) : grup;
        assert false == elementVar2.check(imp_vert_2, param("XXX", grup)) : grup;
        assert false == elementVar2.check(imp_horiz_2, param("907.07", grup)) : grup;
        assert false == elementVar2.check(imp_horiz_2, param("XXX", grup)) : grup;

        grup = 31004; //Если прилегающий артикул
        assert true == elementVar2.check(stv_right_2, param("937", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("XXX", grup)) : grup;

        grup = 31005; //33005, 33005, 37005 //Коды основной текстуры контейнера
        assert true == elementVar2.check(frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementVar2.check(stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 31006; //33006, 34006, 37006 //Коды внутр. текстуры контейнера
        assert true == elementVar2.check(frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementVar2.check(stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 31007; //33007, 34007, 37007 //Коды внешн. текстуры контейнера 
        assert true == elementVar2.check(frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementVar2.check(stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 31008; //Эффективное заполнение изделия, мм
        assert true == elementVar2.check(frame_left_2, param("30", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("32", grup)) : grup;

        grup = 31011; //Толщина внешнего/внутреннего заполнения, мм
        assert true == elementVar2.check(imp_vert_2, param("30/30", grup)) : grup;
        assert true == elementVar2.check(imp_vert_2, param("*/4-32", grup)) : grup;
        assert false == elementVar2.check(imp_vert_2, param("3;31;12/4-32", grup)) : grup;

        grup = 31017; //37017 //Код системы содержит строку
        assert true == elementVar2.check(frame_left_2, param("et-1", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("КП-40", grup)) : grup;

        grup = 31019; //Правило подбора текстур
        elementVar3.check(stv_right_3, param("внутренняя по основной", grup));
        UColor.colorRuleFromParam(stv_right_3);
        assert stv_right_3.spcRec.colorID2 == stv_right_3.spcRec.colorID1 : grup;

        grup = 31020; //Ограничение угла к горизонту, °
        assert true == elementVar2.check(frame_left_2, param("90;270", grup)) : grup;
        assert false == elementVar2.check(frame_right_2, param("91;180", grup)) : grup;
        assert true == elementVar2.check(imp_horiz_2, param("0-89;270,9-359,9", grup)) : grup;
        assert false == elementVar2.check(imp_horiz_2, param("1-89;", grup)) : grup;

        grup = 31033; //Если предыдущий артикул 
        assert true == elementVar2.check(imp_vert_2, param("807", grup)) : grup;

        grup = 31034; //Если следующий артикул 
        assert true == elementVar2.check(imp_vert_2, param("937", grup)) : grup;

        grup = 31037; //38037, 39037, 40037 //Название фурнитуры содержит
        assert true == elementVar2.check(stv_right_2, param("ROTO NT Повортные окна/двери", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("ROTO NT Повортные", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("ACCADO NT Повортные", grup)) : grup;

        grup = 31041; //Ограничение длины профиля, мм
        assert true == elementVar2.check(stv_right_2, param("400,1-10000", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("400,1-800", grup)) : grup;

        grup = 31050; //Контейнер имеет тип 
        assert true == elementVar2.check(stv_right_2, param("2", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("1", grup)) : grup;

        grup = 31051; //Если створка фурнитуры
        assert true == elementVar4.check(stv_right_4, param("ведомая", grup)) : grup;
        assert false == elementVar4.check(stv_right_4, param("ведущая", grup)) : grup;

        grup = 31052; //Поправка в спецификацию, мм
        assert true == elementVar4.check(stv_right_4, param("600", grup)) : grup;
        assert true == elementVar4.check(stv_right_4, param("60", grup)) : grup;

        grup = 31054; //Коды основной текстуры изделия
        assert true == elementVar4.check(stv_right_4, param("1000-1010;", grup)) : grup;
        assert false == elementVar2.check(stv_right_4, param("900-990;", grup)) : grup;

        grup = 31055; //Коды внутр. и внешн. текстуры изд.
        assert true == elementVar4.check(stv_right_4, param("1000-10010;", grup)) : grup;
        assert false == elementVar3.check(stv_right_3, param("1000-1010;", grup)) : grup;

        grup = 31056; //37056 //Коды внутр. или внеш. текстуры изд.
        assert true == elementVar4.check(stv_right_4, param("1000-10010;", grup)) : grup;
        assert true == elementVar3.check(stv_right_3, param("1000-1010;", grup)) : grup;
        assert false == elementVar3.check(stv_right_3, param("100-310;", grup)) : grup;

        grup = 31060; //Допустимый угол между плоскостями, °
        assert true == elementVar4.check(stv_right_4, param("90", grup)) : grup;
        assert false == elementVar4.check(stv_right_4, param("30;", grup)) : grup;

        grup = 31095; //Если признак системы конструкции
        assert true == elementVar4.check(stv_right_4, param("1;2;", grup)) : grup;
        assert false == elementVar4.check(stv_right_4, param("2;9", grup)) : grup;

        grup = 37002; //Если артикул профиля контура
        assert true == elementVar2.check(frame_left_2, param("807", grup)) : grup;
        assert false == elementVar2.check(frame_left_2, param("800", grup)) : grup;

        grup = 37008; //Тип проема
        assert false == elementVar2.check(stv_right_2, param("глухой", grup)) : grup;
        assert true == elementVar2.check(stv_right_2, param("не глухой", grup)) : grup;

        grup = 37009; //Тип заполнения
        assert true == elementVar2.check(stv_right_2, param("Прямоугольное", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("Арочное", grup)) : grup;
        assert false == elementVar2.check(stv_right_2, param("Произвольное", grup)) : grup;

        grup = 37010; //Ограничение ширины/высоты листа, мм
        assert true == elementVar2.check(glass_top_2, param("0-3000/0-334", grup)) : grup;
        assert false == elementVar2.check(glass_left_2, param("0-3000/0-334", grup)) : grup;

        grup = 37030; //Ограничение площади, кв.м.
        assert true == elementVar2.check(glass_top_2, param("0-6,5", grup)) : grup;
        assert false == elementVar2.check(glass_left_2, param("0-0,4", grup)) : grup;

        grup = 37042; //Допустимое соотношение габаритов (б/м)
        assert true == elementVar2.check(glass_top_2, param("4-10", grup)) : grup;
        assert false == elementVar2.check(glass_left_2, param("1-2", grup)) : grup;

        grup = 37054; //Коды основной текстуры изделия
        assert false == elementVar2.check(stv_right_2, param("10000-10999;17000-21999;23000-28999", grup)) : grup;
        assert true == elementVar2.check(stv_right_2, param("1;3;35-37;55;70;1009", grup)) : grup;

        grup = 37055; //Коды внутр. и внешн. текстуры изд.
        assert true == elementVar3.check(stv_right_3, param("1000-10010;", grup)) : grup;
        assert false == elementVar3.check(stv_right_3, param("1000-1010;", grup)) : grup;

        grup = 37056; //Коды внут. или внеш. текстуры изд.
        assert true == elementVar4.check(stv_right_4, param("1000-10010;", grup)) : grup;
        assert true == elementVar3.check(stv_right_3, param("1000-1010;", grup)) : grup;
    }

    /**
     * select c.*, a.text from elempar2 a left join elemdet b on b.id =
     * a.elemdet_id left join element c on c.id = b.element_id left join artikl
     * d on c.artikl_id = d.id where a.groups_id in (31017, 37017)
     */
    public void elementDet() {
        HashMap<Integer, String> mapParam = new HashMap();

        grup = 33000; //34000 //Для технологического кода контейнера
        assert true == elementDet2.check(mapParam, frame_left_2, param("KBE 58;XXX 58;", grup)) : grup;
        assert false == elementDet2.check(mapParam, frame_left_2, param("KBE58;", grup)) : grup;

        grup = 33001; //34001 //Если признак состава 
        //assert true == elementDet2.check(mapParam, frame_left_2, param("KBE 58", grup)) : grup;
        //assert false == elementDet2.check(mapParam, frame_left_2, param("null", grup)) : grup;

        grup = 33005; //31005, 37005 //Коды основной текстуры контейнера
        assert true == elementDet2.check(mapParam, frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementDet2.check(mapParam, stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 33006; //31006, 34006, 37006 //Коды внутр. текстуры контейнера
        assert true == elementDet2.check(mapParam, frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementDet2.check(mapParam, stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 33007; //31007, 34007, 37007 //Коды внешн. текстуры контейнера
        assert true == elementDet2.check(mapParam, frame_left_2, param("0-800;990;1009;1600-2000;", grup)) : grup;
        assert true == elementDet2.check(mapParam, stv_right_2, param("0-1008;1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("0-1008;1010", grup)) : grup;

        grup = 33008; //Эффективное заполнение изд., мм
        assert true == elementDet2.check(mapParam, frame_left_2, param("30", grup)) : grup;
        assert false == elementDet2.check(mapParam, frame_left_2, param("32", grup)) : grup;

        grup = 33011; //Толщина внешнего/внутреннего заполнения, мм
        assert true == elementDet2.check(mapParam, imp_vert_2, param("30/30", grup)) : grup;
        assert true == elementDet2.check(mapParam, imp_vert_2, param("*/4-32", grup)) : grup;
        assert false == elementDet2.check(mapParam, imp_vert_2, param("3;31;12/4-32", grup)) : grup;

        grup = 33017; //34017 //Код системы содержит строку 
        assert true == elementDet2.check(mapParam, frame_left_2, param("et-1", grup)) : grup;
        assert false == elementDet2.check(mapParam, frame_left_2, param("КП-40", grup)) : grup;

        grup = 33063; //34063 //Диапазон веса створки, кг
        assert true == elementDet2.check(mapParam, stv_right_2, param("3-40", grup)) : grup;
        assert true == elementDet2.check(mapParam, stv_right_2, param("40", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("1-12", grup)) : grup;

        grup = 33066; //34066 //Если номер стороны в контуре
        assert true == elementDet3.check(mapParam, frame_left_3, param("4", grup)) : grup;
        assert false == elementDet3.check(mapParam, frame_left_3, param("1", grup)) : grup;

        grup = 33067; //34067, 38067, 39067, 40067 //Коды основной текстуры изделия 
        assert true == elementDet2.check(mapParam, stv_right_2, param("1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("109", grup)) : grup;

        grup = 33068; //34068, 38068, 39068, 40068 //Коды внутр. текстуры изделия
        assert true == elementDet2.check(mapParam, stv_right_2, param("1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("109", grup)) : grup;

        grup = 33069; //34069, 38069, 39069, 40069 //Коды внешн. текстуры изделия
        assert true == elementDet2.check(mapParam, stv_right_2, param("1009", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("109", grup)) : grup;

        grup = 33071;  //34071 //Контейнер типа
        assert true == elementDet2.check(mapParam, stv_right_2, param("1;2;3", grup)) : grup;
        assert true == elementDet2.check(mapParam, stv_right_2, param("1-3", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("1", grup)) : grup;

        grup = 33095; //34095, 38095, 39095, 40095 //Если признак системы конструкции
        assert true == elementDet2.check(mapParam, stv_right_2, param("1;5;4;", grup)) : grup;
        assert false == elementDet2.check(mapParam, stv_right_2, param("5;4;", grup)) : grup;

        grup = 34008; //Эффективное заполнение изделия, мм
        assert true == elementDet2.check(mapParam, frame_left_2, param("30", grup)) : grup;
        assert false == elementDet2.check(mapParam, frame_left_2, param("32", grup)) : grup;

        grup = 38039; //39039 //Для типа открывания 
        assert true == elementDet4.check(mapParam, stv_right_4, param("поворотное", grup)) : grup;
        assert false == elementDet4.check(mapParam, stv_right_4, param("поворотно-откидное", grup)) : grup;

    }

}
