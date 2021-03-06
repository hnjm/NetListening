package de.terrarier.netlistening.api.event;

import de.terrarier.netlistening.Application;
import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataComponent;
import de.terrarier.netlistening.api.DataContainer;
import de.terrarier.netlistening.api.Type;
import de.terrarier.netlistening.network.PreparedListener;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0
 * @author Terrarier2111
 */
public final class DataHandler {

	private final List<PreparedListener> listeners = new ArrayList<>();
	private final Application application;
	
	public DataHandler(@NotNull Application application) {
		this.application = application;
	}

	public void processData(@NotNull List<DataComponent<?>> data, @NotNull Channel channel) {
        final int dataSize = data.size();
        if (dataSize == 0) {
            return;
        }

        final Connection connection = application.getConnection(channel);
        final DataContainer container = new DataContainer(data);
        final DecodeEvent event = new DecodeEvent(connection, container);
        final int listenerSize = listeners.size();
        for (int i = 0; i < listenerSize; i++) {
            final PreparedListener listener = listeners.get(i);
            final Type[] types = listener.getTypes();
            final int length = types.length;
            if (length != 0) {
                if (length != dataSize) {
                    continue;
                }

                int index = 0;
                boolean passedCheck = true;
                for (int id = 0; id < dataSize; id++) {
                    final DataComponent<?> comp = data.get(id);
                    if (types[index++].getId() != comp.getType().getId()) {
                        passedCheck = false;
                        break;
                    }
                }

                if (!passedCheck) {
                    continue;
                }
            }
            container.resetReaderIndex();
            listener.getWrapped().trigger(event);
        }
	}

	public void addListener(@NotNull DecodeListener listener) {
		listeners.add(new PreparedListener(listener));
	}

	public void unregisterListeners() {
		listeners.clear();
	}

}
