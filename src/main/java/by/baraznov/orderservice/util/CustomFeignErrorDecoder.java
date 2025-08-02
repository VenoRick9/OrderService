package by.baraznov.orderservice.util;

import by.baraznov.orderservice.util.feign.FeignClientBadRequestException;
import by.baraznov.orderservice.util.feign.FeignClientNotFoundException;
import by.baraznov.orderservice.util.feign.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = response.body() != null ? Util.toString(response.body().asReader(StandardCharsets.UTF_8)) : null;

            if (body != null && response.status() == 404) {
                ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
                return new FeignClientNotFoundException(error.message());
            }

            if (body != null && response.status() == 400) {
                ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
                return new FeignClientBadRequestException(error.message());
            }

            return new FeignException("Feign call failed: " + response.status() + " " + body);
        } catch (Exception e) {
            return new FeignException("Error parsing Feign error: " + e.getMessage());
        }
    }
}
