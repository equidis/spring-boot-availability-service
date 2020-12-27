import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath("/availabilities") {
            queryParameters {
                parameter 'username': 'jdoe'
            }
        }
    }
    response {
        status OK()
        body('''[
{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-15",
"arrangement":  "ONSITE",
},
{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-16",
"arrangement":  "REMOTE",
},
{
"userId": "5fdb5cdad07bba25f645cd87",
"day": "2020-10-19",
"arrangement":  "OFF",
}
]''')
        headers {
            header(contentType(), applicationJson())
        }
        bodyMatchers {
            jsonPath('$.[*].id', byRegex("^[0-9a-f]{24}\$"))
        }
    }
}
