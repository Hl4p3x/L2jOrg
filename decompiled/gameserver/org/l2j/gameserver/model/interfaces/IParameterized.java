// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

public interface IParameterized<T>
{
    T getParameters();
    
    void setParameters(final T set);
}
