// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerRestore;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.GameClient;

public final class CharacterRestore extends ClientPacket
{
    private int _charSlot;
    
    public void readImpl() {
        this._charSlot = this.readInt();
    }
    
    public void runImpl() {
        if (!((GameClient)this.client).getFloodProtectors().getCharacterSelect().tryPerformAction("CharacterRestore")) {
            return;
        }
        ((GameClient)this.client).restore(this._charSlot);
        final CharSelectionInfo cl = new CharSelectionInfo(((GameClient)this.client).getAccountName(), ((GameClient)this.client).getSessionId().getGameServerSessionId(), 0);
        ((GameClient)this.client).sendPacket(cl);
        ((GameClient)this.client).setCharSelection(cl.getCharInfo());
        final CharSelectInfoPackage charInfo = ((GameClient)this.client).getCharSelection(this._charSlot);
        EventDispatcher.getInstance().notifyEvent(new OnPlayerRestore(charInfo.getObjectId(), charInfo.getName(), (GameClient)this.client));
    }
}
