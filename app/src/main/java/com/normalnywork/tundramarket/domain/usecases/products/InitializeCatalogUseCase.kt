package com.normalnywork.tundramarket.domain.usecases.products

import com.normalnywork.tundramarket.domain.repositories.ProductRepository
import org.koin.core.annotation.Singleton

@Singleton
class InitializeCatalogUseCase(private val repository: ProductRepository) {

    suspend operator fun invoke() = repository.updateCatalog()
}
