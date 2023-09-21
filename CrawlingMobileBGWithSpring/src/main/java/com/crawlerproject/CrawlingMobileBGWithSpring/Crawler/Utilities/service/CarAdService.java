package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.service;

import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.model.CarAd;
import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.repository.CarAdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarAdService {
    private final CarAdRepository carAdRepository;

    @Autowired
    public CarAdService(CarAdRepository carAdRepository) {
        this.carAdRepository = carAdRepository;
    }

    public List<CarAd> getAllCarAds() {
        return carAdRepository.findAll();
    }

    public CarAd saveCarAd(CarAd carAd) {
        return carAdRepository.save(carAd);
    }
}
