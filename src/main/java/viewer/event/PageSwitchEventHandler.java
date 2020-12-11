package viewer.event;

import javafx.event.EventHandler;

public abstract class PageSwitchEventHandler implements EventHandler<CustomEvent> {

    public abstract void onPageSwitch(String param);

    @Override
    public void handle(CustomEvent event) {
        event.invokeHandler(this);
    }
}
