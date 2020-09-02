// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class FeedableBeast extends Monster
{
    public FeedableBeast(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2FeedableBeastInstance);
    }
}
