package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryRestaurantRepository;

@Service
public class CategoryRestaurantService {
	private final CategoryRestaurantRepository categoryRestaurantRepository;
	private final CategoryRepository categoryRepository;
	
	public CategoryRestaurantService(CategoryRestaurantRepository categoryRestaurantRepository, CategoryRepository categoryRepository) {
		this.categoryRestaurantRepository = categoryRestaurantRepository;
		this.categoryRepository = categoryRepository;
	}
	
	@Transactional
	public void create(List<Integer> categoryIds, Restaurant restaurant) {
		for (Integer categoryId : categoryIds) {
			if (categoryId == null) {
				continue;
			}
			
			CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
			Category category = categoryRepository.getReferenceById(categoryId);
			
			categoryRestaurant.setRestaurant(restaurant);
			categoryRestaurant.setCategory(category);
			
			categoryRestaurantRepository.save(categoryRestaurant);
		}
	}
	
	@Transactional
	public void update(List<Integer> newCategoryIds, Restaurant restaurant) {
		List<CategoryRestaurant> existingCategoryRestaurants = categoryRestaurantRepository.findByRestaurantOrderByIdAsc(restaurant);
        List<Integer> existingCategoryIds = categoryRestaurantRepository.findCategoryIdsByRestaurantOrderByIdAsc(restaurant);
        
        // 新しいリストがnullの場合、すべての関連付けを削除する
        if (newCategoryIds == null) {
        	for (CategoryRestaurant existing : existingCategoryRestaurants) {
        		categoryRestaurantRepository.delete(existing);
        	}
        	
        }
        
        // 既存のCategoryRestaurantが新しいリストに存在しない場合は削除する
        for (CategoryRestaurant existing : existingCategoryRestaurants) {
        	if (!newCategoryIds.contains(existing.getCategory().getId())) {
        		categoryRestaurantRepository.delete(existing);
        	}
        }
        
        // 新しいリストのIDが既存のCategoryRestaurantに存在しない場合は新たに作成する4
        for (Integer newCategoryId : newCategoryIds) {
        	if (newCategoryId != null && !existingCategoryIds.contains(newCategoryId)) {
        		CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
        		Category category = categoryRepository.getReferenceById(newCategoryId);
        		
        		categoryRestaurant.setRestaurant(restaurant);
        		categoryRestaurant.setCategory(category);
        		
        		categoryRestaurantRepository.save(categoryRestaurant);
        	}
        }
	}
	
	 @Transactional
	    public void deleteByRestaurant(Restaurant restaurant) {
	        categoryRestaurantRepository.deleteByRestaurant(restaurant);
	    }   
	    
	    @Transactional
	    public void deleteByCategory(Category category) {
	        categoryRestaurantRepository.deleteByCategory(category);
	    }

}
