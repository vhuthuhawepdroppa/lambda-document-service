package za.co.droppa.service.sequence;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import za.co.droppa.dto.sequence.SequenceResponse;
import za.co.droppa.proxy.SequenceClient;

public class SequenceService {

    private final String SEQ_URL = "https://thn1lc863m.execute-api.eu-west-1.amazonaws.com/dev/sequence";

    public SequenceResponse sendQuoteSequenceRequest(){
        SequenceClient client = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(SequenceClient.class, SEQ_URL+"/quote");
        return client.sendSequenceRequest();
    }
}
