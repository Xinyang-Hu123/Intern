# Security Release Gates Implementation Plan

> **For Codex:** Execute this plan task-by-task in the current isolated worktree and verify each acceptance command before closing issues.

**Goal:** Close issues #42-#48 by removing all Critical/High frontend dependency vulnerabilities, restoring reproducible frontend gates, formally accepting the one remaining bounded release risk, and updating the Word test report.

**Architecture:** Remove unused vulnerable packages, replace the only live generated SVG with local markup, upgrade remaining direct dependencies and the Vue 2 build/test toolchain, and enforce safe shared HTTP/cookie boundaries. Keep the existing application architecture and report visual system.

**Tech Stack:** Vue 2, TypeScript, Vue CLI, Jest, ESLint, npm, GitHub Actions, Maven, python-docx, LibreOffice.

---

### Task 1: Establish regression tests

**Files:**
- Add: `frontend/admin-vue/tests/unit/components/Hamburger.spec.ts`
- Add: `frontend/admin-vue/tests/unit/utils/requestSecurity.spec.ts`
- Modify: `frontend/admin-vue/tests/unit/components/Breadcrumb.spec.ts`

1. Add a failing test that requires Hamburger to render a local `<svg>` without the removed `svg-icon` runtime.
2. Add failing tests for relative URL acceptance and cross-origin absolute URL rejection.
3. Inject the router into the existing Breadcrumb mount and await navigation updates.
4. Run `npm run test:unit -- --runInBand` and record the expected red state before production changes.

### Task 2: Remove vulnerable unused/icon chains

**Files:**
- Modify: `frontend/admin-vue/src/main.ts`
- Modify: `frontend/admin-vue/src/components/Hamburger/index.vue`
- Delete: generated files under `frontend/admin-vue/src/icons/components/`
- Modify: `frontend/admin-vue/package.json`

1. Remove `vue-svgicon`, its global plugin registration and generated registry import.
2. Render the hamburger icon as local SVG markup.
3. Remove unused `vuex-persistedstate`.
4. Run the focused Hamburger test, then the full unit suite.

### Task 3: Upgrade and harden runtime dependencies

**Files:**
- Modify: `frontend/admin-vue/package.json`
- Modify: `frontend/admin-vue/src/utils/request.ts`
- Modify: `frontend/admin-vue/src/utils/cookies.ts`
- Modify: direct Cookie callers as needed

1. Upgrade Axios, js-cookie, Moment and path-to-regexp to safe compatible versions.
2. Add and apply a same-origin/relative API URL guard at the shared Axios request interceptor.
3. Fix cookie names and security attributes in the wrapper, preserving current login behavior.
4. Run unit tests after each behavior change.

### Task 4: Modernize build/test baseline and CI

**Files:**
- Modify: `frontend/admin-vue/package.json`
- Modify: `frontend/admin-vue/package-lock.json`
- Delete: `frontend/admin-vue/yarn.lock`
- Modify: `frontend/admin-vue/jest.config.js`
- Modify: `frontend/admin-vue/vue.config.js`
- Modify: `.github/workflows/ci.yml`
- Modify: `frontend/admin-vue/README.md`

1. Upgrade the compatible Vue CLI 5/Jest 27 toolchain for Vue 2.
2. Declare the supported Node/npm versions and a single npm lockfile.
3. Adapt Jest and dev-server configuration to the upgraded APIs.
4. Add frontend clean-install, audit, unit-test, lint and build steps to CI.
5. Verify from a clean archive with `npm ci`.

### Task 5: Prove security acceptance

**Files:**
- Add: `docs/security/risk-acceptance-2026-07-16.md`

1. Run `npm audit --omit=dev --registry=https://registry.npmjs.org` and require zero Critical/High findings.
2. Run the issue-specific `npm ls` commands and confirm no affected version remains.
3. Run unit tests, production build and the configured lint gate.
4. Run backend `mvn -B clean verify`.
5. Record the bounded default-credential acceptance, owner roles, controls, expiry and invalidation triggers. Do not waive dependency findings.

### Task 6: Update and visually verify the Word report

**Files:**
- Modify: `docs/test/老宋速达项目主干测试报告_2026-07-16.docx`

1. Apply minimal local edits to the existing report: results, issue status, risk acceptance, evidence and conclusion.
2. Render the final DOCX to PNG using the bundled document runtime.
3. Inspect every rendered page at 100% and iterate until no clipping, overlap, broken table or missing glyph remains.

### Task 7: Publish and close the issues

1. Review `git diff` and stage only remediation/report files.
2. Commit with a terse security-remediation message and push `agent/security-release-gates`.
3. Open a draft pull request against `main` with validation evidence and `Closes #42` through `Closes #48`.
4. Leave issues open until the PR is merged; if the repository permits a verified merge, merge after required checks and confirm all seven issue states.
