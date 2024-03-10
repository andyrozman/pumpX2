package com.jwoglom.pumpx2.pump.messages.builders;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.AbstractChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeV2Response;

import java.nio.charset.Charset;

public class PumpChallengeRequestBuilder {
    public static PumpChallengeRequest create(AbstractChallengeResponse challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        if (challengeResponse instanceof CentralChallengeResponse) {
            return createV1((CentralChallengeResponse) challengeResponse, pairingCode);
        } else if (challengeResponse instanceof CentralChallengeV2Response) {
            return createV2((CentralChallengeV2Response) challengeResponse, pairingCode);
        } else {
            throw new RuntimeException("invalid CentralChallengeResponse");
        }
    }

    public static String processPairingCode(String pairingCode, PairingCodeType type) throws InvalidPairingCodeFormat {
        if (type == PairingCodeType.LONG_16CHAR) {
            // Remove all dashes and spaces
            String processed = "";
            for (Character c : pairingCode.toCharArray()) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    processed += c;
                }
            }
            if (processed.length() != 16) {
                throw new InvalidLongPairingCodeFormat();
            }
            return processed;
        } else if (type == PairingCodeType.SHORT_6CHAR) {
            // Remove all non-numbers and spaces
            String processed = "";
            for (Character c : pairingCode.toCharArray()) {
                if ((c >= '0' && c <= '9')) {
                    processed += c;
                }
            }
            if (processed.length() != 6) {
                throw new InvalidShortPairingCodeFormat();
            }
            return processed;
        } else {
            throw new InvalidPairingCodeFormat("");
        }
    }

    // HMAC sha1
    public static PumpChallengeRequest createV1(CentralChallengeResponse challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        int appInstanceId = challengeResponse.getAppInstanceId();
        byte[] hmacKey = challengeResponse.getHmacKey();

        String pairingChars = processPairingCode(pairingCode, PairingCodeType.LONG_16CHAR);
        byte[] challengeHash = Packetize.doHmacSha1(hmacKey, pairingChars.getBytes(Charset.forName("UTF-8")));
        return new PumpChallengeRequest(
                appInstanceId,
                challengeHash);
    }

    // ECJPake (Password Authenticated Key Exchange by Juggling over Eliptic Curve)
    public static PumpChallengeRequest createV2(CentralChallengeV2Response challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        int appInstanceId = challengeResponse.getAppInstanceId();
        byte[] hmacKey = challengeResponse.getCentralChallengeHash();

        // TODO
        String pairingChars = processPairingCode(pairingCode, PairingCodeType.SHORT_6CHAR);
        byte[] challengeHash = Packetize.doHmacSha1(hmacKey, pairingChars.getBytes(Charset.forName("UTF-8")));
        return new PumpChallengeRequest(
                appInstanceId,
                challengeHash);
    }

    public static class InvalidPairingCodeFormat extends Exception {
        InvalidPairingCodeFormat(String reason) {
            super("The pairing code entered does not match the expected format. " + reason);
        }
    }

    public static class InvalidLongPairingCodeFormat extends InvalidPairingCodeFormat {
        InvalidLongPairingCodeFormat() {
            super("It should be 16 alphanumeric characters total across 5 groups of 4 characters each.");
        }
    }
    public static class InvalidShortPairingCodeFormat extends InvalidPairingCodeFormat {
        InvalidShortPairingCodeFormat() {
            super("It should be 6 numbers.");
        }
    }
}
