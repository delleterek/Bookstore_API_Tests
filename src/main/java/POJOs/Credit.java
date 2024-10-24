package POJOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Credit {
    private String creditId;
    private String customerId;
    private double totalPrice;
    private String transactionType;
}
