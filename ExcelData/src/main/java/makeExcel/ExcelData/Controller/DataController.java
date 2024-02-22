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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DataController {
    @Autowired // 의존성 주입을 할때 사용하는 어노테이션
    private final DataService dataService;

    @GetMapping("/") // 기본 페이지 이동
    public String allDataList(Model model){
        return "makeExcel";
    } //html로 입장

    @GetMapping("/check") // 조회버튼 클릭시
    public String dataCheck(Model model){
        List<DataDTO> allData = dataService.getAllItemData(); // 모든 데이터를 가져온다.
        model.addAttribute("allData", allData); // "allData"라는 명칭에 주입
        return "makeExcel";
    }

    @PostMapping("/save") // 저장버튼 클릭 시,
    public String addData(@RequestBody DataDTO newData) { //@RequestBody는 html에서 보내는 데이터를 받는다
        dataService.saveData(newData); //받은 데이터들을 save 진행.
        return "makeExcel";
    }

    @PostMapping("/delete") // 삭제 버튼 클릭 시, 삭제하는 value를 가진 리스트를 받아온다. []로 받은 delete할 값들 받기
    public String deleteData(@RequestBody List<String> codesToDelete) {
        for (String code : codesToDelete) { //리스트 안에 있는 value값을 조회
            dataService.deleteData(code); // 삭제하도록 진행.
        }
        return "redirect:/";
    }

    @PostMapping("/download") // 엑셀 다운로드 버튼 누를 시
    public void excelDownload(HttpServletResponse response) throws IOException { // throws IOException 예외처리
        long itemCount = dataService.countItems(); // 데이터의 총 개수를 반환해서 저장
        List<DataDTO> allData = dataService.getAllItemData(); // allData 안에 모든 데이터들을 저장

        Workbook wb = new XSSFWorkbook(); // XSSFWorkbook 객체를 생성해서 wb에 저장. 엑셀파일 생성하는데 사용.
        Sheet sheet = wb.createSheet("첫번째 시트"); // 엑셀 시트를 생성. 이름을 ()로 저장.
        Row row = null; // 엑셀의 행
        Cell cell = null; // 엑셀의 열
        int rowNum = 0; // 행 번호

        row = sheet.createRow(rowNum++); // 첫 번째 행 생성.
        cell = row.createCell(0); // 첫 번째 열 생성.
        cell.setCellValue("번호"); // 값을 번호로 지정.
        cell = row.createCell(1); // 두 번째 열 생성.
        cell.setCellValue("이름"); // 값을 이름 으로 지정
        cell = row.createCell(2); // 세 번째 열 생성.
        cell.setCellValue("가격"); // 값을 가격 으로 지정.
        cell = row.createCell(3); // 네 번째 열 생성.
        cell.setCellValue("판매수량"); // 값을 판매수량 으로 지정.

        for (int i=0; i < itemCount ; i++){ // for문을 이용. 데이터값만큼 반복.
            row = sheet.createRow(rowNum++); // 새로운 행 추가
            cell = row.createCell(0); // 새로운 행에 첫 번째 열 추가
            cell.setCellValue(allData.get(i).getCode()); // allData안의 i번째의 code를 얻어온다.
            cell = row.createCell(1); // 새로운 행에 두 번째 열 추가
            cell.setCellValue(allData.get(i).getName()); // allData안의 i번째의 name을 얻어온다.
            cell = row.createCell(2); // 새로운 행에 세 번째 열 추가
            cell.setCellValue(allData.get(i).getPrice()); // allData안의 i번째의 price를 얻어온다.
            cell = row.createCell(3); // 새로운 행에 네 번째 열 추가
            cell.setCellValue(allData.get(i).getSalesQ()); // allData안의 i번째의 salesQ를 얻어온다.
        }
        response.setContentType("ms-vnd/excel"); // 응답 컨텐츠 타입을 엑셀로 지정. MIME타입을 나타낸다 함
        response.setHeader("Content-Disposition", "attachment;filename=data.xlsx"); // 파일이름을 저장 시 data.xlsx로 저장

        wb.write(response.getOutputStream()); // 생성된 엑셀 파일을 출력 스트림에 사용. 클라이언트가 다운받을 수 있게 함.
        wb.close(); // 작업이 끝났으면 Workbook을 닫는다. 메모리 누수를 방지하기 위함.
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            List<DataDTO> dataDTOs = readExcelFile(file);
            for (DataDTO dataDTO : dataDTOs) {
                dataService.saveData(dataDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    private List<DataDTO> readExcelFile(MultipartFile file) throws IOException {
        List<DataDTO> dataDTOs = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row currentRow : sheet) {
            int code = (int) currentRow.getCell(0).getNumericCellValue();
            String name = currentRow.getCell(1).getStringCellValue();
            int price = (int) currentRow.getCell(2).getNumericCellValue();
            int salesQ = (int) currentRow.getCell(3).getNumericCellValue();

            DataDTO dataDTO = new DataDTO(code, name, price, salesQ);
            dataDTOs.add(dataDTO);
        }

        workbook.close();

        return dataDTOs;
    }
}
