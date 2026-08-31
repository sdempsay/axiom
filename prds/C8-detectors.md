# Product Requirements Document (PRD)

## Project: Axiom C8 — Required detectors

**Status: deferred.** Not in the pilot. Stronger than MR comments; only after lookup and (later) lint-diff comments are trusted.

## Overview

Compile-time or static-analysis checks in the parent POM for `severity: required` intents.

## Purpose

Make a vernacular `try/catch` or banned helper a failed `mvn verify` in application modules, even when CI comments are ignored and the agent never called lookup.

## Functional Requirements

### FR1: Detector pack

**API:**

```xml
<dependency>
  <groupId>org.dempsay.axiom</groupId>
  <artifactId>axiom-detectors</artifactId>
  <optional>true</optional>
</dependency>
```

**Behavior:**
- Ships Checkstyle module and/or Error Prone checkers generated from, or kept in sync with, required anti-patterns.
- v1 may be hand-written checkers bound to named intent ids.

### FR2: First checker — external_failure

**API:**

```text
org.dempsay.axiom.detectors.NoExternalCatchCheck
```

**Behavior:**
- Flags `catch (IOException`, `catch (SQLException`, `catch (InterruptedException` in configured application packages.
- Does not flag those catches inside the Exceptional library itself.
- Does not flag `NullPointerException` or domain types.
- Message includes intent id `external_failure` and the blessed symbol.

### FR3: Parent POM wiring

**Behavior:**
- `dempsay-parent` (or an opt-in profile) enables the check for modules that are not in `axiom.detectors.skip`.
- Library modules that own the blessed API set skip or package excludes.

### FR4: Additional checkers

**Behavior:**
- Second checker only after the first has a known false-positive rate.
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
- Java 21. Checkstyle version matches `dempsay-parent`.
- Error Prone optional; do not require it if the parent does not already run it.

### NFR2: Scope
- Required detectors exist only for required intents.
- Available intents stay on lint-diff comments.

### NFR3: Message quality
- Every failure message must include intent id and blessed symbol. “Avoid catching exceptions” is not acceptable.

## Package Structure

```text
detectors/src/main/java/org/dempsay/axiom/detectors/   # later
├── NoExternalCatchCheck.java
└── IntentMessage.java
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
final ExceptionalResponse<String> response = ExceptionalSupplier.of(() -> Files.readString(path))
    .execute();
if (response.wasError()) {
    return null;
}
return response.response();
```
