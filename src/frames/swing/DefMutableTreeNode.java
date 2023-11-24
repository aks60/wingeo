package frames.swing;

import builder.model.Com5t;
import builder.model.AreaSimple;
import builder.model.ElemSimple;
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
            return ((ElemSimple) obj).type.name + ", " + ((ElemSimple) obj).layout.name.toLowerCase();

        } else if (obj instanceof Com5t) {
            return ((Com5t) obj).type.name;
        } else {
            return String.valueOf(obj);
        }
    }
}
