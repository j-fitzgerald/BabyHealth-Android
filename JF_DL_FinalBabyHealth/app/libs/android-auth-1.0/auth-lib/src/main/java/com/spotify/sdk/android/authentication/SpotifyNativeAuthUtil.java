/*
 * Copyright (c) 2015-2016 Spotify AB
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.sdk.android.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SpotifyNativeAuthUtil {

    /*
     * This is used to pass information about the protocol version
     * to the AuthorizationActivity.
     * DO NOT CHANGE THIS.
     */
    private static final String EXTRA_VERSION = "VERSION";

    /*
     * Constants below have their counterparts in Spotify app where
     * they're used to parse messages received from SDK. 
     * If anything changes in the structure of those messages this version
     * should be bumped and new protocol version should be implemented on the other side. 
     */
    private static final int PROTOCOL_VERSION = 1;

    // PROTOCOL BEGIN

    static final String EXTRA_REPLY = "REPLY";
    static final String EXTRA_ERROR = "ERROR";

    static final String KEY_CLIENT_ID = "CLIENT_ID";
    static final String KEY_REQUESTED_SCOPES = "SCOPES";
    static final String KEY_REDIRECT_URI = "REDIRECT_URI";
    static final String KEY_RESPONSE_TYPE = "RESPONSE_TYPE";
    static final String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";
    static final String KEY_AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
    static final String KEY_EXPIRES_IN = "EXPIRES_IN";

    static final String RESPONSE_TYPE_TOKEN = "token";
    static final String RESPONSE_TYPE_CODE = "code";

    // PROTOCOL END

    private static final String SPOTIFY_AUTH_ACTIVITY_ACTION = "com.spotify.sso.action.START_AUTH_FLOW";
    private static final String SPOTIFY_PACKAGE_NAME = "com.spotify.music";
    private static final String[] SPOTIFY_PACKAGE_SUFFIXES = new String[]{
            ".debug",
            ".canary",
            ".partners",
            ""
    };
    private static final String[] SPOTIFY_SIGNATURE_HASH = new String[] {
            "80abdd17dcc4cb3a33815d354355bf87c9378624",
            "88df4d670ed5e01fc7b3eff13b63258628ff5a00",
            "d834ae340d1e854c5f4092722f9788216d9221e5",
            "1cbedd9e7345f64649bad2b493a20d9eea955352",
            "4b3d76a2de89033ea830f476a1f815692938e33b",
    };


    private Activity mContextActivity;
    private AuthenticationRequest mRequest;

    public SpotifyNativeAuthUtil(Activity contextActivity, AuthenticationRequest request) {
        mContextActivity = contextActivity;
        mRequest = request;
    }

    public boolean startAuthActivity() {
        Intent intent = createAuthActivityIntent();
        if (intent == null) {
            return false;
        }
        intent.putExtra(EXTRA_VERSION, PROTOCOL_VERSION);

        intent.putExtra(KEY_CLIENT_ID, mRequest.getClientId());
        intent.putExtra(KEY_REDIRECT_URI, mRequest.getRedirectUri());
        intent.putExtra(KEY_RESPONSE_TYPE, mRequest.getResponseType());
        intent.putExtra(KEY_REQUESTED_SCOPES, mRequest.getScopes());

        try {
            mContextActivity.startActivityForResult(intent, LoginActivity.REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    private Intent createAuthActivityIntent() {
        Intent intent = null;
        for (String suffix : SPOTIFY_PACKAGE_SUFFIXES) {
            intent = tryResolveActivity(SPOTIFY_PACKAGE_NAME + suffix);
            if (intent != null) {
                break;
            }
        }
        return intent;
    }

    private Intent tryResolveActivity(String packageName) {

        Intent intent = new Intent(SPOTIFY_AUTH_ACTIVITY_ACTION);
        intent.setPackage(packageName);

        ComponentName componentName = intent.resolveActivity(mContextActivity.getPackageManager());

        if (componentName == null) {
            return null;
        }

        if (!validateSignature(componentName.getPackageName())) {
            return null;
        }

        return intent;
    }

    @SuppressLint("PackageManagerGetSignatures")
    private boolean validateSignature(String spotifyPackageName) {
        try {
            final PackageInfo packageInfo = mContextActivity.getPackageManager().getPackageInfo(spotifyPackageName, PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures == null) {
                return false;
            }

            for (Signature signature : packageInfo.signatures) {
                final String signatureString = signature.toCharsString();
                final String sha1Signature = sha1Hash(signatureString);
                for (String s : SPOTIFY_SIGNATURE_HASH) {
                    if (s.equals(sha1Signature)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    public void stopAuthActivity() {
        mContextActivity.finishActivity(LoginActivity.REQUEST_CODE);
    }

    private static String sha1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException ignored) {
        } catch (UnsupportedEncodingException ignored) {
        }
        return hash;
    }

    private final static char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
