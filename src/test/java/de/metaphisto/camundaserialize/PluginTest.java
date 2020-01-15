package de.metaphisto.camundaserialize;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class PluginTest {

    @Rule
    public ProcessEngineRule processEngineRule = new ProcessEngineRule();
    static final Date JETZT = new Date(System.currentTimeMillis() - 3600000);
    static final List<Object> VECTOR = new Vector<>();
    static final long VALUE = 1L;

    @Test
    @Deployment(resources = {"async.bpmn"})
    public void testBpmnParseListener() throws IOException {
        //This test starts a process. The process reaches a UserTask, where values are serialized and deserialized.
        Map<String, Object> variables = new HashMap<>();
        VECTOR.add("Teststring in Vector");
        variables.put("datum", JETZT);
        variables.put("vektor", VECTOR);
        variables.put("zahl", VALUE);
        processEngineRule.getRuntimeService().createProcessInstanceByKey("Process_async").setVariables(variables).executeWithVariablesInReturn();

        TaskService taskService = processEngineRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        assertEquals("UserTask1", task.getName());

        taskService.complete(task.getId());

        Job job = processEngineRule.getManagementService().createJobQuery().singleResult();
        processEngineRule.getManagementService().executeJob(job.getId());

        // check if process instance ended
        assertNull(processEngineRule.getRuntimeService().createProcessInstanceQuery().singleResult());

    }
}
