package com.systems.demo.apnewsdemo.datastructures.nodebased;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LRUCacheTest {

    @Test
    public void testPutAndEvict() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        assertTrue(cache.contains(1));
        assertTrue(cache.contains(2));

        // adding third should evict 1 (LRU)
        cache.put(3, "three");
        assertFalse(cache.contains(1));
        assertTrue(cache.contains(2));
        assertTrue(cache.contains(3));
    }

    @Test
    public void testAccessMakesMostRecent() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        // access 1, so 2 becomes LRU
        String v = cache.get(1);
        assertEquals("one", v);

        cache.put(3, "three");
        // since 2 was LRU it should be evicted
        assertFalse(cache.contains(2));
        assertTrue(cache.contains(1));
        assertTrue(cache.contains(3));
    }

    @Test
    public void testRemoveAndGet() {
        LRUCache<String, String> cache = new LRUCache<>(3);
        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        assertEquals("B", cache.get("b"));
        cache.remove("b");
        assertFalse(cache.contains("b"));
        assertNull(cache.get("b"));
    }
}
