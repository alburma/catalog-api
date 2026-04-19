package works.brm.catalog.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.brm.catalog.domain.Category;
import works.brm.catalog.exception.ApiException;
import works.brm.catalog.repo.CategoryRepository;
import works.brm.catalog.web.dto.CategoryDtos.CategoryRequest;
import works.brm.catalog.web.dto.CategoryDtos.CategoryResponse;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repo;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        if (repo.existsByName(req.name())) throw ApiException.conflict("Category name already exists");
        if (repo.existsBySlug(req.slug())) throw ApiException.conflict("Category slug already exists");
        Category c = Category.builder().name(req.name()).slug(req.slug()).build();
        return toResponse(repo.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw ApiException.notFound("Category");
        repo.deleteById(id);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug());
    }
}
