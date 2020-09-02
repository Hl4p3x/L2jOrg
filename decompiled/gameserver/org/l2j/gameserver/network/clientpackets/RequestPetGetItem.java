// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.world.World;

public final class RequestPetGetItem extends ClientPacket
{
    private int _objectId;
    
    public void readImpl() {
        this._objectId = this.readInt();
    }
    
    public void runImpl() {
        final World world = World.getInstance();
        final Item item = (Item)world.findObject(this._objectId);
        if (item == null || ((GameClient)this.client).getPlayer() == null || !((GameClient)this.client).getPlayer().hasPet()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Castle castle = CastleManager.getInstance().getCastle(item);
        if (castle != null && SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), item.getId()) != null) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Pet pet = ((GameClient)this.client).getPlayer().getPet();
        if (pet.isDead() || pet.isControlBlocked()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (pet.isUncontrollable()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
            return;
        }
        pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
    }
}
