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
                    parameters.put(this.parseString(attrs, "name"), new SkillHolder(this.parseInteger(attrs, "id"), this.parseInteger(attrs, "level")));
                    break;
                }
                case "location": {
                    parameters.put(this.parseString(attrs, "name"), new Location(this.parseInteger(attrs, "x"), this.parseInteger(attrs, "y"), this.parseInteger(attrs, "z"), this.parseInteger(attrs, "heading", Integer.valueOf(0))));
                    break;
                }
                case "minions": {
                    final List<MinionHolder> minions = new ArrayList<MinionHolder>(1);
                    for (Node minions_node = parameters_node.getFirstChild(); minions_node != null; minions_node = minions_node.getNextSibling()) {
                        if (minions_node.getNodeName().equalsIgnoreCase("npc")) {
                            attrs = minions_node.getAttributes();
                            minions.add(new MinionHolder(this.parseInteger(attrs, "id"), this.parseInteger(attrs, "count"), this.parseInteger(attrs, "respawnTime"), this.parseInteger(attrs, "weightPoint")));
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
    
    protected Location parseLocation(final Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final int x = this.parseInteger(attrs, "x");
        final int y = this.parseInteger(attrs, "y");
        final int z = this.parseInteger(attrs, "z");
        final int heading = this.parseInteger(attrs, "heading", Integer.valueOf(0));
        return new Location(x, y, z, heading);
    }
    
    protected ItemHolder parseItemHolder(final Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        return new ItemHolder(this.parseInt(attrs, "id"), this.parselong(attrs, "count"), this.parseInt(attrs, "enchant", 0));
    }
}
