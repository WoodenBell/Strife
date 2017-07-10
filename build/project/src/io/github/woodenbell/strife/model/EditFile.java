package io.github.woodenbell.strife.model;




import java.nio.file.Path;
import java.util.List;

public class EditFile {

	private String fileName;
	private Path path;
	private List<String> content;
	private boolean isSaved;

	public EditFile() {
		this(null, null, null);
	}



	public EditFile(String fileName, Path path, List<String> content) {
		this.fileName = fileName;
		this.path = path;
		this.content = content;
		isSaved = true;
	}

	public boolean isFileSaved() {
		return isSaved;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public List<String> getContent() {
		return content;
	}

}
