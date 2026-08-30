# GitFlow

## Branches

- `main`: stable release history.
- `develop`: integration branch for the current version line.
- `feature/<name>`: isolated work for a single capability.
- `release/v0.1`: stabilization branch before tagging the first release.
- `hotfix/<name>`: urgent fixes after release.

## Merge policy

- Merge feature branches into `develop`.
- Merge `release/v0.1` into `main` only after validation.
- Back-merge release fixes into `develop`.

## Commit style

Use small, single-purpose commits.

Recommended prefixes:

- `chore:` setup and maintenance
- `docs:` documentation only
- `feat:` new functionality
- `test:` test coverage
- `refactor:` code movement without behavior change

Examples:

- `chore: initialize maven build`
- `docs: add v0.1 roadmap`
- `feat: add scan command scaffold`
- `test: cover update without where rule`

## Integration rule

Do not leave a feature branch half-integrated. Each branch should end in a state that can be merged into `develop` without breaking the build.

