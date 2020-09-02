// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

public interface IHandler<K, V>
{
    void registerHandler(final K handler);
    
    void removeHandler(final K handler);
    
    K getHandler(final V val);
    
    int size();
}
