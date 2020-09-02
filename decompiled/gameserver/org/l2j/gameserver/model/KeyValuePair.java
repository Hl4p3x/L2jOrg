// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class KeyValuePair<K, V>
{
    private final K _key;
    private final V _value;
    
    public KeyValuePair(final K key, final V value) {
        this._key = key;
        this._value = value;
    }
    
    public K getKey() {
        return this._key;
    }
    
    public V getValue() {
        return this._value;
    }
}
