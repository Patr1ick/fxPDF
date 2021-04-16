package eu.patrickgeiger.fxpdf.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Patr1ick
 */
public abstract class ViewerEvent extends Event {

    public static final EventType<ViewerEvent> VIEWER_EVENT_TYPE = new EventType(ANY);

    public ViewerEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(ViewerEventHandler handler);
}
