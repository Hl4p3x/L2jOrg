// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.punishment.PunishmentTask;
import java.util.Objects;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExUserBanInfo;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.GameClient;

public class ExRequestUserBanInfo extends ClientPacket
{
    private int playerId;
    
    @Override
    protected void readImpl() throws Exception {
        this.playerId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final int accessLevel = ((GameClient)this.client).getPlayerInfoAccessLevel(this.playerId);
        if (accessLevel < 0) {
            ((GameClient)this.client).sendPacket(new ExUserBanInfo(PunishmentType.PERMANENT_BAN, 0L, "violation of the terms of conduct"));
        }
        else {
            final PunishmentTask punishment = PunishmentManager.getInstance().getPunishment(this.playerId, PunishmentAffect.CHARACTER, PunishmentType.BAN);
            if (Objects.nonNull(punishment) && punishment.getExpirationTime() > 0L) {
                ((GameClient)this.client).sendPacket(new ExUserBanInfo(PunishmentType.BAN, punishment.getExpirationTime(), punishment.getReason()));
            }
        }
    }
}
