package net.andrews.mechlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;

public record GoogleSheetsConfig(String spreadsheetId, String sheetRange) {
	public static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.registerTypeAdapter(GoogleSheetsConfig.class, new GoogleSheetsConfigDeserializer())
		.create();

	public void setSpreadsheetId(String id) {
		var updatedConfig = new GoogleSheetsConfig(id, sheetRange());
		updatedConfig.saveToFile();
		MechList.setConfig(updatedConfig);
	}

	public void setSheetRange(String range) {
		var updatedConfig = new GoogleSheetsConfig(spreadsheetId(), range);
		updatedConfig.saveToFile();
		MechList.setConfig(updatedConfig);
	}

	public void saveToFile() {
		try (BufferedWriter writer = Files.newBufferedWriter(ConfigFiles.CONFIG.getPath())) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			MechList.LOGGER.error("Failed to save config file", e);
		}
	}

	public GoogleSheetsConfig loadFromFile() {
		try {
			String fileContents = new String(Files.readAllBytes(ConfigFiles.CONFIG.getPath()));
			return GSON.fromJson(fileContents.isBlank() ? "{}" : fileContents, GoogleSheetsConfig.class);
		} catch (IOException | JsonSyntaxException e) {
			MechList.LOGGER.error("Failed to read config file");
			return new GoogleSheetsConfig("", "");
		}
	}

	private static class GoogleSheetsConfigDeserializer implements JsonDeserializer<GoogleSheetsConfig> {
		@Override
		public GoogleSheetsConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String spreadsheetId = jsonObject.get("spreadsheetId").getAsString();
			String sheetRange = jsonObject.get("sheetRange").getAsString();
			return new GoogleSheetsConfig(spreadsheetId, sheetRange);
		}
	}
}
