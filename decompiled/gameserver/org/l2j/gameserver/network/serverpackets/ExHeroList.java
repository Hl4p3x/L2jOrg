// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.StatsSet;
import java.util.Map;

public class ExHeroList extends ServerPacket
{
    private final Map<Integer, StatsSet> _heroList;
    
    public ExHeroList() {
        this._heroList = Hero.getInstance().getHeroes();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_HERO_LIST);
        this.writeInt(this._heroList.size());
        for (final Integer heroId : this._heroList.keySet()) {
            final StatsSet hero = this._heroList.get(heroId);
            this.writeString((CharSequence)hero.getString("char_name"));
            this.writeInt(hero.getInt("class_id"));
            this.writeString((CharSequence)hero.getString("clan_name", ""));
            this.writeInt(hero.getInt("clan_crest", 0));
            this.writeString((CharSequence)hero.getString("ally_name", ""));
            this.writeInt(hero.getInt("ally_crest", 0));
            this.writeInt(hero.getInt("count"));
            this.writeInt(0);
        }
    }
}
