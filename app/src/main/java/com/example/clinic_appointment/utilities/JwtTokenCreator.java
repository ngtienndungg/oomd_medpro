package com.example.clinic_appointment.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JwtTokenCreator {
        private static final String SECRET = "Vzc1WG1ONU5hYTB1aGFoWGFBbkJPUTUxUnhFN0RSSGU=";
        private static final String HEADER_CONTENT_TYPE = "stringee-api;v=1";
        private static final String HEADER_TYP = "JWT";
        private static final String HEADER_ALG = "HS256";
        private static final String ISSUER = "SK.0.xFk7KNUL4PxwRlwxzdnCFQH3BlxQGixc";
        private static final String JTI = "SK.0.xFk7KNUL4PxwRlwxzdnCFQH3BlxQGixc-1719153603";
        private static final String USER_ID = "doctor";
        private static final String USER_ID_A = "patient";

    public static String createJwtToken() {
        try {
            // Tạo header
            JSONObject header = new JSONObject();
            header.put("cty", HEADER_CONTENT_TYPE);
            header.put("typ", HEADER_TYP);
            header.put("alg", HEADER_ALG);

            JSONObject payload = new JSONObject();
            payload.put("jti", JTI);
            payload.put("iss", ISSUER);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 3);
            Date exp = calendar.getTime();
            payload.put("exp", exp.getTime() / 1000);
            if (SharedPrefs.getInstance().getData(Constants.KEY_USER_ROLE, Integer.class).equals(4)) {
                payload.put("userId", USER_ID_A);
            } else {
                payload.put("userId", USER_ID);
            }
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            String token = JWT.create()
                    .withHeader(toMap(header))
                    .withPayload(toMap(payload))
                    .sign(algorithm);

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Object> toMap(JSONObject jsonobj) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonobj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, jsonobj.get(key));
        }
        return map;
    }
}