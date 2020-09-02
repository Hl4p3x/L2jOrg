// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public class EffectPoint extends Npc
{
    private final Player _owner;
    
    public EffectPoint(final NpcTemplate template, final Creature owner) {
        super(template);
        this.setInstanceType(InstanceType.L2EffectPointInstance);
        this.setIsInvul(false);
        this._owner = ((owner == null) ? null : owner.getActingPlayer());
        if (owner != null) {
            this.setInstance(owner.getInstanceWorld());
        }
    }
    
    @Override
    public Player getActingPlayer() {
        return this._owner;
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public void onActionShift(final Player player) {
        if (player == null) {
            return;
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public Party getParty() {
        if (this._owner == null) {
            return null;
        }
        return this._owner.getParty();
    }
    
    @Override
    public boolean isInParty() {
        return this._owner != null && this._owner.isInParty();
    }
    
    @Override
    public int getClanId() {
        return (this._owner != null) ? this._owner.getClanId() : 0;
    }
    
    @Override
    public int getAllyId() {
        return (this._owner != null) ? this._owner.getAllyId() : 0;
    }
    
    @Override
    public final byte getPvpFlag() {
        return (byte)((this._owner != null) ? this._owner.getPvpFlag() : 0);
    }
    
    @Override
    public final Team getTeam() {
        return (this._owner != null) ? this._owner.getTeam() : Team.NONE;
    }
}
