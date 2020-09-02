// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

public abstract class AbstractMaskPacket<T extends IUpdateTypeComponent> extends ServerPacket
{
    protected static final byte[] DEFAULT_FLAG_ARRAY;
    
    protected abstract byte[] getMasks();
    
    protected void onNewMaskAdded(final T component) {
    }
    
    @SafeVarargs
    public final void addComponentType(final T... updateComponents) {
        for (final T component : updateComponents) {
            if (!this.containsMask(component)) {
                this.addMask(component.getMask());
                this.onNewMaskAdded(component);
            }
        }
    }
    
    protected void addMask(final int mask) {
        final byte[] masks = this.getMasks();
        final int n = mask >> 3;
        masks[n] |= AbstractMaskPacket.DEFAULT_FLAG_ARRAY[mask & 0x7];
    }
    
    public boolean containsMask(final T component) {
        return this.containsMask(component.getMask());
    }
    
    public boolean containsMask(final int mask) {
        return (this.getMasks()[mask >> 3] & AbstractMaskPacket.DEFAULT_FLAG_ARRAY[mask & 0x7]) != 0x0;
    }
    
    public boolean containsMask(final int masks, final T type) {
        return (masks & type.getMask()) == type.getMask();
    }
    
    static {
        DEFAULT_FLAG_ARRAY = new byte[] { -128, 64, 32, 16, 8, 4, 2, 1 };
    }
}
