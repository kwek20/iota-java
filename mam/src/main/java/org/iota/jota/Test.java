package org.iota.jota;

import java.io.IOException;

import org.iota.jota.c.MamC;

public class Test {
    
    static
    {
        NarSystem.loadLibrary();
    }

    public static void main(String[] args) throws IOException {
        Mam mam = new MamC();
        //int ret = mam.initApi("LVFHWWSIHPAKFUYVEXPXIUTLJJNW9IRWFEZA9YVXLJKBGZXXJBLD9COEPFVVATYIMJJWLOEBYGLUA9999");
        long ret = mam.serializedSize();
        System.out.println(ret);
    }
}
