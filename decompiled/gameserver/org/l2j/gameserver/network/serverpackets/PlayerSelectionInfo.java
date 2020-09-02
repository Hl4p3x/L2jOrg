// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.PlayerSelectInfo;
import java.util.List;

public class PlayerSelectionInfo extends ServerPacket
{
    private final String account;
    private final int sessionId;
    private final List<PlayerSelectInfo> playersInfo;
    private int activeSlot;
    
    public PlayerSelectionInfo(final GameClient client) {
        this(client, client.getActiveSlot());
    }
    
    public PlayerSelectionInfo(final GameClient client, final int activeSlot) {
        this.sessionId = client.getSessionId().getGameServerSessionId();
        this.account = client.getAccountName();
        this.playersInfo = client.getPlayersInfo();
        this.activeSlot = activeSlot;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_SELECTION_INFO);
        final int size = this.playersInfo.size();
        this.writeInt(size);
        this.writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        this.writeByte(size == Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        this.writeByte(1);
        this.writeInt(2);
        this.writeByte(0);
        this.writeByte(0);
        long lastAccess = 0L;
        if (this.activeSlot == -1) {
            for (int i = 0; i < size; ++i) {
                if (lastAccess < this.playersInfo.get(i).getLastAccess()) {
                    lastAccess = this.playersInfo.get(i).getLastAccess();
                    this.activeSlot = i;
                }
            }
        }
        for (int i = 0; i < size; ++i) {
            final PlayerSelectInfo playerInfo = this.playersInfo.get(i);
            final PlayerData data = playerInfo.getData();
            this.writeString((CharSequence)data.getName());
            this.writeInt(data.getCharId());
            this.writeString((CharSequence)this.account);
            this.writeInt(this.sessionId);
            this.writeInt(data.getClanId());
            this.writeInt(data.getAccessLevel());
            this.writeInt(data.isFemale());
            this.writeInt(data.getRace());
            this.writeInt(data.getClassId());
            this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
            this.writeInt(data.getX());
            this.writeInt(data.getY());
            this.writeInt(data.getZ());
            this.writeDouble(data.getHp());
            this.writeDouble(data.getMp());
            this.writeLong(data.getSp());
            this.writeLong(data.getExp());
            this.writeDouble((double)((data.getExp() - LevelData.getInstance().getExpForLevel(data.getLevel())) / (float)(LevelData.getInstance().getExpForLevel(data.getLevel() + 1) - LevelData.getInstance().getExpForLevel(data.getLevel()))));
            this.writeInt((int)data.getLevel());
            this.writeInt(data.getReputation());
            this.writeInt(data.getPk());
            this.writeInt(data.getPvP());
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            for (final InventorySlot slot : this.getPaperdollOrder()) {
                this.writeInt(playerInfo.getPaperdollItemId(slot));
            }
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeShort(playerInfo.getEnchantEffect(InventorySlot.CHEST));
            this.writeShort(playerInfo.getEnchantEffect(InventorySlot.LEGS));
            this.writeShort(playerInfo.getEnchantEffect(InventorySlot.HEAD));
            this.writeShort(playerInfo.getEnchantEffect(InventorySlot.GLOVES));
            this.writeShort(playerInfo.getEnchantEffect(InventorySlot.FEET));
            this.writeInt(playerInfo.getHairStyle());
            this.writeInt(playerInfo.getHairColor());
            this.writeInt(playerInfo.getFace());
            this.writeDouble(data.getMaxtHp());
            this.writeDouble(data.getMaxtMp());
            this.writeInt((data.getDeleteTime() > 0L) ? ((int)((data.getDeleteTime() - System.currentTimeMillis()) / 1000L)) : 0);
            this.writeInt(data.getClassId());
            this.writeInt(i == this.activeSlot);
            this.writeByte(Math.min(playerInfo.getEnchantEffect(InventorySlot.RIGHT_HAND), 127));
            this.writeInt(playerInfo.getAugmentationOption1());
            this.writeInt(playerInfo.getAugmentationOption2());
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeDouble(0.0);
            this.writeDouble(0.0);
            this.writeInt(data.getVitalityPoints());
            this.writeInt((int)(Config.RATE_VITALITY_EXP_MULTIPLIER * 100.0f));
            this.writeInt(playerInfo.getVitalityItemsUsed());
            this.writeInt(data.getAccessLevel() != -100);
            this.writeByte(data.isNobless());
            this.writeByte(Hero.getInstance().isHero(data.getCharId()) ? 2 : 0);
            this.writeByte(playerInfo.isHairAccessoryEnabled());
            this.writeInt(playerInfo.getRemainBanExpireTime());
            this.writeInt((int)(playerInfo.getLastAccess() / 1000L));
        }
    }
}
