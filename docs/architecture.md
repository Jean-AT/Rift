# Architecture

## Goal

Keep the analyzer safe, extensible, and independent from parser implementation details.

## Layers

- `cli`: command parsing, user interaction, exit codes.
- `analyzer`: orchestration of parsing, rules, and result assembly.
- `parser`: SQL parsing and translation into Rift-owned models.
- `model`: domain objects used by rules and reporting.
- `rules`: isolated detection logic for risky migration patterns.
- `risk`: score and risk-level calculation.
- `reporter`: console and machine-readable output.
- `dialect`: database-specific behavior and capabilities.

## Constraints

- The domain model does not depend on JSqlParser classes.
- Rules must be independently testable.
- SQL analysis must never execute the migration.
- SQL Server is the first supported dialect.
- PostgreSQL support is reserved for the next phase after the SQL Server base is stable.

## Initial package direction

The codebase follows a package layout rooted at `io.rift`, matching the current scaffold.

