# Product Requirements Document (PRD)

## Project: Axiom C2 — Maven catalog plugin

## Overview

Maven plugin that validates a module’s `catalog.yaml`, stamps the project version, optionally harvests `@AgentCapability` annotations, and attaches the catalog to the build.

Lives in `sdempsay/axiom-plugin`. Libraries depend on this plugin. They do **not** depend on `axiom-mcp`.

## Purpose

Library owners keep the catalog next to source. The plugin makes an invalid catalog a failed `mvn verify`, and makes a valid catalog a published Maven artifact (`classifier=agent-catalog`) that `axiom-mcp` can fetch by GAV.

## Functional Requirements

### FR1: Default source location

**API:**

```text
src/main/resources/agent-catalog/catalog.yaml
src/main/resources/agent-catalog/examples/
```

**Behavior:**
- Plugin looks here unless `catalogFile` is set.
- Missing file: skip with `INFO` unless `required=true` (default `false` for unmarked modules, `true` when a library has opted in).

### FR2: Validate and stamp

**API:**

```xml
<plugin>
  <groupId>org.dempsay.axiom</groupId>
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
- Reads catalog, validates against C1 schema via `axiom-model`.
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

The plugin does not POST to `axiom-mcp`. Library CI does that after deploy (see C3).

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
- Annotation lives in `org.dempsay.axiom.annotations` (`provided`, artifact `axiom-annotations`).
- Harvest runs only when `harvestAnnotations=true` (default false).
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
- Parent: `org.dempsay.maven:dempsay-parent`.
- Bind default execution to `process-resources` or `prepare-package` so `verify` sees the attached artifact.

### NFR2: Dependencies
- Uses `axiom-model` for parse/validate. No network calls. No dependency on `axiom-mcp`.

### NFR3: Idempotence
- Re-running the goal with the same inputs produces byte-stable YAML field order (plugin writes via the model serializer).

## Package Structure

```text
axiom-plugin/
├── plugin/src/main/java/org/dempsay/axiom/plugin/
│   ├── CatalogMojo.java
│   ├── AnnotationHarvester.java
│   └── CatalogWriter.java
└── annotations/src/main/java/org/dempsay/axiom/annotations/
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

Exceptional (M4) binds the plugin with `required=true`. Until then, plugin tests use a fixture that looks like Exceptional.
