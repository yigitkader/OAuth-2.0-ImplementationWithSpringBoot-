package com.yigitco.oauthauthorizationserver.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig(
    private val passwordEncoder: PasswordEncoder
) {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun oauthSecurityFilterChain(httpSecurity: HttpSecurity) : SecurityFilterChain{
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity)
        return httpSecurity.formLogin(Customizer.withDefaults()).build()
    }


    @Bean
    fun registeredClientRepository(): RegisteredClientRepository? {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("api-client")
            .clientSecret(passwordEncoder.encode("secret"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.PASSWORD)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://127.0.0.1:8080/login/oauth2/code/api-client-oidc")
            .redirectUri("http://127.0.0.1:8080/authorized")
            .scope(OidcScopes.OPENID)
            .scope("api.read")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build()
        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey: RSAKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource<SecurityContext> { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    private fun generateRsa(): RSAKey {
        val keyPair: KeyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }


    private fun generateRsaKey(): KeyPair {
        val keyPair: KeyPair = try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPairGenerator.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
        return keyPair
    }

    @Bean
    fun providerSettings(): ProviderSettings? {
        return ProviderSettings.builder()
            .issuer("http://auth-server:9000")
            .build()
    }
}