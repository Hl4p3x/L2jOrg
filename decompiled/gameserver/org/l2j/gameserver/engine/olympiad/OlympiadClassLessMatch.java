// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;

class OlympiadClassLessMatch extends OlympiadMatch
{
    private Player red;
    private Player blue;
    
    @Override
    public void addParticipant(final Player player) {
        if (Objects.isNull(this.red)) {
            this.red = player;
        }
        else {
            this.blue = player;
        }
    }
    
    @Override
    public void sendMessage(final SystemMessageId messageId) {
        this.red.sendPacket(messageId);
        this.blue.sendPacket(messageId);
    }
}
