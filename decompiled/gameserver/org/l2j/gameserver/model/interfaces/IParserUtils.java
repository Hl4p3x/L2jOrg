// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import java.time.Duration;

public interface IParserUtils
{
    boolean getBoolean(final String key, final boolean defaultValue);
    
    byte getByte(final String key, final byte defaultValue);
    
    short getShort(final String key, final short defaultValue);
    
    int getInt(final String key, final int defaultValue);
    
    long getLong(final String key, final long defaultValue);
    
    float getFloat(final String key, final float defaultValue);
    
    double getDouble(final String key, final double defaultValue);
    
    String getString(final String key, final String defaultValue);
    
    Duration getDuration(final String key, final Duration defaultValue);
    
     <T extends Enum<T>> T getEnum(final String key, final Class<T> clazz, final T defaultValue);
}
