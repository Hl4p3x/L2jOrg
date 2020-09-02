// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.shuttle.ShuttleEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.VehiclePathPoint;
import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import java.util.Iterator;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ShuttleData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<Integer, org.l2j.gameserver.model.shuttle.ShuttleData> _shuttles;
    private final Map<Integer, Shuttle> _shuttleInstances;
    
    private ShuttleData() {
        this._shuttles = new HashMap<Integer, org.l2j.gameserver.model.shuttle.ShuttleData>();
        this._shuttleInstances = new HashMap<Integer, Shuttle>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/ShuttleData.xsd");
    }
    
    public synchronized void load() {
        if (!this._shuttleInstances.isEmpty()) {
            for (final Shuttle shuttle : this._shuttleInstances.values()) {
                shuttle.deleteMe();
            }
            this._shuttleInstances.clear();
        }
        this.parseDatapackFile("data/ShuttleData.xml");
        this.init();
        ShuttleData.LOGGER.info("Loaded: {} Shuttles.", (Object)this._shuttles.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("shuttle".equalsIgnoreCase(d.getNodeName())) {
                        NamedNodeMap attrs = d.getAttributes();
                        final StatsSet set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); ++i) {
                            final Node att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }
                        final org.l2j.gameserver.model.shuttle.ShuttleData data = new org.l2j.gameserver.model.shuttle.ShuttleData(set);
                        for (Node b = d.getFirstChild(); b != null; b = b.getNextSibling()) {
                            if ("doors".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("door".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        data.addDoor(this.parseInteger(attrs, "id"));
                                    }
                                }
                            }
                            else if ("stops".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("stop".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        final ShuttleStop stop = new ShuttleStop(this.parseInteger(attrs, "id"));
                                        for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling()) {
                                            if ("dimension".equalsIgnoreCase(z.getNodeName())) {
                                                attrs = z.getAttributes();
                                                stop.addDimension(new Location(this.parseInteger(attrs, "x"), this.parseInteger(attrs, "y"), this.parseInteger(attrs, "z")));
                                            }
                                        }
                                        data.addStop(stop);
                                    }
                                }
                            }
                            else if ("routes".equalsIgnoreCase(b.getNodeName())) {
                                for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling()) {
                                    if ("route".equalsIgnoreCase(a.getNodeName())) {
                                        attrs = a.getAttributes();
                                        final List<Location> locs = new ArrayList<Location>();
                                        for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling()) {
                                            if ("loc".equalsIgnoreCase(z.getNodeName())) {
                                                attrs = z.getAttributes();
                                                locs.add(new Location(this.parseInteger(attrs, "x"), this.parseInteger(attrs, "y"), this.parseInteger(attrs, "z")));
                                            }
                                        }
                                        final VehiclePathPoint[] route = new VehiclePathPoint[locs.size()];
                                        int j = 0;
                                        for (final Location loc : locs) {
                                            route[j++] = new VehiclePathPoint(loc);
                                        }
                                        data.addRoute(route);
                                    }
                                }
                            }
                        }
                        this._shuttles.put(data.getId(), data);
                    }
                }
            }
        }
    }
    
    private void init() {
        for (final org.l2j.gameserver.model.shuttle.ShuttleData data : this._shuttles.values()) {
            final Shuttle shuttle = new Shuttle(new CreatureTemplate(new StatsSet()));
            shuttle.setData(data);
            shuttle.setHeading(data.getLocation().getHeading());
            shuttle.setLocationInvisible(data.getLocation());
            shuttle.spawnMe();
            shuttle.getStats().setMoveSpeed(300.0f);
            shuttle.getStats().setRotationSpeed(0);
            shuttle.registerEngine(new ShuttleEngine(data, shuttle));
            shuttle.runEngine(1000);
            this._shuttleInstances.put(shuttle.getObjectId(), shuttle);
        }
    }
    
    public Shuttle getShuttle(final int id) {
        for (final Shuttle shuttle : this._shuttleInstances.values()) {
            if (shuttle.getObjectId() == id || shuttle.getId() == id) {
                return shuttle;
            }
        }
        return null;
    }
    
    public static ShuttleData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ShuttleData.class);
    }
    
    private static class Singleton
    {
        private static final ShuttleData INSTANCE;
        
        static {
            INSTANCE = new ShuttleData();
        }
    }
}
