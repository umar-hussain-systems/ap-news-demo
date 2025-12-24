package com.systems.demo.apnewsdemo.datastructures.nodebased;

import lombok.Getter;
import lombok.Setter;

@Getter
public class KeyValue<K, V> {

    private final K key;
    @Setter
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

  @Override
    public String toString() {
        return "KeyValue{" + "key=" + key + ", value=" + value + '}';
    }
}

