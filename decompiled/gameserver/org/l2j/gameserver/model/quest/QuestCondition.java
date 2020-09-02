// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.l2j.gameserver.model.actor.Npc;
import java.util.stream.Stream;
import java.util.HashMap;
import org.l2j.gameserver.model.KeyValuePair;
import java.util.Map;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Predicate;

public class QuestCondition
{
    private final Predicate<Player> _condition;
    private final String _html;
    private Map<Integer, String> _perNpcDialog;
    
    public QuestCondition(final Predicate<Player> cond, final String html) {
        this._condition = cond;
        this._html = html;
    }
    
    @SafeVarargs
    public QuestCondition(final Predicate<Player> cond, final KeyValuePair<Integer, String>... pairs) {
        this._condition = cond;
        this._html = null;
        this._perNpcDialog = new HashMap<Integer, String>();
        Stream.of(pairs).forEach(pair -> this._perNpcDialog.put(pair.getKey(), (String)pair.getValue()));
    }
    
    public boolean test(final Player player) {
        return this._condition.test(player);
    }
    
    public String getHtml(final Npc npc) {
        return (this._perNpcDialog != null) ? this._perNpcDialog.get(npc.getId()) : this._html;
    }
}
