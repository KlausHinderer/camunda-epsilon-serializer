package de.metaphisto.camundaserialize.listener;

import com.thoughtworks.xstream.XStream;
import de.metaphisto.camundaserialize.listener.helper.CamundaVariableVisitor;
import de.metaphisto.camundaserialize.listener.helper.CamundaVariableWalker;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.variable.impl.value.ObjectValueImpl;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.io.IOException;

/**
 *
 */
public class SerializationListener implements ExecutionListener,CamundaVariableVisitor {
    @Override
    public void notify(DelegateExecution instance) throws Exception {
        CamundaVariableWalker.visitVariables((ExecutionEntity) instance, this);
    }

    @Override
    public TypedValue visit(ExecutionEntity executionEntity, String variableName, TypedValue value, CoreVariableInstance coreVariableInstance) {
        String serializedValue = null;
        String objectTypeName = null;
        if (value.getValue() != null) {
            serializedValue = serialize(value.getValue());
            objectTypeName = value.getValue().getClass().getName();
    }

        return new ObjectValueImpl(value.getValue(), serializedValue, "json", objectTypeName, true);   }

    private String serialize(Object value) {
        //Put your implementation here
        XStream xStream = new XStream();
        return xStream.toXML(value);
    }

}
