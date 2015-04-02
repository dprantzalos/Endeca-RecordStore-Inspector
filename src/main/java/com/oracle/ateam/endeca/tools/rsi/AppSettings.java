/**
 * Copyright (c) 2015, Oracle Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 *     * Neither the name of Oracle nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ORACLE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.ateam.endeca.tools.rsi;

/**
 * Application wide configurations settings.
 *
 * @author Jim Prantzalos
 * @version 1.0
 * @since R1.0
 */
public class AppSettings {
    private static final AppSettings instance = new AppSettings();
    public static final String CAS_SERVER_DEFAULT_HOST = "localhost";
    public static final int CAS_SERVER_DEFAULT_PORT = 8500;
    public static final boolean CAS_SERVER_SSL_ENABLED = false;

    private String casServerHost = CAS_SERVER_DEFAULT_HOST;
    private int casServerPort = CAS_SERVER_DEFAULT_PORT;
    private boolean casServerSSLEnabled = CAS_SERVER_SSL_ENABLED;

    /**
     * Returns the CAS Server hostname.
     *
     * @return the CAS Server hostname.
     */
    public static String getCasServerHost() {
        return instance.casServerHost;
    }

    /**
     * Sets the the CAS Server hostname.
     *
     * @param casServerHost the CAS Server hostname.
     */
    public static void setCasServerHost(final String casServerHost) {
        instance.casServerHost = casServerHost;
    }

    /**
     * Returns the CAS Server port number.
     *
     * @return the CAS Server port number.
     */
    public static int getCasServerPort() {
        return instance.casServerPort;
    }

    /**
     * Sets the CAS Server port number.
     *
     * @param casServerPort the CAS Server port number.
     */
    public static void setCasServerPort(final int casServerPort) {
        instance.casServerPort = casServerPort;
    }

    /**
     * Returns <code>true</code> if CAS Server is SSL enabled.
     *
     * @return <code>true</code> if CAS Server is SSL enabled.
     */
    public static boolean isCasServerSSLEnabled() {
        return instance.casServerSSLEnabled;
    }

    /**
     * Sets whether the CAS Server is SSL enabled or not.
     *
     * @param casServerSSLEnabled <code>true</code> if SSL is enabled; <code>false</code> otherwise.
     */
    public static void setCasServerSSLEnabled(boolean casServerSSLEnabled) {
        instance.casServerSSLEnabled = casServerSSLEnabled;
    }
}
