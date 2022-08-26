package de.eckyl.oauth2demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourcesRestController {

    @PostMapping(path = "/echo")
    @ResponseStatus(code = HttpStatus.OK)
    //@Secured({"ROLE_cop-demo-role"})
    public EchoResponse echo(@RequestBody EchoRequest echoRequest) {
        return EchoResponse.builder()
                .data(echoRequest.getData())
                .build();
    }
}
