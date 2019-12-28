package de.metaphisto.camundaserialize.listener.helper;

import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.variable.impl.value.NullValueImpl;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.PrimitiveValue;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class CamundaVariableWalker {

    public static void visitVariables(ExecutionEntity executionEntity, CamundaVariableVisitor visitor) {
        if(executionEntity != null) {
            List<CoreVariableInstance> variableInstancesLocal = executionEntity.getVariableInstancesLocal();
            for (CoreVariableInstance coreVariableInstance : variableInstancesLocal) {
                TypedValue typedValue = coreVariableInstance.getTypedValue(false);
                String variableName = coreVariableInstance.getName();
                if(!(typedValue instanceof PrimitiveValue)) {
                    if (!(typedValue instanceof NullValueImpl)) {
                        TypedValue newValue = visitor.visit(executionEntity, variableName, typedValue, coreVariableInstance);
                        if (newValue != null) {
                    executionEntity.setVariableLocal(variableName, newValue, executionEntity);
                }
            }
                }
            }
            visitVariables(executionEntity.getParent(), visitor);
        }
    }
}
