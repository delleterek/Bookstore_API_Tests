package POJOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Address address;
    private String bookstoreId;
    private String customerId;
    private List<Item> items;
    private String price;
}
