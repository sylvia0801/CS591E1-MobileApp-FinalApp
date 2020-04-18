package Model;

import java.sql.Time;

public class Chat {
    //the previous three attribute form a unique record
    private String fromUserId;
    private String toUserId;
    private String index;//order time
    private String dialog;

    public String getFromUserEmail() {
        return fromUserId;
    }

    public void setFromUserEmail(String fromUserEmail) {
        this.fromUserId = fromUserEmail;
    }

    public String getToUserEmail() {
        return toUserId;
    }

    public void setToUserEmail(String toUserEmail) {
        this.toUserId = toUserEmail;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

    public Chat(String fromUserId, String toUserId, String index, String dialog) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.index = index;
        this.dialog = dialog;
    }
    public String toString(){
        return fromUserId+" -> "+toUserId+" "+index+": "+dialog;
    }
}
