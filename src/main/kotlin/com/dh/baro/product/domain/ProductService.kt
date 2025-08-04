package com.dh.baro.product.domain

import com.dh.baro.product.application.ProductCreateCommand
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

        for (category in categories) {
            product.addCategory(category)
        }

        return productRepository.save(product)
    }
}
