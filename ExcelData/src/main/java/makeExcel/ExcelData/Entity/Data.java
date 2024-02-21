package makeExcel.ExcelData.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="itemdata")
public class Data {

    @Id
    private int code;
    private String name;
    private int price;
    private Integer salesQ;
}
