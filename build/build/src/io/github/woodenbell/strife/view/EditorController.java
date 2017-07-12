package io.github.woodenbell.strife.view;





import io.github.woodenbell.strife.MainApp;
import io.github.woodenbell.strife.model.EditFile;
import io.github.woodenbell.strife.model.EditItem;
import io.github.woodenbell.strife.model.EditorModel;
import io.github.woodenbell.strife.model.IOResult;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;

public class EditorController {


	@FXML
	private ListView<EditItem> editList;

	@FXML
	private TextArea textEditor;

	@FXML
	private MenuItem newItem;
	@FXML
	private MenuItem openItem;
	@FXML
	private MenuItem saveItem;
	@FXML
	private MenuItem closeItem;
	@FXML
	private MenuItem copyItem;
	@FXML
	private MenuItem cutItem;
	@FXML
	private MenuItem pasteItem;
	@FXML
	private MenuItem deleteItem;
	@FXML
	private ScrollPane listScrollPane;

	private boolean checkFileName(String fName) {
		if(fName.length() < 1) return false;
		String[] invalidChars = {"?", "\\", "/", ":", "<", ">", "|", ":", "\"", "*"};
		String[] invalidNames = {
				"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7",
				"COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
		for(String s1:invalidChars) {
			if(fName.contains(s1)) return false;
		}
		for(String s2:invalidNames) {
			if(fName.equals(s2)) return false;
		}
		return true;
		}
	@FXML
	private void onNew() {
		Dialog<Pair<String, String>> d = new Dialog<>();

		d.setTitle("New file");
		d.setHeaderText("Choose the file name");
		d.setContentText("");
		d.setGraphic(new ImageView("file:resources/images/NewFile-icon-64.png"));
		((Stage) d.getDialogPane().getScene().getWindow()).getIcons()
		.add(new Image("file:resources/images/Strife-icon-32.png"));
		ButtonType ok = new ButtonType("Create", ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		d.getDialogPane().getButtonTypes().addAll(ok, cancel);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		TextField fileName = new TextField();
		ComboBox<String> extension = new ComboBox<String>();
		ObservableList<String> supportedTypes = FXCollections.observableArrayList();
		supportedTypes.setAll(".txt", ".md", ".ini", ".xml", ".html", ".js", ".css",
				 ".py", ".java", ".php");
		extension.setItems(supportedTypes);
		extension.getSelectionModel().selectFirst();
		grid.add(fileName, 0, 1);
		grid.add(extension, 1, 1);
		d.getDialogPane().setContent(grid);
		d.setResultConverter(dialogButton -> {
			if(dialogButton == ok) {
				return new Pair<>(fileName.getText(), extension.getSelectionModel().getSelectedItem());
			}
			return null;
		});
		Optional<Pair<String, String>> res = d.showAndWait();
		if(res.isPresent()) {
			if(checkFileName(res.get().getKey())) {
				loadNewFile(new EditItem(res.get().getKey() + res.get().getValue()));
			} else {
				Alert warning = new Alert(AlertType.WARNING);
				warning.setTitle("Create file");
				warning.setHeaderText("Cannot create file");
				warning.setContentText("Invalid file name");
				warning.show();
			}
		} else {
			return;
		}
	}

	@FXML
	private void onLoad() {
		File f;
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose file to open");
		FileChooser.ExtensionFilter allFilter =
				 new FileChooser.ExtensionFilter("All files", "*");
		 FileChooser.ExtensionFilter txtFilter =
				 new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		 FileChooser.ExtensionFilter mdFilter =
				 new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md");
		 FileChooser.ExtensionFilter cssFilter =
				 new FileChooser.ExtensionFilter("CSS files (*.css)", "*.css");
		 FileChooser.ExtensionFilter jsFilter =
				 new FileChooser.ExtensionFilter("Javascript files (*.js)", "*.js");
		 FileChooser.ExtensionFilter htmlFilter =
				 new FileChooser.ExtensionFilter("HTML documents (*.html)", "*.html");
		 FileChooser.ExtensionFilter jsonFilter =
				 new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
		 FileChooser.ExtensionFilter phpFilter =
				 new FileChooser.ExtensionFilter("PHP files (*.php)", "*.php");
		 FileChooser.ExtensionFilter javaFilter =
				 new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java");
		 FileChooser.ExtensionFilter pyFilter =
				 new FileChooser.ExtensionFilter("Python modules (*.py)", "*.py");
		 FileChooser.ExtensionFilter xmlFilter =
				 new FileChooser.ExtensionFilter("XML documents (*.xml)", "*.xml");
		 FileChooser.ExtensionFilter iniFilter =
				 new FileChooser.ExtensionFilter("INI files (*.ini)", "*.ini");
		 fc.getExtensionFilters().addAll(allFilter, txtFilter, mdFilter, cssFilter, jsFilter, htmlFilter, jsonFilter,
				 phpFilter, javaFilter, pyFilter, xmlFilter, iniFilter);
		f = fc.showOpenDialog(new Stage());
		if(f == null) return;
		loadFile(model.load(Paths.get(f.getAbsolutePath())));
	}

	@FXML
	private void onSave() {
		EditItem f = editList.getSelectionModel().getSelectedItem();
		if(f == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No file selected");
			alert.setTitle("Error");
			alert.setContentText("You must select a file to be saved");
			alert.show();
		} else {
			saveFile(f);
		}
	}
	@FXML
	private void onSaveAs() {
		EditItem f = editList.getSelectionModel().getSelectedItem();
		if(f == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No file selected");
			alert.setTitle("Error");
			alert.setContentText("You must select a file to be saved");
			alert.show();
		} else {
			saveFileAs(f);
		}
	}

	@FXML
	private void onSaveAll() {
		if(app.getEditItems().size() == 0) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No files");
			alert.setTitle("Error");
			alert.setContentText("There are no files to be saved");
			alert.show();
		} else {
			boolean atLeastOne = false;
			for(EditItem i:app.getEditItems()) {
				if(saveFile(i) && !atLeastOne) {
					atLeastOne = true;
				}
			}
			if(!atLeastOne) return;
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Save files");
			info.setTitle("Success");
			info.setContentText("All files have been saved");
			info.show();
		}
	}

	@FXML
	private void onClose() {
		EditItem f = editList.getSelectionModel().getSelectedItem();
		if(f == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No file selected");
			alert.setTitle("Error");
			alert.setContentText("You must select a file to be closed");
			alert.show();
		} else {
			closeFile(f, true);
		}
	}

	@FXML
	private void onCloseAll() {
		if(app.getEditItems().size() == 0) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No files");
			alert.setTitle("Error");
			alert.setContentText("There are no files to be closed");
			alert.show();
		} else {
			for(EditItem i:app.getEditItems()) {
				closeFile(i, false);
			}
			Alert info = new Alert(AlertType.INFORMATION);
			info.setHeaderText("Close files");
			info.setTitle("Success");
			info.setContentText("All files have been closed");
			info.show();
			app.getEditItems().clear();
			editList.getItems().clear();
			app.getEditorTexts().clear();
			currFile = null;
			textEditor.setEditable(false);
		}
	}

	@FXML
	private void onDelete() {
		textEditor.replaceSelection("");
	}

	@FXML
	private void onCopy() {
		Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent cc = new ClipboardContent();
		String selection = textEditor.getSelectedText();
		if(selection != null || selection != "") {
			cc.putString(selection);
			cb.setContent(cc);
		}
	}

	@FXML private void onCut() {
		Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent cc = new ClipboardContent();
		String selection = textEditor.getSelectedText();
		if(selection != null || selection != "") {
			cc.putString(selection);
			cb.setContent(cc);
			textEditor.replaceSelection("");
		}
	}

	@FXML
	private void onPaste() {
		Clipboard cb = Clipboard.getSystemClipboard();
		String editorText = textEditor.getText();
		String toPaste = cb.getString();
		if(cb.hasContent(DataFormat.PLAIN_TEXT) ) {
			IndexRange r = textEditor.getSelection();
			String firstPart = editorText.substring(0, r.getStart() );
			String lastPart = editorText.substring(r.getEnd(), editorText.length());
			textEditor.setText(firstPart + toPaste + lastPart);
			textEditor.positionCaret(r.getStart() + toPaste.length());
		}
		}
	@FXML
	private void onAbout() {
		Alert info = new Alert(Alert.AlertType.INFORMATION);
		Stage stage = (Stage) info.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:resources/images/Info-icon-32.png"));
		info.setGraphic(new ImageView("file:resources/images/Strife-icon-64.png"));
		info.setHeaderText(null);
		info.setTitle("About");
		info.setContentText("Author: " + MainApp.author + "\nE-Mail: " + MainApp.authorEmail + " \nVersion: " + MainApp.version);
		info.show();

	}
	private EditorModel model;

	private MainApp app;

	private boolean isFileTextChanging;

	private EditItem currFile;

	public EditorController() {

	}

	public void setModel(EditorModel model) {
		this.model = model;
	}

	private boolean saveFile(EditItem f) {
		f.setContent(Arrays.asList(app.getEditorTexts().get(f)));
		if(f.isAFile()) {
			if(f.isFileSaved()) {
				if(!model.save(f)) {
					Alert error = new Alert(AlertType.ERROR);
					error.setHeaderText("Save error");
					error.setTitle("Error");
					error.setContentText("An error ocurred while trying to save your file," +
					 " please try again");
					error.show();
					removeFile(f, true);
					return false;
				} else {
					f.setFileSaved(true);
					editList.refresh();
					return true;
				}
			} else {
				if(!model.save(f)) {
					Alert error = new Alert(AlertType.ERROR);
					error.setHeaderText("Save error");
					error.setTitle("Error");
					error.setContentText("An error ocurred while trying to save your file," +
					 " please try again");
					error.show();
					removeFile(f, true);
					return false;
				} else {
					f.setFileSaved(true);
					editList.refresh();
					return true;
				}
			}
		} else {
			return saveFileAs(f);
		}
	}

	private boolean saveFileAs(EditItem f) {
		f.setContent(Arrays.asList(app.getEditorTexts().get(f)));
		FileChooser fc = new FileChooser();
		fc.setTitle("Save file " + f.getFileName());
		FileChooser.ExtensionFilter allFilter =
				 new FileChooser.ExtensionFilter("All files", "*");
		 FileChooser.ExtensionFilter txtFilter =
				 new FileChooser.ExtensionFilter("TXT file (*.txt)", "*.txt");
		 FileChooser.ExtensionFilter mdFilter =
				 new FileChooser.ExtensionFilter("Markdown file (*.md)", "*.md");
		 FileChooser.ExtensionFilter cssFilter =
				 new FileChooser.ExtensionFilter("CSS file (*.css)", "*.css");
		 FileChooser.ExtensionFilter jsFilter =
				 new FileChooser.ExtensionFilter("Javascript file (*.js)", "*.js");
		 FileChooser.ExtensionFilter htmlFilter =
				 new FileChooser.ExtensionFilter("HTML document (*.html)", "*.html");
		 FileChooser.ExtensionFilter jsonFilter =
				 new FileChooser.ExtensionFilter("JSON file (*.json)", "*.json");
		 FileChooser.ExtensionFilter phpFilter =
				 new FileChooser.ExtensionFilter("PHP file (*.php)", "*.php");
		 FileChooser.ExtensionFilter javaFilter =
				 new FileChooser.ExtensionFilter("JAVA file (*.java)", "*.java");
		 FileChooser.ExtensionFilter pyFilter =
				 new FileChooser.ExtensionFilter("Python module (*.py)", "*.py");
		 FileChooser.ExtensionFilter xmlFilter =
				 new FileChooser.ExtensionFilter("XML document (*.xml)", "*.xml");
		 FileChooser.ExtensionFilter iniFilter =
				 new FileChooser.ExtensionFilter("INI file (*.ini)", "*.ini");
		 fc.getExtensionFilters().addAll(allFilter, txtFilter, mdFilter, htmlFilter, cssFilter, jsFilter, jsonFilter,
				 phpFilter, javaFilter, pyFilter, xmlFilter, iniFilter);
		 fc.setInitialFileName(f.getOnlyFileName());
		 HashMap<String, FileChooser.ExtensionFilter> hm = new HashMap<>();
		 hm.put(".txt", txtFilter);
		 hm.put(".md", mdFilter);
		 hm.put(".css", cssFilter);
		 hm.put(".html", htmlFilter);
		 hm.put(".js", jsFilter);
		 hm.put(".java", javaFilter);
		 hm.put(".php", phpFilter);
		 hm.put(".json", jsonFilter);
		 hm.put(".ini", iniFilter);
		 hm.put(".xml", xmlFilter);
		 hm.put(".py", pyFilter);
		 FileChooser.ExtensionFilter filter = hm.get(f.getExtension());
		 if(filter == null) filter = allFilter;
		 fc.setSelectedExtensionFilter(filter);
		 File sf =  fc.showSaveDialog(new Stage());
		 if(sf == null) return false;
		 Path p = Paths.get(sf.getAbsolutePath());
		 if(model.saveAs(f, p)) {
			 EditFile ef = model.load(p).getResult();
			 f.setEditFile(ef);
			 editList.refresh();
			 return true;
		 } else {
			 Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("Save error");
			error.setTitle("Error");
			error.setContentText("An error ocurred while trying to save your file," +
				" please try again");
			error.show();
			removeFile(f, true);
			return false;
		 }

	}




	public void setMainApp(MainApp app) {
		this.app = app;
		editList.setItems(app.getEditItems());
		Platform.setImplicitExit(false);
		app.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				for(EditItem i:app.getEditItems()) {
					 if(!closeFile(i, false)) {
						 event.consume();
						 return;
					 }
				}
				Platform.exit();
			}

		 });
	}

	private boolean closeFile(EditItem f, boolean modifyList) {
		if(f.isFileSaved()) {
			currFile = null;
			removeFile(f, modifyList);
			return true;
		} else {
			Alert cl = new Alert(AlertType.CONFIRMATION);
			cl.setGraphic(new ImageView("file:resources/images/Save-icon-64.png"));
			Stage s = (Stage) cl.getDialogPane().getScene().getWindow();
			s.getIcons().add(new Image("file:resources/images/Interrogation-icon-32.png"));
			cl.setTitle("Close file");
			cl.setHeaderText("Save " + f.getFileName() + "?");
			cl.setContentText("The file has not been saved, do you want to save it before closing?");
			ButtonType yes = new ButtonType("Yes");
			ButtonType no = new ButtonType("No");
			ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			cl.getButtonTypes().setAll(yes, no, cancel);
			Optional<ButtonType> result = cl.showAndWait();
			if(result.get() == yes) {
				saveFile(f);
				currFile = null;
				removeFile(f, modifyList);
				return true;
			} else {
				if(result.get() == no) {
					currFile = null;
					removeFile(f, modifyList);
					return true;
				} else {
					return false;
				}
			}


		}
	}
	private void removeFile(EditItem f, boolean modifyList) {
		editList.getSelectionModel().clearSelection();
		if(modifyList) {
			app.getEditItems().remove(f);
			editList.getItems().remove(f);
			app.getEditorTexts().remove(f);
		}
		textEditor.setText("");
		textEditor.setEditable(false);
	}

	public void loadTextFileToEditor(EditItem f) {
		isFileTextChanging = true;
		currFile = f;
		textEditor.setEditable(true);
		textEditor.setText(app.getEditorTexts().get(f));
		editList.getSelectionModel().select(app.getEditItems().indexOf(f));
		isFileTextChanging = false;
	}

	public void loadTextFileToEditor(EditItem oldFile, EditItem newFile) {
		isFileTextChanging = true;
		textEditor.setEditable(true);
		app.setEditorText(oldFile, textEditor.getText());
		//System.out.println("OLd text: " + textEditor.getText());
		//System.out.println(app.getEditorTexts());
		textEditor.setText(app.getEditorTexts().get(newFile));
		editList.getSelectionModel().select(app.getEditItems().indexOf(newFile));
		currFile = newFile;
		isFileTextChanging = false;
	}

	public void loadNewFile(EditItem newFile) {
		String strContent = "";
		for (EditItem i : app.getEditItems()) {
			if(i.isAFile() == false && i.getFileName().equals(newFile.getFileName())) {
			Alert warning = new Alert(AlertType.WARNING);
			warning.setTitle("Open file");
			warning.setHeaderText("File already exists");
			warning.setContentText(newFile.getFileName() + " is already open in this editor");
			warning.show();
			return;
			}
		}
		if(!textEditor.isEditable()) textEditor.setEditable(true);
		app.getEditItems().add(newFile);
		editList.setItems(this.app.getEditItems());
		app.setEditorText(newFile, strContent);
		//System.out.println("load new file called");
		if(currFile == null) {
			textEditor.setEditable(true);
			loadTextFileToEditor(newFile);
		} else {
			loadTextFileToEditor(currFile, newFile);
		}
	}

	public void loadFile(IOResult<EditFile> r) {
		if(r.getSuccess()) {
			EditFile f = r.getResult();
			String[] content = new String[f.getContent().size()];
			content = f.getContent().toArray(content);
			String strContent = String.join("\n", content);
			EditItem item = new EditItem(f);
			for(EditItem e:app.getEditItems()) {
				if(e.getEditFile() == null) continue;
				if(e.getEditFile().getPath().equals(f.getPath())) {
					Alert warning = new Alert(AlertType.WARNING);
					warning.setTitle("Open file");
					warning.setHeaderText("File already opened");
					warning.setContentText(f.getFileName() + " is already open in this editor");
					warning.show();
					return;
				}
			}
			app.getEditItems().add(item);
			editList.setItems(this.app.getEditItems());
			app.setEditorText(item, strContent);
			if(!textEditor.isEditable()) textEditor.setEditable(true);
			if(currFile == null) {
				loadTextFileToEditor(item);
			} else {
				loadTextFileToEditor(currFile, item);
			}
		} else {
			Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("Open error");
			error.setTitle("Error");
			error.setContentText("An error ocurred while trying to open the file");
			error.show();
		}

	}


	@FXML
	public void initialize() {

		currFile = null;
		textEditor.setEditable(false);
		 editList.setCellFactory(new Callback<ListView<EditItem>,  ListCell<EditItem>>(){

	            public ListCell<EditItem> call(ListView<EditItem> p) {

	                ListCell<EditItem> cell = new ListCell<EditItem>(){

	                    @Override
	                    protected void updateItem(EditItem t, boolean bln) {
	                    	super.updateItem(t, bln);
	                    	if (t != null) {
	                        	String s = t.isFileSaved() ? " " : "*";
	                        	String n = t.getFileName().length() > 15 ? t.getFileName().substring(0, 16) + "..." : t.getFileName();
	                            setText(s + n);
	                        } else {
	                        	setText("");
	                        }
	                    }

	                };
	                return cell;
	            }
	        });

		 textEditor.textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
			        EditItem f = editList.getSelectionModel().getSelectedItem();
			        if(f != null && !isFileTextChanging)  {
			        f.setFileSaved(false);
			        //System.out.println("Content changed for: " + currFile);
			        //System.out.println(newValue);
			        app.setEditorText(currFile, newValue);
			        editList.refresh();
			        }
			    }
			});
		 textEditor.setEditable(false);
		 isFileTextChanging = false;
		 editList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EditItem>() {
			    @Override
			    public void changed(ObservableValue<? extends EditItem> observable, EditItem oldValue, EditItem newValue) {
			    	//System.out.println(oldValue + ";;" + newValue);
			    	if(oldValue != null && newValue != null && !oldValue.equals(newValue) && !isFileTextChanging){
			    		loadTextFileToEditor(oldValue, newValue);
			    		//System.out.println(1);
			    	}
			    	if(oldValue == null && newValue != null && app.getEditItems().size() == 1)  {
			    		loadTextFileToEditor(newValue);
			    		//System.out.println(2);
			    	}
			    }


			});

		 	textEditor.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if(event.getCode() == KeyCode.TAB) {
						String editorText = textEditor.getText();
						IndexRange r = textEditor.getSelection();
						String firstPart = editorText.substring(0, r.getStart() );
						String lastPart = editorText.substring(r.getEnd(), editorText.length());
						textEditor.setText(firstPart + "\t" + lastPart);
						textEditor.positionCaret(r.getStart() + 1);
						event.consume();
					}
				}


		 	});

			 newItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
			 openItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
			 saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
			 closeItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
			 copyItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
			 cutItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
			 pasteItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
			 deleteItem.setAccelerator(KeyCombination.keyCombination("Del"));
		 	 listScrollPane.setFitToWidth(true);
	}
}
