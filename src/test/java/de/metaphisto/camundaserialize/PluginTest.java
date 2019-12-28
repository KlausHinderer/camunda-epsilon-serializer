package de.metaphisto.camundaserialize;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
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

    @Test
    @Deployment(resources = { "async.bpmn" })
    public void testBpmnParseListener() throws IOException {
        Map<String,Object> variables = new HashMap<>();
        Date jetzt = new Date(System.currentTimeMillis()-3600000);
        List<Object> vector = new Vector<>();
        vector.add("Teststring in Vector");
        variables.put("datum",jetzt);
        variables.put("vektor",vector);
        variables.put("zahl", 1L);
        ProcessInstanceWithVariables process = processEngineRule.getRuntimeService().createProcessInstanceByKey("Process_async").setVariables(variables).executeWithVariablesInReturn();

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
