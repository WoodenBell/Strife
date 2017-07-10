package io.github.woodenbell.strife.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class EditorModel {
	public boolean save(EditItem f) {
			try {
				Files.write(f.getEditFile().getPath(), f.getContent());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
	}


	public boolean saveAs(EditItem f, Path choosenPath) {
		if(choosenPath.toFile().exists() && choosenPath.toFile().isFile()) {
			try {
				Files.write(choosenPath, f.getContent(), StandardOpenOption.CREATE_NEW);
				f.setFileSaved(true);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				Files.write(choosenPath, f.getContent(), StandardOpenOption.CREATE);
				f.setFileSaved(true);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public IOResult<EditFile> load(Path file) {
		try {
			List<String> lines = Files.readAllLines(file);
			return new IOResult<EditFile>(true, new EditFile(file.getFileName().toString(), file, lines));
		} catch (IOException e) {
			e.printStackTrace();
			return new IOResult<EditFile>(false, null);
		}
	}
}
