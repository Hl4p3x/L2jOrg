// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.io.File;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class SecondaryAuthManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntSet forbiddenPasswords;
    private boolean enabled;
    private int maxAttempts;
    private int banTime;
    private String recoveryLink;
    
    private SecondaryAuthManager() {
        this.forbiddenPasswords = (IntSet)new HashIntSet();
        this.enabled = false;
        this.maxAttempts = 5;
        this.banTime = 480;
        this.recoveryLink = "";
    }
    
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/secondary-auth.xsd", new String[0]);
    }
    
    public synchronized void load() {
        this.forbiddenPasswords.clear();
        this.parseFile(new File("config/secondary-auth.xml"));
        SecondaryAuthManager.LOGGER.info("Loaded {} forbidden passwords.", (Object)this.forbiddenPasswords.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attrs;
        final Object enabled;
        this.forEach((Node)doc, "secondary-auth", node -> {
            attrs = node.getAttributes();
            this.parseBoolean(attrs, "enabled");
            if (!(!(this.enabled = (boolean)enabled))) {
                this.maxAttempts = this.parseInteger(attrs, "max-attempts");
                this.banTime = this.parseInteger(attrs, "ban-time");
                this.recoveryLink = this.parseString(attrs, "recovery-link");
                this.forEach(node, "forbidden-passwords", forbiddenList -> this.forEach(forbiddenList, "password", pwdNode -> this.forbiddenPasswords.add(Integer.parseInt(pwdNode.getTextContent()))));
            }
        });
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public int getMaxAttempts() {
        return this.maxAttempts;
    }
    
    public boolean isForbiddenPassword(final int password) {
        return this.forbiddenPasswords.contains(password);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static SecondaryAuthManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(SecondaryAuthManager.class.getName());
    }
    
    private static class Singleton
    {
        private static final SecondaryAuthManager INSTANCE;
        
        static {
            INSTANCE = new SecondaryAuthManager();
        }
    }
}
