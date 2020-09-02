// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.stats.Stat;

public enum AttributeType
{
    NONE(-2), 
    FIRE(0), 
    WATER(1), 
    WIND(2), 
    EARTH(3), 
    HOLY(4), 
    DARK(5);
    
    public static final AttributeType[] ATTRIBUTE_TYPES;
    private final byte _clientId;
    
    private AttributeType(final int clientId) {
        this._clientId = (byte)clientId;
    }
    
    public static AttributeType findByName(final String attributeName) {
        for (final AttributeType attributeType : values()) {
            if (attributeType.name().equalsIgnoreCase(attributeName)) {
                return attributeType;
            }
        }
        return null;
    }
    
    public static AttributeType findByClientId(final int clientId) {
        for (final AttributeType attributeType : values()) {
            if (attributeType.getClientId() == clientId) {
                return attributeType;
            }
        }
        return null;
    }
    
    public byte getClientId() {
        return this._clientId;
    }
    
    public AttributeType getOpposite() {
        return AttributeType.ATTRIBUTE_TYPES[(this._clientId % 2 == 0) ? (this._clientId + 1) : (this._clientId - 1)];
    }
    
    public Stat toStat() {
        Stat stat = null;
        switch (this) {
            case WATER: {
                stat = Stat.WATER_POWER;
                break;
            }
            case WIND: {
                stat = Stat.WIND_POWER;
                break;
            }
            case EARTH: {
                stat = Stat.EARTH_POWER;
                break;
            }
            case HOLY: {
                stat = Stat.HOLY_POWER;
                break;
            }
            case DARK: {
                stat = Stat.DARK_POWER;
                break;
            }
            default: {
                stat = Stat.FIRE_POWER;
                break;
            }
        }
        return stat;
    }
    
    public Stat toStatResist() {
        Stat stat = null;
        switch (this) {
            case WATER: {
                stat = Stat.WATER_RES;
                break;
            }
            case WIND: {
                stat = Stat.WIND_RES;
                break;
            }
            case EARTH: {
                stat = Stat.EARTH_RES;
                break;
            }
            case HOLY: {
                stat = Stat.HOLY_RES;
                break;
            }
            case DARK: {
                stat = Stat.DARK_RES;
                break;
            }
            default: {
                stat = Stat.FIRE_RES;
                break;
            }
        }
        return stat;
    }
    
    static {
        ATTRIBUTE_TYPES = new AttributeType[] { AttributeType.FIRE, AttributeType.WATER, AttributeType.WIND, AttributeType.EARTH, AttributeType.HOLY, AttributeType.DARK };
    }
}
