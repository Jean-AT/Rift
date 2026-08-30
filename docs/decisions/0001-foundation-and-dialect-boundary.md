# ADR 0001: Foundation and dialect boundary

## Status

Accepted

## Context

Rift needs to support multiple database dialects without tying the domain model to a specific parser library.

## Decision

- Use a parser adapter boundary between JSqlParser and Rift models.
- Keep SQL Server as the first dialect.
- Treat PostgreSQL as a later expansion of the same architecture.
- Keep analysis offline and non-executing.

## Consequences

- Parser-specific behavior stays isolated.
- Rules can operate on consistent internal models.
- New dialects can be added without rewriting the analyzer core.

