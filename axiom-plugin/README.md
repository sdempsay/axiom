# axiom-plugin

Intended GitHub repo: `sdempsay/axiom-plugin`. Sibling service: `sdempsay/axiom-mcp`.

Until the remote exists, this directory is a placeholder inside the `sdempsay/axiom` umbrella. It will become a git submodule.

## Job

Schema, Java model, `@AgentCapability` annotations, and the Maven plugin that attaches `classifier=agent-catalog`.

Libraries depend on this plugin. They do not depend on `axiom-mcp`.

## Coordinates

- groupId `org.dempsay.axiom`
- artifacts: `axiom-model`, `axiom-annotations`, `axiom-maven-plugin`
- parent `org.dempsay.maven:dempsay-parent`
- Java 21
- packages `org.dempsay.axiom.model`, `.annotations`, `.plugin`

## Layout (target)

```text
axiom-plugin/
├── schema/
│   ├── axiom-catalog-1.json
│   └── examples/
├── model/
├── annotations/
└── plugin/
```

PRDs: [C1](../prds/C1-catalog-schema.md), [C2](../prds/C2-maven-plugin.md).
