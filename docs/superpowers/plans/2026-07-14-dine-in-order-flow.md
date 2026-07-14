# Dine-In Order Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Route scanned-seat customers through a table-order confirmation flow that creates a paid, pending-acceptance order without delivery data.

**Architecture:** `seatNumber` in mini-program storage selects dine-in mode. The mini-program uses new native confirmation and success pages while preserving the existing compiled delivery page. The existing submit endpoint resolves the seat before delivery validation; a resolved seat produces a paid `TO_BE_CONFIRMED` order without address fields.

**Tech Stack:** WeChat Mini Program JavaScript/WXML/WXSS, compiled uni-app artifacts, Spring Boot, MyBatis, JUnit 5, Mockito.

## Global Constraints

- `seat_id IS NOT NULL` is the dine-in marker; do not add an `order_type` column.
- Dine-in requests must not read or require `addressBookId`.
- A valid dine-in order uses `TO_BE_CONFIRMED`, `PAID`, `checkout_time = now`, and `pack_amount = 0`.
- The delivery checkout route and its payment flow must remain unchanged when `seatNumber` is absent.
- The branch contains compiled mini-program artifacts; edit only the required generated files and native page files.

---

### Task 1: Make seat-bound submission a first-class backend path

**Files:**
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/service/impl/OrderServiceImpl.java:81-159`
- Modify: `backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/OrderSeatBindingTest.java`

**Interfaces:**
- Consumes: `OrdersSubmitDTO.seatNumber`, `OrdersSubmitDTO.seatId`, and the existing user shopping cart.
- Produces: an inserted `Orders` record with a non-null `seatId`, no address fields, `status = TO_BE_CONFIRMED`, and `payStatus = PAID` for seat-bound requests.

- [ ] **Step 1: Write the failing dine-in submission test**

```java
@Test
void submitDineInOrderSkipsAddressAndCreatesPaidPendingOrder() {
    BaseContext.setCurrentId(7L);
    OrdersSubmitDTO request = new OrdersSubmitDTO();
    request.setSeatNumber("A1");
    request.setAmount(new BigDecimal("38.00"));
    when(shoppingCartMapper.list(any(ShoppingCart.class))).thenReturn(
        Collections.singletonList(ShoppingCart.builder().name("Fried rice").number(1)
            .amount(new BigDecimal("38.00")).build()));
    when(seatService.getAvailableBySeatNumber("A1")).thenReturn(
        Seat.builder().id(11L).seatNumber("A1").status(0).build());
    doAnswer(invocation -> { invocation.getArgument(0, Orders.class).setId(100L); return null; })
        .when(orderMapper).insert(any(Orders.class));

    orderService.submitOrder(request);

    ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
    verify(orderMapper).insert(captor.capture());
    assertEquals(11L, captor.getValue().getSeatId());
    assertEquals(Orders.TO_BE_CONFIRMED, captor.getValue().getStatus());
    assertEquals(Orders.PAID, captor.getValue().getPayStatus());
    assertEquals(0, captor.getValue().getPackAmount());
    assertNull(captor.getValue().getAddressBookId());
    verify(addressBookMapper, never()).getById(any());
}
```

- [ ] **Step 2: Run the test and confirm the existing address validation rejects it**

Run: `mvn -q -pl sky-server -Dtest=OrderSeatBindingTest#submitDineInOrderSkipsAddressAndCreatesPaidPendingOrder test`

Expected: FAIL with the address-book-empty business exception.

- [ ] **Step 3: Add the minimum seat-first branch**

Move seat binding before address lookup. When `order.getSeatId() != null`, skip address lookup, set `TO_BE_CONFIRMED`, `PAID`, `checkoutTime`, and zero packing amount. Keep the current address mapping and pending-payment values in the non-seat branch.

```java
boolean dineIn = order.getSeatId() != null;
if (dineIn) {
    order.setStatus(Orders.TO_BE_CONFIRMED);
    order.setPayStatus(Orders.PAID);
    order.setCheckoutTime(LocalDateTime.now());
    order.setPackAmount(0);
} else {
    // Existing address lookup, address mapping, and pending-payment setup.
}
```

- [ ] **Step 4: Run the focused test and full backend suite**

Run:

```powershell
mvn -q -pl sky-server -Dtest=OrderSeatBindingTest test
mvn -q -pl sky-server test
```

Expected: both commands exit with code 0.

### Task 2: Add native dine-in confirmation and success pages

**Files:**
- Create: `mp-weixin/pages/dineInConfirm/index.js`
- Create: `mp-weixin/pages/dineInConfirm/index.wxml`
- Create: `mp-weixin/pages/dineInConfirm/index.wxss`
- Create: `mp-weixin/pages/dineInConfirm/index.json`
- Create: `mp-weixin/pages/dineInSuccess/index.js`
- Create: `mp-weixin/pages/dineInSuccess/index.wxml`
- Create: `mp-weixin/pages/dineInSuccess/index.wxss`
- Create: `mp-weixin/pages/dineInSuccess/index.json`
- Modify: `mp-weixin/app.json`

**Interfaces:**
- Consumes: `seatNumber` and `token` from WeChat storage; `/user/shoppingCart/list` and `/user/order/submit`.
- Produces: a submission payload `{ seatNumber, payMethod: 1, amount, remark, tablewareNumber, tablewareStatus: 0, packAmount: 0 }` and a success page query with `orderId`, `orderNumber`, and `seatNumber`.

- [ ] **Step 1: Add static page-registration checks before the pages exist**

Run:

```powershell
$app = Get-Content -Raw mp-weixin/app.json | ConvertFrom-Json
if ($app.pages -contains 'pages/dineInConfirm/index') { throw 'Dine-in confirmation page unexpectedly exists.' }
if ($app.pages -contains 'pages/dineInSuccess/index') { throw 'Dine-in success page unexpectedly exists.' }
```

Expected: command exits with code 0 before registration.

- [ ] **Step 2: Create the confirmation page**

Implement a native `Page` that loads the shopping cart using the `authentication` header, calculates `sum(item.amount * item.number)`, validates a seat and non-empty cart, and posts the payload defined above. The WXML must render the table number, line items, quantity, subtotal, remark textarea, tableware stepper, and one `Confirm order` command. Do not add address, delivery, packing, or payment controls.

- [ ] **Step 3: Create the success page and register both pages**

Register both paths in `app.json`. The success page renders the table and order number, then offers `View orders` to `pages/historyOrder/historyOrder` and `Continue ordering` to `pages/index/index` without clearing `seatNumber`.

- [ ] **Step 4: Validate the new mini-program files**

Run:

```powershell
node --check mp-weixin/pages/dineInConfirm/index.js
node --check mp-weixin/pages/dineInSuccess/index.js
$app = Get-Content -Raw mp-weixin/app.json | ConvertFrom-Json
if ($app.pages -notcontains 'pages/dineInConfirm/index' -or $app.pages -notcontains 'pages/dineInSuccess/index') { throw 'Dine-in pages are not registered.' }
```

Expected: all commands exit with code 0.

### Task 3: Switch scanned-seat checkout away from delivery UI

**Files:**
- Modify: `mp-weixin/common/vendor.js:4100-4510`
- Modify: `mp-weixin/pages/index/index.wxml`
- Modify: `mp-weixin/pages/index/index.wxss`

**Interfaces:**
- Consumes: `seatNumber` from WeChat storage.
- Produces: a `dineInSeatNumber` menu state and checkout routing to `pages/dineInConfirm/index` only for scanned-seat sessions.

- [ ] **Step 1: Add the failing static route assertion**

Run:

```powershell
$content = Get-Content -Raw mp-weixin/common/vendor.js
if ($content -match "pages/dineInConfirm/index") { throw 'Dine-in route unexpectedly exists.' }
```

Expected: command exits with code 0 before the route is added.

- [ ] **Step 2: Add the seat-aware menu state and route**

In the compiled index-page module, initialize `dineInSeatNumber` from `uni.getStorageSync('seatNumber')` on load. Change `goOrder` to route to `/pages/dineInConfirm/index` when that value is present, otherwise retain `/pages/order/index`. In `index.wxml`, hide the delivery-oriented restaurant-info panel in dine-in mode and render a compact `Dine-in · {{dineInSeatNumber}}` table banner. Add only the banner styles required by this page.

- [ ] **Step 3: Verify syntax and branch-specific routing artifacts**

Run:

```powershell
node --check mp-weixin/common/vendor.js
$content = Get-Content -Raw mp-weixin/common/vendor.js
if ($content -notmatch "pages/dineInConfirm/index") { throw 'Dine-in checkout route is missing.' }
if ($content -notmatch "pages/order/index") { throw 'Delivery checkout route is missing.' }
```

Expected: all commands exit with code 0.

### Task 4: Verify live submission and prevent delivery regressions

**Files:**
- Test: `backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/OrderSeatBindingTest.java`
- Test: live APIs on `http://localhost:8088`

- [ ] **Step 1: Submit an authenticated seat-bound cart through the live API**

Use the existing local development login, add a dish to its cart, then submit an order with `seatNumber = A1`. Assert that the API returns `code = 1` and query `orders` to assert non-null `seat_id`, status `2`, pay status `1`, null address columns, and packing amount `0`.

- [ ] **Step 2: Verify the seat status and admin order visibility**

Assert that A1 is in-use after submission and that `/admin/order/conditionSearch` returns the new order. Complete or cancel the test order and assert that A1 returns to available.

- [ ] **Step 3: Re-run regression checks**

Run:

```powershell
mvn -q -pl sky-server test
node --check mp-weixin/common/vendor.js
node --check mp-weixin/pages/dineInConfirm/index.js
node --check mp-weixin/pages/dineInSuccess/index.js
```

Expected: all commands exit with code 0.

- [ ] **Step 4: Commit and push**

```powershell
git add backend/sky-take-out/sky-server/src/main/java/com/sky/service/impl/OrderServiceImpl.java backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/OrderSeatBindingTest.java mp-weixin docs/superpowers/plans/2026-07-14-dine-in-order-flow.md
git commit -m "feat(order): add scanned-seat dine-in checkout"
git push
```
