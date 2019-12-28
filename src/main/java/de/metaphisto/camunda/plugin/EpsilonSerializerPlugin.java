package de.metaphisto.camunda.plugin;

import de.metaphisto.camunda.bpmnparse.AddSerializationListenerBpmnParseListener;
import de.metaphisto.camunda.serializer.EpsilonSerializer;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plugin zur Optimierung der Camunda-Performance bei Prozessen, die große Prozessvariablen (z.B. Objektnetze) schreiben.
 *
 * Bei jedem Schreibzugriff auf eine Prozessvariable wird diese von Camunda automatisch serialisiert.
 * Dieses Plugin deaktiviert die Camunda-Serialisierung komplett. An den Stellen im Prozess, in denen die Variablen in die Datenbank gesichert werden (UserTasks, async-Punkte), hängt dieses Plugin Listener, die sich um die Serialisierung kümmern. Diese Listener sind vom Anwender bereitzustellen und gemeinsam mit dem Prozess zu deployen.
 *
 */
public class EpsilonSerializerPlugin extends AbstractProcessEnginePlugin {

    private String serializationListenerClass;
    private String deserializationListenerClass;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<BpmnParseListener> customPostBPMNParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
        if(customPostBPMNParseListeners == null) {
            customPostBPMNParseListeners = new ArrayList<>();
            processEngineConfiguration.setCustomPostBPMNParseListeners(customPostBPMNParseListeners);
        }
        AddSerializationListenerBpmnParseListener addSerializationListenerBpmnParseListener = new AddSerializationListenerBpmnParseListener();
        addSerializationListenerBpmnParseListener.setDeserializationListenerClass(deserializationListenerClass);
        addSerializationListenerBpmnParseListener.setSerializationListenerClass(serializationListenerClass);
        customPostBPMNParseListeners.add(addSerializationListenerBpmnParseListener);

        processEngineConfiguration.setCustomPreVariableSerializers(Collections.singletonList(new EpsilonSerializer("json")));

        //Fallback zum ObjectSerializer verhindern.
        processEngineConfiguration.setDefaultSerializationFormat("json");
    }

    public String getSerializationListenerClass() {
        return serializationListenerClass;
    }

    public void setSerializationListenerClass(String serializationListenerClass) {
        this.serializationListenerClass = serializationListenerClass;
    }

    public String getDeserializationListenerClass() {
        return deserializationListenerClass;
    }

    public void setDeserializationListenerClass(String deserializationListenerClass) {
        this.deserializationListenerClass = deserializationListenerClass;
    }
}
