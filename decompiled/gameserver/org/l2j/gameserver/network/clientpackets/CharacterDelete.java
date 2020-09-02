// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.serverpackets.CharDeleteFail;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDelete;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CharDeleteSuccess;
import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class CharacterDelete extends ClientPacket
{
    private static final Logger LOGGER;
    private int _charSlot;
    
    public void readImpl() {
        this._charSlot = this.readInt();
    }
    
    public void runImpl() {
        try {
            final CharacterDeleteFailType failType = ((GameClient)this.client).markToDeleteChar(this._charSlot);
            if (failType == CharacterDeleteFailType.NONE) {
                ((GameClient)this.client).sendPacket(new CharDeleteSuccess());
                final CharSelectInfoPackage charInfo = ((GameClient)this.client).getCharSelection(this._charSlot);
                EventDispatcher.getInstance().notifyEvent(new OnPlayerDelete(charInfo.getObjectId(), charInfo.getName(), (GameClient)this.client), Listeners.players());
            }
            else {
                ((GameClient)this.client).sendPacket(new CharDeleteFail(failType));
            }
        }
        catch (Exception e) {
            CharacterDelete.LOGGER.error(e.getLocalizedMessage(), (Throwable)e);
        }
        final CharSelectionInfo cl = new CharSelectionInfo(((GameClient)this.client).getAccountName(), ((GameClient)this.client).getSessionId().getGameServerSessionId(), 0);
        ((GameClient)this.client).sendPacket(cl);
        ((GameClient)this.client).setCharSelection(cl.getCharInfo());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CharacterDelete.class);
    }
}
