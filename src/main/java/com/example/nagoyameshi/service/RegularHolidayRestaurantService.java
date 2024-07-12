package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.RegularHoliday;
import com.example.nagoyameshi.entity.RegularHolidayRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RegularHolidayRepository;
import com.example.nagoyameshi.repository.RegularHolidayRestaurantRepository;

@Service
public class RegularHolidayRestaurantService {
	private final RegularHolidayRepository regularHolidayRepository;
	private final RegularHolidayRestaurantRepository regularHolidayRestaurantRepository;
	
	public RegularHolidayRestaurantService(RegularHolidayRepository regularHolidayRepository, RegularHolidayRestaurantRepository regularHolidayRestaurantRepository) {
		this.regularHolidayRepository = regularHolidayRepository;
		this.regularHolidayRestaurantRepository = regularHolidayRestaurantRepository;
	}
	
	@Transactional
	public void create(List<Integer> regularHolidayIds, Restaurant restaurant) {
		for (Integer regularHolidayId : regularHolidayIds) {
			RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
			RegularHoliday regularHoliday = regularHolidayRepository.getReferenceById(regularHolidayId);
			
			regularHolidayRestaurant.setRestaurant(restaurant);
			regularHolidayRestaurant.setRegularHoliday(regularHoliday);
			
			regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
		}
	}
	
	@Transactional
	public void update(List<Integer> newRegularHolidayIds, Restaurant restaurant) {
		List<RegularHolidayRestaurant> existingRegularHolidayRestaurants = regularHolidayRestaurantRepository.findByRestaurantOrderByRegularHoliday_IdAsc(restaurant);
        List<Integer> existingRegularHolidayIds = regularHolidayRestaurantRepository.findRegularHolidayIdsByRestaurantOrderByRegularHoliday_IdAsc(restaurant);
        
        // 新しいリストがnullの場合、すべての関連付けを削除する
        if (newRegularHolidayIds == null) {
        	for (RegularHolidayRestaurant existing : existingRegularHolidayRestaurants) {
        		regularHolidayRestaurantRepository.delete(existing);
        	}
        	
        }
        
        // 既存のCategoryRestaurantが新しいリストに存在しない場合は削除する
        for (RegularHolidayRestaurant existing : existingRegularHolidayRestaurants) {
        	if (!newRegularHolidayIds.contains(existing.getRegularHoliday().getId())) {
        		regularHolidayRestaurantRepository.delete(existing);
        	}
        }
        
        // 新しいリストのIDが既存のCategoryRestaurantに存在しない場合は新たに作成する4
        for (Integer newRegularHolidayId : newRegularHolidayIds) {
        	if (newRegularHolidayId != null && !existingRegularHolidayIds.contains(newRegularHolidayId)) {
        		RegularHolidayRestaurant regularHolidayRestaurant = new RegularHolidayRestaurant();
        		RegularHoliday regularHoliday = regularHolidayRepository.getReferenceById(newRegularHolidayId);
        		
        		regularHolidayRestaurant.setRestaurant(restaurant);
        		regularHolidayRestaurant.setRegularHoliday(regularHoliday);
        		
        		regularHolidayRestaurantRepository.save(regularHolidayRestaurant);
        	}
        }
	}
	
	 @Transactional
	    public void deleteByRestaurant(Restaurant restaurant) {
	        regularHolidayRestaurantRepository.deleteByRestaurant(restaurant);
	    } 


}
