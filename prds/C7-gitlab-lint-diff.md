# Product Requirements Document (PRD)

## Project: Axiom C7 — GitLab lint-diff

## Overview

Shared GitLab CI include that scans MR diffs for catalog anti-patterns and comments the blessed snippet. First enforcement mechanism that does not rely on the agent reading the dispatcher.

## Purpose

Move “we already have this” from human review to an automated note on the merge request.

## Functional Requirements

### FR1: CI include

**API:**

```yaml
# consumer .gitlab-ci.yml
include:
  - project: platform/axiom
    file: gitlab-ci/axiom-lint.yml
```

**Behavior:**
- Defines job `axiom-lint-diff`.
- Runs on merge request pipelines.
- Needs `CI_MERGE_REQUEST_DIFF` or equivalent: changed files in the MR, not the whole tree.

### FR2: Scan

**API:**

```text
axiom lint-diff --index index.yaml --diff changes.diff --format gitlab
```

**Behavior:**
- Applies each intent `antiPatterns.pattern` to added lines only.
- Default: Java/Kotlin files. Configurable `pathInclude` / `pathExclude` (skip `src/test`, generated sources).
- First matching pattern per file region is enough. Do not stack five comments on one catch.

### FR3: Comment body

**API:**

```markdown
Axiom: this diff matches **external_failure** (`required`).

Use `org.dempsay.utils.exceptional.api.ExceptionalSupplier.of`
(`org.dempsay.utils:exceptional:1.0.7`).

```java
var response = ExceptionalSupplier.of(() -> fetchData())
    .with(ex -> log.warn("fetch failed", ex))
    .execute();
```

Do not catch `IOException` here. If this site is a documented exception, say why on the MR.
```

**Behavior:**
- Includes intent id, severity, symbol, GAV, snippet.
- Posts via GitLab MR discussions API on the offending line when the diff position is available; otherwise a single MR note listing files.

### FR4: Severity handling (M6 vs M7)

**Behavior:**
- M6: `required` and `available` both comment. Job exits 0 unless `--fail-on required` is set.
- M7+: `--fail-on required` may be enabled per group. Job exits 1 if a required match remains.

### FR5: Allowlist

**API:**

```text
# .axiom/allow.txt
src/main/java/com/example/legacy/OldReader.java:file_to_optional_string
```

**Behavior:**
- File + intent id pairs suppress comment and failure.
- Allowlist hits are logged. They are not silent.

### FR6: Index fetch

**Behavior:**
- Job calls `axiom cache refresh` or curls the published `index.yaml`.
- Pin available via `AXIOM_INDEX_URL` CI variable.

## Non-Functional Requirements

### NFR1: Runtime
- Same `axiom` CLI image or jar on the runner.
- GitLab token: `CI_JOB_TOKEN` for notes if permissions allow; otherwise a project access token stored as CI variable `AXIOM_GITLAB_TOKEN`.

### NFR2: Time
- Scan of a typical MR (≤ 30 files) under 30 seconds after index is cached.

### NFR3: Noise
- One comment thread per intent per file per MR. Re-runs update the thread; they do not spam new threads.

## Package Structure

```text
gitlab-ci/
├── axiom-lint.yml
└── README.md

cli/.../LintDiffCommand.java
```

## Test Coverage

1. **LintDiffTest**
   - `ioExceptionCatchCommentsExternalFailure()`
   - `testSourcesSkipped()`
   - `allowlistSuppresses()`
   - `availableDoesNotFailByDefault()`
   - `requiredFailsWhenFlagSet()`

## Example Usage

```yaml
# axiom-lint.yml (excerpt)
axiom-lint-diff:
  stage: test
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"
  script:
    - axiom cache refresh
    - axiom lint-diff --diff "$CI_MERGE_REQUEST_DIFF_FILE" --format gitlab
```
