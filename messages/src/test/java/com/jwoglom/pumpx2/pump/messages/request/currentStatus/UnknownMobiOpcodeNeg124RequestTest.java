package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg124Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UnknownMobiOpcodeNeg124RequestTest {
    @Test
    public void testUnknownMobiOpcodeNeg124Request_a() throws DecoderException {
        UnknownMobiOpcodeNeg124Request expected = new UnknownMobiOpcodeNeg124Request(
            new byte[]{98,-107,-117,30,-87,86,-101,121,-68,18,80,42,-46,-128,7,-90,-111,104,84,-75,24,-62,30,25}
        );

        UnknownMobiOpcodeNeg124Request parsedReq = (UnknownMobiOpcodeNeg124Request) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "01ae84ae1862958b1ea9569b79bc12502ad28007",
                -82,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected,
                "00aea6916854b518c21e1961fb"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testUnknownMobiOpcodeNeg124Request_b() throws DecoderException {
        UnknownMobiOpcodeNeg124Request expected = new UnknownMobiOpcodeNeg124Request(
                new byte[]{-23,-107,-117,30,15,-11,-60,-106,104,46,112,-124,-66,45,-84,-66,24,-94,-2,1,-86,98,-120,-56}
        );

        UnknownMobiOpcodeNeg124Request parsedReq = (UnknownMobiOpcodeNeg124Request) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "01c584c518e9958b1e0ff5c496682e7084be2dac",
                -59,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected,
                "00c5be18a2fe01aa6288c8e065"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}