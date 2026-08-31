# PRD updates (post-interview)

Requirements learned while reconciling the original zip with this org. Canonical product text is [PROJECT.md](PROJECT.md) and [PLAN.md](PLAN.md). This file is the decision log.

| ID | Decision |
|---|---|
| U1 | Maven `groupId` is `org.dempsay.axiom`, not `org.axiom`. |
| U2 | Java 21. Parent POM `org.dempsay.maven:dempsay-parent`. Packages `org.dempsay.axiom.*`. |
| U3 | Umbrella GitHub repo `sdempsay/axiom` (this tree). Component repos `sdempsay/axiom-plugin` and `sdempsay/axiom-mcp`, attached as git submodules. |
| U4 | `axiom-plugin` holds schema, annotations, and the Maven plugin only. |
| U5 | `axiom-mcp` holds ingest/merge, HTTP, MCP, and CLI. One process; MCP is another transport on the HTTP app. |
| U6 | GitLab lint-diff, parent-POM detectors, and the consumer dispatcher are **not** in the pilot. |
| U7 | First catalog is Exceptional (`org.dempsay.utils:exceptional`, `sdempsay/exceptional-java` on GitHub). |
| U8 | Axiom is public GitHub. Usage may mix public GitHub libraries with private GitLab apps. |
| U9 | Primary `external_failure` snippet is `ExceptionalSupplier.of(...).execute()` then `wasError()` / `response()`. `chain` / `then` is a second example, not a second intent. |
| U10 | Libraries never depend on `axiom-mcp`. They add the Maven plugin and publish `classifier=agent-catalog`. |
| U11 | Library CI POSTs the GAV to `axiom-mcp` after deploy. A user can also add a Maven GAV by API without waiting for that CI. |
| U12 | Remove-from-catalog is a first-class write on the service. |
| U13 | Pilot store is local files on the machine running `axiom-mcp`. No org-wide `axiom-index` Maven artifact. |
| U14 | Pilot auth: anyone who can reach the service may add or remove. Lookup is equally open. |
| U15 | Pilot hosting: one process on a single dev host. |
| U16 | `catalog_gap` writes a local gap file. GitHub issues are later. |
| U17 | Add-library identifier is a Maven GAV, not a git URL. |
| U18 | Do not call Maven Resolver “Aether” in Axiom docs (name clash with the Aether persistence project). |
| U19 | `ExceptionalResponse` has `wasError()`, not a required `wasNoError()` in the blessed snippet. |
