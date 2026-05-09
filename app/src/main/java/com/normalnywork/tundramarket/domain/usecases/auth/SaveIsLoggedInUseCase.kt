package com.normalnywork.tundramarket.domain.usecases.auth

import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class SaveIsLoggedInUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(isLoggedIn: Boolean) = repository.saveIsLoggedIn(isLoggedIn)
}
