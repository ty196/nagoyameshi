package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.favoriteService = favoriteService;
	}
	
	@GetMapping("/favorites")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			            RedirectAttributes redirectAttributes, Model model) {
		
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
	//	String roleName = user.getRole().getName();
		
	//	if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		
		model.addAttribute("favoritePage", favoritePage);
		
		return "favorites/index";
	}
	
	// お気に入り登録
	@PostMapping("/restaurants/{restaurantId}/favorites/create")
	public String create(@PathVariable(name = "restaurantId") Integer restaurantId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             RedirectAttributes redirectAttributes, Model model) {
		
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		
		favoriteService.create(restaurant, user);
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");
		
		return "redirect:/restaurants/{restaurantId}";
	}
	
	// お気に入り解除(削除)
	@PostMapping("favorites/{favoriteId}/delete")
	public String delete(@PathVariable(name = "favoriteId") Integer favoriteId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
		
//		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Favorite favorite = favoriteRepository.getReferenceById(favoriteId);
		favoriteService.delete(favorite);
		
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		
		String referer = httpServletRequest.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/restaurants/{restaurantId}"); 
	}

}
