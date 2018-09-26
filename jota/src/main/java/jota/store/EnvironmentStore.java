package jota.store;

import java.io.Serializable;

public class EnvironmentStore implements Store {

    @Override
    public void load() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void save() throws Exception {
        throw new Exception("Environment store does not allow saving");
    }

    @Override
    public Serializable get(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable get(String key, Serializable def) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Serializable set(String key, Serializable value) {
        throw new IllegalArgumentException("Environment store does not allow setting values");
        //throw new NotAllowedException("Environment store does not allow setting values");
    }

}
