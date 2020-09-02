// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class MotherTreeZone extends Zone
{
    private int enterMsg;
    private int leaveMsg;
    private int mpRegen;
    private int hpRegen;
    
    public MotherTreeZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        switch (name) {
            case "enterMsgId": {
                this.enterMsg = Integer.parseInt(value);
                break;
            }
            case "leaveMsgId": {
                this.leaveMsg = Integer.parseInt(value);
                break;
            }
            case "MpRegenBonus": {
                this.mpRegen = Integer.parseInt(value);
                break;
            }
            case "HpRegenBonus": {
                this.hpRegen = Integer.parseInt(value);
                break;
            }
            default: {
                super.setParameter(name, value);
                break;
            }
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            creature.setInsideZone(ZoneType.MOTHER_TREE, true);
            if (this.enterMsg != 0) {
                player.sendPacket(SystemMessage.getSystemMessage(this.enterMsg));
            }
        }
    }
    
    @Override
    protected void onExit(final Creature character) {
        if (GameUtils.isPlayer(character)) {
            final Player player = character.getActingPlayer();
            player.setInsideZone(ZoneType.MOTHER_TREE, false);
            if (this.leaveMsg != 0) {
                player.sendPacket(SystemMessage.getSystemMessage(this.leaveMsg));
            }
        }
    }
    
    public int getMpRegenBonus() {
        return this.mpRegen;
    }
    
    public int getHpRegenBonus() {
        return this.hpRegen;
    }
}
