package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
public class HomeController {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;
	
	public HomeController(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;
	}
	
	@GetMapping("/")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		if (userDetailsImpl != null && "ROLE_ADMIN".equals(userDetailsImpl.getUser().getRole().getName())) {
			return "redirect:/admin";
		}
		
		//List<Restaurant> restaurants = restaurantRepository.findAll();
		List<Restaurant> newRestaurants = restaurantRepository.findTop6ByOrderByCreatedAtDesc();
		List<Category> categories = categoryRepository.findAll();
		
		//model.addAttribute("restaurants", restaurants);
		model.addAttribute("newRestaurants", newRestaurants);
		model.addAttribute("categories", categories);
		
		return "index";
	}

}
