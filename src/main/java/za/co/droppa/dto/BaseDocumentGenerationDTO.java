package za.co.droppa.dto;

import lombok.Data;

@Data
public class BaseDocumentGenerationDTO<K> {
    String type;
    private K body;
}
