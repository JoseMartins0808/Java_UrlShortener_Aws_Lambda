package com.br.martins.CreateUrlAwsShortener;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        // NÃO ESQUEÇA de criar um repositório no git hub
        throw new UnsupportedOperationException("Unimplemented method 'handleRequest'");
    }
}