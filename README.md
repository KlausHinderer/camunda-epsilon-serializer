#About Camunda Serialization
Camunda serialization works great: Every time a process variable is written or updated, a serialized form of the value is immediately calculated. However, this doesn't fit all. One of my customers uses, for legal traceability reasons, a framework that in each JavaDelegate updates an object net stored in a process variable. This means the serialization is done in each process step, letting performance go down.
 
#About this project
This project makes Camunda serialize process variables only when it's needed.