// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.GameClient;

public final class RequestWriteHeroWords extends ClientPacket
{
    private String _heroWords;
    
    public void readImpl() {
        this._heroWords = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || !player.isHero()) {
            return;
        }
        if (this._heroWords == null || this._heroWords.length() > 300) {
            return;
        }
        Hero.getInstance().setHeroMessage(player, this._heroWords);
    }
}
