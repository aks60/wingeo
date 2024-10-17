package frames.swing;

//������, ���������� �����
public class FileFilter extends javax.swing.filechooser.FileFilter {

    //��������� ���� ��� ���������� ���
    public boolean accept(java.io.File f) {
        // ��� �������� ���������
        if (f.isDirectory()) {
            return true;
        }
        //��� ������ ������� �� ����������
        String fileName = f.getName();
        int i = fileName.lastIndexOf('.');
        if ((i > 0) && (i < (fileName.length() - 1))) {
            String fileExt = fileName.substring(i + 1);
            if ("gdb".equalsIgnoreCase(fileExt) || "fdb".equalsIgnoreCase(fileExt)) {
                return true;
            }
        }
        return false;
    }

    //���������� �������� �������
    public String getDescription() {
        return "����� ��� ������ (*.gdb; *.fdb))";
    }
}
