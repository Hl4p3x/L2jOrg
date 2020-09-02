// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.idfactory.IdFactory;

public abstract class ZoneArea
{
    protected static final int STEP = 10;
    
    public abstract boolean isInsideZone(final int x, final int y, final int z);
    
    public abstract boolean intersectsRectangle(final int x1, final int x2, final int y1, final int y2);
    
    public abstract double getDistanceToZone(final int x, final int y);
    
    public abstract int getLowZ();
    
    public abstract int getHighZ();
    
    public abstract void visualizeZone(final int z);
    
    protected final void dropDebugItem(final int itemId, final int num, final int x, final int y, final int z) {
        final Item item = new Item(IdFactory.getInstance().getNextId(), itemId);
        item.setCount(num);
        item.spawnMe(x, y, z + 5);
        ZoneManager.getInstance().getDebugItems().add(item);
    }
    
    public abstract Location getRandomPoint();
}
