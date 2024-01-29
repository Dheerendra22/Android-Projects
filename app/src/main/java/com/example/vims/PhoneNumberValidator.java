package com.example.vims;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneNumberValidator {

    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try {
            // Parse the phone number with the given country code
            String regionCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
            String phoneNumberWithRegion = "+" + countryCode + phoneNumber;
            return phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phoneNumberWithRegion, regionCode));
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
