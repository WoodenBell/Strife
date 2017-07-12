package io.github.woodenbell.strife.model;

import java.util.ArrayList;
import java.util.List;

public class EditItem {
	private boolean isSaved;
	private boolean hasFile;
	private List<String> content;
	private EditFile f;
	private String fileName;
	private String extension;
	private String onlyFileName;

	public EditItem(EditFile f) {
		isSaved = true;
		hasFile = true;
		this.f = f;
		content = f.getContent();
		fileName = f.getFileName();
		extension = "." + fileName.split("\\.")[fileName.split("\\.").length - 1];
		onlyFileName = fileName.split(extension)[0];
	}
	public EditItem(String fileName) {
		isSaved = false;
		hasFile = false;
		f = null;
		content = new ArrayList<String>();
		this.fileName = fileName;
		extension = "." + fileName.split("\\.")[fileName.split("\\.").length - 1];
		onlyFileName = fileName.split(extension)[0];
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isFileSaved() {
		return isSaved;
	}

	public boolean isAFile() {
		return hasFile;
	}

	public List<String> getContent() {
		return content;
	}

	public EditFile getEditFile() {
		return f;
	}

	public void setContent(List<String> c) {
		content = c;
	}

	public void setFileSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	public void setIsAFile(boolean is) {
		hasFile = is;
	}

	public void setEditFile(EditFile f) {
		this.f = f;
		content = f.getContent();
		fileName = f.getFileName();
		extension = "." + fileName.split("\\.")[fileName.split("\\.").length - 1];
		onlyFileName = fileName.split(extension)[0];
		hasFile = true;
	}

	public String getExtension() {
		return extension;
	}

	public String getOnlyFileName() {
		return onlyFileName;
	}

	public String toString() {
		return fileName;
	}
	}
