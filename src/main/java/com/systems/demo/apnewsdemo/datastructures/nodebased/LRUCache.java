package com.systems.demo.apnewsdemo.datastructures.nodebased;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

@Getter
public class LRUCache<K, V> {

    private final Queue<KeyValue<K, V>> queue;

    private final Map<K, QueueNode<KeyValue<K, V>>> keyNodeMap;

    private final int cacheSize;

    public LRUCache() {
        this(10);
    }

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize <= 0 ? 10 : cacheSize;
        this.queue = new Queue<>();
        this.keyNodeMap = new HashMap<>();
    }

    /**
     * Insert or update a key->value pair. Marks item as most recently used.
     */
    public void put(K key, V value) {
        if (key == null) return;

        QueueNode<KeyValue<K, V>> existing = keyNodeMap.get(key);
        if (Objects.nonNull(existing)) {
            KeyValue<K, V> kv = existing.getData().getValue();
            kv.setValue(value);
            queue.moveToFront(existing);
            return;
        }

        // Evict if needed
        if (queue.getSize() >= cacheSize) {
            KeyValue<K, V> removed = queue.removeLast();
            if (removed != null) {
                keyNodeMap.remove(removed.getKey());
            }
        }

        // Insert new key->value
        queue.insertFirst(new KeyValue<>(key, value));
        keyNodeMap.put(key, queue.getRootNode());
    }

    /**
     * Get value by key and mark as most recently used; return null if absent.
     */
    public V get(K key) {
        if (key == null) return null;
        QueueNode<KeyValue<K, V>> node = keyNodeMap.get(key);
        if (node == null) return null;
        KeyValue<K, V> kv = node.getData().getValue();
        queue.moveToFront(node);
        return kv.getValue();
    }

    public boolean contains(K key) {
        return key != null && keyNodeMap.containsKey(key);
    }

    public void remove(K key) {
        if (key == null) return;
        QueueNode<KeyValue<K, V>> node = keyNodeMap.remove(key);
        if (node != null) {
            queue.removeNode(node);
        }
    }

    public int size() {
        return queue.getSize();
    }

}
