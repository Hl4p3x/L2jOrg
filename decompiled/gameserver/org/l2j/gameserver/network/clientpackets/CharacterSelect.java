// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.network.serverpackets.CharSelected;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSelect;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class CharacterSelect extends ClientPacket
{
    protected static final Logger LOGGER_ACCOUNTING;
    private int selectedSlot;
    
    public void readImpl() {
        this.selectedSlot = this.readInt();
    }
    
    public void runImpl() {
        if (!((GameClient)this.client).getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterSelect")) {
            return;
        }
        if (SecondaryAuthManager.getInstance().isEnabled() && !((GameClient)this.client).isSecondaryAuthed()) {
            ((GameClient)this.client).openSecondaryAuthDialog();
            return;
        }
        if (((GameClient)this.client).getActivePlayerLock().tryLock()) {
            try {
                if (((GameClient)this.client).getPlayer() == null) {
                    final CharSelectInfoPackage info = ((GameClient)this.client).getCharSelection(this.selectedSlot);
                    if (info == null) {
                        return;
                    }
                    if (PunishmentManager.getInstance().hasPunishment(info.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(((GameClient)this.client).getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN) || PunishmentManager.getInstance().hasPunishment(((GameClient)this.client).getHostAddress(), PunishmentAffect.IP, PunishmentType.BAN)) {
                        ((GameClient)this.client).close(ServerClose.STATIC_PACKET);
                        return;
                    }
                    if (info.getAccessLevel() < 0) {
                        ((GameClient)this.client).close(ServerClose.STATIC_PACKET);
                        return;
                    }
                    if (Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP > 0 && !AntiFeedManager.getInstance().tryAddClient(0, (GameClient)this.client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)) {
                        final NpcHtmlMessage msg = new NpcHtmlMessage();
                        msg.setFile(null, "data/html/mods/IPRestriction.htm");
                        msg.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit((GameClient)this.client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)));
                        ((GameClient)this.client).sendPacket(msg);
                        return;
                    }
                    final Player player = ((GameClient)this.client).load(this.selectedSlot);
                    if (player == null) {
                        return;
                    }
                    PlayerNameTable.getInstance().addName(player);
                    player.setOnlineStatus(true, true);
                    final TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerSelect(player, player.getObjectId(), player.getName(), (GameClient)this.client), Listeners.players(), TerminateReturn.class);
                    if (terminate != null && terminate.terminate()) {
                        Disconnection.of(player).defaultSequence(false);
                        return;
                    }
                    ((GameClient)this.client).setConnectionState(ConnectionState.JOINING_GAME);
                    ((GameClient)this.client).sendPacket(new CharSelected(player, ((GameClient)this.client).getSessionId().getGameServerSessionId()));
                }
            }
            finally {
                ((GameClient)this.client).getActivePlayerLock().unlock();
            }
            CharacterSelect.LOGGER_ACCOUNTING.info("{} Logged in", (Object)this.client);
        }
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
