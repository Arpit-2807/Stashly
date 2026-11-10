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

    @Test
    void incrOnMissingKeyStartsAtOne() {
        assertEquals(1L, store.incrBy("n", 1));
        assertEquals("1", store.get("n"));
    }

    @Test
    void incrAddsToExistingNumber() {
        store.set("n", "10");
        assertEquals(11L, store.incrBy("n", 1));
    }

    @Test
    void decrSubtracts() {
        store.set("n", "10");
        assertEquals(9L, store.incrBy("n", -1));
    }

    @Test
    void incrOnTextThrowsAndKeepsOldValue() {
        store.set("name", "arpit");
        assertThrows(NumberFormatException.class, () -> store.incrBy("name",1));
        assertThrows(ArithmeticException.class, () -> store.get("name"));
    }

    @Test
    void incrOverflowThrows() {
        store.set("n", String.valueOf(Long.MAX_VALUE));
        assertThrows(ArithmeticException.class, () -> store.incrBy("n", 1));
    }

    @Test
    void sizeCountsKeys() {
        store.set("a", "1");
        store.set("b", "2");
        assertEquals(2, store.size());
    }

    @Test
    void clearRemovesEveryting() {
        store.set("a", "1");
        store.clear();
        assertEquals(0, store.size());
    }

    @Test
    void keysMtachesPAttern() {
        store.set("user1", "a");
        store.set("user2", "b");
        store.set("city", "c");
        assertEquals(java.util.List.of("city", "user1", "user2"), store.keys("*"));
        assertEquals(java.util.List.of("user1", "user2") , store.keys("user"));
        assertEquals(java.util.List.of("user1"), store.keys("user1"));
    }

}
