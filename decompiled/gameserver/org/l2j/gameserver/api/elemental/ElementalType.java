// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.api.elemental;

import org.l2j.gameserver.model.stats.Stat;

public enum ElementalType
{
    NONE, 
    FIRE, 
    WATER, 
    WIND, 
    EARTH;
    
    public byte getId() {
        return (byte)this.ordinal();
    }
    
    public static ElementalType of(final byte elementId) {
        return values()[elementId];
    }
    
    public boolean isSuperior(final ElementalType targetType) {
        return this == superior(targetType);
    }
    
    public boolean isInferior(final ElementalType targetType) {
        return targetType == superior(this);
    }
    
    public ElementalType getSuperior() {
        return superior(this);
    }
    
    public static ElementalType superior(final ElementalType elementalType) {
        ElementalType elementalType2 = null;
        switch (elementalType) {
            case FIRE: {
                elementalType2 = ElementalType.WATER;
                break;
            }
            case WATER: {
                elementalType2 = ElementalType.WIND;
                break;
            }
            case WIND: {
                elementalType2 = ElementalType.EARTH;
                break;
            }
            case EARTH: {
                elementalType2 = ElementalType.FIRE;
                break;
            }
            default: {
                elementalType2 = ElementalType.NONE;
                break;
            }
        }
        return elementalType2;
    }
    
    public Stat getAttackStat() {
        Stat stat = null;
        switch (this) {
            case EARTH: {
                stat = Stat.ELEMENTAL_SPIRIT_EARTH_ATTACK;
                break;
            }
            case WIND: {
                stat = Stat.ELEMENTAL_SPIRIT_WIND_ATTACK;
                break;
            }
            case FIRE: {
                stat = Stat.ELEMENTAL_SPIRIT_FIRE_ATTACK;
                break;
            }
            case WATER: {
                stat = Stat.ELEMENTAL_SPIRIT_WATER_ATTACK;
                break;
            }
            default: {
                stat = null;
                break;
            }
        }
        return stat;
    }
    
    public Stat getDefenseStat() {
        Stat stat = null;
        switch (this) {
            case EARTH: {
                stat = Stat.ELEMENTAL_SPIRIT_EARTH_DEFENSE;
                break;
            }
            case WIND: {
                stat = Stat.ELEMENTAL_SPIRIT_WIND_DEFENSE;
                break;
            }
            case FIRE: {
                stat = Stat.ELEMENTAL_SPIRIT_FIRE_DEFENSE;
                break;
            }
            case WATER: {
                stat = Stat.ELEMENTAL_SPIRIT_WATER_DEFENSE;
                break;
            }
            default: {
                stat = null;
                break;
            }
        }
        return stat;
    }
}
