package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetTempRateRequestTest {
    @Test
    public void testSetTempRateRequest_95pct_15min() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443560,pumpTimeSinceReset=1906112,cargo={-88,68,-117,30,-64,21,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1906112L);

        // Temp Rate 95% for 15m
        SetTempRateRequest expected = new SetTempRateRequest(
                new byte[]{-96,-69,13,0,95,0}
        );

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone 2
                // 2024-03-28T00:26:21.525000+00:00
                "0122a4221ea0bb0d005f001c998b1e37eacb5339",
                34,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00227c57675fc47f8c9bf5dec1f0d31d42a4b7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}