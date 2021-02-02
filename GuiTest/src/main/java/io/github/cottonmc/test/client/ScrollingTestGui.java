package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

import net.minecraft.text.LiteralText;

public class ScrollingTestGui extends LightweightGuiDescription {
	public ScrollingTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;
		WBox box = new WBox(Axis.VERTICAL);

		for (int i = 0; i < 20; i++) {
			box.add(new WButton(new LiteralText("Button #" + i)));
		}

		root.add(new WScrollPanel(box), 0, 0, 5, 3);
		root.validate(this);
	}
}
