// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class Recipes implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!Config.IS_CRAFTING_ENABLED) {
            playable.sendMessage("Crafting is disabled, you cannot register this recipe.");
            return false;
        }
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        if (activeChar.isCrafting()) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
            return false;
        }
        final RecipeList rp = RecipeData.getInstance().getRecipeByItemId(item.getId());
        if (rp == null) {
            return false;
        }
        if (activeChar.hasRecipeList(rp.getId())) {
            activeChar.sendPacket(SystemMessageId.THAT_RECIPE_IS_ALREADY_REGISTERED);
            RecipeController.getInstance().requestBookOpen(activeChar, true);
            return false;
        }
        boolean canCraft = false;
        boolean recipeLevel = false;
        boolean recipeLimit = false;
        if (rp.isDwarvenRecipe()) {
            canCraft = activeChar.hasDwarvenCraft();
            recipeLevel = (rp.getLevel() > activeChar.getDwarvenCraft());
            recipeLimit = (activeChar.getDwarvenRecipeBook().length >= activeChar.getDwarfRecipeLimit());
        }
        else {
            canCraft = activeChar.hasCommonCraft();
            recipeLevel = (rp.getLevel() > activeChar.getCommonCraft());
            recipeLimit = (activeChar.getCommonRecipeBook().length >= activeChar.getCommonRecipeLimit());
        }
        if (!canCraft) {
            activeChar.sendPacket(SystemMessageId.THE_RECIPE_CANNOT_BE_REGISTERED_YOU_DO_NOT_HAVE_THE_ABILITY_TO_CREATE_ITEMS);
            return false;
        }
        if (recipeLevel) {
            activeChar.sendPacket(SystemMessageId.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
            return false;
        }
        if (recipeLimit) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_BE_REGISTERED);
            sm.addInt(rp.isDwarvenRecipe() ? activeChar.getDwarfRecipeLimit() : activeChar.getCommonRecipeLimit());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return false;
        }
        if (rp.isDwarvenRecipe()) {
            activeChar.registerDwarvenRecipeList(rp, true);
        }
        else {
            activeChar.registerCommonRecipeList(rp, true);
        }
        activeChar.destroyItem("Consume", item.getObjectId(), 1L, (WorldObject)null, false);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED);
        sm.addItemName(item);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        RecipeController.getInstance().requestBookOpen(activeChar, true);
        return true;
    }
}
