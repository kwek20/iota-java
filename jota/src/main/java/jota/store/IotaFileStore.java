package jota.store;

import java.io.Serializable;

public class IotaFileStore extends IotaClientStore {

    public IotaFileStore(String location) {
        super(new FlatFileStore(location));
    }

    @Override
    public int getIndexAndIncrease(String seed) {
        Serializable index = store.get(seed);
        
        if (index == null) {
            store.set(seed, 1);
            return 0;
        } else if (!(index instanceof Integer)) {
            //Something went wrong
            return -1;
        }
        
        int cur = ((Integer)index).intValue();
        store.set(seed, cur+1);
        return cur;
    }
}
