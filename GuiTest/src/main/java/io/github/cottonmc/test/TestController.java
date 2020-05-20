package io.github.cottonmc.test;

import io.github.cottonmc.cotton.gui.CottonInventoryController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;

public class TestController extends CottonInventoryController {
	
	public TestController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(syncId, playerInventory, getBlockInventory(context), null);
		
		WGridPanel root = (WGridPanel)this.getRootPanel();
		
		root.add(WItemSlot.of(blockInventory, 0, 4, 1), 0, 1);
		
		WButton button = new WButton(new LiteralText("Test Button"));
		root.add(button, 0, 3, 5, 1);
		
		
		root.add(createPlayerInventoryPanel(), 0, 5);
		
		
		this.getRootPanel().validate(this);
	}
}
