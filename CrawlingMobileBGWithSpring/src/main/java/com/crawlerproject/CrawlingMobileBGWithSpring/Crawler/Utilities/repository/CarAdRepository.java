package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.repository;

import com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities.model.CarAd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarAdRepository extends JpaRepository<CarAd, Long> {

}
