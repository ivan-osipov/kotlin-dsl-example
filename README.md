Kotlin DSL Example

This project is an example of Kotlin DSL building. Here you'll find examples of different features usage.
The domain of this example is building of schedule for teachers and students.

Folder `src/test/kotlin/kotlin_dsl` contains follow files:

- Demo.kt which presents DSL usage
- dsl.kt defines DSL's contexts
- annotations.kt which presents tool for context control

Folder `src/main/kotlin/kotlin_dsl` contains follow files:

- model.kt which contains a whole domain model of this example
- Matrix.kt is wrapper for a two-dimensional array
- DataSet.kt is a container which encapsulate data for the scheduler
- scheduler subpackage contains a dummy scheduler implementation