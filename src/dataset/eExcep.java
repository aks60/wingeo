package dataset;

import startup.Main;
import java.util.HashSet;

/**
 * <p>
 * Exception code name </p>
 */
public enum eExcep {

    yesConn("���������� ������� �����������"),
    findDrive("�� ������ ���� ��������"),
    yesUser("����� ����� ��� ��������p. �� �������", 335544665),
    noUser("����� ����� �� ��������p. �� �������"),
    loadDrive("������ �������� ����� ��������"),
    noLogin("������ ����� ����� �����. ��� ������", 335544472),
    noGrant("���������� ����(����) ������� � ��", 335544352),
    noBase("�� ������� ���� ������", 335544344),
    noPort("���� ������ ��� ����� ������ ����������"),
    noConn("������ ���������� � ����� ������."),
    noActiv("������ ��������� ���������"),
    noTable("�� ������� ������� � ���� ������", 335544569);
    public String mes;
    private HashSet<Integer> code = new HashSet<Integer>();

    //�����������
    eExcep(String mes, int... codes) {
        this.mes = mes;
        for (int cod : codes) {
            this.code.add(cod);
        }
    }
    
    public static eExcep getError(int code) {
        for (eExcep con : values()) {
            if (con.code.contains(code) == true) {
                return con;
            }
        }
        return noConn;
    }   
}
