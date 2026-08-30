# Rule Catalog

## v0.1 initial rules

- `UPDATE_WITHOUT_WHERE`
- `DELETE_WITHOUT_WHERE`
- `DROP_TABLE`
- `DROP_COLUMN`
- `ALTER_COLUMN`
- `NOT_NULL_WITHOUT_DEFAULT`

## Rule metadata

Each rule should include:

- Unique id
- Human-readable name
- Severity
- Why it matters
- Suggested remediation

## Rule design goal

Rules should remain independent and deterministic so they can be tested in isolation.

