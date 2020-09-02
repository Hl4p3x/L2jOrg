// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.actor.Creature;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class Attachment extends ItemContainer
{
    private final int _ownerId;
    private int mailId;
    
    public Attachment(final int objectId, final int mailId) {
        this._ownerId = objectId;
        this.mailId = mailId;
    }
    
    @Override
    public String getName() {
        return "Mail";
    }
    
    @Override
    public Player getOwner() {
        return null;
    }
    
    public ItemLocation getBaseLocation() {
        return ItemLocation.MAIL;
    }
    
    public void setNewMailId(final int mailId) {
        this.mailId = mailId;
        for (final Item item : this.items.values()) {
            item.setItemLocation(this.getBaseLocation(), mailId);
        }
        this.updateDatabase();
    }
    
    public void returnToWh(final ItemContainer wh) {
        for (final Item item : this.items.values()) {
            if (wh == null) {
                item.setItemLocation(ItemLocation.WAREHOUSE);
            }
            else {
                this.transferItem("Expire", item.getObjectId(), item.getCount(), wh, null, null);
            }
        }
    }
    
    @Override
    protected void addItem(final Item item) {
        super.addItem(item);
        item.setItemLocation(this.getBaseLocation(), this.mailId);
        item.updateDatabase(true);
    }
    
    @Override
    public void updateDatabase() {
        for (final Item item : this.items.values()) {
            item.updateDatabase(true);
        }
    }
    
    @Override
    public void restore() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND loc=? AND loc_data=?");
                try {
                    statement.setInt(1, this._ownerId);
                    statement.setString(2, this.getBaseLocation().name());
                    statement.setInt(3, this.mailId);
                    final ResultSet inv = statement.executeQuery();
                    try {
                        while (inv.next()) {
                            final Item item = new Item(inv);
                            World.getInstance().addObject(item);
                            if (item.isStackable() && this.getItemByItemId(item.getId()) != null) {
                                this.addItem("Restore", item, null, null);
                            }
                            else {
                                this.addItem(item);
                            }
                        }
                        if (inv != null) {
                            inv.close();
                        }
                    }
                    catch (Throwable t) {
                        if (inv != null) {
                            try {
                                inv.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Attachment.LOGGER.warn("could not restore container:", (Throwable)e);
        }
    }
    
    @Override
    public void deleteMe() {
        for (final Item item : this.items.values()) {
            item.updateDatabase(true);
            item.deleteMe();
            World.getInstance().removeObject(item);
        }
        this.items.clear();
    }
    
    @Override
    public int getOwnerId() {
        return this._ownerId;
    }
}
