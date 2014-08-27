/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Segment.io, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.segment.android;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

public class SegmentBuilderTest extends BaseAndroidTestCase {
  @Mock Application application;
  String mockKey;

  @Override protected void setUp() throws Exception {
    super.setUp();

    // Setup Fake Required Permissions
    when(application.checkCallingOrSelfPermission(Manifest.permission.INTERNET)).thenReturn(
        PackageManager.PERMISSION_GRANTED);
    when(application.checkCallingOrSelfPermission(
        Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);

    mockKey = "a_mock_key";
  }

  public void testNullContextThrowsException() throws Exception {
    try {
      new Segment.Builder(null, null);
      fail("Null context should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Context must not be null.");
    }

    try {
      new Segment.Builder(null, mockKey);
      fail("Null context should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("Context must not be null.");
    }
  }

  public void testMissingPermissionsThrowsException() throws Exception {
    when(application.checkCallingOrSelfPermission(Manifest.permission.INTERNET)).thenReturn(
        PackageManager.PERMISSION_DENIED);
    when(application.checkCallingOrSelfPermission(
        Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_GRANTED);
    try {
      new Segment.Builder(application, mockKey);
      fail("Missing internet permission should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("INTERNET permission is required.");
    }

    when(application.checkCallingOrSelfPermission(Manifest.permission.INTERNET)).thenReturn(
        PackageManager.PERMISSION_GRANTED);
    when(application.checkCallingOrSelfPermission(
        Manifest.permission.ACCESS_NETWORK_STATE)).thenReturn(PackageManager.PERMISSION_DENIED);
    try {
      new Segment.Builder(application, mockKey);
    } catch (IllegalArgumentException expected) {
      fail("Missing access state permission should not throw exception.");
    }
  }

  public void testInvalidApiKeyThrowsException() throws Exception {
    try {
      new Segment.Builder(application, null);
      fail("Null apiKey should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("writeKey must not be null or empty.");
    }

    try {
      new Segment.Builder(application, "");
      fail("Empty apiKey should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("writeKey must not be null or empty.");
    }

    try {
      new Segment.Builder(application, "    ");
      fail("Blank apiKey should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("writeKey must not be null or empty.");
    }
  }

  public void testInvalidQueueSizeThrowsException() throws Exception {
    try {
      new Segment.Builder(application, mockKey).maxQueueSize(-1);
      fail("maxQueueSize < 0 should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("maxQueueSize must be greater than or equal to zero.");
    }

    try {
      new Segment.Builder(application, mockKey).maxQueueSize(0);
      fail("maxQueueSize = 0 should throw exception.");
    } catch (IllegalArgumentException expected) {
      assertThat(expected).hasMessage("maxQueueSize must be greater than or equal to zero.");
    }

    Segment.Builder builder = new Segment.Builder(application, mockKey).maxQueueSize(10);
    try {
      builder.maxQueueSize(20);
      fail("setting maxQueueSize again should throw exception.");
    } catch (IllegalStateException expected) {
      assertThat(expected).hasMessage("maxQueueSize is already set.");
    }
  }
}
