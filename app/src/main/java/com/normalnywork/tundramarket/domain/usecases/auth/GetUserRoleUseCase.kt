package com.normalnywork.tundramarket.domain.usecases.auth

import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetUserRoleUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(): UserRole? {
        return repository.getUserRole()
    }
}
