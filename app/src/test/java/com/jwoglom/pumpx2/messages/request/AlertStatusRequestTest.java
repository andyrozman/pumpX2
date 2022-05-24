package com.jwoglom.pumpx2.messages.request;

import static com.jwoglom.pumpx2.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.AlertStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertStatusRequestTest {
    @Test
    public void testAlertStatusRequest() throws DecoderException {
        // empty cargo
        AlertStatusRequest expected = new AlertStatusRequest();

        AlertStatusRequest parsedReq = (AlertStatusRequest) MessageTester.test(
                "0003440300a258",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
