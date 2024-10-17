package common;

//����� ��� �������������� double-����� � �����-������� ��������
public class MoneyInWords {
    private static final String dig1[][] = {{"����", "���", "���", "������", "����", "�����", "����", "������", "������"},
                                            {"����", "���"}}; //dig[0] - female, dig[1] - male
    private static final String dig10[]  = {"������","�����������", "����������", "����������", "������������", 
                                            "����������", "�����������", "����������", "������������", "������������"};
    private static final String dig20[]  = {"��������", "��������", "�����", "���������", 
                                            "����������", "���������", "�����������", "���������"};
    private static final String dig100[] = {"���","������", "������", "���������", "�������", 
                                            "��������", "�������", "���������", "���������"};
    private static final String leword[][] = { {"�������", "�������", "������", "0"},
                                               {"�����", "�����", "������", "1"},
                                               {"������", "������", "�����", "0"},
                                               {"�������", "��������", "���������", "1"},
                                               {"��������", "���������", "����������", "1"},
                                               {"��������", "���������", "����������", "1"}};

    //����������� ������� �������������� ������ ����� num � �����
    private static String num2words(long num, int level) { 
        StringBuilder words = new StringBuilder(50);
        if (num==0) words.append("���� ");         //�������������� ������
        int sex = leword[level][3].indexOf("1")+1; //�� ������� �������, �� ��������
        int h = (int)(num%1000);    //������� ����������� �������
        int d = h/100;              //����� �����
        if (d>0) words.append(dig100[d-1]).append(" ");
        int n = h%100;              
        d = n/10;                   //����� ��������
        n = n%10;                   //����� ������
        switch(d) {
            case 0: break;
            case 1: words.append(dig10[n]).append(" ");
                    break;
            default: words.append(dig20[d-2]).append(" ");
        }
        if (d==1) n=0;              //��� ���������� ������� �� 10 �� 19, ����� ������ �� ������ �����������
        switch(n) {
            case 0: break;
            case 1: 
            case 2: words.append(dig1[sex][n-1]).append(" "); 
                    break;
            default: words.append(dig1[0][n-1]).append(" "); 
        }   
        switch(n) {
            case 1: words.append(leword[level][0]);
                    break;
            case 2: 
            case 3:
            case 4: words.append(leword[level][1]);
                    break;
            default: if((h!=0)||((h==0)&&(level==1)))  //���� ����������� ������� = 0, �� ��������� ����� ������ "������"
                        words.append(leword[level][2]); 
        } 
        long nextnum = num/1000;
        if(nextnum>0) {
            return (num2words(nextnum, level+1) + " " + words.toString()).trim();
        } else {
            return words.toString().trim();
        }
    }

    //������� �������������� ������������� ����� � �����-�������
    //��� �������� money ����� 50-70 ���������� ������ �������� �������� �������, ���������� ��� ������ ������ �������
    public static String inwords(double money) {  
        if (money<0.0) return "error: ������������� ��������"; 
        String sm = String.format("%.2f", money);
        String skop = sm.substring(sm.length()-2, sm.length());    //�������� ������ � ������
        int iw;
        switch(skop.substring(1)) {
            case "1": iw = 0;
                      break;
            case "2": 
            case "3":   
            case "4": iw = 1;
                      break;
            default:  iw = 2;          
        }
        long num = (long)Math.floor(money);
        if (num<1000000000000000l) { 
            return num2words(num, 1) + " " + skop + " " + leword[0][iw];
        } else 
            return "error: ������� ����� ������ " + skop + " " + leword[0][iw];
    }
 }
