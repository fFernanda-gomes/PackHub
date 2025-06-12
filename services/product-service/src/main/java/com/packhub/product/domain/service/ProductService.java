package com.packhub.product.domain.service;

import com.packhub.product.config.AuthenticatedUserProvider;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.repositories.ProductRepository;
import com.packhub.product.dto.CreateProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    public Product createProduct(CreateProductDTO dto, MultipartFile image) {
        String imageUrl = cloudinaryService.upload(image);
        String userCode = authenticatedUserProvider.getUserCodeFromToken();

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setImageUrl(imageUrl);
        product.setUserCode(userCode);

        return repository.save(product);
    }

    public List<Product> getProducts() {
        return this.repository.findAll();
    }

    public List<Product> getProductsByUserCode(String userCode) {
        return repository.findAllByUserCode(userCode);
    }

    public void deleteProduct(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        String userCode = authenticatedUserProvider.getUserCodeFromToken();

        if (!product.getUserCode().equals(userCode)) {
            throw new AccessDeniedException("Você não tem permissão para excluir este produto.");
        }

        repository.delete(product);

    }
}
