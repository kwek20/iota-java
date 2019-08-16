package org.iota.jota;

import java.io.IOException;

import org.iota.jota.c.MamC;
import org.iota.jota.dto.MamReturnSerialisedSize;

public class Test {
    
    static
    {
        NarSystem.loadLibrary();
    }

    public static void main(String[] args) throws IOException {
        Mam mam = new MamC();
        mam.initApi("LVFHWWSIHPAKFUYVEXPXIUTLJJNW9IRWFEZA9YVXLJKBGZXXJBLD9COEPFVVATYIMJJWLOEBYGLUA9999");
        MamReturnSerialisedSize ret = mam.serializedSize();
        System.out.println(ret);
    }
}
