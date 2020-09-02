// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.function.BiConsumer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public abstract class Condition
{
    private final InstanceTemplate _template;
    private final StatsSet _parameters;
    private final boolean _leaderOnly;
    private final boolean _showMessageAndHtml;
    private SystemMessageId _systemMsg;
    private BiConsumer<SystemMessage, Player> _systemMsgParams;
    
    public Condition(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        this._systemMsg = null;
        this._systemMsgParams = null;
        this._template = template;
        this._parameters = parameters;
        this._leaderOnly = onlyLeader;
        this._showMessageAndHtml = showMessageAndHtml;
    }
    
    protected final StatsSet getParameters() {
        return this._parameters;
    }
    
    public InstanceTemplate getInstanceTemplate() {
        return this._template;
    }
    
    public boolean validate(final Npc npc, final List<Player> group, final BiConsumer<Player, String> htmlCallback) {
        for (final Player member : group) {
            if (!this.test(member, npc, group)) {
                this.sendMessage(group, member, htmlCallback);
                return false;
            }
            if (this._leaderOnly) {
                break;
            }
        }
        return true;
    }
    
    private void sendMessage(final List<Player> group, final Player member, final BiConsumer<Player, String> htmlCallback) {
        final String html = this._parameters.getString("html", null);
        if (html != null && htmlCallback != null) {
            htmlCallback.accept(group.get(0), html);
            if (!this._showMessageAndHtml) {
                return;
            }
        }
        final String message = this._parameters.getString("message", null);
        if (message != null) {
            if (this._leaderOnly) {
                member.sendMessage(message);
            }
            else {
                group.forEach(p -> p.sendMessage(message));
            }
            return;
        }
        if (this._systemMsg != null) {
            final SystemMessage msg = SystemMessage.getSystemMessage(this._systemMsg);
            if (this._systemMsgParams != null) {
                this._systemMsgParams.accept(msg, member);
            }
            if (this._leaderOnly) {
                member.sendPacket(msg);
            }
            else {
                group.forEach(p -> p.sendPacket(msg));
            }
        }
    }
    
    public void applyEffect(final List<Player> group) {
        for (final Player member : group) {
            this.onSuccess(member);
            if (this._leaderOnly) {
                break;
            }
        }
    }
    
    protected void setSystemMessage(final SystemMessageId msg) {
        this._systemMsg = msg;
    }
    
    protected void setSystemMessage(final SystemMessageId msg, final BiConsumer<SystemMessage, Player> params) {
        this.setSystemMessage(msg);
        this._systemMsgParams = params;
    }
    
    protected boolean test(final Player player, final Npc npc, final List<Player> group) {
        return this.test(player, npc);
    }
    
    protected boolean test(final Player player, final Npc npc) {
        return true;
    }
    
    protected void onSuccess(final Player player) {
    }
}
