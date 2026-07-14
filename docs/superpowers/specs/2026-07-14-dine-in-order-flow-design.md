# Scan-to-Table Dine-In Order Flow

## Goal

When a guest enters through a seat QR code, the mini program must use a dine-in order journey instead of the delivery journey. A confirmed dine-in order is created immediately, with no address selection, delivery scheduling, delivery fee, packing fee, or payment-page redirect.

## Scope

This change applies only when `seatNumber` exists in mini-program storage after the guest confirms a scanned seat. The existing non-seat delivery journey remains unchanged.

## User Journey

1. The guest opens `pages/seat/index` from a QR code and confirms an available seat.
2. The seat page stores `seatNumber` and relaunches the menu page.
3. The menu page identifies dine-in mode from `seatNumber` and shows `Dine-in · <seatNumber>` near the menu context.
4. The guest adds dishes or set meals to the existing shopping cart.
5. The cart action routes to a new dine-in confirmation page instead of `pages/order/index`.
6. The confirmation page shows the seat, order lines, quantity, subtotal, remark, and tableware quantity.
7. `Confirm order` submits the existing order API with `seatNumber`, a zero packing amount, and the calculated dish subtotal.
8. The success page confirms that the table order is waiting for merchant acceptance. The guest may view orders or return to the menu.

## UI Rules

### Menu Page

- Preserve menu browsing and cart behavior.
- In dine-in mode, replace delivery-oriented context with `Dine-in · <seatNumber>`.
- The cart command must route to `pages/dineInConfirm/index` when `seatNumber` is present; otherwise it must retain the existing delivery route.

### Dine-In Confirmation Page

- Show: table number, line items, quantities, dish subtotal, free-text remark, tableware quantity, and total.
- Do not request or render: address book, consignee, phone, delivery time, delivery status, delivery fee, packing fee, or payment selection.
- Disable repeated confirmation while the submission request is in flight.
- Reject confirmation when the stored table number is absent or the cart has no items.

### Dine-In Success Page

- Show the order number, table number, and `Order submitted. Waiting for merchant acceptance.`
- Provide `View orders` and `Continue ordering` actions.
- `Continue ordering` returns to the menu while preserving the stored table number. It does not submit a second order automatically.

## Backend Contract

The existing `POST /user/order/submit` endpoint remains the only submission endpoint.

- If `seatNumber` or `seatId` is supplied, the service resolves and atomically occupies the seat before insertion.
- A resolved seat makes the request a dine-in order. The service must skip address-book lookup and must not require delivery-only fields.
- Dine-in orders are inserted with `seat_id`, `status = TO_BE_CONFIRMED`, `pay_status = PAID`, `checkout_time = now`, `pack_amount = 0`, and no delivery address fields.
- Delivery orders retain current address validation, pending-payment status, and payment-page behavior.
- The first dine-in submission changes the seat from available to in use. Existing cancellation, rejection, and completion logic continues to release the seat.
- Re-submission for an already in-use seat is not included in this scope. The flow creates one primary order per seat session.

## Data Model

No database table or new order-type column is required. `orders.seat_id IS NOT NULL` is the operational marker for a dine-in order. This avoids a broad migration while preserving the existing delivery records and admin views.

## Error Handling

- Missing or unavailable seat: return the existing business error and keep the cart intact.
- Empty cart: return the existing shopping-cart error and do not create an order.
- Failed submission: re-enable confirmation and display the server message.
- If the seat number is cleared before confirmation, route the user through the normal delivery checkout instead.

## Acceptance Criteria

1. A scan for A1 enters the menu in dine-in mode and does not display a delivery destination.
2. Checkout from a scanned seat opens the dine-in confirmation page, not the address-based order page.
3. The confirmation page has no address, delivery schedule, packing fee, delivery fee, or payment controls.
4. Confirming a non-empty cart creates an order with A1's `seat_id`, paid status, and pending-acceptance status.
5. The order does not have an address-book ID, address, consignee, or phone.
6. The A1 seat changes to in use after successful submission.
7. A normal non-scanned order retains the existing delivery checkout and payment flow.
8. Cancelling, rejecting, or completing the dine-in order releases A1 as already implemented.
