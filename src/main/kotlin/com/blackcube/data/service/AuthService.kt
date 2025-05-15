package com.blackcube.data.service

import com.blackcube.data.models.auth.LoginRequest
import com.blackcube.data.models.auth.RegistrationRequest
import com.blackcube.data.repository.UserRepository
import com.blackcube.data.util.PasswordHasher
import com.blackcube.models.auth.UserModel
import com.blackcube.utils.JwtConfig

class AuthService(private val userRepository: UserRepository) {
    suspend fun register(req: RegistrationRequest): UserModel {
        userRepository.findByEmail(req.email)?.let {
            throw AuthenticationException("User already exist")
        }
        val hash = PasswordHasher.hash(req.password)
        userRepository.create(req.email, req.name, hash)
        return userRepository.findByEmail(req.email) ?: throw AuthenticationException("User nof found")
    }

    @Throws(AuthenticationException::class)
    suspend fun authenticate(req: LoginRequest): UserModel {
        val user = userRepository.findByEmail(req.email) ?: throw AuthenticationException("Invalid credentials")
        if (!PasswordHasher.verify(req.password, user.passwordHash)) {
            throw AuthenticationException("Invalid credentials")
        }
        return user
    }

    fun generateToken(user: UserModel): String = JwtConfig.generateToken(user)
}

class AuthenticationException(message: String) : RuntimeException(message)