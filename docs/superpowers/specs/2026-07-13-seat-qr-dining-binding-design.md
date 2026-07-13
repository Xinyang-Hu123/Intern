# Seat QR Dining Binding Design

## 1. Goal

Enable an administrator to create a seat and immediately obtain a downloadable
QR code. A logged-in Mini Program user can scan that code to identify the
physical seat, review the result, and explicitly confirm before joining the
seat's dining session.

The confirmed requirements are:

- The scan screen first displays a message such as "你当前在 A 区 01 号桌".
- Scanning alone must not bind the user, create a dining session, or change the
  seat status.
- The user must tap "确认开始点餐" before binding takes effect.
- Seat capacity is enforced using `seat.capacity`.
- A single logged-in user counts at most once for one dining session, including
  repeated scans or repeated confirmation requests.
- When a new participant would exceed capacity, the Mini Program displays:
  "该座位已被占用，请联系店员".
- After an administrator creates a seat, the management UI opens a QR preview
  dialog with a download action.

## 2. Scope

This work covers the existing admin seat-management frontend, seat and
dining-session backend APIs, database persistence, and the Mini Program
`scanSeat` flow.

It does not change order pricing, cart rules, payment, existing historical
orders, or the QR rotation workflow beyond preserving its current signed QR
validation behavior.

## 3. User Flow

### 3.1 Administrator

1. The administrator creates a seat with zone, table number, and capacity.
2. The backend saves the seat with `qrVersion = 1` and a signed QR payload.
3. The API returns the created seat identity (or the frontend obtains it from
   the creation response).
4. The admin UI opens a modal that renders the existing QR image download
   endpoint.
5. The administrator can download the image for printing or placement at the
   physical table.

### 3.2 Mini Program User

1. A logged-in user scans the seat QR code.
2. The Mini Program sends the payload to a validate-only scan API.
3. The backend validates the signed seat payload and verifies that the seat is
   usable.
4. The scan page displays the seat, for example "你当前在 A 区 01 号桌", and
   presents "确认开始点餐".
5. The user taps the confirmation button.
6. The backend atomically joins the user to the open dining session if there is
   capacity. It then returns the dining session ID and current participant
   count.
7. Only after this success response does the Mini Program store
   `diningSessionId`, `seatId`, and `seatCode`, then navigate to ordering.
8. If the seat is full, the confirmation remains unsuccessful and the page
   displays "该座位已被占用，请联系店员".

## 4. Backend Design

### 4.1 Validate-Only Scan Endpoint

The existing scan route is changed to be side-effect free.

It validates the QR payload in the established
`seatCode:qrVersion:sign` format and returns:

- Seat ID, zone, table number, display name, and capacity.
- Current confirmed participant count.
- Whether the authenticated user is already a participant in the current open
  session, if one exists.
- A result appropriate for invalid, disabled, deleted, or otherwise unusable
  seats.

It must not create a `dining_session`, insert a participant record, or mark a
seat as occupied.

### 4.2 Confirm Dining Binding Endpoint

The existing session-creation route is repurposed or replaced with a clear
confirm-binding operation. It executes inside one database transaction:

1. Resolve the target seat and verify it can be used.
2. Resolve the current open dining session for that seat, creating it only when
   one does not exist.
3. Check whether the authenticated user already has a participant record for
   that session. If so, return the existing session successfully without
   increasing the count.
4. Lock the relevant session/seat state and count confirmed participants.
5. Reject a new participant when the count is already greater than or equal to
   `seat.capacity`.
6. Insert the participant record, mark the seat as `OCCUPIED`, and return the
   session ID, capacity, and confirmed count.

The operation must be concurrency-safe: simultaneous confirmations cannot
produce more participant rows than the seat capacity.

### 4.3 Persistence

Add a dining-session participant table, conceptually:

| Field | Purpose |
| --- | --- |
| `id` | Primary key |
| `dining_session_id` | The joined dining session |
| `user_id` | Logged-in Mini Program user |
| `created_time` | Join time |

The database enforces a unique constraint on
`(dining_session_id, user_id)`. This is the final safeguard that makes repeat
confirmation idempotent for one user.

The service uses database locking or an equivalent conditional update strategy
around session capacity checks and participant insertion. Application-level
checks alone are insufficient because two users may confirm at the same time.

### 4.4 Compatibility

Existing QR generation in `SeatServiceImpl.save()` and the QR image endpoint
remain the source of truth. The new behavior changes when a dining session is
created: it happens on explicit confirmation, not scan.

Existing status semantics are preserved where practical: a newly confirmed
participant changes the seat to `OCCUPIED`. The capacity decision itself is
based on confirmed participant records rather than status alone.

## 5. Frontend Design

### 5.1 Admin Seat Management

After a successful create request, the seat management page opens a focused QR
preview dialog. It loads the existing image endpoint as a Blob/Object URL or
other authenticated-safe image source, labels the seat clearly, and provides a
download command. The dialog can be closed without affecting the created seat.

### 5.2 Mini Program Scan Page

The scan page has distinct states:

- Loading while QR validation is in progress.
- Valid seat, showing the identified position and confirmation action.
- Already joined, where confirmation is safe and returns the same dining
  session.
- Full seat, showing the required full-seat message and preventing an attempt
  to enter ordering.
- Invalid or unavailable QR, with a concise error and a rescan path.

No dining-session information is persisted locally before confirmation
succeeds.

## 6. Error Handling

- Invalid signature, malformed payload, outdated QR version, unavailable seat:
  show a scan failure state and do not start ordering.
- Session has ended between scan and confirmation: ask the user to scan again.
- Capacity is reached during confirmation: show the exact required message.
- Duplicate confirmation: return success for the original session without
  incrementing occupancy.
- Network failure: keep the user on the scan confirmation page and allow retry.

## 7. Tests and Acceptance Criteria

Backend tests cover:

1. Creating a seat creates a valid signed QR payload.
2. Scan validation returns seat information without creating a session or
   changing seat occupancy.
3. First confirmation creates or joins the open session and adds one
   participant.
4. Repeated confirmation by the same user is idempotent.
5. A new user is rejected once confirmed participant count reaches
   `seat.capacity`.
6. Concurrent confirmation attempts cannot exceed `seat.capacity`.

Frontend verification covers:

1. Admin creation opens the QR preview and its download action returns the QR
   image.
2. The Mini Program displays the recognized zone and table before confirmation.
3. Mini Program storage and navigation occur only after successful confirmation.
4. A full seat displays "该座位已被占用，请联系店员".

## 8. Non-Goals and Assumptions

- Capacity represents the maximum number of distinct logged-in Mini Program
  users who can join one open dining session.
- A user who has already joined can rescan and reconfirm successfully even if
  the table later becomes full.
- How and when an open dining session is closed follows the existing business
  flow; this feature does not introduce a new checkout or table-clearing rule.
