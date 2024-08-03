package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants/{restaurantId}/reviews")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, ReviewService reviewService) {
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.reviewService = reviewService;
	}
	
	// レビュー一覧
	@GetMapping
	public String index(@PathVariable(name = "restaurantId") Integer restaurantId, Model model,
			            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
		
		Page<Review> reviewPage = null;
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		String roleName = user.getRole().getName();
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		
		if (roleName.equals("ROLE_FREE_MEMBER")) {
			reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
		}
		
		boolean hasPostedReview = reviewService.hasUserAlreadyReviewed(restaurant, user);
		
		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("hasPostedReview", hasPostedReview);
		model.addAttribute("roleName", roleName);
		
		return "reviews/index";
		
	}
	
	@GetMapping("/register")
	public String register(@PathVariable(name = "restaurantId") Integer restaurantId,
			               @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			               RedirectAttributes redirectAttributes, Model model) {
//		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		
		ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm();
        reviewRegisterForm.setScore(5);
		
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reviewRegisterForm", reviewRegisterForm);
		
		return "reviews/register";
	}
	
	// レビュー投稿
	@PostMapping("/create")
	public String create(@PathVariable(name = "restaurantId") Integer restaurantId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
			             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			return "reviews/register";
		}
		
		reviewService.create(restaurant, user, reviewRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
		
		return "redirect:/restaurants/{restaurantId}/reviews";
	}
	
	// レビュー編集
	@GetMapping("/{reviewId}/edit")
	public String edit(@PathVariable(name = "restaurantId") Integer restaurantId,
			           @PathVariable(name = "reviewId") Integer reviewId,
			           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			           RedirectAttributes redirectAttributes, Model model) {
//		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		Review review = reviewRepository.getReferenceById(reviewId);
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(), review.getScore(), review.getContent());
		
		model.addAttribute("restaurant", restaurant);
        model.addAttribute("review", review);
        model.addAttribute("reviewEditForm", reviewEditForm);
        
        return "reviews/edit";
	}
	
	// レビュー更新
	@PostMapping("/{reviewId}/update")
	public String update(@PathVariable(name = "restaurantId") Integer restaurantId,
			             @PathVariable(name = "reviewId") Integer reviewId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             @ModelAttribute @Validated ReviewEditForm reviewEditForm,
			             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		
//		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		Review review = reviewRepository.getReferenceById(reviewId);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			model.addAttribute("review", review);
			return "reviews/edit";
		}
		
		reviewService.update(reviewEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		
		return "redirect:/restaurants/{restaurantId}/reviews";
	}
	
	// レビュー削除
	@PostMapping("/{reviewId}/delete")
	public String delete(@PathVariable(name = "restaurantId") Integer restaurantId,
			             @PathVariable(name = "reviewId") Integer reviewId,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             RedirectAttributes redirectAttributes) {
		
//		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
//		String roleName = user.getRole().getName();
//		
//		if (roleName.equals("ROLE_FREE_MEMBER")){
//			redirectAttributes.addFlashAttribute("errorMessage", "この機能を利用するには有料会員への登録が必要です。");
//			return "redirect:/subscription/register";
//		}
		
		Review review = reviewRepository.getReferenceById(reviewId);
		reviewService.delete(review);
		
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		
		return "redirect:/restaurants/{restaurantId}/reviews";
	}

}
