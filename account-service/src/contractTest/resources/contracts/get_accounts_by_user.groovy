package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description "Get all accounts for a user"
    request {
        method 'GET'
        url '/api/account/user/100'
    }
    response {
        status 200
        body([
                [
                        id: 1,
                        userId: 100,
                        currency: "USD",
                        balance: 5000.00,
                        active: true
                ]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
