package cn.handyplus.neoipse.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IpUtil测试类
 *
 * @author 滔天
 */
class IpUtilTest {

    @Test
    void testIsValidIp() {
        // 测试IPv4地址
        assertTrue(IpUtil.isValidIp("192.168.1.1"));
        assertTrue(IpUtil.isValidIp("8.8.8.8"));
        assertFalse(IpUtil.isValidIp("256.256.256.256"));
        assertFalse(IpUtil.isValidIp("192.168.1"));

        // 测试IPv6地址
        assertTrue(IpUtil.isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        assertTrue(IpUtil.isValidIp("::1"));
        assertFalse(IpUtil.isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334:1234"));

        // 测试无效IP
        assertFalse(IpUtil.isValidIp(null));
        assertFalse(IpUtil.isValidIp(""));
        assertFalse(IpUtil.isValidIp("test"));
    }

    @Test
    void testGetStr() {
        assertEquals("test", IpUtil.getStr("test"));
        assertEquals("0", IpUtil.getStr(null));
        assertEquals("0", IpUtil.getStr(""));
    }

}
