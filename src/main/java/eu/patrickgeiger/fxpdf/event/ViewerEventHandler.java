package eu.patrickgeiger.fxpdf.event;

import javafx.event.EventHandler;
import lombok.NonNull;

/**
 * @author Patr1ick
 */
public abstract class ViewerEventHandler implements EventHandler<ViewerEvent> {

    public abstract void onViewerEvent(@NonNull Parameter parameter);

    @Override
    public void handle(ViewerEvent event) {
        event.invokeHandler(this);
    }
}
