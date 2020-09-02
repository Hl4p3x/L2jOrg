// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.HashMap;
import java.util.Map;

public final class Elementals
{
    public static final int FIRST_WEAPON_BONUS = 20;
    public static final int NEXT_WEAPON_BONUS = 5;
    public static final int ARMOR_BONUS = 6;
    public static final int[] WEAPON_VALUES;
    public static final int[] ARMOR_VALUES;
    protected static final byte NONE = -1;
    protected static final byte FIRE = 0;
    protected static final byte WATER = 1;
    protected static final byte WIND = 2;
    protected static final byte EARTH = 3;
    protected static final byte HOLY = 4;
    protected static final byte DARK = 5;
    private static final Map<Integer, ElementalItems> TABLE;
    
    public static byte getItemElement(final int itemId) {
        final ElementalItems item = Elementals.TABLE.get(itemId);
        if (item != null) {
            return item._element;
        }
        return -1;
    }
    
    public static ElementalItems getItemElemental(final int itemId) {
        return Elementals.TABLE.get(itemId);
    }
    
    public static int getMaxElementLevel(final int itemId) {
        final ElementalItems item = Elementals.TABLE.get(itemId);
        if (item != null) {
            return item._type._maxLevel;
        }
        return -1;
    }
    
    static {
        WEAPON_VALUES = new int[] { 0, 25, 75, 150, 175, 225, 300, 325, 375, 450, 475, 525, 600, Integer.MAX_VALUE };
        ARMOR_VALUES = new int[] { 0, 12, 30, 60, 72, 90, 120, 132, 150, 180, 192, 210, 240, Integer.MAX_VALUE };
        TABLE = new HashMap<Integer, ElementalItems>();
        for (final ElementalItems item : ElementalItems.values()) {
            Elementals.TABLE.put(item._itemId, item);
        }
    }
    
    public enum ElementalItemType
    {
        Stone(3), 
        Roughore(3), 
        Crystal(6), 
        Jewel(9), 
        Energy(12);
        
        public int _maxLevel;
        
        private ElementalItemType(final int maxLvl) {
            this._maxLevel = maxLvl;
        }
    }
    
    public enum ElementalItems
    {
        fireStone((byte)0, 9546, ElementalItemType.Stone), 
        waterStone((byte)1, 9547, ElementalItemType.Stone), 
        windStone((byte)2, 9549, ElementalItemType.Stone), 
        earthStone((byte)3, 9548, ElementalItemType.Stone), 
        divineStone((byte)4, 9551, ElementalItemType.Stone), 
        darkStone((byte)5, 9550, ElementalItemType.Stone), 
        fireRoughtore((byte)0, 10521, ElementalItemType.Roughore), 
        waterRoughtore((byte)1, 10522, ElementalItemType.Roughore), 
        windRoughtore((byte)2, 10524, ElementalItemType.Roughore), 
        earthRoughtore((byte)3, 10523, ElementalItemType.Roughore), 
        divineRoughtore((byte)4, 10526, ElementalItemType.Roughore), 
        darkRoughtore((byte)5, 10525, ElementalItemType.Roughore), 
        fireCrystal((byte)0, 9552, ElementalItemType.Crystal), 
        waterCrystal((byte)1, 9553, ElementalItemType.Crystal), 
        windCrystal((byte)2, 9555, ElementalItemType.Crystal), 
        earthCrystal((byte)3, 9554, ElementalItemType.Crystal), 
        divineCrystal((byte)4, 9557, ElementalItemType.Crystal), 
        darkCrystal((byte)5, 9556, ElementalItemType.Crystal), 
        fireJewel((byte)0, 9558, ElementalItemType.Jewel), 
        waterJewel((byte)1, 9559, ElementalItemType.Jewel), 
        windJewel((byte)2, 9561, ElementalItemType.Jewel), 
        earthJewel((byte)3, 9560, ElementalItemType.Jewel), 
        divineJewel((byte)4, 9563, ElementalItemType.Jewel), 
        darkJewel((byte)5, 9562, ElementalItemType.Jewel), 
        fireEnergy((byte)0, 9564, ElementalItemType.Energy), 
        waterEnergy((byte)1, 9565, ElementalItemType.Energy), 
        windEnergy((byte)2, 9567, ElementalItemType.Energy), 
        earthEnergy((byte)3, 9566, ElementalItemType.Energy), 
        divineEnergy((byte)4, 9569, ElementalItemType.Energy), 
        darkEnergy((byte)5, 9568, ElementalItemType.Energy);
        
        public byte _element;
        public int _itemId;
        public ElementalItemType _type;
        
        private ElementalItems(final byte element, final int itemId, final ElementalItemType type) {
            this._element = element;
            this._itemId = itemId;
            this._type = type;
        }
    }
}
