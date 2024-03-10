
package common.listener;

import dataset.Record;

public interface ListenerOffset {
   
    public double[] action(Record rec1, Record rec2);
}
