# Product Requirements Document (PRD)

## Project: Axiom C8 — Required detectors

## Overview

Compile-time or static-analysis checks in the parent POM for `severity: required` intents. Stronger than MR comments. Smaller set.

## Purpose

Make a vernacular `try/catch` or banned helper a failed `mvn verify` in application modules, even when CI comments are ignored and the agent never called lookup.

## Functional Requirements

### FR1: Detector pack

**API:**

```xml
<dependency>
  <groupId>org.axiom</groupId>
  <artifactId>axiom-detectors</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <optional>true</optional>
</dependency>
```

**Behavior:**
- Ships Checkstyle module and/or Error Prone checkers generated from, or kept in sync with, required anti-patterns.
- v1 may be hand-written checkers bound to named intent ids. Generation from YAML is allowed later.

### FR2: First checker — external_failure

**API:**

```text
org.axiom.detectors.NoExternalCatchCheck
```

**Behavior:**
- Flags `catch (IOException`, `catch (SQLException`, `catch (InterruptedException` in configured application packages.
- Does not flag those catches inside the Exceptional library itself.
- Does not flag `NullPointerException` or domain types.
- Message includes intent id `external_failure` and the blessed symbol.

### FR3: Parent POM wiring

**API:**

```xml
<!-- company-parent -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  ...
</plugin>
```

**Behavior:**
- Company parent enables the check for modules that are not in an allowlist property `axiom.detectors.skip`.
- Library modules that own the blessed API set `axiom.detectors.skip=true` or package excludes.

### FR4: Additional checkers

**Behavior:**
- Second checker only after the first has a known false-positive rate.
- Candidates: `Files.readAllBytes` / `Files.readString` → `file_to_optional_string`; homemade MAC regex → `normalize_mac`.
- Each checker maps to exactly one intent id.

### FR5: Suppression

**API:**

```java
@SuppressWarnings("axiom:external_failure")
```

**Behavior:**
- Documented suppression token per intent.
- Suppressions are acceptable for the carve-outs listed in the intent’s `never` field. They are review-visible.

## Non-Functional Requirements

### NFR1: Java
- Java 21. Checkstyle version matches company parent.
- Error Prone optional; do not require it if the parent does not already run it.

### NFR2: Scope
- Required detectors exist only for required intents.
- Available intents stay on lint-diff comments.

### NFR3: Message quality
- Every failure message must include intent id and blessed symbol. “Avoid catching exceptions” is not acceptable.

## Package Structure

```text
detectors/src/main/java/org/axiom/detectors/
├── NoExternalCatchCheck.java
└── IntentMessage.java

detectors/src/test/resources/
├── catch-io.bad.java
└── exceptional-use.good.java
```

## Test Coverage

1. **NoExternalCatchCheckTest**
   - `catchIoInServiceFails()`
   - `exceptionalSupplierUsePasses()`
   - `npeCatchNotFlagged()`
   - `libraryPackageExcluded()`
   - `messageContainsIntentId()`

## Example Usage

Bad (fails check):

```java
try {
    return Files.readString(path);
} catch (IOException e) {
    return null;
}
```

Good:

```java
var response = ExceptionalSupplier.of(() -> Files2.readOptional(path).orElseThrow())
    .with(ex -> log.warn("read failed", ex))
    .execute();
```
