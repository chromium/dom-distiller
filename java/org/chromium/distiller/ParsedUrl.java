// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Wraps a javascript URL object and its URLUtils properties, with additional methods and instance
 * fields as needed.
 */
public final class ParsedUrl {

    private static class Url extends JavaScriptObject {

        protected Url() {}

        /**
         * Returns a new URL javascript object if url is valid.  Otherwise returns null.
         */
        private static native Url createUrl(String url) /*-{
            try {
                return new URL(url);
            } catch (e) {
                return null;
            }
        }-*/;

        private final native String getHost() /*-{
            return this.host;
        }-*/;

        private final native String getOrigin() /*-{
            return this.origin;
        }-*/;

        private final native String getPath() /*-{
            return this.pathname;
        }-*/;

        private final native String getQuery() /*-{
            return this.search;
        }-*/;

        private final native String getUsername() /*-{
            return this.username;
        }-*/;

        private final native String getPassword() /*-{
            return this.password;
        }-*/;

        private final native String setUsername(String username) /*-{
            this.username = username;
        }-*/;

        private final native String setPassword(String password) /*-{
            this.password = password;
        }-*/;

        private final native String replaceQueryValue(String queryName, String currentQueryValue,
                String newQueryValue) /*-{
            return this.href.replace(queryName + "=" + currentQueryValue,
                    queryName + "=" + newQueryValue);
        }-*/;

        /**
         * Returns pathname without leading and trailing '/'s and part after ';'.
         * This is needed for ParsedUrl.getPathComponents(), and probably other callers.
         */
        private final native String getTrimmedPath() /*-{
            var path = this.pathname.replace(/;.*$/, "");  // Strip away params i.e. part after ';'.
            path = path.replace(/^\//, "");  // Remove leading '/'.
            return path.replace(/\/$/, "");  // Remove trailing '/'.
        }-*/;

    }  // Url

    private Url mUrl;
    private String mTrimmedPath = null;
    private String[] mPathComponents = null;
    private String[][] mQueryParams = null;

    /**
     * Returns a ParsedUrl object if urlStr is valid.  Otherwise returns null.
     */
    public static ParsedUrl create(String urlStr) {
        Url url = Url.createUrl(urlStr);
        return url == null ? null : new ParsedUrl(url);
    }

    /**
     * Returns URLUtils.host.
     */
    public final String getHost() {
        return mUrl.getHost();
    }

    /**
     * Returns URLUtils.origin.
     */
    public final String getOrigin() {
        return mUrl.getOrigin();
    }

    /**
     * Returns URLUtils.pathname as is.
     */
    public final String getPath() {
        return mUrl.getPath();
    }

    /**
     * Returns URLUtils.pathname without leading and trailing '/'s and part after ';'.
     */
    public final String getTrimmedPath() {
        if (mTrimmedPath == null) mTrimmedPath = mUrl.getTrimmedPath();
        return mTrimmedPath;
    }

    /**
     * Returns URLUtils.search.
     */
    public final String getQuery() {
        return mUrl.getQuery();
    }

    /**
     * Returns URLUtils.username.
     */
    public final String getUsername() {
        return mUrl.getUsername();
    }

    /**
     * Returns URLUtils.password.
     */
    public final String getPassword() {
        return mUrl.getPassword();
    }

    /**
     * Returns a array of components broken down from URLUtils.path without the part after ';'.
     */
    public final String[] getPathComponents() {
        if (mPathComponents == null) {
            String path = getTrimmedPath();
            if (path.isEmpty()) {
                mPathComponents = new String[0];
            } else {
                mPathComponents = StringUtil.split(path, "\\/");
            }
        }
        return mPathComponents;
    }

    /**
     * Returns an array of name-value pairs extracted from URLUtils.search.
     * TODO(kuan): we don't need it currently, but this should be implemented as URLSearchParams,
     * with its defined interface, and adding one method to iterate through all query params.
     */
    public final String[][] getQueryParams() {
        if (mQueryParams == null) {
            final String query = mUrl.getQuery();
            if (query.isEmpty()) {
                mQueryParams = new String[0][2];
            } else {
                // Extracts the name-value components from the query without leading '?'.
                String[] components = StringUtil.split(query.substring(1), "\\&");
                mQueryParams = new String[components.length][2];
                for (int i = 0; i < components.length; i++) {
                    String[] keyValuePair = StringUtil.split(components[i], "=");
                    mQueryParams[i][0] = keyValuePair[0];
                    mQueryParams[i][1] = keyValuePair.length > 1 ? keyValuePair[1] : "";
                }
            }
        }
        return mQueryParams;
    }

    /**
     * Sets URLUtils.username.
     */
    public final String setUsername(String username) {
        return mUrl.setUsername(username);
    }

    /**
     * Sets URLUtils.password.
     */
    public final String setPassword(String password) {
        return mUrl.setPassword(password);
    }

    /**
     * Replaces the specified name-value query parameter with the new query value.
     * Returns the new HRef.  The original HRef is not mutated.
     */
    public final String replaceQueryValue(String queryName, String currentQueryValue,
            String newQueryValue) {
       return mUrl.replaceQueryValue(queryName, currentQueryValue, newQueryValue);
    }

    @Override
    public String toString() {
        return mUrl.toString();
    }

    private ParsedUrl(Url url) {
        mUrl = url;
    }

}
