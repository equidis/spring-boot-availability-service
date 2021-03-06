import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath("/availabilities/123456789012345678901234")
    }
    response {
        status NOT_FOUND()
        body(
                message: $(anyNonEmptyString()),
                code: 404
        )
        headers {
            header(contentType(), applicationJson())
        }
    }
}
