// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public class RequestSaveKeyMapping extends ClientPacket
{
    private byte[] _uiKeyMapping;
    
    public void readImpl() {
        final int dataSize = this.readInt();
        if (dataSize > 0) {
            this.readBytes(this._uiKeyMapping = new byte[dataSize]);
        }
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (!Config.STORE_UI_SETTINGS || player == null || this._uiKeyMapping == null || ((GameClient)this.client).getConnectionState() != ConnectionState.IN_GAME) {
            return;
        }
        String uiKeyMapping = "";
        for (final Byte b : this._uiKeyMapping) {
            uiKeyMapping = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/Byte;)Ljava/lang/String;, uiKeyMapping, b);
        }
        player.setUiKeyMapping(uiKeyMapping);
    }
}
