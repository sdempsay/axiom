# Product Requirements Document (PRD)

## Project: Axiom C1 — Catalog schema

## Overview

Canonical record format for an intent fact. Every other Axiom component reads or writes this format. A catalog file belongs to one artifact. The org index is a merge of catalog files.

## Purpose

Define a single YAML document that names a job, the blessed implementation, how to detect the vernacular form, and whether missing it is a build failure.

## Functional Requirements

### FR1: Catalog document

A catalog file declares the producing artifact and a list of intents.

**API:**

```yaml
# catalog.yaml
schemaVersion: 1
artifact:
  groupId: org.dempsay.utils
  artifactId: exceptional
  version: "1.0.7"    # filled by plugin if omitted
owner:
  repo: platform/exceptional-java
  contact: library-owners
intents:
  - id: external_failure
    ...
```

**Behavior:**
- `schemaVersion` is required and must be `1` for this PRD.
- `artifact.groupId` and `artifact.artifactId` are required.
- `artifact.version` may be omitted in source; the plugin writes the project version at build time.
- `intents` must contain at least one entry.
- Intent `id` values must be unique inside the file.

### FR2: Intent record

**API:**

```yaml
id: external_failure
title: Handle external / unrecoverable failure
severity: required          # required | available
language: java
blessed:
  symbol: org.dempsay.utils.exceptional.api.ExceptionalSupplier.of
  kind: method              # method | type | package
snippetRef: examples/ExceptionalSupplier.java
triggers:
  - IOException
  - SQLException
  - "network call"
  - "read file"
antiPatterns:
  - pattern: "catch\\s*\\(\\s*IOException"
    message: Use ExceptionalSupplier, do not catch IOException
  - pattern: "throws\\s+IOException"
    message: Do not bubble IOException on service APIs
never:
  - programming errors (NullPointerException, broken invariants)
  - domain / business exceptions
docs:
  - https://github.com/sdempsay/exceptional-java/blob/master/WhyBeExceptional.md
```

**Behavior:**
- `id` is kebab-or-snake stable across versions. Changing meaning requires a new id and `supersedes`.
- `severity: required` means consumers may fail CI. `available` means prefer, do not fail.
- `blessed.symbol` is the primary entry point, not every overload.
- `snippetRef` is a path relative to the catalog file. The snippet must compile against the declared artifact version.
- `triggers` are plain-language and token hints for lookup, not detectors.
- `antiPatterns.pattern` is a regex applied to source text by lint-diff and detectors.
- `never` is human/agent guidance. It is not a detector.
- `docs` are optional cold links. Lookup must not require following them.

### FR3: Optional supersession

**API:**

```yaml
id: file_to_optional_string
supersedes: [file_read_optional]
```

**Behavior:**
- Aggregator treats superseded ids as aliases for lookup.
- New writes must use the current id.

### FR4: Snippet file

**API:**

```java
// examples/ExceptionalSupplier.java
var response = ExceptionalSupplier.of(() -> fetchData())
    .with(ex -> log.warn("fetch failed", ex))
    .execute();
if (response.wasNoError()) {
    return response.response();
}
```

**Behavior:**
- One primary snippet per intent.
- No surrounding class required. Lint tools that need a compilation unit may wrap it in tests, not in the snippet file.
- Snippet is the object an agent is expected to copy.

### FR5: Validation

**API:**

```text
axiom validate src/main/resources/agent-catalog/catalog.yaml
```

**Behavior:**
- Exit 0 if schema-valid, ids unique, every `snippetRef` exists, every `antiPatterns.pattern` compiles as a regex.
- Exit 1 otherwise. Print the failing intent id and field.

### FR6: JSON Schema publication

**API:**

```text
schema/axiom-catalog-1.json
```

**Behavior:**
- YAML catalogs validate against this schema.
- Schema is versioned with `schemaVersion`.

## Non-Functional Requirements

### NFR1: Language
- Schema is JSON Schema 2020-12.
- Catalog documents are YAML 1.2.

### NFR2: Size
- A catalog should hold on the order of tens of intents, not hundreds.
- No field is a substitute for Javadoc of the full type.

### NFR3: Stability
- Intent ids are immutable after the first aggregated release.
- Additive fields are allowed in `schemaVersion: 1` if they are optional.

## Package Structure

```text
schema/
├── axiom-catalog-1.json
├── examples/
│   ├── exceptional.catalog.yaml
│   └── exceptional/
│       └── ExceptionalSupplier.java
└── README.md
```

## Test Coverage

1. **ValidateCatalogTest**
   - `validCatalogPasses()`
   - `duplicateIntentIdFails()`
   - `missingSnippetRefFails()`
   - `invalidRegexFails()`
   - `missingArtifactCoordinatesFails()`

2. **SchemaExampleTest**
   - `exceptionalExampleValidates()`
   - `fileOptionalExampleValidates()`
   - `macNormalizeExampleValidates()`

## Example Usage

```yaml
schemaVersion: 1
artifact:
  groupId: org.dempsay.utils
  artifactId: exceptional
intents:
  - id: external_failure
    title: Handle external / unrecoverable failure
    severity: required
    language: java
    blessed:
      symbol: org.dempsay.utils.exceptional.api.ExceptionalSupplier.of
      kind: method
    snippetRef: examples/ExceptionalSupplier.java
    triggers: [IOException, SQLException, "external I/O"]
    antiPatterns:
      - pattern: "catch\\s*\\(\\s*(IOException|SQLException)"
        message: Use Exceptional, do not catch checked external exceptions
    never:
      - NullPointerException
      - domain exceptions
```
