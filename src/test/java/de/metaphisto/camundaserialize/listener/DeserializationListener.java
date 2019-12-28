package de.metaphisto.camundaserialize.listener;

import com.thoughtworks.xstream.XStream;
import de.metaphisto.camundaserialize.listener.helper.CamundaVariableVisitor;
import de.metaphisto.camundaserialize.listener.helper.CamundaVariableWalker;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.variable.impl.value.ObjectValueImpl;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Date;

/**
 *
 */
public class DeserializationListener implements ExecutionListener, CamundaVariableVisitor{
    @Override
    public void notify(DelegateExecution instance) throws Exception {
        CamundaVariableWalker.visitVariables((ExecutionEntity) instance, this);
    }

    @Override
    public TypedValue visit(ExecutionEntity executionEntity, String variableName, TypedValue value, CoreVariableInstance coreVariableInstance) {
        String serialized = ((ObjectValue) value).getValueSerialized();
        String variableType = ((ObjectValue) value).getObjectTypeName();

        // Beim Wiederanstoßen eines UserTasks können von außen weitere Variablen gesetzt werden, die nicht serialisiert wurden, sondern nur deserialisiert vorliegen.
        Object deserialized = null;
        if (((ObjectValue) value).isDeserialized()) {
            deserialized = value.getValue();
        }

        if (serialized != null) {
            try {
                deserialized = deserialize(serialized);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        TypedValue typedValue = new ObjectValueImpl(deserialized,null, "json", variableType, true);
        return typedValue;
    }

    private Object deserialize(String serialized) {
        //Put your custom implementation here
        XStream xStream = new XStream();
        return xStream.fromXML(serialized);
    }
}
