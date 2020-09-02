// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import java.time.Duration;

public interface IParserAdvUtils extends IParserUtils
{
    boolean getBoolean(final String key);
    
    byte getByte(final String key);
    
    short getShort(final String key);
    
    int getInt(final String key);
    
    long getLong(final String key);
    
    float getFloat(final String key);
    
    double getDouble(final String key);
    
    String getString(final String key);
    
    Duration getDuration(final String key);
    
     <T extends Enum<T>> T getEnum(final String key, final Class<T> clazz);
}
