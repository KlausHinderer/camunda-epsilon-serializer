package de.metaphisto.camundaserialize.listener.helper;

import org.camunda.bpm.engine.impl.core.variable.CoreVariableInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.variable.value.TypedValue;

/**
 *
 */
public interface CamundaVariableVisitor {

    public TypedValue visit(ExecutionEntity executionEntity, String variableName, TypedValue value, CoreVariableInstance coreVariableInstance);
}
