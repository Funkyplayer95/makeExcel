package makeExcel.ExcelData.Service;

import lombok.RequiredArgsConstructor;
import makeExcel.ExcelData.DTO.DataDTO;
import makeExcel.ExcelData.Entity.Data;
import makeExcel.ExcelData.Repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    @Autowired
    private final DataRepository dataRepository;

    public List<DataDTO> getAllItemData() {
        List<Data> data = dataRepository.findAll();
        List<DataDTO> dataAll = new ArrayList<>();

        for (Data datalist : data){
            DataDTO dataDTO =new DataDTO(
                    datalist.getCode(),
                    datalist.getName(),
                    datalist.getPrice(),
                    datalist.getSalesQ());
            dataAll.add(dataDTO);
        }   return dataAll;
    }
    public long countItems() {
        return dataRepository.count();
    }

    public DataDTO saveData(DataDTO dataDTO){
        Data data = new Data();

        data.setCode(dataDTO.getCode());
        data.setName(dataDTO.getName());
        data.setPrice(dataDTO.getPrice());
        data.setSalesQ(dataDTO.getSalesQ());

        data = dataRepository.save(data);

        return new DataDTO(data.getCode(), data.getName(), data.getPrice(),data.getSalesQ());
    }
    public void deleteData(String id) {
        dataRepository.deleteById(id);
    }
}
