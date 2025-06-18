package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Perform cash deposit operation"
    request {
        method 'POST'
        url '/api/account/operation'
        body(
                operationType: "CASH_DEPOSIT",
                accountId: 1,
                amount: 100.00,
                currency: "USD"
        )
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body(
                status: "SUCCESS",
                message: "Operation completed"
        )
        headers {
            contentType(applicationJson())
        }
    }
}
