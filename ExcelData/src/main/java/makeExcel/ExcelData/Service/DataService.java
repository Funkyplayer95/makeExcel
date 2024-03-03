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
        List<Data> data = dataRepository.findAll();// findAll로 모든것을 찾아 안에 넣는다.
        List<DataDTO> dataAll = new ArrayList<>(); // 새로운 리스트 객체를 생성

        for (Data dataList : data){ // dataList안에 data가 반복적으로 진행

            DataDTO dataDTO =new DataDTO( // 새로운 DTO객체 생성
                    dataList.getCode(), // code를 얻어온다
                    dataList.getName(), // name을 얻어온다
                    dataList.getPrice(), // price를 얻어온다
                    dataList.getSalesQ()); // salesQ를 얻어온다
            dataAll.add(dataDTO);// 얻어온 데이터를 dataAll안에 넣어둔다.

        }   return dataAll; // getAllItemData는 dataAll을 반환한다.
    }

    public long countItems() {
        return dataRepository.count();
    } // count를 사용해서 데이터 개수를 가져온다


    public void saveData(Data serverData){
        Data data = new Data();
        data.setCode(serverData.getCode());
        data.setName(serverData.getName());
        data.setPrice(serverData.getPrice());
        data.setSalesQ(serverData.getSalesQ());

        dataRepository.save(serverData);
    }
    public void deleteData(int id) {
        dataRepository.deleteById(id); // 인자로 받은 id값을 찾아서 데이터베이스에서 삭제.
    }

    public void uploadData(MultipartFile file) throws IOException {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int max_seq = (int) dataRepository.count()+1;
        //
        for(Row row : sheet) {
            Data data = new Data();
            data.setCode(max_seq);
            data.setName(row.getCell(0).getStringCellValue());
            data.setPrice((int) row.getCell(1).getNumericCellValue());
            data.setSalesQ((int) row.getCell(2).getNumericCellValue());
            max_seq +=1;
            dataRepository.save(data);
        }
    }

}
