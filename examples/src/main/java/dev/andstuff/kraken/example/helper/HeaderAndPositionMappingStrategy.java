package dev.andstuff.kraken.example.helper;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class HeaderAndPositionMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        super.generateHeader(bean);

        int fieldCount = getFieldMap().values().size();
        String[] header = new String[fieldCount];

        for (int i = 0; i < fieldCount; i++) {
            header[i] = findField(i).getField().getDeclaredAnnotation(CsvBindByName.class).column();
        }

        return header;
    }
}
