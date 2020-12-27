import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath("/availabilities") {
            queryParameters {
                parameter 'username': 'mdoe'
            }
        }
    }
    response {
        status OK()
        body('''[]''')
        headers {
            header(contentType(), applicationJson())
        }
        bodyMatchers {
            jsonPath('$', byType {
                maxOccurrence(0)
            })
        }
    }
}
