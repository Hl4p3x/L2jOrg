// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.function.BiConsumer;
import java.util.List;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.instancezone.Instance;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public abstract class AbstractInstance extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private final int[] _templateIds;
    
    public AbstractInstance(final int... templateId) {
        if (templateId.length == 0) {
            throw new IllegalStateException("No template ids were provided!");
        }
        this._templateIds = templateId;
    }
    
    public int[] getTemplateId() {
        return this._templateIds;
    }
    
    public boolean isInInstance(final Instance instance) {
        return instance != null && Util.contains(this._templateIds, instance.getTemplateId());
    }
    
    public Instance getPlayerInstance(final Player player) {
        return InstanceManager.getInstance().getPlayerInstance(player, false);
    }
    
    public void showOnScreenMsg(final Instance instance, final NpcStringId npcStringId, final int position, final int time, final String... params) {
        instance.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(npcStringId, position, time, params) });
    }
    
    public void showOnScreenMsg(final Instance instance, final NpcStringId npcStringId, final int position, final int time, final boolean showEffect, final String... params) {
        instance.broadcastPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(npcStringId, position, time, showEffect, params) });
    }
    
    protected final void enterInstance(final Player player, final Npc npc, final int templateId) {
        Instance instance = this.getPlayerInstance(player);
        if (instance != null) {
            if (instance.getTemplateId() != templateId) {
                player.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_INSTANT_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER);
                return;
            }
            this.onEnter(player, instance, false);
        }
        else {
            final InstanceManager manager = InstanceManager.getInstance();
            final InstanceTemplate template = manager.getInstanceTemplate(templateId);
            if (template == null) {
                AbstractInstance.LOGGER.warn("Player {} wants to create instance with unknown template id {} !", (Object)player, (Object)templateId);
                return;
            }
            final List<Player> enterGroup = (List<Player>)template.getEnterGroup(player);
            if (enterGroup == null) {
                AbstractInstance.LOGGER.warn("Instance {} has invalid group size limits!", (Object)template);
                return;
            }
            if (!player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS) && (!template.validateConditions((List)enterGroup, npc, (BiConsumer)this::showHtmlFile) || !this.validateConditions(enterGroup, npc, template))) {
                return;
            }
            if (template.getMaxWorlds() != -1 && manager.getWorldCount(templateId) >= template.getMaxWorlds()) {
                player.sendPacket(SystemMessageId.THE_NUMBER_OF_INSTANT_ZONES_THAT_CAN_BE_CREATED_HAS_BEEN_EXCEEDED_PLEASE_TRY_AGAIN_LATER);
                return;
            }
            for (final Player member : enterGroup) {
                if (this.getPlayerInstance(member) != null) {
                    enterGroup.forEach(p -> p.sendPacket(SystemMessageId.THE_MAXIMUM_NUMBER_OF_INSTANT_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER));
                    return;
                }
                if (InstanceManager.getInstance().getInstanceTime(member, templateId) > 0L) {
                    enterGroup.forEach(p -> p.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET).addString(member.getName()) }));
                    return;
                }
            }
            instance = manager.createInstance(template, player);
            for (final Player member : enterGroup) {
                instance.addAllowed(member);
                this.onEnter(member, instance, true);
            }
            template.applyConditionEffects((List)enterGroup);
            if (instance.getReenterType() == InstanceReenterType.ON_ENTER) {
                instance.setReenterTime();
            }
        }
    }
    
    protected void onEnter(final Player player, final Instance instance, final boolean firstEnter) {
        this.teleportPlayerIn(player, instance);
    }
    
    protected void teleportPlayerIn(final Player player, final Instance instance) {
        final Location loc = instance.getEnterLocation();
        if (loc != null) {
            player.teleToLocation((ILocational)loc, instance);
        }
        else {
            AbstractInstance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, instance.getId()));
        }
    }
    
    protected void teleportPlayerOut(final Player player, final Instance instance) {
        instance.ejectPlayer(player);
    }
    
    protected void finishInstance(final Player player) {
        final Instance inst = player.getInstanceWorld();
        if (inst != null) {
            inst.finishInstance();
        }
    }
    
    protected void finishInstance(final Player player, final int delay) {
        final Instance inst = player.getInstanceWorld();
        if (inst != null) {
            inst.finishInstance(delay);
        }
    }
    
    protected boolean validateConditions(final List<Player> group, final Npc npc, final InstanceTemplate template) {
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractInstance.class);
    }
}
