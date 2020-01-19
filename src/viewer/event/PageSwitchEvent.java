package viewer.event;

import javafx.event.EventType;

public class PageSwitchEvent extends CustomEvent {

    public static final EventType<CustomEvent> PAGE_SWITCH_EVENT_TYPE = new EventType(CUSTOM_EVENT_TYPE, "PageSwitchEvent");

    private final String param;

    public PageSwitchEvent(String param) {
        super(PAGE_SWITCH_EVENT_TYPE);
        this.param = param;
    }



    @Override
    public void invokeHandler(PageSwitchEventHandler handler) {
        handler.onPageSwitch(param);
    }
}
