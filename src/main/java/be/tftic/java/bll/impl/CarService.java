package be.tftic.java.bll.impl;

import be.tftic.java.bll.AbstractCrudService;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.domain.cdi.ToInstanciate;
import be.tftic.java.domain.models.entity.Car;

@ToInstanciate("carService")
public class CarService extends AbstractCrudService<Car, Long> {
    public CarService() {
        String configName = Car.class.getAnnotation(Table.class).configName();
        super(ORMLoader.getLoader(configName).getRepo(Car.class), Car.class);
    }
}
