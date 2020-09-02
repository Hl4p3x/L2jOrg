// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.holders.SiegeGuardHolder;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Playable;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Map;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class MercTicket extends AbstractNpcAI implements IItemHandler
{
    private final Map<Integer, Item> _items;
    
    public MercTicket() {
        this._items = new ConcurrentHashMap<Integer, Item>();
    }
    
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)activeChar);
        if (castle == null || activeChar.getClan() == null || castle.getOwnerId() != activeChar.getClanId() || !activeChar.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES);
            return false;
        }
        final int castleId = castle.getId();
        final SiegeGuardHolder holder = SiegeGuardManager.getInstance().getSiegeGuardByItem(castleId, item.getId());
        if (holder == null || castleId != holder.getCastleId()) {
            activeChar.sendPacket(SystemMessageId.MERCENARIES_CANNOT_BE_POSITIONED_HERE);
            return false;
        }
        if (castle.getSiege().isInProgress()) {
            activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
            return false;
        }
        if (SiegeGuardManager.getInstance().isTooCloseToAnotherTicket(activeChar)) {
            activeChar.sendPacket(SystemMessageId.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT);
            return false;
        }
        if (SiegeGuardManager.getInstance().isAtNpcLimit(castleId, item.getId())) {
            activeChar.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
            return false;
        }
        this._items.put(activeChar.getObjectId(), item);
        final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.PLACE_S1_IN_THE_CURRENT_LOCATION_AND_DIRECTION_DO_YOU_WISH_TO_CONTINUE);
        dlg.addTime(15000);
        dlg.addNpcName(holder.getNpcId());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)dlg });
        activeChar.addAction(PlayerAction.MERCENARY_CONFIRM);
        return true;
    }
    
    @RegisterEvent(EventType.ON_PLAYER_DLG_ANSWER)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerDlgAnswer(final OnPlayerDlgAnswer event) {
        final Player activeChar = event.getActiveChar();
        if (activeChar.removeAction(PlayerAction.MERCENARY_CONFIRM) && this._items.containsKey(activeChar.getObjectId())) {
            if (SiegeGuardManager.getInstance().isTooCloseToAnotherTicket(activeChar)) {
                activeChar.sendPacket(SystemMessageId.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT);
                return;
            }
            if (event.getAnswer() == 1) {
                final Item item = this._items.get(activeChar.getObjectId());
                SiegeGuardManager.getInstance().addTicket(item.getId(), activeChar);
                activeChar.destroyItem("Consume", item.getObjectId(), 1L, (WorldObject)null, false);
            }
            this._items.remove(activeChar.getObjectId());
        }
    }
}
