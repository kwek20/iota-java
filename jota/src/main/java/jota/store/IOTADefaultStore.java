package jota.store;

import java.util.List;
import java.io.Serializable;

import jota.model.Bundle;

public class IOTADefaultStore implements IotaClientStorage {
    
    private Store store;

    public IOTADefaultStore(String location) {
        this.store = new FlatFileStore(location);
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

    @Override
    public List<Bundle> getPendingBundles() {
        // TODO Auto-generated method stub
        return null;
    }

}
