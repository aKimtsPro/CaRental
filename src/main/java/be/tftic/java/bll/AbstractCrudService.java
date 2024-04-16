package be.tftic.java.bll;

import be.tftic.java.bll.exceptions.NotFoundException;
import be.tftic.java.dal.orm.repository.IRepository;

import java.util.List;

public abstract class AbstractCrudService<T, ID> implements ICrudService<T, ID> {

    private final IRepository<T, ID> repository;
    private final Class<T> entityClass;

    public AbstractCrudService(IRepository<T, ID> repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    @Override
    public T getOne(ID id) {
        return repository.getOne(id)
                .orElseThrow(() -> new NotFoundException(entityClass, id, "id"));
    }

    @Override
    public List<T> getAll() {
        return repository.getAll();
    }

    @Override
    public T create(T t) {
        return repository.insert(t);
    }

    @Override
    public T update(T t) {
        return repository.update(t);
    }

    @Override
    public T delete(ID id) {
        T t = getOne(id);
        repository.delete(id);
        return t;
    }

    protected IRepository<T, ID> getRepository() {
        return repository;
    }
}
