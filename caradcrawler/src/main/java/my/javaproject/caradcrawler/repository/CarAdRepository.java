package my.javaproject.caradcrawler.repository;

import my.javaproject.caradcrawler.entity.CarAd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarAdRepository extends JpaRepository<CarAd, Long> {
    List<CarAd> findByMakeAndModelAndPrice(String make, String model, String price);

}