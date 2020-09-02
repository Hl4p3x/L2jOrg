// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.database.data.BuyListInfo;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.w3c.dom.NamedNodeMap;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.model.buylist.Product;
import java.util.Objects;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.BuyListDAO;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.buylist.ProductList;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class BuyListData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<ProductList> buyLists;
    
    private BuyListData() {
        this.buyLists = (IntMap<ProductList>)new HashIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/buylist.xsd");
    }
    
    public synchronized void load() {
        this.buyLists.clear();
        this.parseDatapackDirectory("data/buylists", false);
        if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).loadCustomBuyList()) {
            this.parseDatapackDirectory("data/buylists/custom", false);
        }
        this.releaseResources();
        BuyListData.LOGGER.info("Loaded {} BuyLists.", (Object)this.buyLists.size());
        final ProductList list;
        Product product;
        ((BuyListDAO)DatabaseAccess.getDAO((Class)BuyListDAO.class)).findAll().forEach(info -> {
            list = this.getBuyList(info.getId());
            if (Objects.isNull(list)) {
                BuyListData.LOGGER.warn("BuyList found in database but not loaded from xml! BuyListId: {}", (Object)info.getId());
            }
            else {
                product = list.getProductByItemId(info.getItemId());
                if (Objects.isNull(product)) {
                    BuyListData.LOGGER.warn("ItemId found in database but not loaded from xml! BuyListId: {} item id {}", (Object)info.getId(), (Object)info.getItemId());
                }
                else {
                    product.updateInfo(info);
                }
            }
        });
    }
    
    public void parseDocument(final Document doc, final File f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   java/io/File.getName:()Ljava/lang/String;
        //     4: ldc             ".xml"
        //     6: ldc             ""
        //     8: invokevirtual   java/lang/String.replaceAll:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //    11: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //    14: istore_3        /* buyListId */
        //    15: aload_0         /* this */
        //    16: aload_1         /* doc */
        //    17: ldc             "list"
        //    19: aload_0         /* this */
        //    20: iload_3         /* buyListId */
        //    21: aload_2         /* f */
        //    22: invokedynamic   BootstrapMethod #1, accept:(Lorg/l2j/gameserver/data/xml/impl/BuyListData;ILjava/io/File;)Ljava/util/function/Consumer;
        //    27: invokevirtual   org/l2j/gameserver/data/xml/impl/BuyListData.forEach:(Lorg/w3c/dom/Node;Ljava/lang/String;Ljava/util/function/Consumer;)V
        //    30: goto            52
        //    33: astore_3        /* e */
        //    34: getstatic       org/l2j/gameserver/data/xml/impl/BuyListData.LOGGER:Lorg/slf4j/Logger;
        //    37: aload_2         /* f */
        //    38: invokevirtual   java/io/File.getName:()Ljava/lang/String;
        //    41: invokedynamic   BootstrapMethod #2, makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
        //    46: aload_3         /* e */
        //    47: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //    52: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  doc   
        //  f     
        //    StackMapTable: 00 02 61 07 00 82 12
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      30     33     52     Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public ProductList getBuyList(final int listId) {
        return (ProductList)this.buyLists.get(listId);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static BuyListData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)BuyListData.class);
    }
    
    private static class Singleton
    {
        private static final BuyListData INSTANCE;
        
        static {
            INSTANCE = new BuyListData();
        }
    }
}
