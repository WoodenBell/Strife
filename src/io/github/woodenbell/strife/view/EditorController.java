package io.github.woodenbell.strife.view;




import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import io.github.woodenbell.strife.MainApp;
import io.github.woodenbell.strife.model.EditFile;
import io.github.woodenbell.strife.model.EditItem;
import io.github.woodenbell.strife.model.EditorModel;
import io.github.woodenbell.strife.model.IOResult;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

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
	private void onNew() {
		TextInputDialog d = new TextInputDialog();
		d.setTitle("New file");
		d.setHeaderText("Choose the file name");
		d.setContentText("");
		Optional<String> res = d.showAndWait();
		if(res.isPresent()) {
			loadNewFile(new EditItem(res.get()));
		} else {
			return;
		}
	}

	@FXML
	private void onLoad() {
		File f;
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose file to open");
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
			for(EditItem i:app.getEditItems()) {
				saveFile(i);
			}
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
		info.setHeaderText(null);
		info.setTitle("About");
		info.setContentText("Author: WoodenBell\nE-Mail: codingcookie@gmail.com\nVersion: 0.0.1");
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

	private void saveFile(EditItem f) {
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
					return;
				} else {
					f.setFileSaved(true);
					editList.refresh();
				}
			} else {
				f.setContent(Arrays.asList(textEditor.getText().split("\n")));
				if(!model.save(f)) {
					Alert error = new Alert(AlertType.ERROR);
					error.setHeaderText("Save error");
					error.setTitle("Error");
					error.setContentText("An error ocurred while trying to save your file," +
					 " please try again");
					error.show();
					removeFile(f, true);
					return;
				} else {
					f.setFileSaved(true);
					editList.refresh();
				}
			}
		} else {
			saveFileAs(f);
		}
	}

	private void saveFileAs(EditItem f) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save file " + f.getFileName());
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
		 fc.getExtensionFilters().addAll(allFilter, txtFilter, mdFilter, cssFilter, jsFilter, htmlFilter, jsonFilter,
				 phpFilter, javaFilter, pyFilter);
		 File sf =  fc.showSaveDialog(new Stage());
		 if(sf == null) return;
		 Path p = Paths.get(sf.getAbsolutePath());
		 if(model.saveAs(f, p)) {
			 EditFile ef = model.load(p).getResult();
			 f.setEditFile(ef);
			 editList.refresh();
		 } else {
			 Alert error = new Alert(AlertType.ERROR);
			error.setHeaderText("Save error");
			error.setTitle("Error");
			error.setContentText("An error ocurred while trying to save your file," +
				" please try again");
			error.show();
			removeFile(f, true);
			return;
		 }

	}




	public void setMainApp(MainApp app) {
		this.app = app;
		editList.setItems(app.getEditItems());
		app.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				for(EditItem i:app.getEditItems()) {
					closeFile(i, false);
				}
				Platform.exit();
			}

		 });
	}

	private void closeFile(EditItem f, boolean modifyList) {
		if(f.isFileSaved()) {
			currFile = null;
			removeFile(f, modifyList);
		} else {
			Alert cl = new Alert(AlertType.CONFIRMATION);
			cl.setTitle("Close file");
			cl.setHeaderText("Close " + f.getFileName() + "?");
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
			} else {
				if(result.get() == no) {
					currFile = null;
					removeFile(f, modifyList);
				} else {
					return;
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
		textEditor.setEditable(true);
		editList.getSelectionModel().select(app.getEditItems().indexOf(f));
		textEditor.setText(app.getEditorTexts().get(f));
		currFile = f;
		isFileTextChanging = false;
	}

	public void loadTextFileToEditor(EditItem oldFile, EditItem newFile) {
		isFileTextChanging = true;
		textEditor.setEditable(true);
		app.getEditorTexts().put(oldFile, textEditor.getText());
		editList.getSelectionModel().select(app.getEditItems().indexOf(newFile));
		textEditor.setText(app.getEditorTexts().get(newFile));
		currFile = newFile;
		isFileTextChanging = false;
	}

	public void loadNewFile(EditItem newFile) {
		String strContent = "";
		if(app.getEditItems().contains(newFile)) {
			Alert warning = new Alert(AlertType.WARNING);
			warning.setTitle("Open file");
			warning.setHeaderText("File already exists");
			warning.setContentText(newFile.getFileName() + " is already open in this editor");
			warning.show();
			return;
		}
		app.getEditItems().add(newFile);
		editList.setItems(this.app.getEditItems());
		app.getEditorTexts().put(newFile, strContent);
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
			app.getEditorTexts().put(item, strContent);
			if(currFile == null) {
				textEditor.setEditable(true);
				loadTextFileToEditor(item);
			} else {
				loadTextFileToEditor(currFile, item);
			}
		}

	}


	@FXML
	public void initialize() {

		currFile = null;

		 editList.setCellFactory(new Callback<ListView<EditItem>,  ListCell<EditItem>>(){

	            public ListCell<EditItem> call(ListView<EditItem> p) {

	                ListCell<EditItem> cell = new ListCell<EditItem>(){

	                    @Override
	                    protected void updateItem(EditItem t, boolean bln) {
	                        super.updateItem(t, bln);
	                        if (t != null) {
	                        	String s = t.isFileSaved() ? "" : "*";
	                            setText(t.getFileName() + s);
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
			        if(f != null && !isFileTextChanging)  f.setFileSaved(false);
			        editList.refresh();
			    }
			});
		 textEditor.setEditable(false);
		 isFileTextChanging = false;
		 editList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EditItem>() {
			    @Override
			    public void changed(ObservableValue<? extends EditItem> observable, EditItem oldValue, EditItem newValue) {
			    	if(oldValue != null && newValue != null && !oldValue.equals(newValue)) loadTextFileToEditor(oldValue, newValue);
			    	if(oldValue == null && newValue != null && app.getEditItems().size() == 1) loadTextFileToEditor(newValue);
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
	}
}
