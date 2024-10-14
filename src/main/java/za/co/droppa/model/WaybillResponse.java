package za.co.droppa.model;

import lombok.Data;

@Data
public class WaybillResponse {
    private String errorDescription;
    private int errorCode;
    private String waybillNumber;
}
