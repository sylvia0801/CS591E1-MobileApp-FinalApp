package Model;

public class Favourite {
    //Every Favourite record is unique

    private String userId;
    private Item item;

    public Favourite(String userId, Item item) {
        this.userId = userId;
        this.item = item;

    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String toString(){
        return "uid: "+userId+",iid: "+item;
    }
}
