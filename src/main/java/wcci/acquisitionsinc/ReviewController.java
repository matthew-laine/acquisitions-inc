package wcci.acquisitionsinc;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({ "/all-reviews/", "/all-reviews" })
public class ReviewController {

	@Resource
	private ReviewRepository reviewRepo;

	@Resource
	private CategoryRepository categoryRepo;

	@Resource
	private ReviewTagRepository reviewTagRepo;

	@RequestMapping({ "", "/" })
	public String findAll(Model model) {
		model.addAttribute("reviewsAttribute", reviewRepo.findAll());
		return "reviewsTemplate";
	}

	@RequestMapping({ "/{id}", "/{id}/" })
	public String getReview(@PathVariable("id") Long id, Model model) {
		model.addAttribute("reviewAttribute", reviewRepo.findById(id).get());
		return "reviewTemplate";
	}

	@PostMapping({ "/add-review", "/add-review/" })
	public String addReview(String title, String imageUrl, String content, String category, String reviewTag) {

		Review reviewToAdd = new Review(title, imageUrl, content);
		reviewRepo.save(reviewToAdd);

		Category categoryToAdd = new Category(category);
		if (categoryRepo.findByName(categoryToAdd.getName()) == null) {
			categoryRepo.save(categoryToAdd);
		}
		
		reviewRepo.findById(reviewToAdd.getId()).get().addCategory(categoryRepo.findByName(categoryToAdd.getName()));

		reviewTag.replace(" ", "");
		String[] reviewTags = reviewTag.split(",");
		for (String tagToAdd : reviewTags) {
			ReviewTag reviewTagToAdd = new ReviewTag(tagToAdd);
			if (reviewTagRepo.findByName(reviewTagToAdd.getName()) == null) {
				reviewTagRepo.save(reviewTagToAdd);
			}
			reviewRepo.findById(reviewToAdd.getId()).get().addReviewTag(reviewTagRepo.findByName(reviewTagToAdd.getName()));
		}
		
		
		reviewRepo.save(reviewRepo.findById(reviewToAdd.getId()).get());

		return "redirect:/all-reviews";
	}

}
