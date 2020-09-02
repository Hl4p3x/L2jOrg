// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeLock;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestCostumeLock extends ClientPacket
{
    private int id;
    private boolean lock;
    
    @Override
    protected void readImpl() throws Exception {
        this.id = this.readInt();
        this.lock = this.readBoolean();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE);
            return;
        }
        Util.doIfNonNull((Object)player.getCostume(this.id), costume -> {
            costume.setLocked(this.lock);
            ((GameClient)this.client).sendPacket(new ExCostumeLock(this.id, this.lock, true));
        });
    }
}
