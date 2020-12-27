import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath("/availabilities/5fe8a239b76c4a3c325ba4e5")
    }
    response {
        status OK()
        body('''{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-15",
"arrangement":  "ONSITE",
"id": "5fe8a239b76c4a3c325ba4e5"
}''')
        headers {
            header(contentType(), applicationJson())
        }
        bodyMatchers {
            jsonPath('$.id', byRegex("^[0-9a-f]{24}\$"))
        }
    }
}
