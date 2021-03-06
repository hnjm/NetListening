package de.terrarier.netlistening.api;

import de.terrarier.netlistening.api.type.DataType;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0
 * @author Terrarier2111
 * @param <T> the type of the data contained in this DataComponent.
 */
public final class DataComponent<T> {

	private final DataType<T> type;
	private T content;

	@Deprecated
	public DataComponent(@NotNull DataType<T> type) {
		this.type = type;
	}
	
	public DataComponent(@NotNull DataType<T> type, T content) {
		this.type = type;
		this.content = content;
	}

	/**
	 * @return the type of the content.
	 */
	@NotNull
	public DataType<T> getType() {
		return type;
	}

	/**
	 * @return the contained data.
	 */
	public T getData() {
		return content;
	}

	/**
	 * Sets the passed data as the content.
	 *
	 * @param data the new content value.
	 */
	@Deprecated
	public void setData(T data) {
		content = data;
	}
	
}
