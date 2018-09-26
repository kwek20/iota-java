package jota.store;

import java.util.List;

import jota.model.Bundle;

public interface IotaClientStorage {
    
    int getIndexAndIncrease(String seed);
    
    List<Bundle> getPendingBundles();
}
