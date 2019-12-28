package de.metaphisto.camunda.serializer;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.variable.serializer.AbstractObjectValueSerializer;
import org.camunda.bpm.engine.impl.variable.serializer.ValueFields;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class EpsilonSerializer extends AbstractObjectValueSerializer {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final List<Class> NOT_SUPPORTED_OBJECTS = Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Void.class);

    public EpsilonSerializer(String serializationDataFormat) {
        super(serializationDataFormat);
    }

    @Override
    protected String getTypeNameForDeserialized(Object o) {
        return o.getClass().getName();
    }

    @Override
    protected boolean canSerializeValue(Object o) {
        if(o== null || NOT_SUPPORTED_OBJECTS.contains(o.getClass())){
            return false;
        }
        return true;
    }

    @Override
    protected byte[] serializeToByteArray(Object o) throws Exception {
        return EMPTY_BYTE_ARRAY;
    }

    @Override
    protected Object deserializeFromByteArray(byte[] bytes, String s) throws Exception {
        return null;
    }

    @Override
    protected boolean isSerializationTextBased() {
        return true;
    }

    @Override
    public String getName() {
        return "EpsilonSerializer";
    }

    @Override
    public void writeValue(ObjectValue value, ValueFields valueFields) {
        String serializedStringValue = value.getValueSerialized();
        byte[] serializedByteValue = getSerializedBytesValue(serializedStringValue);
        writeToValueFields(value, valueFields, serializedByteValue);
        updateTypedValue(value, serializedStringValue);
    }
}
