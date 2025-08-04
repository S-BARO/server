package com.dh.baro.product.domain

import com.dh.baro.core.ErrorMessage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    @Transactional
    fun createCategory(id: Long, name: String): Category {
        if (categoryRepository.existsById(id))
            throw IllegalArgumentException(ErrorMessage.CATEGORY_ALREADY_EXISTS.format(id))

        return categoryRepository.save(Category(id, name))
    }

    fun getCategoriesByIds(ids: List<Long>): List<Category> {
        val categories = categoryRepository.findAllById(ids).toList()
        if (categories.size != ids.size)
            throw IllegalArgumentException(ErrorMessage.CATEGORY_NOT_FOUND.format(ids))
        return categories
    }
}
