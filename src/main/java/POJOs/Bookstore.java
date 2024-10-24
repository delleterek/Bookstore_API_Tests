package POJOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Bookstore {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Boolean isActive;
}
