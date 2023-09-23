package frames.swing;

import builder.IArea5e;
import builder.ICom5t;
import builder.IElem5e;
import dataset.Record;
import domain.eSystree;
import javax.swing.tree.DefaultMutableTreeNode;

public class DefMutableTreeNode<E> extends DefaultMutableTreeNode {

    public E obj = null;

    public DefMutableTreeNode(E obj) {
        super();
        this.obj = obj;
    }

    public ICom5t com5t() {
        return (ICom5t) obj;
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

        } else if (obj instanceof IArea5e) {
            return ((IArea5e) obj).type().name;

        } else if (obj instanceof IElem5e) {
            return ((IElem5e) obj).type().name + ", " + ((IElem5e) obj).layout().name.toLowerCase();

        } else if (obj instanceof ICom5t) {
            return ((ICom5t) obj).type().name;
        } else {
            return String.valueOf(obj);
        }
    }
}
