package makeExcel.ExcelData.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import makeExcel.ExcelData.DTO.DataDTO;
import makeExcel.ExcelData.Entity.Data;
import makeExcel.ExcelData.Service.DataService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DataController {
    @Autowired // 의존성 주입을 할때 사용하는 어노테이션
    private final DataService dataService;


    @PostMapping("/") // 기본 페이지 이동
    public String allDataList(){
        return "makeExcel";
    } //html로 입장

    @PostMapping("/check") // 조회버튼 클릭시
    public String dataCheck(Model model){
        List<DataDTO> allData = dataService.getAllItemData(); // 모든 데이터를 가져온다.
        model.addAttribute("allData", allData); // "allData"라는 명칭에 주입
        return "makeExcel";
    }
    @ResponseBody
    @PostMapping("/save") // 저장버튼 클릭 시,
    public String saveData(@RequestBody List<Data> codesToSave) { //@RequestBody는 html에서 보내는 데이터를 받는다
        try {
            for (Data data : codesToSave) { //리스트 안에 있는 value값을 조회
                dataService.saveData(data); // 삭제하도록 진행.
            }
            return "makeExcel";
        } catch (Exception e) {
            return "makeExcel";
        }
    }

    @PostMapping("/delete") // 삭제 버튼 클릭 시, 삭제하는 value를 가진 리스트를 받아온다. []로 받은 delete할 값들 받기
    public String deleteData(@RequestBody List<Integer> codesToDelete) {
        try {
            for (int code : codesToDelete) { //리스트 안에 있는 value값을 조회
                dataService.deleteData(code); // 삭제하도록 진행.
            }
            return "makeExcel";
        } catch (Exception e) {
            return "makeExcel";
        }

    }

    @PostMapping("/download") // 엑셀 다운로드 버튼 누를 시
    public void excelDownload(HttpServletResponse response) throws IOException { // throws IOException 예외처리
        long itemCount = dataService.countItems(); // 데이터의 총 개수를 반환해서 저장
        List<DataDTO> allData = dataService.getAllItemData(); // allData 안에 모든 데이터들을 저장
        Workbook wb = new XSSFWorkbook(); // XSSFWorkbook 객체를 생성해서 wb에 저장. 엑셀파일 생성하는데 사용.
        Sheet sheet = wb.createSheet("첫번째 시트"); // 엑셀 시트를 생성. 이름을 ()로 저장.
        Row row; // 엑셀의 행
        Cell cell; // 엑셀의 열
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
    public String uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        try{
            if(!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")){
                redirectAttributes.addFlashAttribute("message","엑셀 파일만 업로드 가능합니다.");
                return "redirect:/";
            }

            dataService.uploadData(file);
            redirectAttributes.addFlashAttribute("message","업로드 성공");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "업로드 실패. 오류 : " + e.getMessage());

        }
        return "redirect:/";
    }

}
