package com.benruehl.transaction_normalizer.api.controllers

import com.benruehl.transaction_normalizer.application.transactions.CamtDocument
import com.benruehl.transaction_normalizer.application.transactions.TransactionImportService
import com.benruehl.transaction_normalizer.application.transactions.TransactionImportDto
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/customers/{customerId}/transactions")
class TransactionController(
    val transactionImportService: TransactionImportService
) {

    @PostMapping(
        "/upload",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadJson(
        @PathVariable customerId: String,
        @RequestBody transactions: Flux<TransactionImportDto>
    ): Mono<ResponseEntity<Void>> {
        return transactionImportService.import(customerId, transactions)
            .thenReturn(ResponseEntity.ok().build<Void>())
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    @PostMapping(
        "/upload",
        consumes = [MediaType.APPLICATION_XML_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadXml(
        @PathVariable customerId: String,
        @RequestBody camtDocument: Mono<CamtDocument>
    ): Mono<ResponseEntity<Void>> {
        return transactionImportService.import(customerId, camtDocument)
            .thenReturn(ResponseEntity.ok().build<Void>())
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }
}