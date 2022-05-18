package net.andrews.mechlist;

import java.nio.file.Path;

public enum ConfigFiles {
	CREDENTIALS("credentials.json"),
	TOKENS("tokens"),
	CONFIG("config.json");

	private final String fileName;

	ConfigFiles(String fileName) {
		this.fileName = fileName;
	}

	public Path getPath() {
		return MechList.CONFIG_DIR.resolve(fileName);
	}
}
