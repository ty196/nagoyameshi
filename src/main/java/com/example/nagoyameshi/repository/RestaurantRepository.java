package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	 public Page<Restaurant> findByNameLike(String nameKeyword, Pageable pageable);
	 
	 public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	 public Page<Restaurant> findByNameLikeOrAddressLikeOrderByLowestPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);
	 public Page<Restaurant> findByNameLikeOrAddressLikeOrderByHighestPriceDesc(String nameKeyword, String addressKeyword, Pageable pageable);

	 @Query("SELECT r FROM Restaurant r JOIN r.categoryRestaurants cr WHERE cr.category.id = :categoryId")
	 public Page<Restaurant> findByIdOrderByCreatedAtDesc(@Param("categoryId") Integer categoryId, Pageable pageable);
	 public Page<Restaurant> findByIdOrderByLowestPriceAsc(@Param("categoryId") Integer categoryId, Pageable pageable);
	 public Page<Restaurant> findByIdOrderByHighestPriceDesc(@Param("categoryId") Integer categoryId, Pageable pageable);


	 public Page<Restaurant> findByLowestPriceLessThanEqual(Integer price, Pageable pageable);
	// public Page<Restaurant> findByLowestPriceLessThanEqualOrderByPriceAsc(Integer price);
	// public Page<Restaurant> findByLowestPriceLessThanEqualOrderByPriceDesc(Integer price);

	 
	 public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
     public Page<Restaurant> findAllByOrderByLowestPriceAsc(Pageable pageable);
     public Page<Restaurant> findAllByOrderByHighestPriceDesc(Pageable pageable);


	 
	 public List<Restaurant> findTop6ByOrderByCreatedAtDesc();

}
