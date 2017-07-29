/*
* Copyright 2017 WoodenBell
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package io.github.woodenbell.strife;


import java.io.IOException;
import java.util.HashMap;

import io.github.woodenbell.strife.model.EditItem;
import io.github.woodenbell.strife.model.EditorModel;
import io.github.woodenbell.strife.view.EditorController;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	    private Stage primaryStage;
	    private BorderPane ui;
	    private Scene scene;
	    private ObservableList<EditItem> edits;
	    private HashMap<EditItem, String> editorText;
	    public final static String author = "WoodenBell";
	    public final static String version = "1.1";
	    public final static String authorEmail = "codingcookie@gmail.com";

	    @Override
	    public void start(Stage primaryStage) {
	        this.primaryStage = primaryStage;
	        this.primaryStage.setTitle("Strife");
	        this.primaryStage.getIcons().add(new Image("file:resources/images/Strife-icon-32.png"));
	        initializeEditableFiles();
	        initUI();
	    }

	    public Scene getScene() {
	    	return scene;
	    }

	    public ObservableList<EditItem> getEditItems() {
	    	return edits;
	    }

	    public HashMap<EditItem, String> getEditorTexts() {
	    	return editorText;
	    }

	    public void setEditorText(EditItem k, String v) {
	    	//System.out.println(k + " ::: " + v);
	    	//System.out.println(new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName() + "()");
	    	editorText.put(k, v);
	    }
	    public void initUI() {
	        try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(MainApp.class.getResource("view/ui.fxml"));
	            ui = (BorderPane) loader.load();
	            EditorController controller = loader.getController();
	            Preferences.defaultPreferences();
	            controller.setModel(new EditorModel());
	            controller.setMainApp(this);
	            scene = new Scene(ui);
	            primaryStage.setScene(scene);
	            primaryStage.setResizable(false);
	            primaryStage.show();
	            Preferences.loadPreferences(controller);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }



	    public Stage getPrimaryStage() {
	        return primaryStage;
	    }

	    public void initializeEditableFiles() {
	    	edits = FXCollections.observableArrayList();
	    	editorText = new HashMap<>();

	    }
	    public static void main(String[] args) {
	        launch(args);
	    }
	}