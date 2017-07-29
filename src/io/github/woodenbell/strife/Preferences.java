package io.github.woodenbell.strife;

import java.util.HashMap;

import io.github.woodenbell.strife.view.EditorController;

public class Preferences {

	public static HashMap<String, String> editorPreferences;

	static {
		editorPreferences = new HashMap<>();
	}

	public static void loadPreferences(EditorController c) {
		c.loadPrefs();
	}

	public static void defaultPreferences() {
		editorPreferences.put("highlight-fill", "#4a4ace");
		editorPreferences.put("text-fill", "#000000");
		editorPreferences.put("bg-color", "#f7f9f9");
		editorPreferences.put("font-size", "18px");
	}
}
