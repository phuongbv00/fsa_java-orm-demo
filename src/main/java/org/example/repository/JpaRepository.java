package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface JpaRepository<E, ID> {
    List<E> findAll();

    List<E> findAll(int page, int size);

    Optional<E> findById(ID id);

    E save(E entity);

    E update(E entity);

    void delete(ID id);

    <R> R withTransaction(Supplier<R> processor);
}
