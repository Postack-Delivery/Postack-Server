package com.postack.domain.data

import com.postack.domain.models.Category

interface CategoryDataSource {
    suspend fun insertCategory(category: Category)
    suspend fun insertSubCategory(categoryId: String, name: String)
    suspend fun getAllCategories(): List<Category>
    suspend fun deleteCategory(id: String)

}