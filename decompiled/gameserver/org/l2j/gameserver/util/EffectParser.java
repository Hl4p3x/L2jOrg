// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import java.util.Iterator;
import io.github.joealisson.primitive.HashIntMap;
import org.w3c.dom.NamedNodeMap;
import java.util.Map;
import java.util.Objects;
import org.l2j.gameserver.model.StatsSet;
import io.github.joealisson.primitive.IntMap;
import org.w3c.dom.Node;

public abstract class EffectParser extends GameXmlReader
{
    protected void parseEffectNode(final Node node, final IntMap<StatsSet> levelInfo, final int startLevel, final StatsSet staticStatSet, final boolean forceLevel) {
        final NamedNodeMap attr = node.getAttributes();
        if (Objects.nonNull(attr) && Objects.nonNull(attr.getNamedItem("initial"))) {
            ((StatsSet)levelInfo.computeIfAbsent(startLevel, l -> new StatsSet())).set(node.getNodeName(), this.parseString(attr, "initial"));
            for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
                this.parseEffectNode(child, levelInfo, startLevel, staticStatSet, forceLevel);
            }
        }
        else if ("value".equals(node.getNodeName())) {
            final int level = this.parseInt(node.getAttributes(), "level");
            final String attributeName = node.getParentNode().getNodeName();
            ((StatsSet)levelInfo.computeIfAbsent(level, l -> new StatsSet())).set(attributeName, node.getTextContent());
            if (level > startLevel && !((StatsSet)levelInfo.getOrDefault(level - 1, (Object)StatsSet.EMPTY_STATSET)).contains(attributeName)) {
                for (int previous = level - 1; previous >= startLevel; --previous) {
                    if (((StatsSet)levelInfo.getOrDefault(previous, (Object)StatsSet.EMPTY_STATSET)).contains(attributeName)) {
                        final String value = ((StatsSet)levelInfo.get(previous)).getString(attributeName);
                        for (int i = previous + 1; i < level; ++i) {
                            ((StatsSet)levelInfo.computeIfAbsent(i, l -> new StatsSet())).set(attributeName, value);
                        }
                        break;
                    }
                }
            }
        }
        else if (Objects.nonNull(attr) && Objects.nonNull(attr.getNamedItem("level"))) {
            final int level = this.parseInt(attr, "level");
            ((StatsSet)levelInfo.computeIfAbsent(level, l -> new StatsSet())).merge(this.parseAttributes(node));
            if (node.hasChildNodes()) {
                for (Node child2 = node.getFirstChild(); Objects.nonNull(child2); child2 = child2.getNextSibling()) {
                    this.parseEffectNode(child2, levelInfo, level, staticStatSet, true);
                }
            }
            else {
                ((StatsSet)levelInfo.computeIfAbsent(level, l -> new StatsSet())).set(node.getNodeName(), node.getTextContent());
            }
        }
        else {
            if (node.hasAttributes()) {
                final Map<String, Object> parsedAttr = (Map<String, Object>)this.parseAttributes(node);
                if (forceLevel) {
                    ((StatsSet)levelInfo.computeIfAbsent(startLevel, l -> new StatsSet())).merge(parsedAttr);
                }
                else {
                    staticStatSet.merge(parsedAttr);
                }
            }
            if (node.hasChildNodes()) {
                for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
                    if ("#text".equals(child.getNodeName())) {
                        if (forceLevel) {
                            ((StatsSet)levelInfo.computeIfAbsent(startLevel, l -> new StatsSet())).set(node.getNodeName(), child.getNodeValue());
                        }
                        else {
                            staticStatSet.set(node.getNodeName(), child.getNodeValue());
                        }
                    }
                    else {
                        this.parseEffectNode(child, levelInfo, startLevel, staticStatSet, forceLevel);
                    }
                }
            }
            else if (forceLevel) {
                ((StatsSet)levelInfo.computeIfAbsent(startLevel, l -> new StatsSet())).set(node.getNodeName(), node.getTextContent());
            }
            else {
                staticStatSet.set(node.getNodeName(), node.getTextContent());
            }
        }
    }
    
    protected IntMap<StatsSet> parseEffectChildNodes(final Node node, final int startLevel, final int stopLevel, final StatsSet staticStatSet) {
        final IntMap<StatsSet> levelInfo = (IntMap<StatsSet>)new HashIntMap();
        for (Node child = node.getFirstChild(); Objects.nonNull(child); child = child.getNextSibling()) {
            if (this.isUnboundNode(child)) {
                this.parseNodeList(startLevel, stopLevel, staticStatSet, levelInfo, child);
            }
            else {
                this.parseEffectNode(child, levelInfo, startLevel, staticStatSet, false);
                if (node.getChildNodes().getLength() > 1 && !((StatsSet)levelInfo.getOrDefault(stopLevel, (Object)StatsSet.EMPTY_STATSET)).contains(child.getNodeName()) && ((StatsSet)levelInfo.getOrDefault(startLevel, (Object)StatsSet.EMPTY_STATSET)).contains(child.getNodeName())) {
                    for (int previous = stopLevel - 1; previous >= startLevel; --previous) {
                        if (((StatsSet)levelInfo.getOrDefault(previous, (Object)StatsSet.EMPTY_STATSET)).contains(child.getNodeName())) {
                            final String value = ((StatsSet)levelInfo.get(previous)).getString(child.getNodeName());
                            for (int i = previous + 1; i <= stopLevel; ++i) {
                                ((StatsSet)levelInfo.computeIfAbsent(i, l -> new StatsSet())).set(child.getNodeName(), value);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return levelInfo;
    }
    
    protected void parseNodeList(final int startLevel, final int stopLevel, final StatsSet staticStatSet, final IntMap<StatsSet> levelInfo, final Node child) {
        final StatsSet childStats = new StatsSet(this.parseAttributes(child));
        final boolean forceLevel = childStats.contains("level");
        final int childLevel = childStats.getInt("level", startLevel);
        final String childKey = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, child.getNodeName(), child.hashCode());
        if (child.hasChildNodes()) {
            final IntMap<StatsSet> childLevelInfo = (IntMap<StatsSet>)new HashIntMap();
            for (Node n = child.getFirstChild(); Objects.nonNull(n); n = n.getNextSibling()) {
                this.parseEffectNode(n, childLevelInfo, childLevel, childStats, false);
            }
            if (!childLevelInfo.isEmpty()) {
                for (final IntMap.Entry<StatsSet> entry : childLevelInfo.entrySet()) {
                    final StatsSet stats = (StatsSet)entry.getValue();
                    final int level = entry.getKey();
                    stats.merge(childStats);
                    ((StatsSet)levelInfo.computeIfAbsent(level, i -> new StatsSet())).set(childKey, stats);
                }
                if (childLevelInfo.size() < stopLevel) {
                    final int levelBase = childLevelInfo.keySet().stream().max().orElse(0);
                    for (int i = levelBase + 1; i <= stopLevel; ++i) {
                        ((StatsSet)levelInfo.computeIfAbsent(i, level -> new StatsSet())).set(childKey, childLevelInfo.get(levelBase));
                    }
                }
            }
            else if (forceLevel) {
                ((StatsSet)levelInfo.computeIfAbsent(childLevel, i -> new StatsSet())).set(childKey, childStats);
            }
            else {
                staticStatSet.set(childKey, childStats);
            }
        }
        else if (forceLevel) {
            ((StatsSet)levelInfo.computeIfAbsent(childLevel, i -> new StatsSet())).set(childKey, childStats);
        }
        else {
            staticStatSet.set(childKey, childStats);
        }
    }
    
    protected boolean isUnboundNode(final Node child) {
        return (Objects.nonNull(child.getNextSibling()) && child.getNextSibling().getNodeName().equals(child.getNodeName())) || (Objects.nonNull(child.getPreviousSibling()) && !child.getPreviousSibling().equals(child) && child.getPreviousSibling().getNodeName().equals(child.getNodeName()));
    }
}
