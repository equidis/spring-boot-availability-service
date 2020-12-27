import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        urlPath("/availabilities")
        body('''{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "10/11/2020",
"arrangement":  "ONSITE"
}''')
        headers {
            header(contentType(), applicationJson())
        }
    }
    response {
        status BAD_REQUEST()
        body(
                message: $(anyNonEmptyString()),
                code: 400
        )
        headers {
            header(contentType(), applicationJson())
        }
    }
}
