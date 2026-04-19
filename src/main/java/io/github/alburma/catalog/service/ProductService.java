package io.github.alburma.catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.alburma.catalog.domain.Category;
import io.github.alburma.catalog.domain.Product;
import io.github.alburma.catalog.exception.ApiException;
import io.github.alburma.catalog.repo.CategoryRepository;
import io.github.alburma.catalog.repo.ProductRepository;
import io.github.alburma.catalog.web.dto.ProductDtos.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String q, Long categoryId, boolean activeOnly, Pageable pageable) {
        return productRepo.search(q, categoryId, activeOnly, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepo.findById(id).map(this::toResponse)
            .orElseThrow(() -> ApiException.notFound("Product"));
    }

    @Transactional
    public ProductResponse create(ProductRequest req) {
        if (productRepo.existsBySku(req.sku())) throw ApiException.conflict("SKU already exists");
        Product p = Product.builder()
            .sku(req.sku())
            .name(req.name())
            .description(req.description())
            .priceCents(req.priceCents())
            .currency(req.currency() == null ? "EUR" : req.currency().toUpperCase())
            .stock(req.stock() == null ? 0 : req.stock())
            .category(resolveCategory(req.categoryId()))
            .active(req.active() == null ? Boolean.TRUE : req.active())
            .build();
        return toResponse(productRepo.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest req) {
        Product p = productRepo.findById(id).orElseThrow(() -> ApiException.notFound("Product"));
        if (req.name() != null) p.setName(req.name());
        if (req.description() != null) p.setDescription(req.description());
        if (req.priceCents() != null) p.setPriceCents(req.priceCents());
        if (req.currency() != null) p.setCurrency(req.currency().toUpperCase());
        if (req.stock() != null) p.setStock(req.stock());
        if (req.active() != null) p.setActive(req.active());
        if (req.categoryId() != null) p.setCategory(resolveCategory(req.categoryId()));
        return toResponse(productRepo.save(p));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepo.existsById(id)) throw ApiException.notFound("Product");
        productRepo.deleteById(id);
    }

    private Category resolveCategory(Long id) {
        if (id == null) return null;
        return categoryRepo.findById(id).orElseThrow(() -> ApiException.badRequest("Unknown categoryId: " + id));
    }

    private ProductResponse toResponse(Product p) {
        Category c = p.getCategory();
        return new ProductResponse(
            p.getId(), p.getSku(), p.getName(), p.getDescription(),
            p.getPriceCents(), p.getCurrency(), p.getStock(),
            c == null ? null : c.getId(),
            c == null ? null : c.getName(),
            p.getActive(), p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
