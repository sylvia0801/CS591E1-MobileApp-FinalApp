package DAO;

import java.util.List;

import Model.Picture;

public interface PictureRepository {
    List<Picture> getPictureByItemId(String itemId);
    void save(Picture picture);
}
