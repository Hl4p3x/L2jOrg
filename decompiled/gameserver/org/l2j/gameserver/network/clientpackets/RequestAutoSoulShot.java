// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.commons.util.Util;
import java.util.function.ToIntFunction;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.enums.PrivateStoreType;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class RequestAutoSoulShot extends ClientPacket
{
    private int itemId;
    private boolean enable;
    private int type;
    
    public void readImpl() {
        this.itemId = this.readInt();
        this.enable = this.readIntAsBoolean();
        this.type = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.isDead() || Objects.nonNull(player.getActiveRequester()) || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return;
        }
        final Item item = player.getInventory().getItemByItemId(this.itemId);
        if (Objects.isNull(item) || item.getItemType() != EtcItemType.SOULSHOT) {
            return;
        }
        final ShotType shotType = ShotType.of(this.type);
        if (Objects.isNull(shotType)) {
            return;
        }
        if (this.enable) {
            if (player.getInventory().isBlocked(item)) {
                player.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS)).addItemName(this.itemId));
                return;
            }
            if (this.isSummonShot(item, shotType)) {
                if (player.hasSummon()) {
                    this.rechargeSummonShots(player, item, shotType);
                }
                else {
                    ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION);
                }
            }
            else if (this.isPlayerShot(item, shotType)) {
                player.enableAutoSoulShot(shotType, this.itemId);
            }
        }
        else {
            player.disableAutoShot(shotType);
        }
    }
    
    private void rechargeSummonShots(final Player player, final Item item, final ShotType shotType) {
        final boolean isSoulShot = shotType == ShotType.BEAST_SOULSHOTS;
        final int shotsCount = this.getSummonSoulShotCount(player, isSoulShot ? Summon::getSoulShotsPerHit : Summon::getSpiritShotsPerHit);
        if (shotsCount > item.getCount()) {
            final SystemMessageId message = isSoulShot ? SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR : SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
            ((GameClient)this.client).sendPacket(message);
            return;
        }
        player.enableAutoSoulShot(shotType, this.itemId);
    }
    
    public int getSummonSoulShotCount(final Player player, final ToIntFunction<Summon> function) {
        return Util.zeroIfNullOrElse((Object)player.getPet(), (ToIntFunction)function) + player.getServitors().values().stream().mapToInt(function).sum();
    }
    
    private boolean isPlayerShot(final Item item, final ShotType type) {
        boolean b = false;
        switch (item.getAction()) {
            case SPIRITSHOT: {
                b = (type == ShotType.SPIRITSHOTS);
                break;
            }
            case SOULSHOT:
            case FISHINGSHOT: {
                b = (type == ShotType.SOULSHOTS);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    private boolean isSummonShot(final Item item, final ShotType type) {
        boolean b = false;
        switch (item.getAction()) {
            case SUMMON_SOULSHOT: {
                b = (type == ShotType.BEAST_SOULSHOTS);
                break;
            }
            case SUMMON_SPIRITSHOT: {
                b = (type == ShotType.BEAST_SPIRITSHOTS);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
}
