package com.normalnywork.tundramarket.domain.usecases.products

import com.normalnywork.tundramarket.domain.repositories.ProductRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetCatalogUseCase(private val repository: ProductRepository) {

    operator fun invoke() = repository.getCatalog()
}
