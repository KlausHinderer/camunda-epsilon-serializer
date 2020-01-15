package de.metaphisto.camundaserialize;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static org.junit.Assert.assertEquals;

public class Ausgabe implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        assertEquals(PluginTest.JETZT, execution.getVariable("datum"));
        assertEquals(PluginTest.VECTOR, execution.getVariable("vektor"));
        assertEquals(PluginTest.VALUE, execution.getVariable("zahl"));
    }
}
