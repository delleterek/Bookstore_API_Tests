package POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private String orderTrackingId;
    private String orderId;
    private String orderStatus;
    private String message;
}
