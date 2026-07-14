# Local Acceptance Login Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Allow the local mini program to use a deterministic demonstration user for acceptance testing without changing remote-environment login behavior.

**Architecture:** The compiled mini-program environment module will derive a local-development flag from the configured server URL. The index page will use the existing user-login API with a fixed development code only when that flag is true; the backend then creates or reuses a real user and returns a normal JWT. Any non-local server continues through the existing WeChat profile flow.

**Tech Stack:** WeChat Mini Program compiled JavaScript, existing Spring Boot user-login API, MySQL.

## Global Constraints

- Enable automatic login only for `http://localhost` and `http://127.0.0.1` server URLs.
- Reuse `/user/user/login`; do not add an anonymous order API or bypass JWT validation.
- Keep the existing WeChat authorization path unchanged for non-local URLs.
- Use the existing compiled mini-program files because no uni-app source directory exists in this branch.

---

### Task 1: Add the local acceptance-login guard

**Files:**
- Modify: `mp-weixin/common/vendor.js`

**Interfaces:**
- Consumes: `baseUrl` from the compiled `utils/env.js` module and `userLogin({ code })` from the compiled API module.
- Produces: `developmentLoginEnabled` boolean and local automatic token initialization for the index page.

- [x] **Step 1: Establish the failing reproduction**

Run the user login API with a fixed local code and record that it returns a valid JWT, while the index-page code still opens the WeChat-profile modal when its token is empty.

```powershell
$body = @{ code = 'local-acceptance-user' } | ConvertTo-Json
$result = Invoke-RestMethod -Uri 'http://localhost:8088/user/user/login' -Method Post -ContentType 'application/json' -Body $body
if ($result.code -ne 1 -or [string]::IsNullOrWhiteSpace($result.data.token)) { throw 'Expected a JWT from the development login.' }
```

- [x] **Step 2: Add the local URL flag**

In the compiled `utils/env.js` module, export `developmentLoginEnabled` using this expression after `baseUrl` is normalized:

```javascript
var developmentLoginEnabled = /^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?(?:\/|$)/.test(baseUrl);
exports.developmentLoginEnabled = developmentLoginEnabled;
```

- [x] **Step 3: Add the minimal local automatic-login branch**

At the beginning of the existing empty-token branch in the index page `getData` method, call the existing API and initialize the normal token state:

```javascript
if (_env.developmentLoginEnabled) {
  (0, _api.userLogin)({ code: 'local-acceptance-user' }).then(function (success) {
    if (success.code === 1 && success.data && success.data.token) {
      _this.setToken(success.data.token);
      _this.init();
    }
  });
  return;
}
```

- [x] **Step 4: Verify the client artifact and API contract**

Run:

```powershell
node --check mp-weixin/common/vendor.js
$body = @{ code = 'local-acceptance-user' } | ConvertTo-Json
$result = Invoke-RestMethod -Uri 'http://localhost:8088/user/user/login' -Method Post -ContentType 'application/json' -Body $body
if ($result.code -ne 1 -or [string]::IsNullOrWhiteSpace($result.data.token)) { throw 'Development login verification failed.' }
```

Expected: Node exits with code 0 and the API returns `code = 1` with a token.

- [x] **Step 5: Commit the implementation**

```powershell
git add mp-weixin/common/vendor.js docs/superpowers/plans/2026-07-14-local-acceptance-login.md
git commit -m "feat(miniprogram): add local acceptance login"
git push
```
