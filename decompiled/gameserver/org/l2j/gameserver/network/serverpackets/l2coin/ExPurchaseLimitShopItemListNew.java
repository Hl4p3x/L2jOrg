// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.l2coin;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExPurchaseLimitShopItemListNew extends ServerPacket
{
    private final byte index;
    
    public ExPurchaseLimitShopItemListNew(final byte index) {
        this.index = index;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getstatic       org/l2j/gameserver/network/ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW:Lorg/l2j/gameserver/network/ServerExPacketId;
        //     4: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeId:(Lorg/l2j/gameserver/network/ServerExPacketId;)V
        //     7: aload_0         /* this */
        //     8: aload_0         /* this */
        //     9: getfield        org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.index:B
        //    12: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeByte:(B)V
        //    15: invokestatic    org/l2j/gameserver/engine/item/shop/LCoinShop.getInstance:()Lorg/l2j/gameserver/engine/item/shop/LCoinShop;
        //    18: invokevirtual   org/l2j/gameserver/engine/item/shop/LCoinShop.getProductInfos:()Lio/github/joealisson/primitive/IntMap;
        //    21: astore_2        /* products */
        //    22: aload_0         /* this */
        //    23: aload_2         /* products */
        //    24: invokeinterface io/github/joealisson/primitive/IntMap.size:()I
        //    29: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //    32: aload_2         /* products */
        //    33: invokeinterface io/github/joealisson/primitive/IntMap.values:()Ljava/util/Collection;
        //    38: invokeinterface java/util/Collection.iterator:()Ljava/util/Iterator;
        //    43: astore_3       
        //    44: aload_3        
        //    45: invokeinterface java/util/Iterator.hasNext:()Z
        //    50: ifeq            137
        //    53: aload_3        
        //    54: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    59: checkcast       checkcast      !!! ERROR
        //    62: astore          product
        //    64: aload_0         /* this */
        //    65: aload           product
        //    67: invokevirtual   invokevirtual  !!! ERROR
        //    70: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //    73: aload_0         /* this */
        //    74: aload           product
        //    76: invokevirtual   invokevirtual  !!! ERROR
        //    79: invokevirtual   org/l2j/gameserver/model/holders/ItemHolder.getId:()I
        //    82: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //    85: aload_0         /* this */
        //    86: aload           product
        //    88: invokevirtual   invokevirtual  !!! ERROR
        //    91: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeIngredients:(Ljava/util/List;)V
        //    94: aload_0         /* this */
        //    95: aload           product
        //    97: invokevirtual   invokevirtual  !!! ERROR
        //   100: invokestatic    org/l2j/gameserver/engine/item/shop/LCoinShop.getInstance:()Lorg/l2j/gameserver/engine/item/shop/LCoinShop;
        //   103: aload_1         /* client */
        //   104: invokevirtual   org/l2j/gameserver/network/GameClient.getPlayer:()Lorg/l2j/gameserver/model/actor/instance/Player;
        //   107: aload           product
        //   109: invokevirtual   org/l2j/gameserver/engine/item/shop/LCoinShop.boughtCount:(Lorg/l2j/gameserver/model/actor/instance/Player;invokevirtual  !!! ERROR
        //   112: isub           
        //   113: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //   116: aload_0         /* this */
        //   117: aload           product
        //   119: invokevirtual   invokevirtual  !!! ERROR
        //   122: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //   125: aload_0         /* this */
        //   126: aload           product
        //   128: invokevirtual   invokevirtual  !!! ERROR
        //   131: invokevirtual   org/l2j/gameserver/network/serverpackets/l2coin/ExPurchaseLimitShopItemListNew.writeInt:(I)V
        //   134: goto            44
        //   137: return         
        //    MethodParameters:
        //  Name    Flags  
        //  ------  -----
        //  client  
        //    StackMapTable: 00 02 FD 00 2C 07 00 26 07 00 3A FA 00 5C
        // 
        // The error that occurred was:
        // 
        // java.lang.reflect.GenericSignatureFormatError
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.error(SignatureParser.java:70)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseFormalParameters(SignatureParser.java:416)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodTypeSignature(SignatureParser.java:407)
        //     at com.strobel.assembler.metadata.signatures.SignatureParser.parseMethodSignature(SignatureParser.java:88)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethodSignature(MetadataParser.java:234)
        //     at com.strobel.assembler.metadata.MetadataParser.parseMethod(MetadataParser.java:166)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethod(ClassFileReader.java:1303)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupMethodHandle(ClassFileReader.java:1258)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookup(ClassFileReader.java:1352)
        //     at com.strobel.assembler.ir.MetadataReader.readAttributeCore(MetadataReader.java:306)
        //     at com.strobel.assembler.metadata.ClassFileReader.readAttributeCore(ClassFileReader.java:261)
        //     at com.strobel.assembler.ir.MetadataReader.inflateAttributes(MetadataReader.java:439)
        //     at com.strobel.assembler.metadata.ClassFileReader.visitAttributes(ClassFileReader.java:1134)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:439)
        //     at com.strobel.assembler.metadata.ClassFileReader.readClass(ClassFileReader.java:377)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveType(MetadataSystem.java:129)
        //     at com.strobel.decompiler.NoRetryMetadataSystem.resolveType(DecompilerDriver.java:476)
        //     at com.strobel.assembler.metadata.MetadataSystem.resolveCore(MetadataSystem.java:81)
        //     at com.strobel.assembler.metadata.MetadataResolver.resolve(MetadataResolver.java:104)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedType.resolve(CoreMetadataFactory.java:616)
        //     at com.strobel.assembler.metadata.MetadataHelper.isRawType(MetadataHelper.java:1581)
        //     at com.strobel.assembler.metadata.MetadataHelper.asSubType(MetadataHelper.java:715)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1496)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:881)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
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
    
    private void writeIngredients(final List<ItemHolder> ingredients) {
        for (int i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                this.writeInt(ingredients.get(i).getId());
            }
            else {
                this.writeInt(0);
            }
        }
        for (int i = 0; i < 3; ++i) {
            if (i < ingredients.size()) {
                this.writeLong(ingredients.get(i).getCount());
            }
            else {
                this.writeLong(0L);
            }
        }
    }
}
