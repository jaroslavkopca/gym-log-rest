package cz.cvut.fel.ear.posilovna.rest;


import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import cz.cvut.fel.ear.posilovna.rest.util.RestUtils;
import cz.cvut.fel.ear.posilovna.security.SecurityUtils;
import cz.cvut.fel.ear.posilovna.service.WorkoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
@RequestMapping("/rest/categories")
public class WorkoutController {
/*
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    private final WorkoutService service;


    @Autowired
    public CategoryController(WorkoutService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkoutRecord> getWorkoutRecords(Member member) {
        return service.findAll(member);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        service.persist(category);
        LOG.debug("Created category {}.", category);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", category.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    // If the parameter name matches parameter in the mapping value, it is not necessary to explicitly provide it
    public Category getById(@PathVariable Integer id) {
        final Category category = service.find(id);
        if (category == null) {
            throw NotFoundException.create("Category", id);
        }
        return category;
    }

    @GetMapping(value = "/{id}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getProductsByCategory(@PathVariable Integer id) {
        return productService.findAll(getById(id));
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/{id}/products", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProductToCategory(@PathVariable Integer id, @RequestBody Product product) {
        final Category category = getById(id);
        service.addProduct(category, product);
        LOG.debug("Product {} added into category {}.", product, category);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{categoryId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductFromCategory(@PathVariable Integer categoryId,
                                          @PathVariable Integer productId) {
        final Category category = getById(categoryId);
        final Product toRemove = productService.find(productId);
        if (toRemove == null) {
            throw NotFoundException.create("Product", productId);
        }
        service.removeProduct(category, toRemove);
        LOG.debug("Product {} removed from category {}.", toRemove, category);
    }
}

public class WorkoutController {*/
}
