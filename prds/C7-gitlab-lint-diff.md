# Product Requirements Document (PRD)

## Project: Axiom C7 — GitLab lint-diff

**Status: deferred.** Not in the pilot. Private consuming apps may live on GitLab while Axiom itself is public GitHub. This job is how GitLab MRs get “we already have this” without relying on the agent.

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
  - project: <later>
    file: gitlab-ci/axiom-lint.yml
```

**Behavior:**
- Defines job `axiom-lint-diff`.
- Runs on merge request pipelines.
- Needs changed files in the MR, not the whole tree.
- Index comes from `axiom-mcp` HTTP or a cached export; not from a GitLab `axiom-catalog` project (that repo does not exist).

### FR2: Scan

**API:**

```text
axiom lint-diff --data ~/.axiom --diff changes.diff --format gitlab
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
(`org.dempsay.utils:exceptional:1.0.9`).

```java
final ExceptionalResponse<Data> response = ExceptionalSupplier.of(() -> fetchData())
    .execute();
```

Do not catch `IOException` here. If this site is a documented exception, say why on the MR.
```

**Behavior:**
- Includes intent id, severity, symbol, GAV, primary snippet.
- Posts via GitLab MR discussions API on the offending line when the diff position is available; otherwise a single MR note listing files.

### FR4: Severity handling

**Behavior:**
- First rollout: `required` and `available` both comment. Job exits 0 unless `--fail-on required` is set.
- Later: `--fail-on required` may be enabled per group.

### FR5: Allowlist

**API:**

```text
# .axiom/allow.txt
src/main/java/com/example/legacy/OldReader.java:file_to_optional_string
```

**Behavior:**
- File + intent id pairs suppress comment and failure.
- Allowlist hits are logged. They are not silent.

## Non-Functional Requirements

### NFR1: Runtime
- Same `axiom` CLI on the runner.
- GitLab token as needed for notes.

### NFR2: Time
- Scan of a typical MR (≤ 30 files) under 30 seconds after index is cached.

### NFR3: Noise
- One comment thread per intent per file per MR. Re-runs update the thread; they do not spam new threads.

## Package Structure

```text
gitlab-ci/                 # later
├── axiom-lint.yml
└── README.md
```

## Test Coverage

1. **LintDiffTest**
   - `ioExceptionCatchCommentsExternalFailure()`
   - `testSourcesSkipped()`
   - `allowlistSuppresses()`
   - `availableDoesNotFailByDefault()`
   - `requiredFailsWhenFlagSet()`
