package eu.patrickgeiger.fxpdf.event;

import javafx.event.EventType;
import lombok.NonNull;

/**
 * @author Patr1ick
 */
public class ViewerActionEvent extends ViewerEvent {

    public static final EventType<ViewerEvent> VIEWER_ACTION_EVENT_TYPE = new EventType(VIEWER_EVENT_TYPE, "ViewerEvent");

    private final Parameter parameter;

    /**
     * @param parameter The parameter represents the action which was done
     */
    public ViewerActionEvent(@NonNull Parameter parameter) {
        super(VIEWER_ACTION_EVENT_TYPE);
        this.parameter = parameter;
    }

    @Override
    public void invokeHandler(ViewerEventHandler handler) {
        handler.onViewerEvent(parameter);
    }
}
