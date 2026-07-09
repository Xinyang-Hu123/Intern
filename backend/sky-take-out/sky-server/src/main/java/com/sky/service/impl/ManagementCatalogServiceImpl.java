package com.sky.service.impl;

import com.sky.dto.ManagementPageQueryDTO;
import com.sky.entity.*;
import com.sky.result.PageResult;
import com.sky.service.ManagementCatalogService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ManagementCatalogServiceImpl implements ManagementCatalogService {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final Map<Long, MarketingActivity> marketingStore = new LinkedHashMap<>();
    private final Map<Long, CouponActivity> couponStore = new LinkedHashMap<>();
    private final Map<Long, SystemUser> userStore = new LinkedHashMap<>();
    private final Map<Long, SystemRole> roleStore = new LinkedHashMap<>();
    private final Map<Long, SystemMenu> menuStore = new LinkedHashMap<>();

    public ManagementCatalogServiceImpl() {
        seed();
    }

    @Override
    public PageResult pageMarketing(ManagementPageQueryDTO queryDTO) {
        return page(marketingStore.values(), queryDTO, MarketingActivity::getName);
    }

    @Override
    public MarketingActivity saveMarketing(MarketingActivity activity) {
        if (activity.getId() == null) {
            activity.setId(idGenerator.incrementAndGet());
        }
        if (activity.getStatus() == null) {
            activity.setStatus(1);
        }
        marketingStore.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public void deleteMarketing(Long id) {
        marketingStore.remove(id);
    }

    @Override
    public void setMarketingStatus(Long id, Integer status) {
        MarketingActivity activity = marketingStore.get(id);
        if (activity != null) {
            activity.setStatus(status);
        }
    }

    @Override
    public PageResult pageCoupons(ManagementPageQueryDTO queryDTO) {
        return page(couponStore.values(), queryDTO, CouponActivity::getName);
    }

    @Override
    public CouponActivity saveCoupon(CouponActivity coupon) {
        if (coupon.getId() == null) {
            coupon.setId(idGenerator.incrementAndGet());
        }
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }
        couponStore.put(coupon.getId(), coupon);
        return coupon;
    }

    @Override
    public void deleteCoupon(Long id) {
        couponStore.remove(id);
    }

    @Override
    public void setCouponStatus(Long id, Integer status) {
        CouponActivity coupon = couponStore.get(id);
        if (coupon != null) {
            coupon.setStatus(status);
        }
    }

    @Override
    public PageResult pageUsers(ManagementPageQueryDTO queryDTO) {
        return page(userStore.values(), queryDTO, user -> user.getName() + user.getUsername());
    }

    @Override
    public SystemUser saveUser(SystemUser user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        user.setUpdateTime(LocalDateTime.now());
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userStore.remove(id);
    }

    @Override
    public void setUserStatus(Long id, Integer status) {
        SystemUser user = userStore.get(id);
        if (user != null) {
            user.setStatus(status);
            user.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public void assignUserRoles(Long id, List<String> roles) {
        SystemUser user = userStore.get(id);
        if (user != null) {
            user.setRoles(roles);
            user.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public PageResult pageRoles(ManagementPageQueryDTO queryDTO) {
        return page(roleStore.values(), queryDTO, role -> role.getName() + role.getRoleCode());
    }

    @Override
    public SystemRole saveRole(SystemRole role) {
        if (role.getId() == null) {
            role.setId(idGenerator.incrementAndGet());
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        role.setUpdateTime(LocalDateTime.now());
        roleStore.put(role.getId(), role);
        return role;
    }

    @Override
    public void deleteRole(Long id) {
        roleStore.remove(id);
    }

    @Override
    public void setRoleStatus(Long id, Integer status) {
        SystemRole role = roleStore.get(id);
        if (role != null) {
            role.setStatus(status);
            role.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public void assignRoleMenus(Long id, List<Long> menuIds) {
        SystemRole role = roleStore.get(id);
        if (role != null) {
            role.setMenuIds(menuIds);
            role.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public PageResult pageMenus(ManagementPageQueryDTO queryDTO) {
        return page(menuStore.values(), queryDTO, menu -> menu.getName() + menu.getPermission() + menu.getPath());
    }

    @Override
    public SystemMenu saveMenu(SystemMenu menu) {
        if (menu.getId() == null) {
            menu.setId(idGenerator.incrementAndGet());
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        menu.setUpdateTime(LocalDateTime.now());
        menuStore.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public void deleteMenu(Long id) {
        menuStore.remove(id);
    }

    @Override
    public void setMenuStatus(Long id, Integer status) {
        SystemMenu menu = menuStore.get(id);
        if (menu != null) {
            menu.setStatus(status);
            menu.setUpdateTime(LocalDateTime.now());
        }
    }

    private <T> PageResult page(Collection<T> source, ManagementPageQueryDTO queryDTO, java.util.function.Function<T, String> nameProvider) {
        int page = queryDTO.getPage() <= 0 ? 1 : queryDTO.getPage();
        int pageSize = queryDTO.getPageSize() <= 0 ? 10 : queryDTO.getPageSize();
        String keyword = queryDTO.getName();
        List<T> filtered = source.stream()
                .filter(item -> keyword == null || keyword.trim().isEmpty() || Optional.ofNullable(nameProvider.apply(item)).orElse("").contains(keyword.trim()))
                .filter(item -> queryDTO.getStatus() == null || Objects.equals(getStatus(item), queryDTO.getStatus()))
                .collect(Collectors.toList());
        int fromIndex = Math.min((page - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        return new PageResult(filtered.size(), filtered.subList(fromIndex, toIndex));
    }

    private Integer getStatus(Object item) {
        if (item instanceof MarketingActivity) {
            return ((MarketingActivity) item).getStatus();
        }
        if (item instanceof CouponActivity) {
            return ((CouponActivity) item).getStatus();
        }
        if (item instanceof SystemUser) {
            return ((SystemUser) item).getStatus();
        }
        if (item instanceof SystemRole) {
            return ((SystemRole) item).getStatus();
        }
        if (item instanceof SystemMenu) {
            return ((SystemMenu) item).getStatus();
        }
        return null;
    }

    private void seed() {
        saveMarketing(MarketingActivity.builder()
                .id(1L)
                .name("新客满减")
                .rule("满50减8")
                .scopeType("全场商品")
                .status(1)
                .startTime(LocalDateTime.now().minusDays(7))
                .endTime(LocalDateTime.now().plusDays(30))
                .build());
        saveCoupon(CouponActivity.builder()
                .id(2L)
                .name("会员优惠券")
                .couponType("满减券")
                .thresholdAmount(new BigDecimal("30"))
                .discountAmount(new BigDecimal("5"))
                .scopeType("全场商品")
                .status(1)
                .startTime(LocalDateTime.now().minusDays(7))
                .endTime(LocalDateTime.now().plusDays(30))
                .build());
        saveMenu(SystemMenu.builder().id(3L).name("订单管理").permission("order:list").path("/order").component("orderDetails/index").sort(1).visible(1).status(1).build());
        saveMenu(SystemMenu.builder().id(4L).name("营销管理").permission("activity:marketing").path("/activity/marketing").component("management/ResourceList").sort(2).visible(1).status(1).build());
        saveRole(SystemRole.builder().id(5L).roleCode("admin").name("管理员").status(1).menuIds(Arrays.asList(3L, 4L)).build());
        saveUser(SystemUser.builder().id(6L).username("admin").name("系统管理员").phone("13800138000").status(1).roles(Collections.singletonList("admin")).build());
    }
}
