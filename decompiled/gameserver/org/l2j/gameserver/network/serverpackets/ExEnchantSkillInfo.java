// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collections;
import java.util.Set;

public final class ExEnchantSkillInfo extends ServerPacket
{
    private final Set<Integer> _routes;
    private final int _skillId;
    private final int _skillLevel;
    private final int _skillSubLevel;
    private final int _currentSubLevel;
    
    public ExEnchantSkillInfo(final int skillId, final int skillLevel, final int skillSubLevel, final int currentSubLevel) {
        this._skillId = skillId;
        this._skillLevel = skillLevel;
        this._skillSubLevel = skillSubLevel;
        this._currentSubLevel = currentSubLevel;
        this._routes = Collections.emptySet();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_SKILL_INFO);
        this.writeInt(this._skillId);
        this.writeShort((short)this._skillLevel);
        this.writeShort((short)this._skillSubLevel);
        this.writeInt(this._skillSubLevel % 1000 == 0);
        this.writeInt((int)((this._skillSubLevel > 1000) ? 1 : 0));
        this.writeInt(this._routes.size());
        final int routeId;
        final int currentRouteId;
        final int subLevel;
        this._routes.forEach(route -> {
            routeId = route / 1000;
            currentRouteId = this._skillSubLevel / 1000;
            subLevel = ((this._currentSubLevel > 0) ? (route + this._currentSubLevel % 1000 - 1) : route);
            this.writeShort((short)this._skillLevel);
            this.writeShort((short)((currentRouteId != routeId) ? subLevel : Math.min(subLevel + 1, route)));
        });
    }
}
