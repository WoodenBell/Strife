package io.github.woodenbell.strife.model;

public class IOResult<T> {
	private T result;
	private boolean success;

	public IOResult(boolean success, T result) {
		this.result = result;
		this.success = success;
	}

	public T getResult() {
		return result;
	}

	public boolean getSuccess() {
		return success;
	}

	public boolean hasData() {
		return result != null;
	}
}
