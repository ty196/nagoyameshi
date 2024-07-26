package com.example.nagoyameshi.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.CategoryRestaurant;
import com.example.nagoyameshi.entity.RegularHolidayRestaurant;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.CategoryRestaurantRepository;
import com.example.nagoyameshi.repository.RegularHolidayRestaurantRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	// 予算のセレクトボックスの範囲
	private final Integer PRICE_MIN = 500;
	private final Integer PRICE_MAX = 10000;
	
	// 何円刻みにするか
	private final Integer PRICE_UNIT = 500;
	
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;
	private final RegularHolidayRestaurantRepository regularHolidayRestaurantRepository;
	private final CategoryRestaurantRepository categoryRestaurantRepository; 
	
	public RestaurantController(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository, 
			                    RegularHolidayRestaurantRepository regularHolidayRestaurantRepository, CategoryRestaurantRepository categoryRestaurantRepository){
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;
		this.regularHolidayRestaurantRepository = regularHolidayRestaurantRepository;
		this.categoryRestaurantRepository = categoryRestaurantRepository;
	}
	
	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			            @RequestParam(name = "categoryId", required = false) Integer categoryId,
			            @RequestParam(name = "price", required = false) Integer price,
			            @RequestParam(name = "order", required = false) String order,
			            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			            Model model) {
		Page<Restaurant> restaurantPage;
		
		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("lowestPriceAsc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByLowestPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
			} else if (order != null && order.equals("highestPriceDesc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByHighestPriceDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
			}
		} else if (categoryId != null) {
			if (order != null && order.equals("lowestPriceAsc")) {
				restaurantPage = restaurantRepository.findByIdOrderByLowestPriceAsc(categoryId, pageable);
			} else if (order != null && order.equals("highestPriceDesc")) {
				restaurantPage = restaurantRepository.findByIdOrderByHighestPriceDesc(categoryId, pageable);
			} else {
				restaurantPage = restaurantRepository.findByIdOrderByCreatedAtDesc(categoryId, pageable);
			}
		} else if (price != null) {
			//if (order != null && order.equals("priceAsc")) {
				//restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByPriceAsc(price);
			//}  else if (order != null && order.equals("priceDesc")) {
			//	restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByPriceDesc(price);
			//} else {
				restaurantPage = restaurantRepository.findByLowestPriceLessThanEqual(price, pageable);
			//}
		} else  {
			if (order != null && order.equals("lowestPriceAsc")) {
				restaurantPage = restaurantRepository.findAllByOrderByLowestPriceAsc(pageable);
			} else if (order != null && order.equals("highestPriceDesc")) {
				restaurantPage = restaurantRepository.findAllByOrderByHighestPriceDesc(pageable);
			} else {
				restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}
		
		List<Category> categories = categoryRepository.findAll();
		
		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		model.addAttribute("categories", categories);
		model.addAttribute("optionPrices", generatePriceList(PRICE_MIN, PRICE_MAX, PRICE_UNIT));
		
		return "restaurants/index";
	}
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model ) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        List<RegularHolidayRestaurant> regularHolidayRestaurants = regularHolidayRestaurantRepository.findByRestaurantOrderByRegularHolidayIdAsc(restaurant);
        List<CategoryRestaurant> categoryRestaurants = categoryRestaurantRepository.findByRestaurantOrderByIdAsc(restaurant);    
        
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("regularHolidayRestaurants", regularHolidayRestaurants);
        model.addAttribute("categoryRestaurants", categoryRestaurants);
        
        return "restaurants/show";
	}
        
	
	 private List<Integer> generatePriceList(Integer min, Integer max, Integer unit) {
	        List<Integer> prices = new ArrayList<>();
	        for (int i = 0; i <= (max - min) / unit; i++) {
	            int price = min + (unit * i);
	            prices.add(price);
	        }
	        return prices;
	    }    
}
