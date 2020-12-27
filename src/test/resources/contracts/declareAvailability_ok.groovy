import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        urlPath("/availabilities")
        body('''{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-20",
"arrangement":  "ONSITE"
}''')
        headers {
            header(contentType(), applicationJson())
        }
    }
    response {
        status OK()
        body('''{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-20",
"arrangement":  "ONSITE"
}''')
        headers {
            header(contentType(), applicationJson())
        }
    }
}
