package client.responses;

/**
 * Created by flbaue on 09.11.14.
 */
public abstract class AbstractResponse implements Response {

    private final String command;
    private final String payload;

    protected AbstractResponse(String command, String payload) {
        this.command = command;
        this.payload = payload;
    }

    protected AbstractResponse(String command) {
        this(command, null);
    }

    @Override
    public String getPayload() {
        return payload != null ? payload : EMPTY_PAYLOAD;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public String toString() {
        return getCommand() + " " + getPayload();
    }
}
