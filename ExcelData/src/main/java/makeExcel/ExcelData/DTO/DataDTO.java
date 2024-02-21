package makeExcel.ExcelData.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataDTO {
    private int code;
    private String name;
    private int price;
    private int salesQ;
}
