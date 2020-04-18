package DAO;

import java.util.List;
import Model.Chat;

public interface ChatRepository {
    void save(Chat chat); // save to chat table
    List<Chat> getChatByIdTask(String fromUserEmail, String toUserEmail);// need sort by index time
    void saveListChat( List<Chat> l);

}
