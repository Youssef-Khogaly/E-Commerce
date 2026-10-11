package com.ecommerce.util;

import com.ecommerce.util.exception.PhoneNumberParseException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Optional;

// wrapper for google phone number validation library

public class PhoneNumberUtilWrapper {
    private final com.google.i18n.phonenumbers.PhoneNumberUtil util;
    private static final PhoneNumberUtilWrapper instance;
    static {
        instance = new PhoneNumberUtilWrapper();
    }
    private PhoneNumberUtilWrapper() {
        util = PhoneNumberUtil.getInstance();
    }

    public static PhoneNumberUtilWrapper getIntance(){
        return instance;
    }
    /**
     * Validate phone number.
     *
     * @param phoneNumber raw user input
     * @param region      two-letter ISO region ("EG"), or null if number starts with +
     */
    public void isValidPhone(CharSequence phoneNumber , String region) throws PhoneNumberParseException{
        Phonenumber.PhoneNumber phone=null;
        try{
            phone = toPhoneNumber(phoneNumber,region);

            if (util.isPossibleNumber(phone)) {
                util.isValidNumber(phone);
            }

        } catch (NumberParseException e) {
            String msg = null;
            if(e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE)
                msg = "Invalid region code";
            else
                msg = "Invalid phone number format";
            throw  new PhoneNumberParseException(msg);
        }

    }
    private Phonenumber.PhoneNumber toPhoneNumber(CharSequence phone , String region) throws NumberParseException {
        return util.parse(phone,region);
    }
    /**
     * Convert to E164 normalized form (+201234567890).
     */
    public Optional<String> toE164( String phone , String region){
        try{
            var phoneNum = toPhoneNumber(phone,region);
            return Optional.of(util.format(phoneNum , PhoneNumberUtil.PhoneNumberFormat.E164));
        }catch (PhoneNumberParseException | NumberParseException n){
            return Optional.empty();
        }
    }
}
