package com.benruehl.transaction_normalizer.api.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
class TransactionControllerTest {
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setup(context: ApplicationContext) {
        client = WebTestClient.bindToApplicationContext(context).build()
    }

    @Test
    fun `upload should return 200 when transactions are present`() {
        client.post().uri("/customers/1/transactions/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""[
                {
                    "bookingDate": "2024-05-28",
                    "valueDate": "2024-05-27",
                    "amount": -85.12,
                    "currency": "EUR",
                    "purpose": "SVWZ+",
                    "transactionCode": "PMNT-ICDT-STDO",
                    "creditorName": "Amazon EU SARL"
                }
            ]""".trimIndent())
            .exchange()
            .expectStatus().isOk
    }
}