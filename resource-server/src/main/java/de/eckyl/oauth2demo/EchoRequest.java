package de.eckyl.oauth2demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EchoRequest {
    private String data;
}
