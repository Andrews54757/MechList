package net.andrews.mechlist;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public enum Styles {
	DARK_GREEN_BOLD(
		Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(true)
	),
	DARK_GREEN(
		Style.EMPTY.withColor(Formatting.DARK_GREEN).withBold(false)
	),
	DARK_AQUA(
		Style.EMPTY.withColor(Formatting.DARK_AQUA).withBold(false)
	),
	RED(
		Style.EMPTY.withColor(Formatting.RED).withBold(false)
	),
	GOLD(
		Style.EMPTY.withColor(Formatting.GOLD).withBold(false)
	);

	private final Style style;

	Styles(Style style) {
		this.style = style;
	}

	public Style getStyle() {
		return style;
	}
}
