package com.benruehl.transaction_normalizer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransactionNormalizerApplication

fun main(args: Array<String>) {
	runApplication<TransactionNormalizerApplication>(*args)
}
