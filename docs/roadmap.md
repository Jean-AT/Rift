# Roadmap

## Current status

As of 2026-09-01:

- Completed: T1-T8
- Next work block: T9-T12
- Remaining to close v0.1: T9-T22

The project now has:

- a working CLI scaffold,
- executable packaging,
- SQL Server parser normalization,
- ticket tracking under `docs/tickets.md`.

## v0.1 phases

### Phase 0: project foundation

- Create Maven build and root metadata.
- Add docs structure and engineering decisions.
- Establish GitFlow and commit conventions.

### Phase 1: CLI skeleton

- Provide `rift`, `scan`, and `version` commands.
- Wire Picocli into the executable entry point.
- Keep command behavior minimal until analysis exists.

### Phase 2: domain model

- Define Rift-owned SQL statement, severity, finding, and risk abstractions.
- Keep parser output separate from rule input.

### Phase 3: SQL Server parsing

- Add a parser adapter over JSqlParser.
- Normalize statements into Rift models.
- Capture line numbers and table references where possible.

### Phase 4: rule engine

- Implement the first dangerous-migration rules.
- Keep rules independent and focused.

### Phase 5: reporting and risk

- Produce console output for findings and risk.
- Add a score and risk level for CLI consumption.

### Phase 6: stabilization

- Add tests for valid SQL, dangerous SQL, edge cases, and false positives.
- Harden the CLI contract and prepare the `v0.1` release branch.

## Future phases

- PostgreSQL dialect support.
- JSON output.
- Directory scanning.
- Rule explanations and catalog command.
