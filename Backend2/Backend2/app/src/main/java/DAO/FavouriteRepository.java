package DAO;

import java.util.List;
import Model.Favourite;


public interface FavouriteRepository {
    void save(Favourite favourite);
   void getFavouritesByUserId(String userId); // list of  fav items
   void deleteFav(Favourite fav);
}
