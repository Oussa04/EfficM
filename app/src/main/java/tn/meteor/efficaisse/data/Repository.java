package tn.meteor.efficaisse.data;

import java.util.List;

/**
 * Created by ahmed on 01/28/18.
 */

public interface Repository<T, ID> {

    T find(T parent);

    T find(int id);

    void delete(T parent);

    void delete(T parent, ID child);

    List<T> findAll();

    void save(T parent);

    void save(List<T> parents);


}
