package com.example.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.util.Base64;

public class AzureAdJwtToken {

    protected final String token;

    // Header
    protected final String x5t;
    protected final String kid;

    // Payload
    protected final String issuer;
    protected final String ipAddr;
    protected final String name;
    protected final String uniqueName;

    public AzureAdJwtToken(String token) {
        this.token = token;

        String[] parts = token.split("\\.");

        // Header
        String headerStr = new String(Base64.getUrlDecoder().decode((parts[0])));
        JSONObject header = new JSONObject(headerStr);

        x5t = header.getString("x5t");
        kid = header.getString("kid");

        // Payload
        // reserved, public, and private claims.
        String payloadStr = new String(Base64.getUrlDecoder().decode((parts[1])));
        JSONObject payload = new JSONObject(payloadStr);

        issuer = payload.getString("iss");
        ipAddr = payload.getString("ipaddr");
        name = payload.getString("name");
        uniqueName = payload.getString("unique_name");
    }

    /**
     *     1. go to here: https://login.microsoftonline.com/common/.well-known/openid-configuration
     *     2. check the value of "jwks_uri", which is "https://login.microsoftonline.com/common/discovery/keys"
     *     3. go to https://login.microsoftonline.com/common/discovery/keys
     *     4. get "kid" value from header, which is "Y4ueK2oaINQiQb5YEBSYVyDcpAU"
     *     5. search Y4ueK2oaINQiQb5YEBSYVyDcpAU in key file to get the key.
     *
     *     (We can manually decode JWT token at https://jwt.io/ by copy'n'paste)
     *     to select the public key used to sign this token.
     *     (There are about three keys which are rotated about everyday.)
     *
     * @throws IOException
     * @throws CertificateException
     */
    protected PublicKey loadPublicKey() throws IOException, CertificateException {

        // Key Info (RSA PublicKey)
        String openidConfigStr = readUrl("https://login.microsoftonline.com/common/.well-known/openid-configuration");
        JSONObject openidConfig = new JSONObject(openidConfigStr);

        String jwksUri = openidConfig.getString("jwks_uri");

        String jwkConfigStr = readUrl(jwksUri);
        JSONObject jwkConfig = new JSONObject(jwkConfigStr);

        JSONArray keys = jwkConfig.getJSONArray("keys");
        for (int i = 0; i < keys.length(); i++) {
            JSONObject key = keys.getJSONObject(i);

            String kid = key.getString("kid");
            String x5t = key.getString("x5t");
            String n = key.getString("n");
            String e = key.getString("e");
            String x5c = key.getJSONArray("x5c").getString(0);

            String keyStr = "-----BEGIN CERTIFICATE-----\r\n";
            String tmp = x5c;
            while (tmp.length() > 0) {
                if (tmp.length() > 64) {
                    String x = tmp.substring(0, 64);
                    keyStr += x + "\r\n";
                    tmp = tmp.substring(64);
                } else {
                    keyStr += tmp + "\r\n";
                    tmp = "";
                }
            }
            keyStr += "-----END CERTIFICATE-----\r\n";
            /*
             * go to https://jwt.io/ and copy'n'paste the thow jwt token to the left side, it will be decoded on the right side,
             * copy'n'past the public key (from ----BEGIN... to END CERT...) to the verify signature part, it will show signature verified.
             */

            // read certification
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            InputStream stream = new ByteArrayInputStream(keyStr.getBytes(StandardCharsets.US_ASCII));
            X509Certificate cer = (X509Certificate) fact.generateCertificate(stream);

            // get public key from certification
            PublicKey publicKey = cer.getPublicKey();

            if (this.kid.equals(kid)) {
                return publicKey;
            }
        }
        return null;
    }

    //TODO: cache content to file to prevent access internet everytime.
    protected String readUrl(String url) throws IOException {
        URL addr = new URL(url);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(addr.openStream()))) {
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }

    public void verify() throws IOException, CertificateException {

        PublicKey publicKey = loadPublicKey();

        //TODO: possible decode without 3rd party library...
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAKey) publicKey)).withIssuer(issuer).build();
        DecodedJWT jwt = verifier.verify(token);
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public String toString() {
        return "AzureAdJwtToken [issuer=" + issuer + ", ipAddr=" + ipAddr + ", name=" + name + ", uniqueName="
                + uniqueName + "]";
    }
}
