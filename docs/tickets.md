# Ticket Status

Current date: 2026-09-01

This document tracks the v0.1 execution status from T1 to the release finish line.

## Status Summary

- Done: T1-T9
- Pending: T10-T22
- Release gap: rules, reporting, stabilization, and release packaging

## Ticket Map

| Ticket | Status | Description |
| --- | --- | --- |
| T1 | Done | `scan` command no longer acts as a scaffold. |
| T2 | Done | `scan` accepts a file or directory and discovers `.sql` files. |
| T3 | Done | `scan` returns real exit codes for success, usage, and IO errors. |
| T4 | Done | `JSqlParserAdapter` parses SQL into Rift-owned statements. |
| T5 | Done | SQL is normalized into `SqlStatement` and `ParsedMigration`. |
| T6 | Done | SQL Server batch separators and statement splitting are handled. |
| T7 | Done | Statement line numbers and table names are captured. |
| T8 | Done | Implement `UPDATE_WITHOUT_WHERE`. |
| T9 | Done | Implement `DELETE_WITHOUT_WHERE`. |
| T10 | Pending | Implement `DROP_TABLE` and `DROP_COLUMN`. |
| T11 | Pending | Implement `ALTER_COLUMN`. |
| T12 | Pending | Implement `NOT_NULL_WITHOUT_DEFAULT` if it remains in v0.1 scope. |
| T13 | Pending | Connect findings to `RiskCalculator`. |
| T14 | Pending | Print stable console output with severity, line, SQL, and explanation. |
| T15 | Pending | Align `risk score` and `risk level` output with `Rift.md`. |
| T16 | Pending | Add isolated unit tests for each rule. |
| T17 | Pending | Add parser tests for valid SQL, invalid SQL, and edge cases. |
| T18 | Pending | Add CLI tests for `scan` and `version`. |
| T19 | Pending | Verify false positives and dialect differences. |
| T20 | Pending | Create and stabilize `release/v0.1`. |
| T21 | Pending | Run the full build and keep it reproducible in CI. |
| T22 | Pending | Tag `v0.1.0` once build, tests, and reporting are green. |

## What Is Missing To Close v0.1

- The first two risk-relevant rules are implemented, but the remaining rules are not.
- Findings do not flow into the CLI reporter yet.
- The risk score is defined, but not wired to real analysis results.
- The release packaging and branch stabilization steps are still pending.

## Next Execution Block

Recommended next tickets:

1. T10-T12: finish the first rule engine pass.
2. T13-T15: findings, risk calculation, and console reporting.
3. T16-T19: coverage and regression protection.
4. T20-T22: release stabilization and tagging.
