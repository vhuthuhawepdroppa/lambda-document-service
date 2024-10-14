package za.co.droppa.model;


import lombok.Data;

@Data
public class CodeConfirmation {

    private String code;

    private boolean confirmed;

    public CodeConfirmation() { }

    public void setCode(String newCode) {
        if (newCode == null || !(newCode.trim().length() > 0))
            throw new IllegalArgumentException("Attempting to confirm null or empty");
        this.code = newCode.trim();
    }

}
