// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AgitDecoInfo;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.residences.AbstractResidence;

public class ResidenceFunctionData
{
    private int id;
    private int level;
    private long expiration;
    private AbstractResidence residence;
    private ScheduledFuture<?> _task;
    
    public ResidenceFunctionData() {
    }
    
    public ResidenceFunctionData(final int id, final int level, final long expiration, final AbstractResidence residence) {
        this.id = id;
        this.level = level;
        this.expiration = expiration;
        this.residence = residence;
        this.init();
    }
    
    public ResidenceFunctionData(final int id, final int level, final AbstractResidence residence) {
        this.id = id;
        this.level = level;
        final ResidenceFunctionTemplate template = this.getTemplate();
        this.expiration = Instant.now().toEpochMilli() + template.getDuration().toMillis();
        this.residence = residence;
        this.init();
    }
    
    private void init() {
        final ResidenceFunctionTemplate template = this.getTemplate();
        if (template != null && this.expiration > System.currentTimeMillis()) {
            this._task = (ScheduledFuture<?>)ThreadPool.schedule(this::onFunctionExpiration, this.expiration - System.currentTimeMillis());
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public long getExpiration() {
        return this.expiration;
    }
    
    public int getOwnerId() {
        return this.residence.getOwnerId();
    }
    
    public double getValue() {
        final ResidenceFunctionTemplate template = this.getTemplate();
        return (template == null) ? 0.0 : template.getValue();
    }
    
    public ResidenceFunctionType getType() {
        final ResidenceFunctionTemplate template = this.getTemplate();
        return (template == null) ? ResidenceFunctionType.NONE : template.getType();
    }
    
    public ResidenceFunctionTemplate getTemplate() {
        return ResidenceFunctionsData.getInstance().getFunction(this.id, this.level);
    }
    
    private void onFunctionExpiration() {
        if (!this.reactivate()) {
            this.residence.removeFunction(this);
            final Clan clan = ClanTable.getInstance().getClan(this.residence.getOwnerId());
            if (clan != null) {
                clan.broadcastToOnlineMembers(new AgitDecoInfo(this.residence));
            }
        }
    }
    
    public boolean reactivate() {
        final ResidenceFunctionTemplate template = this.getTemplate();
        if (template == null) {
            return false;
        }
        final Clan clan = ClanTable.getInstance().getClan(this.residence.getOwnerId());
        if (clan == null) {
            return false;
        }
        final ItemContainer wh = clan.getWarehouse();
        final Item item = wh.getItemByItemId(template.getCost().getId());
        if (item == null || item.getCount() < template.getCost().getCount()) {
            return false;
        }
        if (wh.destroyItem("FunctionFee", item, template.getCost().getCount(), null, this) != null) {
            this.expiration = System.currentTimeMillis() + template.getDuration().getSeconds() * 1000L;
            this.init();
        }
        return true;
    }
    
    public void cancelExpiration() {
        if (this._task != null && !this._task.isDone()) {
            this._task.cancel(true);
        }
        this._task = null;
    }
    
    public void initResidence(final AbstractResidence residence) {
        this.residence = residence;
        if (Objects.isNull(this._task)) {
            this.init();
        }
    }
}
