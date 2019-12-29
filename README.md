#About Camunda Serialization
Camunda serialization works great: Every time a process variable is written or updated, a serialized form of the value is immediately calculated. However, this doesn't fit all. One of my customers uses, for legal traceability reasons, a framework that in each JavaDelegate updates an object net stored in a process variable. This means the serialization is done in each process step, letting performance go down.
 
#About this project
This project makes Camunda serialize process variables only when it's needed. Serialization is needed before async continuations, UserTasks and ExternalTasks.

## Component: EpsilonSerializer
EpsilonSerializer must be added to the camunda configuration. It then replaces the serialization of Objects with a dummy operation. Primitive values are still serialized by standard camunda serializers.

##Component: AddSerializationListenerBpmnParseListener
When deploying a bpmn process, this plugin adds a serialization listener to the points where serialization is needed, and a deserialization listeners to the point where deserialization is needed. This is done during the deployment of the ProcessApplication, the changes made by this plugin are only visible in the database.
 
##Component: SerializationListener and DeserializationListener
These are implementations that you have to provide in your deployment. In the test-part of this project, some implementation is provided that you can use as a template. Packaged with your application, the implementation will have full access to all of your classes.
 
##Configuration:
See camunda.cfg.xml for the configuration. Add the camundaSerializer.jar to the classpath next to the camunda libraries. Deploy your application containing the bpmn-processes and listener implementations and your process instances will do serialization only when needed.

#Status of this project
This project disables serialization and adds your listener implementation to the correct points in your processes. I can't guarantee all cases are covered, but I have used this implementation in real-world projects without any problems.
I may or may not provide updates for this project, but this is released to the public domain, so you can tailor it for your project.