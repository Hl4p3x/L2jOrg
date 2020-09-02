// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.item.enchant.EnchantResultType;

public class EnchantResult extends ServerPacket
{
    private final EnchantResultType result;
    private final int crystal;
    private final long crystalCount;
    private final int enchantLevel;
    private final int[] enchantOptions;
    private int stone;
    private long stoneCount;
    
    private EnchantResult(final EnchantResultType result, final int crystal, final long crystalCount, final int enchantLevel, final int[] options) {
        this.result = result;
        this.crystal = crystal;
        this.crystalCount = crystalCount;
        this.enchantLevel = enchantLevel;
        this.enchantOptions = options;
    }
    
    private EnchantResult(final EnchantResultType result, final Item item) {
        this(result, 0, 0L, item.getEnchantLevel(), item.getEnchantOptions());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ENCHANT_RESULT);
        this.writeInt(this.result.ordinal());
        this.writeInt(this.crystal);
        this.writeLong(this.crystalCount);
        this.writeInt(this.stone);
        this.writeLong(this.stoneCount);
        this.writeInt(this.enchantLevel);
        for (final int option : this.enchantOptions) {
            this.writeInt(option);
        }
    }
    
    public void withStone(final int stoneId, final long stoneCount) {
        this.stone = stoneId;
        this.stoneCount = stoneCount;
    }
    
    public static EnchantResult success(final Item item) {
        return new EnchantResult(EnchantResultType.SUCCESS, item);
    }
    
    public static EnchantResult error() {
        return new EnchantResult(EnchantResultType.ERROR, 0, 0L, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }
    
    public static EnchantResult fail() {
        return new EnchantResult(EnchantResultType.NO_CRYSTAL, 0, 0L, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }
    
    public static EnchantResult fail(final int crystalId, final int count) {
        return new EnchantResult(EnchantResultType.FAIL, crystalId, count, 0, Item.DEFAULT_ENCHANT_OPTIONS);
    }
    
    public static EnchantResult safe(final Item item) {
        return new EnchantResult(EnchantResultType.SAFE_FAIL, item);
    }
    
    public static EnchantResult safeReduced(final Item item) {
        return new EnchantResult(EnchantResultType.SAFE_REDUCED, item);
    }
    
    public static EnchantResult blessed(final Item item) {
        return new EnchantResult(EnchantResultType.BLESSED_FAIL, item);
    }
}
