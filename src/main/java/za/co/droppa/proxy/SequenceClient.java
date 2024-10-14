package za.co.droppa.proxy;

import feign.RequestLine;
import za.co.droppa.dto.sequence.SequenceResponse;

public interface SequenceClient {
    @RequestLine("GET")
    SequenceResponse sendSequenceRequest();
}
