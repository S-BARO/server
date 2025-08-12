package com.dh.baro.product.domain.service

import com.dh.baro.product.application.ProductCreateCommand
import com.dh.baro.product.domain.Category
import com.dh.baro.product.domain.Product
import com.dh.baro.product.domain.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    @Transactional
    fun createProduct(cmd: ProductCreateCommand, categories: List<Category>): Product {
        val product = Product.newProduct(
            name = cmd.name,
            price = cmd.price,
            quantity = cmd.quantity,
            description = cmd.description,
            likesCount = cmd.likesCount,
            thumbnailUrl = cmd.thumbnailUrl,
        )
        product.addImages(cmd.imageUrls)
        product.addCategories(categories)

        return productRepository.save(product)
    }
}
