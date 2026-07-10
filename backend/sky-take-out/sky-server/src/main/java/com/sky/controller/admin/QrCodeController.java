package com.sky.controller.admin;

import com.sky.service.SeatService;
import com.sky.utils.QrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * 管理端-二维码相关接口
 */
@RestController
@RequestMapping("/admin/qr")
@Slf4j
public class QrCodeController {

    @Autowired
    private SeatService seatService;

    /**
     * 下载座位二维码图片
     */
    @GetMapping("/download/{id}")
    public void downloadQrCode(@PathVariable Long id, HttpServletResponse response) throws Exception {
        com.sky.entity.Seat seat = seatService.getById(id);
        if (seat == null) {
            response.setStatus(404);
            return;
        }

        // scene格式: seatCode:qrVersion:sign
        String scene = seat.getSeatCode() + ":" + seat.getQrVersion() + ":" + seat.getQrSign();
        String title = seat.getSeatName();

        byte[] qrBytes = QrCodeUtil.generateQRCodeWithTitle(scene, title);

        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(title + "_qr.png", "UTF-8"));
        response.getOutputStream().write(qrBytes);
        response.getOutputStream().flush();
    }

    /**
     * 批量导出二维码（返回JSON列表，前端循环下载）
     */
    @GetMapping("/batch/{ids}")
    public void batchDownloadQrCodes(@PathVariable String ids, HttpServletResponse response) throws Exception {
        // 简化处理：返回第一个座位的二维码，批量功能前端循环调用单个接口
        String[] idArray = ids.split(",");
        if (idArray.length > 0) {
            Long firstId = Long.parseLong(idArray[0]);
            downloadQrCode(firstId, response);
        }
    }
}
