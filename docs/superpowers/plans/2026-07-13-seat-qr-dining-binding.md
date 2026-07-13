# Seat QR Dining Binding Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让座位二维码先完成位置识别，只有用户确认开始点餐后才原子化绑定就餐会话，并按座位容量限制不同用户加入。

**Architecture:** 保留现有签名二维码和二维码图片下载接口。扫码接口改为只读校验；新增确认接口在事务内锁定座位行，创建或取得开放会话、检查该用户是否已加入、校验人数、写入参与者记录并更新座位状态。后台在新增座位成功后使用已认证的 Blob 请求打开二维码预览；小程序只在确认成功后保存会话信息和跳转点餐。

**Tech Stack:** Spring Boot 2.7、MyBatis XML、MySQL/InnoDB、JUnit 5 + Mockito、Vue 2 + TypeScript 3.6 + Element UI、微信小程序原生 API。

---

## File Structure

- Create: `backend/sky-take-out/migration-seat-capacity.sql`
  - 为已有数据库增加就餐会话参与者表和唯一约束。
- Modify: `backend/sky-take-out/init.sql`
  - 让全新初始化数据库同时创建参与者表。
- Create: `backend/sky-take-out/sky-pojo/src/main/java/com/sky/entity/DiningSessionParticipant.java`
  - 描述一名用户加入一个就餐会话的持久化实体。
- Modify: `backend/sky-take-out/sky-pojo/src/main/java/com/sky/vo/SeatScanResultVO.java`
  - 同时承载只读扫码状态和确认后的会话结果。
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/mapper/SeatMapper.java`
  - 暴露行锁、参与者查询、计数和插入方法。
- Modify: `backend/sky-take-out/sky-server/src/main/resources/mapper/SeatMapper.xml`
  - 实现 InnoDB 行锁和参与者 SQL。
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/service/SeatService.java`
  - 将“创建/取得会话”替换为“确认加入会话”的服务契约，新增座位返回创建后的 `Seat`。
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/service/impl/SeatServiceImpl.java`
  - 负责无副作用扫码和事务化确认。
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/controller/admin/SeatController.java`
  - 新增座位时返回包含 ID、签名和版本的座位对象。
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/controller/user/SeatUserController.java`
  - 提供 `POST /user/seat/session/confirm`。
- Create: `backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/SeatServiceImplTest.java`
  - 覆盖扫码无副作用、容量、幂等和会话创建行为。
- Modify: `frontend/admin-vue/src/api/seat.ts`
  - 保留 Blob 下载 API 供预览和下载共用。
- Modify: `frontend/admin-vue/src/utils/request.ts`
  - 让二维码图片的 Blob 响应绕过 JSON 业务码解析。
- Modify: `frontend/admin-vue/src/views/seat/index.vue`
  - 新增后显示二维码预览、下载；同时修复该文件中 TypeScript 3.6 不支持的 `catch (err: any)` 语法。
- Create: `frontend/admin-vue/tests/unit/views/seat/index.spec.ts`
  - 覆盖新增成功后打开二维码预览。
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.js`
  - 扫码后仅展示；点击确认后调用绑定接口并保存本地会话。
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.wxml`
  - 显示“你当前在 …”和确认、重扫、满座状态。
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.wxss`
  - 为确认中、已满、错误和重扫状态提供稳定样式。

### Task 1: Add the Participant Persistence Contract

**Files:**
- Create: `backend/sky-take-out/migration-seat-capacity.sql`
- Modify: `backend/sky-take-out/init.sql`
- Create: `backend/sky-take-out/sky-pojo/src/main/java/com/sky/entity/DiningSessionParticipant.java`
- Modify: `backend/sky-take-out/sky-pojo/src/main/java/com/sky/vo/SeatScanResultVO.java`
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/mapper/SeatMapper.java`
- Modify: `backend/sky-take-out/sky-server/src/main/resources/mapper/SeatMapper.xml`

- [ ] **Step 1: Add a database migration for the unique participant record**

Create `backend/sky-take-out/migration-seat-capacity.sql`:

```sql
CREATE TABLE IF NOT EXISTS dining_session_participant (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  dining_session_id BIGINT NOT NULL COMMENT '用餐会话ID',
  user_id BIGINT NOT NULL COMMENT '小程序用户ID',
  create_time DATETIME NOT NULL COMMENT '确认加入时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_session_user (dining_session_id, user_id),
  KEY idx_session_id (dining_session_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用餐会话参与者';
```

Append the same `CREATE TABLE IF NOT EXISTS` statement after the
`dining_session` definition in `backend/sky-take-out/init.sql`, so a new
database and an upgraded database have the same schema.

- [ ] **Step 2: Add the Java entity and response fields before writing service tests**

Create the Lombok entity:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningSessionParticipant implements Serializable {
    private Long id;
    private Long diningSessionId;
    private Long userId;
    private LocalDateTime createTime;
}
```

Add these fields to `SeatScanResultVO` while keeping its existing fields:

```java
private Integer capacity;
private Integer participantCount;
private Boolean joined;
private Boolean full;
```

`diningSessionId` remains `null` for an unconfirmed scan unless the current
user has already joined an open session. This makes the distinction explicit
to the Mini Program.

- [ ] **Step 3: Add mapper contracts needed by the transactional service**

Add these methods to `SeatMapper`:

```java
Seat getByIdForUpdate(@Param("id") Long id);
int countParticipants(@Param("diningSessionId") Long diningSessionId);
int countParticipantBySessionAndUser(@Param("diningSessionId") Long diningSessionId,
                                     @Param("userId") Long userId);
void insertParticipant(DiningSessionParticipant participant);
```

Import `DiningSessionParticipant`. Remove the unused
`getOpenSessionBySeatId` declaration rather than leaving an unimplemented
mapper method.

- [ ] **Step 4: Implement the mapper SQL**

Add this row-lock query to `SeatMapper.xml`:

```xml
<select id="getByIdForUpdate" resultType="com.sky.entity.Seat">
    select <include refid="seatColumns"/> from seat where id = #{id} for update
</select>
```

Add the participant queries:

```xml
<select id="countParticipants" resultType="int">
    select count(*) from dining_session_participant
    where dining_session_id = #{diningSessionId}
</select>

<select id="countParticipantBySessionAndUser" resultType="int">
    select count(*) from dining_session_participant
    where dining_session_id = #{diningSessionId} and user_id = #{userId}
</select>

<insert id="insertParticipant" useGeneratedKeys="true" keyProperty="id">
    insert into dining_session_participant(dining_session_id, user_id, create_time)
    values (#{diningSessionId}, #{userId}, #{createTime})
</insert>
```

The `for update` query is intentionally used only inside the confirmation
transaction. It serializes confirmations for one physical seat without
blocking other seats.

- [ ] **Step 5: Verify the persistence layer compiles**

Run:

```powershell
mvn -pl sky-server -am -DskipTests compile
```

Expected: Maven finishes with `BUILD SUCCESS`.

- [ ] **Step 6: Commit the persistence contract**

```powershell
git add backend/sky-take-out/migration-seat-capacity.sql backend/sky-take-out/init.sql backend/sky-take-out/sky-pojo/src/main/java/com/sky/entity/DiningSessionParticipant.java backend/sky-take-out/sky-pojo/src/main/java/com/sky/vo/SeatScanResultVO.java backend/sky-take-out/sky-server/src/main/java/com/sky/mapper/SeatMapper.java backend/sky-take-out/sky-server/src/main/resources/mapper/SeatMapper.xml
git commit -m "feat: persist dining session participants"
```

### Task 2: Implement and Test Validate-Only Scan plus Atomic Confirmation

**Files:**
- Create: `backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/SeatServiceImplTest.java`
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/service/SeatService.java`
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/service/impl/SeatServiceImpl.java`

- [ ] **Step 1: Write failing service tests**

Create `SeatServiceImplTest` using JUnit 5 and Mockito. Set
`BaseContext.setCurrentId(101L)` in `@BeforeEach`, call
`BaseContext.removeCurrentId()` in `@AfterEach`, and set `qrSecretKey` to
`test-secret` with `ReflectionTestUtils`.

Add these tests:

```java
@Test
void parseSeatByScene_doesNotCreateSessionOrChangeSeatStatus() {
    Seat seat = availableSeat(1L, "A01", 2, 1);
    when(seatMapper.getBySeatCode("A01")).thenReturn(seat);

    SeatScanResultVO result = service.parseSeatByScene(sceneFor("A01", 1));

    assertThat(result.getSuccess()).isTrue();
    assertThat(result.getDiningSessionId()).isNull();
    assertThat(result.getParticipantCount()).isEqualTo(0);
    verify(seatMapper, never()).insertSession(any(DiningSession.class));
    verify(seatMapper, never()).updateStatus(anyLong(), eq("OCCUPIED"));
}

@Test
void confirmSession_createsSessionAndParticipantForFirstUser() {
    when(seatMapper.getByIdForUpdate(1L)).thenReturn(availableSeat(1L, "A01", 2, 1));
    when(seatMapper.getOpenSessionBySeat(1L)).thenReturn(null);
    doAnswer(invocation -> {
        invocation.<DiningSession>getArgument(0).setId(9L);
        return null;
    }).when(seatMapper).insertSession(any(DiningSession.class));
    when(seatMapper.countParticipantBySessionAndUser(9L, 101L)).thenReturn(0);
    when(seatMapper.countParticipants(9L)).thenReturn(0);

    SeatScanResultVO result = service.confirmSession(1L);

    assertThat(result.getDiningSessionId()).isEqualTo(9L);
    verify(seatMapper).insertParticipant(argThat(p ->
        p.getDiningSessionId().equals(9L) && p.getUserId().equals(101L)));
    verify(seatMapper).updateStatus(1L, "OCCUPIED");
}

@Test
void confirmSession_returnsExistingSessionWithoutAddingSameUserTwice() {
    DiningSession session = openSession(9L, 1L);
    when(seatMapper.getByIdForUpdate(1L)).thenReturn(availableSeat(1L, "A01", 2, 1));
    when(seatMapper.getOpenSessionBySeat(1L)).thenReturn(session);
    when(seatMapper.countParticipantBySessionAndUser(9L, 101L)).thenReturn(1);
    when(seatMapper.countParticipants(9L)).thenReturn(2);

    SeatScanResultVO result = service.confirmSession(1L);

    assertThat(result.getDiningSessionId()).isEqualTo(9L);
    assertThat(result.getJoined()).isTrue();
    verify(seatMapper, never()).insertParticipant(any());
}

@Test
void confirmSession_rejectsNewUserWhenCapacityIsFull() {
    when(seatMapper.getByIdForUpdate(1L)).thenReturn(availableSeat(1L, "A01", 2, 1));
    when(seatMapper.getOpenSessionBySeat(1L)).thenReturn(openSession(9L, 1L));
    when(seatMapper.countParticipantBySessionAndUser(9L, 101L)).thenReturn(0);
    when(seatMapper.countParticipants(9L)).thenReturn(2);

    assertThatThrownBy(() -> service.confirmSession(1L))
        .isInstanceOf(SeatBusinessException.class)
        .hasMessage("该座位已被占用，请联系店员");
    verify(seatMapper, never()).insertParticipant(any());
}
```

`availableSeat`, `openSession`, and `sceneFor` are private test helpers in
the same test class. `sceneFor` calculates the same MD5 substring as the
production code using the literal test secret.

- [ ] **Step 2: Run the focused test and verify it fails**

Run:

```powershell
mvn -pl sky-server -am -Dtest=SeatServiceImplTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compilation fails because `confirmSession`, the new mapper methods,
or the new response fields do not yet exist.

- [ ] **Step 3: Replace the service contract**

In `SeatService`:

```java
Seat save(SeatDTO seatDTO);
SeatScanResultVO confirmSession(Long seatId);
```

Remove `Long createOrGetSession(Long seatId)`. Keep
`closeSessionAndRelease`, `occupySeat`, and `releaseSeat` unchanged so
existing order-related code remains compatible.

In `SeatServiceImpl.save`, return the fully populated seat after calling
`generateAndSaveQrSign`:

```java
seatMapper.insert(seat);
generateAndSaveQrSign(seat.getId());
return seatMapper.getById(seat.getId());
```

- [ ] **Step 4: Make QR validation read-only**

Replace the `createOrGetSession` call in `parseSeatByScene` with:

```java
DiningSession session = seatMapper.getOpenSessionBySeat(seat.getId());
int participantCount = session == null ? 0 : seatMapper.countParticipants(session.getId());
boolean joined = session != null
    && seatMapper.countParticipantBySessionAndUser(session.getId(), BaseContext.getCurrentId()) > 0;
boolean full = !joined && participantCount >= seat.getCapacity();

return SeatScanResultVO.builder()
    .success(true)
    .message(full ? "该座位已被占用，请联系店员" : "扫码成功")
    .seatId(seat.getId())
    .seatCode(seat.getSeatCode())
    .seatName(seat.getSeatName())
    .areaName(seat.getAreaName())
    .capacity(seat.getCapacity())
    .participantCount(participantCount)
    .joined(joined)
    .full(full)
    .diningSessionId(joined ? session.getId() : null)
    .build();
```

Do not catch `SeatBusinessException` in this method. Continue returning a
`success=false` response object for malformed payload, nonexistent seat,
disabled seat, invalid QR version, or invalid signature. Do not create a
session or change seat status in any scan branch.

- [ ] **Step 5: Implement the transactional confirmation**

Add `@Transactional` to `confirmSession(Long seatId)`. Its required order is:

```java
Seat seat = seatMapper.getByIdForUpdate(seatId);
assertSeatUsable(seat);

DiningSession session = seatMapper.getOpenSessionBySeat(seatId);
if (session == null) {
    session = newOpenSession(seatId);
    seatMapper.insertSession(session);
}

Long userId = BaseContext.getCurrentId();
int participantCount = seatMapper.countParticipants(session.getId());
if (seatMapper.countParticipantBySessionAndUser(session.getId(), userId) > 0) {
    return confirmedResult(seat, session.getId(), participantCount);
}
if (participantCount >= seat.getCapacity()) {
    throw new SeatBusinessException("该座位已被占用，请联系店员");
}

seatMapper.insertParticipant(DiningSessionParticipant.builder()
    .diningSessionId(session.getId())
    .userId(userId)
    .createTime(LocalDateTime.now())
    .build());
seatMapper.updateStatus(seatId, "OCCUPIED");
return confirmedResult(seat, session.getId(), participantCount + 1);
```

`assertSeatUsable` rejects `null` with `"座位不存在"` and `DISABLED` with
`"该座位暂不可用"`. `confirmedResult` sets `success=true`, `joined=true`,
`full=false`, seat display fields, `capacity`, participant count, and session
ID.

The seat row is locked before reading/creating the open session and before
checking capacity. Therefore confirmations for one seat serialize inside one
InnoDB transaction, and the unique database index remains a second guard
against duplicate membership.

- [ ] **Step 6: Run focused tests and then the backend test suite**

Run:

```powershell
mvn -pl sky-server -am -Dtest=SeatServiceImplTest -Dsurefire.failIfNoSpecifiedTests=false test
mvn -pl sky-server -am test
```

Expected: both commands finish with `BUILD SUCCESS`.

- [ ] **Step 7: Commit the service behavior**

```powershell
git add backend/sky-take-out/sky-server/src/test/java/com/sky/service/impl/SeatServiceImplTest.java backend/sky-take-out/sky-server/src/main/java/com/sky/service/SeatService.java backend/sky-take-out/sky-server/src/main/java/com/sky/service/impl/SeatServiceImpl.java
git commit -m "feat: confirm seat dining session atomically"
```

### Task 3: Expose the Correct HTTP Contract

**Files:**
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/controller/admin/SeatController.java`
- Modify: `backend/sky-take-out/sky-server/src/main/java/com/sky/controller/user/SeatUserController.java`

- [ ] **Step 1: Update controller-level tests in the existing service test class**

Add lightweight MockMvc tests only if a controller test base already exists;
otherwise add direct Mockito tests for controller delegation:

```java
@Test
void confirmEndpoint_delegatesToConfirmSession() {
    SeatScanResultVO expected = SeatScanResultVO.builder()
        .success(true).diningSessionId(9L).build();
    when(seatService.confirmSession(1L)).thenReturn(expected);

    Result<SeatScanResultVO> result = controller.confirmSession(1L);

    assertThat(result.getData()).isSameAs(expected);
    verify(seatService).confirmSession(1L);
}
```

Place direct controller tests in
`backend/sky-take-out/sky-server/src/test/java/com/sky/controller/user/SeatUserControllerTest.java`
if no MVC test configuration is present.

- [ ] **Step 2: Run controller test and verify it fails**

Run:

```powershell
mvn -pl sky-server -am -Dtest=SeatUserControllerTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compilation fails because the confirmation controller method is not
present.

- [ ] **Step 3: Return the created seat from admin creation**

Change the admin method to:

```java
public Result<Seat> save(@RequestBody SeatDTO seatDTO) {
    log.info("新增座位: {}", seatDTO);
    return Result.success(seatService.save(seatDTO));
}
```

This gives the frontend the new seat ID required to fetch the QR image without
performing a fragile list refresh and lookup.

- [ ] **Step 4: Replace the user create-session route with an explicit confirmation route**

Replace `/session/create` with:

```java
@PostMapping("/session/confirm")
@ApiOperation("确认加入座位并开始点餐")
public Result<SeatScanResultVO> confirmSession(@RequestParam Long seatId) {
    return Result.success(seatService.confirmSession(seatId));
}
```

Do not leave a public route that creates a session without a participant. Keep
the existing close, occupy, and release routes unchanged.

- [ ] **Step 5: Run controller and full backend tests**

Run:

```powershell
mvn -pl sky-server -am test
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 6: Commit the API contract**

```powershell
git add backend/sky-take-out/sky-server/src/main/java/com/sky/controller/admin/SeatController.java backend/sky-take-out/sky-server/src/main/java/com/sky/controller/user/SeatUserController.java backend/sky-take-out/sky-server/src/test/java/com/sky/controller/user/SeatUserControllerTest.java
git commit -m "feat: expose seat confirmation API"
```

### Task 4: Add Admin QR Preview and Download

**Files:**
- Modify: `frontend/admin-vue/src/api/seat.ts`
- Modify: `frontend/admin-vue/src/utils/request.ts`
- Modify: `frontend/admin-vue/src/views/seat/index.vue`
- Create: `frontend/admin-vue/tests/unit/views/seat/index.spec.ts`

- [ ] **Step 1: Write the failing frontend test**

Create `tests/unit/views/seat/index.spec.ts`, mock `addSeat` and
`downloadQrCode`, and assert that a successful add opens a QR preview for the
created seat:

```ts
it('opens the QR preview after a seat is created', async () => {
  ;(addSeat as jest.Mock).mockResolvedValue({
    data: { code: 1, data: { id: 12, seatName: 'A区 01号桌' } }
  })
  ;(downloadQrCode as jest.Mock).mockResolvedValue(new Blob(['qr']))
  const wrapper = shallowMount(SeatManagement, { stubs: ['el-dialog', 'el-form'] })
  const vm: any = wrapper.vm
  vm.$refs.seatForm = { validate: (callback: Function) => callback(true) }
  vm.form = { seatCode: 'A01', seatName: 'A区 01号桌', areaName: 'A区', capacity: 4, sort: 0 }
  ;(URL as any).createObjectURL = jest.fn(() => 'blob:seat-qr')

  vm.submitForm()
  await wrapper.vm.$nextTick()

  expect(addSeat).toHaveBeenCalled()
  expect(vm.qrPreviewVisible).toBe(true)
  expect(vm.qrPreviewUrl).toBe('blob:seat-qr')
  expect(vm.qrPreviewSeat.seatName).toBe('A区 01号桌')
})
```

Mock list/statistics calls used by `created()` to resolve harmless empty
responses, and stub `$message`.

- [ ] **Step 2: Run the focused frontend test and verify it fails**

Run:

```powershell
npm run test:unit -- --runInBand tests/unit/views/seat/index.spec.ts
```

Expected: the test fails because the QR preview state and loading method do
not yet exist.

- [ ] **Step 3: Keep the QR request authenticated and Blob-safe**

Keep `downloadQrCode(id)` in `src/api/seat.ts` as a GET request with
`responseType: 'blob'`. In the Axios response interceptor, return the raw
response when `response.config.responseType === 'blob'`, before reading
`response.data.code`:

```ts
if (response.config.responseType === 'blob') {
  return response
}
```

This permits the seat view to use the same token-bearing Axios client for both
preview and download.

- [ ] **Step 4: Add the preview dialog and lifecycle cleanup**

Add a separate `el-dialog` after the create/edit dialog:

```html
<el-dialog title="座位二维码" :visible.sync="qrPreviewVisible" width="360px"
  @closed="clearQrPreview">
  <div class="qr-preview" v-if="qrPreviewSeat">
    <div class="qr-seat-name">{{ qrPreviewSeat.seatName }}</div>
    <img :src="qrPreviewUrl" :alt="qrPreviewSeat.seatName + '二维码'" />
  </div>
  <div slot="footer">
    <el-button @click="qrPreviewVisible = false">关闭</el-button>
    <el-button type="primary" :disabled="!qrPreviewUrl" @click="downloadPreviewQr">下载二维码</el-button>
  </div>
</el-dialog>
```

Add state and methods:

```ts
private qrPreviewVisible = false
private qrPreviewUrl = ''
private qrPreviewSeat: any = null

private async openQrPreview(seat: any) {
  this.clearQrPreview()
  const response: any = await downloadQrCode(seat.id)
  this.qrPreviewUrl = URL.createObjectURL(response.data)
  this.qrPreviewSeat = seat
  this.qrPreviewVisible = true
}

private clearQrPreview() {
  if (this.qrPreviewUrl) URL.revokeObjectURL(this.qrPreviewUrl)
  this.qrPreviewUrl = ''
  this.qrPreviewSeat = null
}

beforeDestroy() {
  this.clearQrPreview()
}
```

`downloadPreviewQr` creates a temporary anchor with
`download = qrPreviewSeat.seatCode + '-二维码.png'`, clicks it, and removes
it. Reuse `openQrPreview(row)` for the list download action when an
administrator wants to inspect/download an existing QR.

In `submitForm`, store the `addSeat` response. On creation, await
`openQrPreview(response.data.data)` before refreshing list/layout/statistics.
On edit, do not open a QR dialog.

Replace every `catch (err: any)` in this file with `catch (err)`, preserving
the existing error message logic; TypeScript 3.6 does not permit a typed catch
binding.

- [ ] **Step 5: Run the frontend test and production build**

Run:

```powershell
npm run test:unit -- --runInBand tests/unit/views/seat/index.spec.ts
npm run build
```

Expected: the focused test passes and the production build completes without
the current typed-catch syntax error.

- [ ] **Step 6: Commit the admin QR workflow**

```powershell
git add frontend/admin-vue/src/api/seat.ts frontend/admin-vue/src/utils/request.ts frontend/admin-vue/src/views/seat/index.vue frontend/admin-vue/tests/unit/views/seat/index.spec.ts
git commit -m "feat: preview QR code after seat creation"
```

### Task 5: Change Mini Program Scan to Explicit Confirmation

**Files:**
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.js`
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.wxml`
- Modify: `frontend/mp-weixin/pages/scanSeat/scanSeat.wxss`

- [ ] **Step 1: Define the state transitions in `scanSeat.js`**

Replace the current scan-time `diningSessionId` persistence with:

```js
data: {
  seatInfo: null,
  errorMsg: '',
  loading: true,
  confirming: false,
  full: false,
  scene: ''
}
```

`parseScene(scene)` must save `scene` only in page data, call
`POST /user/seat/scan`, and set `seatInfo`, `full`, and `errorMsg`. It must
not call `wx.setStorageSync` and must not navigate.

Use the backend response directly:

```js
seatInfo: {
  seatId: data.seatId,
  seatCode: data.seatCode,
  seatName: data.seatName,
  areaName: data.areaName,
  capacity: data.capacity,
  participantCount: data.participantCount,
  joined: data.joined
},
full: !!data.full,
errorMsg: data.full ? '该座位已被占用，请联系店员' : ''
```

- [ ] **Step 2: Implement the confirmation request**

Add `confirmStartOrder`:

```js
confirmStartOrder: function () {
  var that = this
  if (!this.data.seatInfo || this.data.confirming || this.data.full) return
  this.setData({ confirming: true, errorMsg: '' })
  wx.request({
    url: app.globalData.baseUrl + '/user/seat/session/confirm?seatId=' + this.data.seatInfo.seatId,
    method: 'POST',
    header: { authentication: wx.getStorageSync('token') || '' },
    success: function (res) {
      var data = res.data && res.data.data
      if (res.data && res.data.code === 1 && data && data.success) {
        wx.setStorageSync('diningSessionId', data.diningSessionId)
        wx.setStorageSync('seatId', data.seatId)
        wx.setStorageSync('seatCode', data.seatCode)
        wx.switchTab({ url: '/pages/index/index' })
        return
      }
      that.setData({
        full: res.data && res.data.msg === '该座位已被占用，请联系店员',
        errorMsg: (res.data && res.data.msg) || '确认失败，请稍后重试'
      })
    },
    fail: function () {
      that.setData({ errorMsg: '网络连接失败，请检查网络' })
    },
    complete: function () {
      that.setData({ confirming: false })
    }
  })
}
```

Declare `var that = this` inside `reScan` as well; the current implementation
uses it without defining it.

- [ ] **Step 3: Render the confirmed wording and state-specific actions**

Replace the seat card body with:

```xml
<view class="seat-info" wx:if="{{seatInfo && !loading}}">
  <view class="seat-title">你当前在 {{seatInfo.areaName}} {{seatInfo.seatName}}</view>
  <view class="seat-meta">当前已确认 {{seatInfo.participantCount}} / {{seatInfo.capacity}} 人</view>
</view>

<view class="error-msg" wx:if="{{errorMsg}}"><text>{{errorMsg}}</text></view>

<view class="actions" wx:if="{{seatInfo && !loading}}">
  <button class="scan-btn" wx:if="{{!full}}" loading="{{confirming}}"
    disabled="{{confirming}}" bindtap="confirmStartOrder">确认开始点餐</button>
  <button class="re-scan-btn" bindtap="reScan">重新扫码</button>
</view>
```

Keep the loading indicator and invalid-QR error state. Do not render
`goToOrder`; entering the order tab is only possible in the confirmation
success branch.

- [ ] **Step 4: Add focused visual styles without changing global navigation**

Use a compact seat title, muted participant-count line, disabled confirmation
button, and error card. Preserve the existing `#0B5F48` primary color and
avoid adding global app styles. The existing fixed button dimensions must be
kept stable while the loading state changes.

- [ ] **Step 5: Verify Mini Program syntax and behavior**

Run:

```powershell
node --check pages/scanSeat/scanSeat.js
```

from `frontend/mp-weixin`.

Expected: no syntax output and exit code `0`.

Then use WeChat Developer Tools against a capacity-1 test seat:

1. Scan as user A: verify the position is shown and no `dining_session` or
   participant row exists before confirmation.
2. Confirm as user A: verify one participant row, seat becomes `OCCUPIED`,
   session and seat data are stored, and the app enters the ordering tab.
3. Scan as user B: verify the exact message
   `"该座位已被占用，请联系店员"` and no confirmation button.
4. Scan again as user A: verify the user can continue without creating a
   second participant row.

- [ ] **Step 6: Commit the Mini Program flow**

```powershell
git add frontend/mp-weixin/pages/scanSeat/scanSeat.js frontend/mp-weixin/pages/scanSeat/scanSeat.wxml frontend/mp-weixin/pages/scanSeat/scanSeat.wxss
git commit -m "feat: require scan confirmation before ordering"
```

### Task 6: End-to-End Verification and README Check

**Files:**
- Modify only if the existing startup or seat-management instructions are now inaccurate: `README.md`

- [ ] **Step 1: Apply the database migration to the development database**

Run the new `migration-seat-capacity.sql` through the same MySQL connection
configured in `backend/sky-take-out/sky-server/src/main/resources/application.yml`.

Verify:

```sql
SHOW CREATE TABLE dining_session_participant;
SHOW INDEX FROM dining_session_participant WHERE Key_name = 'uk_session_user';
```

Expected: the table exists and the unique index covers
`dining_session_id,user_id`.

- [ ] **Step 2: Run all automated verification**

Run:

```powershell
mvn -pl sky-server -am test
npm run test:unit -- --runInBand tests/unit/views/seat/index.spec.ts
npm run build
node --check pages/scanSeat/scanSeat.js
```

Run the final Node command from `frontend/mp-weixin`; run the Maven command
from `backend/sky-take-out`; run frontend commands from `frontend/admin-vue`.

- [ ] **Step 3: Manually verify the admin browser workflow**

With the backend on port `8088` and admin frontend on port `8082`:

1. Log in using the existing admin account.
2. Add a unique test seat.
3. Verify the create dialog closes and a QR preview dialog opens for that
   exact seat.
4. Verify the preview is nonblank and the download button downloads a PNG.
5. Reopen the QR action for an existing seat and verify the same preview and
   download behavior.

- [ ] **Step 4: Update README only when instructions differ from reality**

Review the existing uncommitted `README.md` changes before editing. Do not
discard them. If it lacks the new migration name or the explicit
scan-confirmation rule, add concise Chinese instructions covering:

```markdown
- 执行 `backend/sky-take-out/migration-seat-capacity.sql` 后，座位容量按已确认的不同用户人数计算。
- 用户扫码只识别座位；点击“确认开始点餐”后才会绑定座位并进入点餐。
```

If the current README already states both accurately, leave it unchanged.

- [ ] **Step 5: Review the final diff and commit only feature-owned files**

Run:

```powershell
git diff --check
git status --short
```

Inspect every changed file. Do not stage or revert unrelated pre-existing
changes, including files under `.superpowers/`, `Intern_repo`, or user work
outside this feature.

Commit only the README if it was changed by this task:

```powershell
git add README.md
git commit -m "docs: document seat QR confirmation flow"
```
