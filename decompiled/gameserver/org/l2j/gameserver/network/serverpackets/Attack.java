// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.Hit;
import java.util.List;
import org.l2j.gameserver.model.Location;

public class Attack extends ServerPacket
{
    private final int attackerObjId;
    private final Location attackerLoc;
    private final Location targetLoc;
    private final List<Hit> hits;
    private final int additionalSoulshot;
    private int hitsWithShots;
    
    public Attack(final Creature attacker, final Creature target) {
        this.hits = new ArrayList<Hit>();
        this.hitsWithShots = 0;
        this.attackerObjId = attacker.getObjectId();
        this.attackerLoc = attacker.getLocation();
        this.targetLoc = target.getLocation();
        this.additionalSoulshot = Util.zeroIfNullOrElse((Object)attacker.getActingPlayer(), Player::getAdditionalSoulshot);
    }
    
    public void addHit(final Hit hit) {
        this.hits.add(hit);
        if (hit.isShotUsed()) {
            ++this.hitsWithShots;
        }
    }
    
    public List<Hit> getHits() {
        return this.hits;
    }
    
    public boolean hasHits() {
        return !this.hits.isEmpty();
    }
    
    public boolean isShotUsed() {
        return this.hits.stream().anyMatch(Hit::isShotUsed);
    }
    
    public int getHitsWithSoulshotCount() {
        return this.hitsWithShots;
    }
    
    public void writeImpl(final GameClient client) {
        final Iterator<Hit> it = this.hits.iterator();
        final Hit firstHit = it.next();
        this.writeId(ServerPacketId.ATTACK);
        this.writeInt(this.attackerObjId);
        this.writeInt(firstHit.getTargetId());
        this.writeInt(this.additionalSoulshot);
        this.writeInt(firstHit.getDamage());
        this.writeInt(firstHit.getFlags());
        this.writeInt(firstHit.getGrade());
        this.writeInt(this.attackerLoc.getX());
        this.writeInt(this.attackerLoc.getY());
        this.writeInt(this.attackerLoc.getZ());
        this.writeShort(this.hits.size() - 1);
        while (it.hasNext()) {
            this.writeHit(it.next());
        }
        this.writeInt(this.targetLoc.getX());
        this.writeInt(this.targetLoc.getY());
        this.writeInt(this.targetLoc.getZ());
    }
    
    private void writeHit(final Hit hit) {
        this.writeInt(hit.getTargetId());
        this.writeInt(hit.getDamage());
        this.writeInt(hit.getFlags());
        this.writeInt(hit.getGrade());
    }
}
