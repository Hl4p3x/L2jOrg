// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.TimeStamp;
import java.util.List;

public class SkillCoolTime extends ServerPacket
{
    private final long _currentTime;
    private final List<TimeStamp> _skillReuseTimeStamps;
    
    public SkillCoolTime(final Player player) {
        this._skillReuseTimeStamps = new ArrayList<TimeStamp>();
        this._currentTime = System.currentTimeMillis();
        for (final TimeStamp ts : player.getSkillReuseTimeStamps().values()) {
            if (this._currentTime < ts.getStamp() && !SkillEngine.getInstance().getSkill(ts.getSkillId(), ts.getSkillLvl()).isNotBroadcastable()) {
                this._skillReuseTimeStamps.add(ts);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SKILL_COOL_TIME);
        this.writeInt(this._skillReuseTimeStamps.size());
        for (final TimeStamp ts : this._skillReuseTimeStamps) {
            this.writeInt(ts.getSkillId());
            this.writeInt(0);
            this.writeInt((int)ts.getReuse() / 1000);
            this.writeInt((int)Math.max(ts.getStamp() - this._currentTime, 0L) / 1000);
        }
    }
}
