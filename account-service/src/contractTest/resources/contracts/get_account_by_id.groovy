package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Get single account by ID"
    request {
        method 'GET'
        url '/api/account/1'
    }
    response {
        status 200
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
