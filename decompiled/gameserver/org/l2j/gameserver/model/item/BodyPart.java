// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import io.github.joealisson.primitive.HashLongMap;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Objects;
import org.l2j.gameserver.enums.InventorySlot;
import io.github.joealisson.primitive.LongMap;

public enum BodyPart
{
    GREAT_WOLF(-104L, (InventorySlot)null), 
    BABY_PET(-103L, (InventorySlot)null), 
    STRIDER(-102L, (InventorySlot)null), 
    HATCHLING(-101L, (InventorySlot)null), 
    WOLF(-100L, (InventorySlot)null), 
    NONE(0L, (InventorySlot)null), 
    PENDANT(1L, InventorySlot.PENDANT), 
    RIGHT_EAR(2L, InventorySlot.RIGHT_EAR), 
    LEFT_EAR(4L, InventorySlot.LEFT_EAR), 
    EAR(BodyPart.RIGHT_EAR.id | BodyPart.LEFT_EAR.id, InventorySlot.LEFT_EAR), 
    NECK(8L, InventorySlot.NECK), 
    RIGHT_FINGER(16L, InventorySlot.RIGHT_FINGER), 
    LEFT_FINGER(32L, InventorySlot.LEFT_FINGER), 
    FINGER(BodyPart.RIGHT_FINGER.id | BodyPart.LEFT_FINGER.id, InventorySlot.LEFT_FINGER), 
    HEAD(64L, InventorySlot.HEAD), 
    RIGHT_HAND(128L, InventorySlot.RIGHT_HAND), 
    LEFT_HAND(256L, InventorySlot.LEFT_HAND), 
    GLOVES(512L, InventorySlot.GLOVES), 
    CHEST(1024L, InventorySlot.CHEST), 
    LEGS(2048L, InventorySlot.LEGS), 
    FEET(4096L, InventorySlot.FEET), 
    BACK(8192L, InventorySlot.CLOAK), 
    TWO_HAND(16384L, InventorySlot.TWO_HAND), 
    FULL_ARMOR(32768L, InventorySlot.CHEST), 
    HAIR(65536L, InventorySlot.HAIR), 
    ALL_DRESS(131072L, InventorySlot.CHEST), 
    HAIR2(262144L, InventorySlot.HAIR2), 
    HAIR_ALL(524288L, InventorySlot.HAIR), 
    RIGHT_BRACELET(1048576L, InventorySlot.RIGHT_BRACELET), 
    LEFT_BRACELET(2097152L, InventorySlot.LEFT_BRACELET), 
    TALISMAN(4194304L, InventorySlot.TALISMAN1), 
    BELT(268435456L, InventorySlot.BELT), 
    BROOCH(536870912L, InventorySlot.BROOCH), 
    BROOCH_JEWEL(1073741824L, InventorySlot.BROOCH_JEWEL1), 
    AGATHION(206158430208L, InventorySlot.AGATHION1), 
    ARTIFACT_BOOK(2199023255552L, InventorySlot.ARTIFACT_BOOK), 
    ARTIFACT(4398046511104L, InventorySlot.ARTIFACT1);
    
    private static final LongMap<BodyPart> mapper;
    private final long id;
    private final InventorySlot slot;
    
    private BodyPart(final long id, final InventorySlot slot) {
        this.id = id;
        this.slot = slot;
    }
    
    public long getId() {
        return this.id;
    }
    
    public InventorySlot slot() {
        return this.slot;
    }
    
    public boolean isAnyOf(final BodyPart... parts) {
        if (Objects.isNull(parts)) {
            return false;
        }
        for (final BodyPart bodyPart : parts) {
            if (bodyPart == this) {
                return true;
            }
        }
        return false;
    }
    
    public static BodyPart fromSlot(final long slot) {
        return (BodyPart)BodyPart.mapper.getOrDefault(slot, (Object)BodyPart.NONE);
    }
    
    public static BodyPart fromEquippedPaperdoll(final Item item) {
        final InventorySlot slot;
        if (!item.isEquipped() || Objects.isNull(slot = InventorySlot.fromId(item.getLocationSlot()))) {
            return BodyPart.NONE;
        }
        BodyPart bodyPart = null;
        switch (item.getBodyPart()) {
            case EAR: {
                bodyPart = ((slot == BodyPart.LEFT_EAR.slot) ? BodyPart.LEFT_EAR : BodyPart.RIGHT_EAR);
                break;
            }
            case FINGER: {
                bodyPart = ((slot == BodyPart.LEFT_FINGER.slot) ? BodyPart.LEFT_FINGER : BodyPart.RIGHT_FINGER);
                break;
            }
            default: {
                bodyPart = item.getBodyPart();
                break;
            }
        }
        return bodyPart;
    }
    
    static {
        mapper = (LongMap)new HashLongMap();
        for (final BodyPart value : values()) {
            BodyPart.mapper.put(value.id, (Object)value);
        }
    }
}
