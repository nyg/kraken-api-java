package dev.andstuff.kraken.api.endpoint.account.csv;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBeanIntrospectionException;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRuntimeException;

/**
 * Taken from <a href="https://github.com/hendrixjoseph/opencsv-record-mapping">hendrixjoseph/opencsv-record-mapping</a>.
 * Modified to support @{@link com.opencsv.bean.CsvBindByName CsvBindByName} annotations.
 */
public class RecordMappingStrategy<T extends Record> extends HeaderColumnNameMappingStrategy<T> {

    public RecordMappingStrategy(Class<T> type) {
        setType(type);
    }

    @Override
    public T populateNewBean(String[] line) throws CsvBeanIntrospectionException, CsvFieldAssignmentException, CsvChainedException {
        RecordComponent[] recordComponents = type.getRecordComponents();
        if (recordComponents.length != line.length) {
            throw new CsvRuntimeException("Mismatch between line values and record components");
        }

        Map<String, Object> valuesByRecordComponentName = createValuesMap(line);
        Object[] initArgs = Stream.of(recordComponents)
                .map(recordComponent -> valuesByRecordComponentName.get(recordComponent.getName()))
                .toArray();

        try {
            Class<?>[] array = Arrays.stream(recordComponents).map(RecordComponent::getType).toArray(Class[]::new);
            return type.getConstructor(array).newInstance(initArgs);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CsvRuntimeException("Error creating instance of record", e);
        }
    }

    private Map<String, Object> createValuesMap(String[] line) throws CsvConstraintViolationException, CsvDataTypeMismatchException {
        Map<String, Object> valuesByRecordComponentName = new HashMap<>();

        for (int i = 0; i < line.length; i++) {
            Field field = findField(i).getField();
            valuesByRecordComponentName.put(
                    field.getName(),
                    determineConverter(
                            field, field.getType(),
                            null, null,
                            field.getType() == Instant.class
                                    ? KrakenInstantConverter.class
                                    : null).convertToRead(line[i]));
        }

        return valuesByRecordComponentName;
    }

    // TODO not great to have this here
    public static class KrakenInstantConverter extends AbstractCsvConverter {

        @Override
        public Object convertToRead(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
            String[] dateTime = value.split(" ");
            return Instant.parse("%sT%sZ".formatted(dateTime[0], dateTime[1]));
        }
    }
}
