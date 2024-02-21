package makeExcel.ExcelData.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import makeExcel.ExcelData.DTO.DataDTO;
import makeExcel.ExcelData.Service.DataService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DataController {
    @Autowired
    private final DataService dataService;

    @GetMapping("/")
    public String allDataList(Model model){
        return "makeExcel";
    }
    @GetMapping("/check")
    public String dataCheck(Model model){
        List<DataDTO> allData = dataService.getAllItemData();
        model.addAttribute("allData", allData);
        return "makeExcel";
    }
    @PostMapping("/save")
    public String addData(@RequestBody DataDTO newData) {
        dataService.saveData(newData);
        return "makeExcel";
    }

    @PostMapping("/deleteData")
    public String deleteData(@RequestBody List<String> codesToDelete) {
        for (String code : codesToDelete) {
            dataService.deleteData(code);
        }
        return "redirect:/";
    }


//    @GetMapping("/excel/upload")



    @PostMapping("/download")
    public void excelDownload(HttpServletResponse response) throws IOException {
        long itemCount = dataService.countItems();
        List<DataDTO> allData = dataService.getAllItemData();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("번호");
        cell = row.createCell(1);
        cell.setCellValue("이름");
        cell = row.createCell(2);
        cell.setCellValue("가격");
        cell = row.createCell(3);
        cell.setCellValue("판매수량");

        for (int i=0; i < itemCount ; i++){
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(allData.get(i).getCode());
            cell = row.createCell(1);
            cell.setCellValue(allData.get(i).getName());
            cell = row.createCell(2);
            cell.setCellValue(allData.get(i).getPrice());
            cell = row.createCell(3);
            cell.setCellValue(allData.get(i).getSalesQ());
        }
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=data.xlsx");

        wb.write(response.getOutputStream());
        wb.close();
    }
}
