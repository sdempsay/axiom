# Product Requirements Document (PRD)

## Project: Axiom C2 — Maven catalog plugin

## Overview

Maven plugin that validates a module’s `catalog.yaml`, stamps the project version, optionally harvests `@AgentCapability` annotations, and attaches the catalog to the build.

## Purpose

Library owners keep the catalog next to source. The plugin makes an invalid catalog a failed `mvn verify`, and makes a valid catalog a published artifact consumers and the aggregator can fetch.

## Functional Requirements

### FR1: Default source location

**API:**

```text
src/main/resources/agent-catalog/catalog.yaml
src/main/resources/agent-catalog/examples/
```

**Behavior:**
- Plugin looks here unless `catalogFile` is set.
- Missing file: skip with `INFO` unless `required=true` (default `false` for unmarked modules, `true` when the plugin is bound in a library that opted in).

### FR2: Validate and stamp

**API:**

```xml
<plugin>
  <groupId>org.axiom</groupId>
  <artifactId>axiom-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>catalog</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

**Behavior:**
- Reads catalog, validates against C1 schema.
- Writes `artifact.version` from `${project.version}` when absent or when `stampVersion=true` (default true).
- Fails the build on validation error.

### FR3: Attach classified artifact

**API:**

```text
${project.groupId}:${project.artifactId}:${project.version}:yaml:agent-catalog
```

**Behavior:**
- Attaches the stamped YAML as classifier `agent-catalog`, type `yaml`.
- Also copies stamped YAML to `META-INF/axiom/catalog.yaml` in the main jar when `embedInJar=true` (default true).
- Copies referenced example files next to the classified artifact in a zip classifier `agent-catalog-examples` when examples exist.

### FR4: Optional annotation harvest

**API:**

```java
@AgentCapability(
    id = "file_to_optional_string",
    title = "Read file to Optional<String>",
    severity = Severity.AVAILABLE,
    triggers = {"read file", "Optional<String>"},
    antiPatterns = {"Files\\.readAllBytes", "Files\\.readString"})
public static Optional<String> readOptional(Path path) { ... }
```

**Behavior:**
- Annotation lives in `org.axiom.annotations` (`provided` or a tiny `axiom-annotations` jar).
- Harvest runs only when `harvestAnnotations=true`.
- Harvested rows merge with YAML. YAML wins on field conflict.
- Harvest does not invent snippets. `snippetRef` must still exist in YAML or the build fails for that intent.

### FR5: Skip and require flags

**API:**

```xml
<required>true</required>
<skip>${axiom.skip}</skip>
```

**Behavior:**
- `required=true` and missing catalog → fail.
- `skip=true` → no-op.

### FR6: Report

**API:**

```text
target/axiom/catalog.yaml
target/axiom/report.md
```

**Behavior:**
- Writes stamped catalog and a count of intents by severity.
- Log line: `Axiom catalog: N intents (R required, A available) for g:a:v`.

## Non-Functional Requirements

### NFR1: Maven
- Maven 3.8+. Java 21.
- Bind default execution to `process-resources` or `prepare-package` so `verify` sees the attached artifact.

### NFR2: Dependencies
- Uses `axiom-model` for parse/validate. No network calls.

### NFR3: Idempotence
- Re-running the goal with the same inputs produces byte-stable YAML field order (plugin writes via the model serializer).

## Package Structure

```text
plugin/src/main/java/org/axiom/maven/
├── CatalogMojo.java
├── AnnotationHarvester.java
└── CatalogWriter.java

annotations/src/main/java/org/axiom/annotations/
├── AgentCapability.java
└── Severity.java
```

## Test Coverage

1. **CatalogMojoTest**
   - `validCatalogAttachesClassifier()`
   - `invalidCatalogFailsBuild()`
   - `stampsProjectVersion()`
   - `missingOptionalCatalogSkips()`
   - `missingRequiredCatalogFails()`
   - `embedInJarWritesMetaInf()`

2. **AnnotationHarvesterTest**
   - `harvestMergesYamlWins()`
   - `harvestWithoutSnippetFailsWhenEnabled()`

## Example Usage

```xml
<plugin>
  <groupId>org.axiom</groupId>
  <artifactId>axiom-maven-plugin</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <executions>
    <execution>
      <goals><goal>catalog</goal></goals>
      <configuration>
        <required>true</required>
      </configuration>
    </execution>
  </executions>
</plugin>
```
