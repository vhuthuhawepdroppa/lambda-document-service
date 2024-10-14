package za.co.droppa.model.rental;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DailyDistance{

    private double initialDistance;

    private double finalDistance;

    private LocalDateTime date;

}
