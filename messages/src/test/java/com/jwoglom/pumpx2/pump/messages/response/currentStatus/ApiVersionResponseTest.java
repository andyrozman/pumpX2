package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ApiVersionResponseTest {
    @Test
    public void testApiVersionResponseSW71() throws DecoderException {
        // Control-IQ pump v7.3.1
        ApiVersionResponse expected = new ApiVersionResponse(2, 0);

        ApiVersionResponse parsedRes = (ApiVersionResponse) MessageTester.test(
                "00022102040200000077c8",
                2,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getMajorVersion(), parsedRes.getMajorVersion());
        assertEquals(expected.getMinorVersion(), parsedRes.getMinorVersion());
    }
    @Test
    public void testApiVersionResponseSW74() throws DecoderException {
        // Control-IQ pump v7.4
        ApiVersionResponse expected = new ApiVersionResponse(2, 1);

        ApiVersionResponse parsedRes = (ApiVersionResponse) MessageTester.test(
                "00072107040200010047b8",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getMajorVersion(), parsedRes.getMajorVersion());
        assertEquals(expected.getMinorVersion(), parsedRes.getMinorVersion());
    }
    @Test
    public void testApiVersionResponseMobi() throws DecoderException {
        // Tandem Mobi release version
        ApiVersionResponse expected = new ApiVersionResponse(3, 5);

        ApiVersionResponse parsedRes = (ApiVersionResponse) MessageTester.test(
                "000421040403000500d7cc",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getMajorVersion(), parsedRes.getMajorVersion());
        assertEquals(expected.getMinorVersion(), parsedRes.getMinorVersion());
    }
}
