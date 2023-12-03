import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Identifiable {
    private Long id;
    private String name;
    private String lastName;
    private Byte age;

}