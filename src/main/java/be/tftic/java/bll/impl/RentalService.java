package be.tftic.java.bll.impl;

import be.tftic.java.bll.AbstractCrudService;
import be.tftic.java.bll.exceptions.NotFoundException;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.repository.IRepository;
import be.tftic.java.dal.orm.repository.UserRepository;
import be.tftic.java.domain.cdi.ToInstanciate;
import be.tftic.java.domain.models.entity.Car;
import be.tftic.java.domain.models.entity.Rental;
import be.tftic.java.domain.models.entity.User;

import java.time.Duration;
import java.time.LocalDateTime;

@ToInstanciate("rentalService")
public class RentalService extends AbstractCrudService<Rental, Long> {

    private final IRepository<Car, Long> carRepo;
    private final IRepository<User, Long> userRepo;

    public RentalService() {
        String configName = Rental.class.getAnnotation(Table.class).configName();
        super(ORMLoader.getLoader(configName).getRepo(Rental.class), Rental.class);
        this.carRepo = ORMLoader.getLoader(configName).getRepo(Car.class);
        this.userRepo = ORMLoader.getLoader(configName).getRepo(User.class);
    }


    public Rental createRental(Long carId, LocalDateTime start, LocalDateTime end, String username){
        if( end.isBefore(start) )
            throw new RuntimeException("Invalid rental");

        Car car = carRepo.getOne(carId)
                .orElseThrow(() -> new NotFoundException(Car.class,carId, "id"));

        User user = getUserRepo().getByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class, username, "username"));

        long days = Duration.between(start, end).toDays();

        Rental rental = new Rental();
        rental.setStartDate(start.toLocalDate());
        rental.setEndDate(end.toLocalDate());
        rental.setRented(car);
        rental.setRenter(user);
        rental.setPrice(car.getDailyRate() * days);
        rental.setDeposit(car.getDailyRate() * days);
        return getRepository().insert(rental);
    }

    public UserRepository getUserRepo(){
        return (UserRepository) userRepo;
    }
}
