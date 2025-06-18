package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Create new account"
    request {
        method 'POST'
        url '/api/account'
        body(
                id: 1,
                userId: 100,
                currency: "USD",
                balance: 5000.00,
                active: true
        )
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 201
        body(
                id: 1,
                userId: 100,
                currency: "USD",
                balance: 5000.00,
                active: true
        )
        headers {
            contentType(applicationJson())
        }
    }
}
