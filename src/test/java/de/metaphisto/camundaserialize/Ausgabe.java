package de.metaphisto.camundaserialize;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.variable.impl.value.ObjectValueImpl;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class Ausgabe implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println(execution.getVariable("datum"));
        System.out.println(execution.getVariable("vektor"));
        System.out.println(execution.getVariable("zahl"));
    }
}
