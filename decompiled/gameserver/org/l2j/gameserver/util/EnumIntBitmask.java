// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

public final class EnumIntBitmask<E extends Enum<E>> implements Cloneable
{
    private final Class<E> _enumClass;
    private int _bitmask;
    
    public EnumIntBitmask(final Class<E> enumClass, final boolean all) {
        this._enumClass = enumClass;
        final E[] values = this._enumClass.getEnumConstants();
        if (values.length > 32) {
            throw new IllegalArgumentException("Enum too big for an integer bitmask.");
        }
        if (all) {
            this.setAll();
        }
        else {
            this.clear();
        }
    }
    
    public EnumIntBitmask(final Class<E> enumClass, final int bitmask) {
        this._enumClass = enumClass;
        this._bitmask = bitmask;
    }
    
    public static <E extends Enum<E>> int getAllBitmask(final Class<E> enumClass) {
        int allBitmask = 0;
        final E[] values = enumClass.getEnumConstants();
        if (values.length > 32) {
            throw new IllegalArgumentException("Enum too big for an integer bitmask.");
        }
        for (final E value : values) {
            allBitmask |= 1 << value.ordinal();
        }
        return allBitmask;
    }
    
    public void setAll() {
        this.set((E[])this._enumClass.getEnumConstants());
    }
    
    public void clear() {
        this._bitmask = 0;
    }
    
    @SafeVarargs
    public final void set(final E... many) {
        this.clear();
        for (final E one : many) {
            this._bitmask |= 1 << one.ordinal();
        }
    }
    
    @SafeVarargs
    public final void set(final E first, final E... more) {
        this.clear();
        this.add(first, more);
    }
    
    @SafeVarargs
    public final void add(final E first, final E... more) {
        this._bitmask |= 1 << first.ordinal();
        if (more != null) {
            for (final E one : more) {
                this._bitmask |= 1 << one.ordinal();
            }
        }
    }
    
    @SafeVarargs
    public final void remove(final E first, final E... more) {
        this._bitmask &= ~(1 << first.ordinal());
        if (more != null) {
            for (final E one : more) {
                this._bitmask &= ~(1 << one.ordinal());
            }
        }
    }
    
    @SafeVarargs
    public final boolean has(final E first, final E... more) {
        if ((this._bitmask & 1 << first.ordinal()) == 0x0) {
            return false;
        }
        for (final E one : more) {
            if ((this._bitmask & 1 << one.ordinal()) == 0x0) {
                return false;
            }
        }
        return true;
    }
    
    public EnumIntBitmask<E> clone() {
        return new EnumIntBitmask<E>(this._enumClass, this._bitmask);
    }
    
    public int getBitmask() {
        return this._bitmask;
    }
    
    public void setBitmask(final int bitmask) {
        this._bitmask = bitmask;
    }
}
