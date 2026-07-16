# Security and Release-Gate Remediation Design

**Date:** 2026-07-16  
**Scope:** GitHub issues #42-#48 and the remaining release blockers recorded in the 2026-07-16 main-branch test report.

## Outcome

Remove every Critical and High npm advisory from the admin frontend, restore a reproducible frontend quality gate, and change the release decision from blocked to conditionally approved only when the sole remaining blocker is covered by a bounded formal risk acceptance.

## Dependency strategy

- Remove `vuex-persistedstate`: the application does not register or call it, so retaining its vulnerable `shvl` chain has no product value.
- Remove `vue-svgicon`: the live UI uses only the hamburger icon. Replace that use with an inline local SVG and stop importing the generated icon registry. This removes the vulnerable `underscore`, `svgo`, `nth-check`, and legacy yargs chains instead of masking them with overrides.
- Upgrade direct runtime dependencies to maintained safe versions: Axios 1.18.1, js-cookie 3.0.8, Moment 2.30.1, and path-to-regexp 3.3.0.
- Upgrade Vue CLI, webpack, Jest and their Vue 2 adapters as one compatible toolchain. Regenerate the npm lockfile with the documented Node/npm baseline.
- Use npm overrides only for unavoidable transitive advisories after the direct upgrades, and only when a clean install plus tests prove compatibility.

## Security behavior

- Axios must reject cross-origin absolute URLs at the shared request boundary while preserving relative `/api` requests, authentication headers, cancellation and error handling.
- Cookie writes must go through a fixed-name wrapper with fixed attributes. Authentication data must not accept caller-supplied cookie attributes.
- The local static SVG must not execute or load external content.
- Prototype-pollution probes remain unchanged because the unused persisted-state package is removed entirely.

## Quality gates

The frontend CI job uses the same supported Node version declared in `package.json` and runs: clean install, production audit, unit tests, lint without auto-fix, and production build. Existing lint debt is not silently rewritten; the CI command checks only new/changed source files for this remediation while the full legacy lint inventory remains a tracked non-blocking quality item in the report.

## Formal risk acceptance

The remaining default-demo-credential blocker is accepted only for an isolated teaching/demo environment. The acceptance forbids public or production deployment, requires network isolation and credential rotation before any broader deployment, expires after 30 days or at first production/public deployment (whichever is earlier), and is invalidated by scope expansion or sensitive data use. No Critical/High dependency vulnerability is eligible for waiver.

## Report update

Preserve the existing Word report design and update the tested commit/branch, dependency counts, issue disposition, quality-gate results, risk-acceptance record and release conclusion. The final DOCX must be rendered to page images and every page visually inspected.
