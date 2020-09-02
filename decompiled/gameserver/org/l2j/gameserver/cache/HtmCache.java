// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.cache;

import org.l2j.commons.cache.CacheFactory;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import java.nio.file.Path;
import java.nio.file.Files;
import org.l2j.commons.util.FilterUtil;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import javax.cache.Cache;
import java.util.regex.Pattern;
import org.slf4j.Logger;

public class HtmCache
{
    private static final Logger LOGGER;
    private static final Pattern EXTEND_PATTERN;
    private static final Pattern ABSTRACT_BLOCK_PATTERN;
    private static final Pattern BLOCK_PATTERN;
    private static final Cache<String, String> CACHE;
    
    private HtmCache() {
        this.reload();
    }
    
    public void reload() {
        HtmCache.CACHE.clear();
        HtmCache.LOGGER.info("Cache[HTML]: Running lazy cache");
    }
    
    public boolean purge(final String path) {
        return HtmCache.CACHE.remove((Object)path);
    }
    
    public String loadFile(final String filePath) {
        final Path path = ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve(filePath);
        if (FilterUtil.htmlFile(path)) {
            try {
                String content = this.processHtml(Files.readString(path));
                content = content.replaceAll("(?s)<!--.*?-->", "").replaceAll("[\r\n\t]", "");
                HtmCache.CACHE.put((Object)filePath, (Object)content);
                return content;
            }
            catch (Exception e) {
                HtmCache.LOGGER.warn("Problem with htm file:", (Throwable)e);
            }
        }
        return null;
    }
    
    public String getHtmForce(final Player player, final String path) {
        String content = this.getHtm(player, path);
        if (content == null) {
            content = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path);
            HtmCache.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path));
        }
        return content;
    }
    
    public String getHtm(final Player player, final String path) {
        final String content = this.getHtm(path);
        if (content != null && !this.contains(path)) {
            HtmCache.CACHE.put((Object)path, (Object)content);
        }
        if (player != null && player.isGM() && path != null && Config.GM_DEBUG_HTML_PATHS) {
            BuilderUtil.sendHtmlMessage(player, path.substring(5));
        }
        return content;
    }
    
    private String getHtm(final String path) {
        return (String)(Util.isNullOrEmpty((CharSequence)path) ? "" : (HtmCache.CACHE.containsKey((Object)path) ? HtmCache.CACHE.get((Object)path) : this.loadFile(path)));
    }
    
    public boolean contains(final String path) {
        return HtmCache.CACHE.containsKey((Object)path);
    }
    
    public boolean isLoadable(final String path) {
        return FilterUtil.htmlFile(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve(path));
    }
    
    private String parseTemplateName(final String name) {
        if (!name.startsWith("data/")) {
            if (name.startsWith("html/")) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
            }
            if (name.startsWith("CommunityBoard/")) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
            }
            if (name.startsWith("scripts/")) {
                return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name);
            }
        }
        return name;
    }
    
    private String processHtml(String result) {
        final Matcher extendMatcher = HtmCache.EXTEND_PATTERN.matcher(result);
        if (extendMatcher.find()) {
            final String templateName = this.parseTemplateName(extendMatcher.group(1));
            final Map<String, String> blockMap = this.generateBlockMap(result);
            String template = this.getHtm(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, templateName));
            if (template != null) {
                final Matcher blockMatcher = HtmCache.ABSTRACT_BLOCK_PATTERN.matcher(template);
                while (blockMatcher.find()) {
                    final String name = blockMatcher.group(1);
                    if (!blockMap.containsKey(name)) {
                        HtmCache.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
                    }
                    else {
                        template = template.replace(blockMatcher.group(0), blockMap.get(name));
                    }
                }
                result = result.replace(extendMatcher.group(0), template);
            }
            else {
                HtmCache.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, templateName));
            }
        }
        return result;
    }
    
    private Map<String, String> generateBlockMap(final String data) {
        final Map<String, String> blockMap = new LinkedHashMap<String, String>();
        final Matcher blockMatcher = HtmCache.BLOCK_PATTERN.matcher(data);
        while (blockMatcher.find()) {
            final String name = blockMatcher.group(1);
            final String content = blockMatcher.group(2);
            blockMap.put(name, content);
        }
        return blockMap;
    }
    
    public static HtmCache getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HtmCache.class);
        EXTEND_PATTERN = Pattern.compile("<extend template=\"([a-zA-Z0-9-_./ ]*)\">(.*?)</extend>", 32);
        ABSTRACT_BLOCK_PATTERN = Pattern.compile("<abstract block=\"([a-zA-Z0-9-_. ]*)\" ?/>", 32);
        BLOCK_PATTERN = Pattern.compile("<block name=\"([a-zA-Z0-9-_. ]*)\">(.*?)</block>", 32);
        CACHE = CacheFactory.getInstance().getCache("html", (Class)String.class, (Class)String.class);
    }
    
    private static class Singleton
    {
        private static final HtmCache INSTANCE;
        
        static {
            INSTANCE = new HtmCache();
        }
    }
}
