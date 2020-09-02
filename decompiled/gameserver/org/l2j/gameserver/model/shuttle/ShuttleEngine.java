// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.shuttle;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.slf4j.Logger;

public class ShuttleEngine implements Runnable
{
    private static final Logger LOGGER;
    private static final int DELAY = 15000;
    private final Shuttle _shuttle;
    private final Door _door1;
    private final Door _door2;
    private int _cycle;
    
    public ShuttleEngine(final ShuttleData data, final Shuttle shuttle) {
        this._cycle = 0;
        this._shuttle = shuttle;
        this._door1 = DoorDataManager.getInstance().getDoor(data.getDoors().get(0));
        this._door2 = DoorDataManager.getInstance().getDoor(data.getDoors().get(1));
    }
    
    @Override
    public void run() {
        try {
            if (!this._shuttle.isSpawned()) {
                return;
            }
            switch (this._cycle) {
                case 0: {
                    this._door1.openMe();
                    this._door2.closeMe();
                    this._shuttle.openDoor(0);
                    this._shuttle.closeDoor(1);
                    this._shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule((Runnable)this, 15000L);
                    break;
                }
                case 1: {
                    this._door1.closeMe();
                    this._door2.closeMe();
                    this._shuttle.closeDoor(0);
                    this._shuttle.closeDoor(1);
                    this._shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule((Runnable)this, 1000L);
                    break;
                }
                case 2: {
                    this._shuttle.executePath(this._shuttle.getShuttleData().getRoutes().get(0));
                    break;
                }
                case 3: {
                    this._door1.closeMe();
                    this._door2.openMe();
                    this._shuttle.openDoor(1);
                    this._shuttle.closeDoor(0);
                    this._shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule((Runnable)this, 15000L);
                    break;
                }
                case 4: {
                    this._door1.closeMe();
                    this._door2.closeMe();
                    this._shuttle.closeDoor(0);
                    this._shuttle.closeDoor(1);
                    this._shuttle.broadcastShuttleInfo();
                    ThreadPool.schedule((Runnable)this, 1000L);
                    break;
                }
                case 5: {
                    this._shuttle.executePath(this._shuttle.getShuttleData().getRoutes().get(1));
                    break;
                }
            }
            ++this._cycle;
            if (this._cycle > 5) {
                this._cycle = 0;
            }
        }
        catch (Exception e) {
            ShuttleEngine.LOGGER.info(e.getMessage(), (Throwable)e);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ShuttleEngine.class);
    }
}
