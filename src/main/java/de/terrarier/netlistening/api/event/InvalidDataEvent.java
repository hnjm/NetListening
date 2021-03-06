package de.terrarier.netlistening.api.event;

import de.terrarier.netlistening.Connection;
import org.jetbrains.annotations.NotNull;

/**
 * This event gets called when invalid data was received.
 *
 * @since 1.0
 * @author Terrarier2111
 */
public final class InvalidDataEvent extends ConnectionEvent {

	private final DataInvalidReason reason;
	private final byte[] data;
	
	public InvalidDataEvent(@NotNull Connection connection, @NotNull DataInvalidReason reason, byte[] data) {
		super(connection);
		this.reason = reason;
		this.data = data;
	}

	/**
	 * @return the reason why the data was marked as invalid.
	 */
	@NotNull
	public DataInvalidReason getReason() {
		return reason;
	}

	/**
	 * @return the data which was detected as invalid.
	 */
	public byte[] getData() {
		return data;
	}

}
