// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.teleport;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.TeleportRequest;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.model.TeleportData;
import java.util.function.Consumer;
import org.l2j.gameserver.data.xml.impl.TeleportEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestTeleport extends ClientPacket
{
    private int id;
    
    @Override
    protected void readImpl() throws Exception {
        this.id = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        TeleportEngine.getInstance().getInfo(this.id).ifPresent(this::teleport);
    }
    
    private void teleport(final TeleportData info) {
        final Player player = ((GameClient)this.client).getPlayer();
        if (info.getCastleId() != -1 && CastleManager.getInstance().getCastleById(info.getCastleId()).isInSiege()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
            return;
        }
        if (GameUtils.canTeleport(player) && (player.getLevel() <= 40 || player.reduceAdena("Teleport", info.getPrice(), null, true))) {
            player.addRequest(new TeleportRequest(player, this.id));
            player.useMagic(CommonSkill.TELEPORT.getSkill(), null, false, true);
        }
        if (!GameUtils.canTeleport(player)) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_TELEPORT_IN_THIS_AREA);
        }
    }
}
