package com.upgrade.campsite.services;

import org.springframework.stereotype.Component;

@Component
public class IdService {
    private final String allowedCharacters="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateId(Integer length) {
        String result = "";
        Integer aLength = allowedCharacters.length();
        Integer counter=length;
        while (counter > 0) {
            Integer r = Double.valueOf(aLength * Math.random()).intValue();
            result = result + allowedCharacters.charAt(r);
            counter--;
        }
        return result;
    }
}
