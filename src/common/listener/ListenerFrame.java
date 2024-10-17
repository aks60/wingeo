package common.listener;

import dataset.Field;

public interface ListenerFrame<A, B> {

    default void actionRequest(A o) {
    }

    default void actionResponse(B o) {
    }

    default Object getValueAt(int col, int row, Object val) {
        return val;
    }
}
