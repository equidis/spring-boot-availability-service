import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath("/availabilities") {
            queryParameters {
                parameter 'userId': '5fe773204edcff0fbfbf45e4'
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
                minOccurrence(0)
                maxOccurrence(0)
            })
        }
    }
}
