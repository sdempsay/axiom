# axiom-mcp

Intended GitHub repo: `sdempsay/axiom-mcp`.

Until the remote exists, this directory is a placeholder inside the `sdempsay/axiom` umbrella. It will become a git submodule.

## Job

Live catalog service on a single host: HTTP + MCP in one process, CLI, local file store.

- Add/remove catalogs by Maven GAV (`classifier=agent-catalog`)
- Lookup / search / get
- Gaps as local files
- No auth in the pilot

Libraries do not depend on this artifact. Library CI POSTs a GAV after deploy, or a user runs `axiom catalog add g:a:v`.

## Coordinates

- groupId `org.dempsay.axiom`
- artifact `axiom-mcp`
- parent `org.dempsay.maven:dempsay-parent`
- Java 21
- packages `org.dempsay.axiom.mcp`, `.cli`
- data dir `$AXIOM_DATA` or `~/.axiom`

## Layout (target)

```text
axiom-mcp/
├── ingest/
├── server/
└── cli/
```

PRDs: [C3](../prds/C3-aggregator.md), [C4](../prds/C4-mcp-server.md), [C5](../prds/C5-cli.md), [C9](../prds/C9-gap-workflow.md).
