package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public interface BackgroundPainter {
	/**
	 * Paint the specified panel to the screen.
	 * @param left The absolute position of the left of the panel, in gui-screen coordinates
	 * @param top The absolute position of the top of the panel, in gui-screen coordinates
	 * @param panel The panel being painted
	 */
	public void paintBackground(int left, int top, WWidget panel);
	
	
	
	public static BackgroundPainter VANILLA = (left, top, panel) -> {
		ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16);
	};

	public static BackgroundPainter SLOT = (left, top, panel) -> {
		if (!(panel instanceof WItemSlot)) {
			ScreenDrawing.drawBeveledPanel(left-1, top-1, panel.getWidth()+2, panel.getHeight()+2, 0xB8000000, 0x4C000000, 0xB8FFFFFF);
		} else {
			WItemSlot slot = (WItemSlot)panel;
			for(int x = 0; x < slot.getWidth()/18; ++x) {
				for(int y = 0; y < slot.getHeight()/18; ++y) {
					int lo = 0xB8000000;
					int bg = 0x4C000000;
					//this will cause a slightly discolored bottom border on vanilla backgrounds but it's necessary for color support, it shouldn't be *too* visible unless you're looking for it
					int hi = 0xB8FFFFFF;
					if (slot.isBigSlot()) {
						ScreenDrawing.drawBeveledPanel((x * 18) + left - 4, (y * 18) + top - 4, 16+8, 16+8,
								lo, bg, hi);
					} else {
						ScreenDrawing.drawBeveledPanel((x * 18) + left - 1, (y * 18) + top - 1, 16+2, 16+2,
								lo, bg, hi);
					}
				}
			}
		}
	};
	
	public static BackgroundPainter createColorful(int panelColor) {
		return (left, top, panel) -> {
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, panelColor);
		};
	}
	
	public static BackgroundPainter createColorful(int panelColor, float contrast) {
		return (left, top, panel) -> {
			int shadowColor = ScreenDrawing.multiplyColor(panelColor, 1.0f - contrast);
			int hilightColor = ScreenDrawing.multiplyColor(panelColor, 1.0f + contrast);
			
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, shadowColor, panelColor, hilightColor, 0xFF000000);
		};
	}

	/**
	 * Utility method to call {@link NinePatch#NinePatch(Identifier)}.
	 *
	 * @param texture the background painter texture
	 * @return a new nine-patch background painter
	 */
	public static BackgroundPainter.NinePatch createNinePatch(Identifier texture) {
		return new NinePatch(texture);
	}

	/**
	 * Utility method to call {@link NinePatch#NinePatch(Identifier)}
	 * and set the padding of the nine-patch painter.
	 *
	 * @param texture the background painter texture
	 * @param padding the padding of the painter
	 * @return a new nine-patch background painter
	 */
	public static BackgroundPainter.NinePatch createNinePatch(Identifier texture, int padding) {
		return new NinePatch(texture).setPadding(padding);
	}

	/**
	 * Creates a background painter that uses either the {@code light} or the {@code dark} background painter
	 * depending on the current setting.
	 *
	 * @param light the light mode background painter
	 * @param dark the dark mode background painter
	 * @return a new background painter that chooses between the two inputs
	 */
	public static BackgroundPainter createLightDarkVariants(BackgroundPainter light, BackgroundPainter dark) {
		return (left, top, panel) -> {
			if (LibGuiClient.config.darkMode) dark.paintBackground(left, top, panel);
			else light.paintBackground(left, top, panel);
		};
	}

	/**
	 * The nine-patch background painter paints rectangles using a nine-patch texture.
	 *
	 * <p>Nine-patch textures are separated into nine sections: four corners, four edges and a center part.
	 * The edges and the center are either tiled or stretched, depending on the {@linkplain BackgroundPainter.NinePatch.Mode mode},
	 * to fill the area between the corners.
	 *
	 * <p>{@code NinePatch} painters have a customizable padding that can be applied.
	 * For example, a GUI panel for a container block might have a padding of 8 pixels, like {@link BackgroundPainter#VANILLA}.
	 * You can set the padding using {@link NinePatch#setPadding(int)}.
	 */
	public static class NinePatch implements BackgroundPainter {
		private final Identifier texture;
		private final int cornerSize;
		private final float cornerUv;
		private int topPadding = 0;
		private int leftPadding = 0;
		private int bottomPadding = 0;
		private int rightPadding = 0;
		private Mode mode = Mode.STRETCHING;

		/**
		 * Creates a nine-patch background painter with 4 px corners and a 0.25 cornerUv (corner fraction of whole texture).
		 *
		 * @param texture the texture ID
		 */
		public NinePatch(Identifier texture) {
			this(texture, 4, 0.25f);
		}

		/**
		 * Creates a nine-patch background painter.
		 *
		 * @param texture the texture ID
		 * @param cornerSize the size of the corners on the screen
		 * @param cornerUv the fraction of the corners of the whole texture
		 */
		public NinePatch(Identifier texture, int cornerSize, float cornerUv) {
			this.texture = texture;
			this.cornerSize = cornerSize;
			this.cornerUv = cornerUv;
		}

		public int getTopPadding() {
			return topPadding;
		}

		public NinePatch setTopPadding(int topPadding) {
			this.topPadding = topPadding;
			return this;
		}

		public int getLeftPadding() {
			return leftPadding;
		}

		public NinePatch setLeftPadding(int leftPadding) {
			this.leftPadding = leftPadding;
			return this;
		}

		public int getBottomPadding() {
			return bottomPadding;
		}

		public NinePatch setBottomPadding(int bottomPadding) {
			this.bottomPadding = bottomPadding;
			return this;
		}

		public int getRightPadding() {
			return rightPadding;
		}

		public NinePatch setRightPadding(int rightPadding) {
			this.rightPadding = rightPadding;
			return this;
		}

		public NinePatch setPadding(int padding) {
			this.topPadding = this.leftPadding = this.bottomPadding = this.rightPadding = padding;
			return this;
		}

		public NinePatch setPadding(int vertical, int horizontal) {
			this.topPadding = this.bottomPadding = vertical;
			this.leftPadding = this.rightPadding = horizontal;
			return this;
		}

		public NinePatch setPadding(int topPadding, int leftPadding, int bottomPadding, int rightPadding) {
			this.topPadding = topPadding;
			this.leftPadding = leftPadding;
			this.bottomPadding = bottomPadding;
			this.rightPadding = rightPadding;

			return this;
		}

		public Identifier getTexture() {
			return texture;
		}

		public int getCornerSize() {
			return cornerSize;
		}

		public float getCornerUv() {
			return cornerUv;
		}

		public Mode getMode() {
			return mode;
		}

		public NinePatch setMode(Mode mode) {
			this.mode = mode;
			return this;
		}

		public NinePatch stretch() {
			this.mode = Mode.STRETCHING;
			return this;
		}

		public NinePatch tile() {
			this.mode = Mode.TILING;
			return this;
		}

		@Override
		public void paintBackground(int left, int top, WWidget panel) {
			int width = panel.getWidth() + leftPadding + rightPadding;
			int height = panel.getHeight() + topPadding + bottomPadding;
			left = left - leftPadding;
			top = top - topPadding;
			int x1 = left + cornerSize;
			int x2 = left + width - cornerSize;
			int y1 = top + cornerSize;
			int y2 = top + height - cornerSize;
			float uv1 = cornerUv;
			float uv2 = 1.0f - cornerUv;

			ScreenDrawing.texturedRect(left, top, cornerSize, cornerSize, texture, 0, 0, uv1, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x2, top, cornerSize, cornerSize, texture, uv2, 0, 1, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(left, y2, cornerSize, cornerSize, texture, 0, uv2, uv1, 1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x2, y2, cornerSize, cornerSize, texture, uv2, uv2, 1, 1, 0xFF_FFFFFF);

			if (mode == Mode.TILING) {
				int tileSize = (int) (cornerSize / cornerUv - 2 * cornerSize);
				int widthLeft = width - 2 * cornerSize;
				int heightLeft = height - 2 * cornerSize;
				int tileCountX = MathHelper.ceil((float) widthLeft / tileSize);
				int tileCountY = MathHelper.ceil((float) heightLeft / tileSize);
				for (int i = 0; i < tileCountX; i++) {
					float px = 1 / 16f;
					int tileWidth = Math.min(widthLeft, tileSize);
					float uo = (tileSize - tileWidth) * px; // Used to remove unnecessary pixels on the X axis

					ScreenDrawing.texturedRect(x1 + i * tileSize, top, tileWidth, cornerSize, texture, uv1, 0, uv2 - uo, uv1, 0xFF_FFFFFF);
					ScreenDrawing.texturedRect(x1 + i * tileSize, y2, tileWidth, cornerSize, texture, uv1, uv2, uv2 - uo, 1, 0xFF_FFFFFF);

					// Reset the height left each time the Y is looped
					heightLeft = height - 2 * cornerSize;

					for (int j = 0; j < tileCountY; j++) {
						int tileHeight = Math.min(heightLeft, tileSize);
						float vo = (tileSize - tileHeight) * px; // Used to remove unnecessary pixels on the Y axis

						ScreenDrawing.texturedRect(left, y1 + j * tileSize, cornerSize, tileHeight, texture, 0, uv1, uv1, uv2 - vo, 0xFF_FFFFFF);
						ScreenDrawing.texturedRect(x2, y1 + j * tileSize, cornerSize, tileHeight, texture, uv2, uv1, 1, uv2 - vo, 0xFF_FFFFFF);

						ScreenDrawing.texturedRect(x1 + i * tileSize, y1 + j * tileSize, tileWidth, tileHeight, texture, uv1, uv1, uv2 - uo, uv2 - vo, 0xFF_FFFFFF);
						heightLeft -= tileSize;
					}
					widthLeft -= tileSize;
				}
			} else {
				ScreenDrawing.texturedRect(x1, top, width - 2 * cornerSize, cornerSize, texture, uv1, 0, uv2, uv1, 0xFF_FFFFFF);
				ScreenDrawing.texturedRect(left, y1, cornerSize, height - 2 * cornerSize, texture, 0, uv1, uv1, uv2, 0xFF_FFFFFF);
				ScreenDrawing.texturedRect(x1, y2, width - 2 * cornerSize, cornerSize, texture, uv1, uv2, uv2, 1, 0xFF_FFFFFF);
				ScreenDrawing.texturedRect(x2, y1, cornerSize, height - 2 * cornerSize, texture, uv2, uv1, 1, uv2, 0xFF_FFFFFF);

				ScreenDrawing.texturedRect(x1, y1, width - 2 * cornerSize, height - 2 * cornerSize, texture, uv1, uv1, uv2, uv2, 0xFF_FFFFFF);
			}
		}

		/**
		 * The mode of a nine-patch painter defines how it fills the area between the corners.
		 */
		public enum Mode {
			/**
			 * The texture is stretched to fill the edges and the center.
			 * This is the default mode.
			 */
			STRETCHING,

			/**
			 * The texture is tiled to fill the edges and the center.
			 */
			TILING;
		}
	}
}
