// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.raidbossinfo;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import java.util.HashMap;
import java.util.Map;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestRaidBossSpawnInfo extends ClientPacket
{
    private Map<Integer, Integer> _bossIds;
    
    public RequestRaidBossSpawnInfo() {
        this._bossIds = new HashMap<Integer, Integer>();
    }
    
    public void readImpl() {
        for (int count = this.readInt(), i = 0; i < count; ++i) {
            final int bossId = this.readInt();
            if (GrandBossManager.getInstance().getBossStatus(bossId) > -1) {
                if (GrandBossManager.getInstance().getBoss(bossId) != null && GrandBossManager.getInstance().getBoss(bossId).getAggroList().size() > 0) {
                    this._bossIds.put(bossId, 2);
                }
                else {
                    this._bossIds.put(bossId, GrandBossManager.getInstance().getBossStatus(bossId));
                }
            }
            else if (DBSpawnManager.getInstance().isDefined(bossId) && DBSpawnManager.getInstance().getNpcs().get(bossId) != null && ((Attackable)DBSpawnManager.getInstance().getNpcs().get(bossId)).getAggroList().size() > 0) {
                this._bossIds.put(bossId, 2);
            }
            else {
                this._bossIds.put(bossId, (DBSpawnManager.getInstance().getNpcStatusId(bossId) == RaidBossStatus.ALIVE) ? 1 : 0);
            }
        }
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(new ExRaidBossSpawnInfo(this._bossIds));
    }
}
