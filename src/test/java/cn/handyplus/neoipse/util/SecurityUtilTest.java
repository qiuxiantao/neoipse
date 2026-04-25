package cn.handyplus.neoipse.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityUtil测试类
 *
 * @author 滔天
 */
class SecurityUtilTest {

    private final SecurityUtil securityUtil = SecurityUtil.getInstance();

    @Test
    void testIsValidIp() {
        // 测试IPv4地址
        assertTrue(securityUtil.isValidIp("192.168.1.1"));
        assertTrue(securityUtil.isValidIp("8.8.8.8"));
        assertFalse(securityUtil.isValidIp("256.256.256.256"));
        assertFalse(securityUtil.isValidIp("192.168.1"));

        // 测试IPv6地址
        assertTrue(securityUtil.isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        assertTrue(securityUtil.isValidIp("::1"));
        assertFalse(securityUtil.isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334:1234"));

        // 测试无效IP
        assertFalse(securityUtil.isValidIp(null));
        assertFalse(securityUtil.isValidIp(""));
        assertFalse(securityUtil.isValidIp("test"));
    }

    @Test
    void testIsValidApiKey() {
        // 测试有效的API密钥
        assertTrue(securityUtil.isValidApiKey("abc123"));
        assertTrue(securityUtil.isValidApiKey("a1b2c3d4e5f6"));
        assertTrue(securityUtil.isValidApiKey("1234567890abcdef"));

        // 测试无效的API密钥
        assertFalse(securityUtil.isValidApiKey(null));
        assertFalse(securityUtil.isValidApiKey(""));
        assertFalse(securityUtil.isValidApiKey("12345")); // 太短
        assertFalse(securityUtil.isValidApiKey("a".repeat(65))); // 太长
        assertFalse(securityUtil.isValidApiKey("test key")); // 包含空格
    }

    @Test
    void testSanitizeIp() {
        // 测试有效的IP地址
        assertEquals("192.168.1.1", securityUtil.sanitizeIp("192.168.1.1"));
        assertEquals("8.8.8.8", securityUtil.sanitizeIp(" 8.8.8.8 ")); // 带空格

        // 测试无效的IP地址
        assertNull(securityUtil.sanitizeIp(null));
        assertNull(securityUtil.sanitizeIp(""));
        assertNull(securityUtil.sanitizeIp("test"));
    }

    @Test
    void testSanitizeApiKey() {
        // 测试有效的API密钥
        assertEquals("abc123", securityUtil.sanitizeApiKey("abc123"));
        assertEquals("a1b2c3", securityUtil.sanitizeApiKey(" a1b2c3 ")); // 带空格

        // 测试无效的API密钥
        assertNull(securityUtil.sanitizeApiKey(null));
        assertNull(securityUtil.sanitizeApiKey(""));
        assertNull(securityUtil.sanitizeApiKey("12345")); // 太短
    }

    @Test
    void testMaskApiKey() {
        // 测试掩码功能
        assertEquals("", securityUtil.maskApiKey(null));
        assertEquals("", securityUtil.maskApiKey(""));
        assertEquals("****", securityUtil.maskApiKey("1234"));
        assertEquals("ab****yz", securityUtil.maskApiKey("abcdefyz"));
        assertEquals("12**************78", securityUtil.maskApiKey("123456789012345678"));
    }

}
