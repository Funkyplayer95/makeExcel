package makeExcel.ExcelData.Service;

import lombok.RequiredArgsConstructor;
import makeExcel.ExcelData.DTO.DataDTO;
import makeExcel.ExcelData.Entity.Data;
import makeExcel.ExcelData.Repository.DataRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    @Autowired
    private final DataRepository dataRepository;

    public List<DataDTO> getAllItemData() {
        List<Data> data = dataRepository.findAll(); // findAll로 모든것을 찾아 안에 넣는다.
        List<DataDTO> dataAll = new ArrayList<>(); // 새로운 리스트 객체를 생성

        for (Data dataList : data){ // dataList안에 data가 반복적으로 진행
            DataDTO dataDTO =new DataDTO( // 새로운 DTO객체 생성
                    dataList.getCode(), // code를 얻어온다
                    dataList.getName(), // name을 얻어온다
                    dataList.getPrice(), // price를 얻어온다
                    dataList.getSalesQ()); // salesQ를 얻어온다
            dataAll.add(dataDTO); // 얻어온 데이터를 dataAll안에 넣어둔다.
        }   return dataAll; // getAllItemData는 dataAll을 반환한다.
    }

    public long countItems() {
        return dataRepository.count();
    } // count를 사용해서 데이터 개수를 가져온다


    public void saveData(DataDTO dataDTO){
        Data data = new Data(); // 새로운 Data 객체 생성

        data.setCode(dataDTO.getCode()); // DTO에 저장된 code를 entity에 set함
        data.setName(dataDTO.getName()); // DTO에 저장된 naem을 entity에 set함
        data.setPrice(dataDTO.getPrice()); // DTO에 저장된 price를 entity에 set함
        data.setSalesQ(dataDTO.getSalesQ()); // DTO에 저장된 salesQ를 entity에 set함

        data = dataRepository.save(data); // update할 수 있도록 save문을 사용.

        new DataDTO(data.getCode(), data.getName(), data.getPrice(), data.getSalesQ());
        // 저장된 데이터를 이용, 새로운 DTO객체를 생성하고 반환. 클라이언트에게 저장된 데이터의 정보를 전달.
    }
    public void deleteData(String id) {
        dataRepository.deleteById(id); // 인자로 받은 id값을 찾아서 데이터베이스에서 삭제.
    }

    public void uploadData(MultipartFile file) throws IOException {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row : sheet) {
            Data data = new Data();
            data.setCode((int) row.getCell(0).getNumericCellValue());
            data.setName(row.getCell(1).getStringCellValue());
            data.setPrice((int) row.getCell(2).getNumericCellValue());
            data.setSalesQ((int) row.getCell(3).getNumericCellValue());

            dataRepository.save(data);
        }
    }

}
