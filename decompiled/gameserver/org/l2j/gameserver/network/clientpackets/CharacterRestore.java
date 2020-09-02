// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerRestore;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PlayerSelectionInfo;
import org.l2j.gameserver.network.GameClient;

public final class CharacterRestore extends ClientPacket
{
    private int slot;
    
    public void readImpl() {
        this.slot = this.readInt();
    }
    
    public void runImpl() {
        if (!((GameClient)this.client).getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterRestore")) {
            return;
        }
        ((GameClient)this.client).restore(this.slot);
        ((GameClient)this.client).sendPacket(new PlayerSelectionInfo((GameClient)this.client, this.slot));
        final PlayerSelectInfo playerInfo = ((GameClient)this.client).getPlayerSelection(this.slot);
        if (Objects.nonNull(playerInfo)) {
            EventDispatcher.getInstance().notifyEvent(new OnPlayerRestore(playerInfo.getObjectId(), playerInfo.getName(), (GameClient)this.client));
        }
    }
}
