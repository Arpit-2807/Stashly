package com.arpit.stashly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

    private final Store store = new Store();

    @Test
    void setThenGet() {
        store.set("name" , "arpit");
        assertEquals("arpit" , store.get("name"));
    }

    @Test
    void getMissingKeyIsNull() {
        store.set("city" , "surat");
        store.set("city" , "vadodara");
        assertEquals("vadodara" , store.get("city"));
    }

    @Test
    void deleteRemovesTheKey() {
        store.set("a" , "1");
        store.delete("a");
        assertNull(store.get("a"));
    }

    @Test
    void deleteMissingKeyReturnsFalse() {
        assertFalse(store.delete("ghost"));
    }

    @Test
    void existWorks() {
        store.set("a" , "1");
        assertTrue(store.exists("a"));
        assertFalse(store.exists("b"));
    }

}
