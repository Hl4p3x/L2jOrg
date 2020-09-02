// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.model.holders.MinionHolder;
import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.SkillHolder;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import java.io.File;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.commons.xml.XmlReader;

public abstract class GameXmlReader extends XmlReader
{
    protected void parseDatapackFile(final String path) {
        this.parseFile(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve(path).toFile());
    }
    
    protected boolean parseDatapackDirectory(final String path, final boolean recursive) {
        return this.parseDirectory(new File(Config.DATAPACK_ROOT, path), recursive);
    }
    
    protected Map<String, Object> parseParameters(final Node n) {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        for (Node parameters_node = n.getFirstChild(); parameters_node != null; parameters_node = parameters_node.getNextSibling()) {
            NamedNodeMap attrs = parameters_node.getAttributes();
            final String lowerCase = parameters_node.getNodeName().toLowerCase();
            switch (lowerCase) {
                case "param": {
                    parameters.put(this.parseString(attrs, "name"), this.parseString(attrs, "value"));
                    break;
                }
                case "skill": {
                    parameters.put(this.parseString(attrs, "name"), new SkillHolder(this.parseInt(attrs, "id"), this.parseInt(attrs, "level")));
                    break;
                }
                case "location": {
                    parameters.put(this.parseString(attrs, "name"), new Location(this.parseInt(attrs, "x"), this.parseInt(attrs, "y"), this.parseInt(attrs, "z"), this.parseInt(attrs, "heading", 0)));
                    break;
                }
                case "minions": {
                    final List<MinionHolder> minions = new ArrayList<MinionHolder>(1);
                    for (Node minions_node = parameters_node.getFirstChild(); minions_node != null; minions_node = minions_node.getNextSibling()) {
                        if (minions_node.getNodeName().equalsIgnoreCase("npc")) {
                            attrs = minions_node.getAttributes();
                            minions.add(new MinionHolder(this.parseInt(attrs, "id"), this.parseInt(attrs, "count"), this.parseInt(attrs, "respawnTime"), this.parseInt(attrs, "weightPoint")));
                        }
                    }
                    if (!minions.isEmpty()) {
                        parameters.put(this.parseString(parameters_node.getAttributes(), "name"), minions);
                        break;
                    }
                    break;
                }
            }
        }
        return parameters;
    }
    
    public Location parseLocation(final Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final int x = this.parseInt(attrs, "x");
        final int y = this.parseInt(attrs, "y");
        final int z = this.parseInt(attrs, "z");
        final int heading = this.parseInt(attrs, "heading", 0);
        return new Location(x, y, z, heading);
    }
    
    public ItemHolder parseItemHolder(final Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        return new ItemHolder(this.parseInt(attrs, "id"), this.parseLong(attrs, "count"), this.parseInt(attrs, "enchant", 0));
    }
}
