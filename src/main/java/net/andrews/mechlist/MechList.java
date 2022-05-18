package net.andrews.mechlist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MechList implements ModInitializer {

	public static final String MOD_ID = "mechlist";
	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String USER_ID = "user";
	public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

	private static GoogleSheetsConfig config = new GoogleSheetsConfig(null, null);

	@Override
	public void onInitialize() {
		config = config.loadFromFile();
	}

	public static GoogleSheetsConfig getConfig() {
		return config;
	}

	protected static void setConfig(GoogleSheetsConfig config) {
		MechList.config = config;
	}
}
