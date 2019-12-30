/*
 * Released to the public domain.
 */

package de.metaphisto.camunda.bpmnparse;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateListener;
import org.camunda.bpm.engine.impl.bpmn.listener.ClassDelegateExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

import java.util.Collections;

/**
 * https://docs.camunda.org/manual/7.8/user-guide/process-engine/transactions-in-processes/
 * <p>
 * Serialization:
 * async-before: add Take Listener to the transition
 * async-after: add End Listener to the Activity
 * async-before at Start: throw Exception, if always_serialize_on_send is false
 * UserTask: Start Listener
 * <p>
 * Deserialization:
 * async-before: start Listener
 * async-after: take Listener
 * UserTask: EndListener
 *
 */
public class AddSerializationListenerBpmnParseListener extends AbstractBpmnParseListener {

    public static final String START_EVENT_LISTENER = "start";
    public static final String END_EVENT_LISTENER = "end";
    public static final String TAKE_EVENT_LISTENER = "take";

    private String serializationListenerClass;
    private String deserializationListenerClass;
    private boolean alwaysSerializeOnSendTasksOrCallActivities = false;

    @Override
    public void parseSequenceFlow(Element sequenceFlowElement, ScopeImpl scopeElement, TransitionImpl transition) {
        super.parseSequenceFlow(sequenceFlowElement, scopeElement, transition);
        if (transition.getSource().isAsyncAfter()) {
            transition.addListener(TAKE_EVENT_LISTENER, createDeserializationListener());
        }
        if (transition.getDestination().isAsyncBefore()) {
            transition.addListener(TAKE_EVENT_LISTENER, createSerializationListener());
        }
    }

    @Override
    public void parseManualTask(Element manualTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseManualTask(manualTaskElement, scope, activity);
        activity.addListener(START_EVENT_LISTENER, createSerializationListener());
        activity.addListener(END_EVENT_LISTENER, createDeserializationListener(), 0);
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseUserTask(userTaskElement, scope, activity);
        activity.addListener(START_EVENT_LISTENER, createSerializationListener());
        activity.addListener(END_EVENT_LISTENER, createDeserializationListener(), 0);
    }

    @Override
    public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseServiceTask(serviceTaskElement, scope, activity);
        if (activity.isAsyncBefore()) {
            activity.addListener(START_EVENT_LISTENER, createDeserializationListener(), 0);
        }
        if (activity.isAsyncAfter()) {
            activity.addListener(END_EVENT_LISTENER, createSerializationListener());
        }
    }

    @Override
    public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl startEventActivity) {
        super.parseStartEvent(startEventElement, scope, startEventActivity);
        if (startEventActivity.isAsyncBefore()) {
            if (alwaysSerializeOnSendTasksOrCallActivities) {
                startEventActivity.addListener(START_EVENT_LISTENER, createDeserializationListener(), 0);
            } else {
                throw new RuntimeException("async-before on start events is disabled by configuration. Can't you use async-after instead?");
            }
        }
        if (startEventActivity.isAsyncAfter()) {
            startEventActivity.addListener(END_EVENT_LISTENER, createSerializationListener());
        }
    }

    @Override
    public void parseTask(Element taskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseTask(taskElement, scope, activity);
        if (activity.isAsyncBefore()) {
            activity.addListener(START_EVENT_LISTENER, createDeserializationListener(), 0);
        }
        if (activity.isAsyncAfter()) {
            activity.addListener(END_EVENT_LISTENER, createSerializationListener());
        }
    }

    @Override
    public void parseCallActivity(Element callActivityElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseCallActivity(callActivityElement, scope, activity);
        if (activity.isAsyncBefore()) {
            activity.addListener(START_EVENT_LISTENER, createDeserializationListener(), 0);
        }
        if (activity.isAsyncAfter()) {
            activity.addListener(END_EVENT_LISTENER, createSerializationListener());
        }
        if(alwaysSerializeOnSendTasksOrCallActivities) {
            activity.addListener(START_EVENT_LISTENER, createSerializationListener());
        }
    }

    @Override
    public void parseScriptTask(Element scriptTaskElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseScriptTask(scriptTaskElement, scope, activity);
        if(activity.isAsyncBefore()) {
            activity.addListener(START_EVENT_LISTENER, createDeserializationListener(),0);
        }
        if(activity.isAsyncAfter()) {
            activity.addListener(END_EVENT_LISTENER, createSerializationListener());
        }
    }

    public void setSerializationListenerClass(String serializationListenerClass) {
        this.serializationListenerClass = serializationListenerClass;
    }

    public void setDeserializationListenerClass(String deserializationListenerClass) {
        this.deserializationListenerClass = deserializationListenerClass;
    }

    public void setAlwaysSerializeOnSendTasksOrCallActivities(boolean alwaysSerializeOnSendTasksOrCallActivities) {
        this.alwaysSerializeOnSendTasksOrCallActivities = alwaysSerializeOnSendTasksOrCallActivities;
    }

    protected DelegateListener<DelegateExecution> createSerializationListener() {
        return new ClassDelegateExecutionListener(serializationListenerClass, Collections.emptyList());
    }

    protected DelegateListener<DelegateExecution> createDeserializationListener() {
        return new ClassDelegateExecutionListener(deserializationListenerClass, Collections.emptyList());
    }
}
