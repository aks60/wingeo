package frames.swing;

import builder.model.Com5t;
import builder.model.AreaSimple;
import builder.model.ElemSimple;
import static dataset.Field.TYPE.type;
import dataset.Record;
import domain.eSystree;
import javax.swing.tree.DefaultMutableTreeNode;

public class DefMutableTreeNode<E> extends DefaultMutableTreeNode {

    public E obj = null;

    public DefMutableTreeNode(E obj) {
        super();
        this.obj = obj;
    }

    public Com5t com5t() {
        return (Com5t) obj;
    }

    public Record rec() {
        return (Record) obj;
    }

    public DefMutableTreeNode add(DefMutableTreeNode newChild) {
        super.add(newChild);
        return newChild;
    }

    public DefMutableTreeNode getLastChild() {
        return (DefMutableTreeNode) super.getLastChild();
    }

    public String toString() {
        if (obj instanceof Record) {
            return ((Record) obj).getStr(eSystree.name);

        } else if (obj instanceof AreaSimple) {
            return ((AreaSimple) obj).type.name;

        } else if (obj instanceof ElemSimple) {
            String s = ((ElemSimple) obj).type.name;
            int ind = s.indexOf(" ", 0);
            if (ind != -1) {
                s = s.substring(0, ind);
            }
            return s + " " + ((ElemSimple) obj).layout().name.toLowerCase();
            //return " " + ((ElemSimple) obj).layout().name.toLowerCase();

        } else if (obj instanceof Com5t) {
            return ((Com5t) obj).type.name;
        } else {
            return String.valueOf(obj);
        }
    }
}
