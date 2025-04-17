package fsa.java.orm.demo.repository;

import java.util.List;
import java.util.Optional;

public interface MyJpaRepository<E, ID> {
    List<E> findAll();

    List<E> findAll(int page, int size);

    Optional<E> findById(ID id);

    E save(E entity);

    E update(E entity);

    void delete(ID id);
}
