// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class AssertTest extends JsTestCase {
    private static class CheckFailedException extends RuntimeException {
    }

    private static abstract class Checker {
        abstract void run(boolean withMessage);
        boolean test(boolean withMessage) {
            try {
                run(withMessage);
                return true;
            } catch (Throwable t) {
                return false;
            }
        }
    }

    @Override
    public void setUp() {
        disableAssertConsoleTrace();
    }

    @Override
    public void tearDown() {
    }

    private static void check(boolean expectSuccess, Checker checker) {
        if (checker.test(false) != expectSuccess) {
            throw new CheckFailedException();
        }

        if (checker.test(true) != expectSuccess) {
            throw new CheckFailedException();
        }
    }

    public void testCheck() {
        Checker passer = new Checker() {
            void run(boolean withMessage) {
            }
        };
        Checker failer = new Checker() {
            void run(boolean withMessage) {
                fail();
            }
        };

        // These should not throw exceptions.
        check(true, passer);
        check(false, failer);

        // Each of these should fail.
        try {
            check(false, passer);
            fail();
        } catch (CheckFailedException e) {
        }

        try {
            check(true, failer);
            fail();
        } catch (CheckFailedException e) {
        }

        // A Checker's success/failure is expected to be consistent across null/non-null messages.
        // If not, check should fail regardless of the expectedSuccess value.
        Checker nullFailer = new Checker() {
            void run(boolean withMessage) {
                if (!withMessage) fail();
            }
        };
        Checker stringFailer = new Checker() {
            void run(boolean withMessage) {
                if (withMessage) fail();
            }
        };

        try {
            check(true, nullFailer);
            fail();
        } catch (CheckFailedException e) {
        }

        try {
            check(false, nullFailer);
            fail();
        } catch (CheckFailedException e) {
        }

        try {
            check(true, stringFailer);
            fail();
        } catch (CheckFailedException e) {
        }

        try {
            check(false, stringFailer);
            fail();
        } catch (CheckFailedException e) {
        }
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final char[] expecteds,
            final char[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final byte[] expecteds,
            final byte[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final short[] expecteds,
            final short[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final int[] expecteds,
            final int[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final long[] expecteds,
            final long[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkArrayEquals(boolean expectSuccess,
            final Object[] expecteds,
            final Object[] actuals) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertArrayEquals("", expecteds, actuals);
                } else {
                    assertArrayEquals(expecteds, actuals);
                }
            }
        });
    }

    private static void checkEquals(boolean expectSuccess,
            final double expected,
            final double actual,
            final double delta) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertEquals("", expected, actual, delta);
                } else {
                    assertEquals(expected, actual, delta);
                }
            }
        });
    }

    private static void checkEquals(boolean expectSuccess,
            final long expected,
            final long actual) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertEquals("", expected, actual);
                } else {
                    assertEquals(expected, actual);
                }
            }
        });
    }

    private static void checkEquals(boolean expectSuccess,
            final Object expected,
            final Object actual) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertEquals("", expected, actual);
                } else {
                    assertEquals(expected, actual);
                }
            }
        });
    }

    private static void checkAssertFalse(boolean expectSuccess,
            final boolean value) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertFalse("", value);
                } else {
                    assertFalse(value);
                }
            }
        });
    }

    private static void checkAssertTrue(boolean expectSuccess,
            final boolean value) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertTrue("", value);
                } else {
                    assertTrue(value);
                }
            }
        });
    }


    private static void checkAssertNull(boolean expectSuccess,
            final Object value) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertNull("", value);
                } else {
                    assertNull(value);
                }
            }
        });
    }

    private static void checkAssertNotNull(boolean expectSuccess,
            final Object value) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertNotNull("", value);
                } else {
                    assertNotNull(value);
                }
            }
        });
    }

    private static void checkAssertSame(boolean expectSuccess,
            final Object expected,
            final Object actual) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertSame("", expected, actual);
                } else {
                    assertSame(expected, actual);
                }
            }
        });
    }

    private static void checkAssertNotSame(boolean expectSuccess,
            final Object expected,
            final Object actual) {
        check(expectSuccess, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    assertNotSame("", expected, actual);
                } else {
                    assertNotSame(expected, actual);
                }
            }
        });
    }

    public void testAssertNull() {
        checkAssertNull(true, null);
        checkAssertNull(false, "");
        checkAssertNull(false, new Object());
    }

    public void testCharArrayEquals() {
        checkArrayEquals(true, new char[]{0, 1}, new char[]{0, 1});
        checkArrayEquals(true, new char[0], new char[0]);
        checkArrayEquals(true, new char[2], new char[2]);
        checkArrayEquals(false, new char[]{0}, new char[]{0, 1});
        checkArrayEquals(false, new char[]{0, 1}, new char[]{0});
        checkArrayEquals(false, new char[]{0, 1}, new char[]{0, 2});
        checkArrayEquals(false, new char[]{0, 1}, new char[]{2, 1});
        checkArrayEquals(false, new char[]{0, 1}, null);
        checkArrayEquals(false, new char[0], new char[2]);
    }

    public void testByteArrayEquals() {
        checkArrayEquals(true, new byte[]{0, 1}, new byte[]{0, 1});
        checkArrayEquals(true, new byte[0], new byte[0]);
        checkArrayEquals(true, new byte[2], new byte[2]);
        checkArrayEquals(false, new byte[]{0}, new byte[]{0, 1});
        checkArrayEquals(false, new byte[]{0, 1}, new byte[]{0});
        checkArrayEquals(false, new byte[]{0, 1}, new byte[]{0, 2});
        checkArrayEquals(false, new byte[]{0, 1}, new byte[]{2, 1});
        checkArrayEquals(false, new byte[]{0, 1}, null);
        checkArrayEquals(false, new byte[0], new byte[2]);
    }

    public void testShortArrayEquals() {
        checkArrayEquals(true, new short[]{0, 1}, new short[]{0, 1});
        checkArrayEquals(true, new short[0], new short[0]);
        checkArrayEquals(true, new short[2], new short[2]);
        checkArrayEquals(false, new short[]{0}, new short[]{0, 1});
        checkArrayEquals(false, new short[]{0, 1}, new short[]{0});
        checkArrayEquals(false, new short[]{0, 1}, new short[]{0, 2});
        checkArrayEquals(false, new short[]{0, 1}, new short[]{2, 1});
        checkArrayEquals(false, new short[]{0, 1}, null);
        checkArrayEquals(false, new short[0], new short[2]);
    }

    public void testIntArrayEquals() {
        checkArrayEquals(true, new int[]{0, 1}, new int[]{0, 1});
        checkArrayEquals(true, new int[0], new int[0]);
        checkArrayEquals(true, new int[2], new int[2]);
        checkArrayEquals(false, new int[]{0}, new int[]{0, 1});
        checkArrayEquals(false, new int[]{0, 1}, new int[]{0});
        checkArrayEquals(false, new int[]{0, 1}, new int[]{0, 2});
        checkArrayEquals(false, new int[]{0, 1}, new int[]{2, 1});
        checkArrayEquals(false, new int[]{0, 1}, null);
        checkArrayEquals(false, new int[0], new int[2]);
    }

    public void testLongArrayEquals() {
        checkArrayEquals(true, new long[]{0, 1}, new long[]{0, 1});
        checkArrayEquals(true, new long[0], new long[0]);
        checkArrayEquals(true, new long[2], new long[2]);
        checkArrayEquals(false, new long[]{0}, new long[]{0, 1});
        checkArrayEquals(false, new long[]{0, 1}, new long[]{0});
        checkArrayEquals(false, new long[]{0, 1}, new long[]{0, 2});
        checkArrayEquals(false, new long[]{0, 1}, new long[]{2, 1});
        checkArrayEquals(false, new long[]{0, 1}, null);
        checkArrayEquals(false, new long[0], new long[2]);
    }

    public void testObjectArrayEquals() {
        checkArrayEquals(true, new String[]{"0", "1"}, new String[]{"0", "1"});
        checkArrayEquals(true, new String[0], new String[0]);
        checkArrayEquals(true, new String[2], new String[2]);
        checkArrayEquals(true, new Object[0], new Object[0]);
        Object v = new Object();
        Object o = new Object();
        checkArrayEquals(true, new Object[]{v}, new Object[]{v});
        checkArrayEquals(false, new Object[]{v}, new Object[]{o});
        checkArrayEquals(false, new String[]{"0"}, new String[]{"0", "1"});
        checkArrayEquals(false, new String[]{"0", "1"}, new String[]{"0"});
        checkArrayEquals(false, new String[]{"0", "1"}, new String[]{"0", "2"});
        checkArrayEquals(false, new String[]{"0", "1"}, new String[]{"2", "1"});
        checkArrayEquals(false, new String[]{"0", "1"}, null);
        checkArrayEquals(false, new Object[0], new Object[2]);
        checkArrayEquals(false, new Object[0], new Object[2]);
    }

    public void testLongEquals() {
        checkEquals(true, 0, 0);
        checkEquals(true, 0l, 0l);
        checkEquals(true, 1, 1);
        checkEquals(true, (byte)-1, (byte)-1);
        checkEquals(false, 0, 1);
        checkEquals(false, -1, 1);
    }

    public void testDoubleEquals() {
        checkEquals(true, 0.0, 0.0, 1e-20);
        checkEquals(true, 1.0, 1.0, 1e-20);
        checkEquals(false, 0.0, 1.0, 1e-20);
        checkEquals(false, -1.0, 1.0, 1e-20);

        checkEquals(true, 0.0, 1.0, 2.0);
        checkEquals(true, 0.0, 2.0, 3.0);
        checkEquals(true, 0.0, -2.0, 3.0);
        checkEquals(false, 0.0, 4.0, 3.0);
        checkEquals(false, 0.0, -4.0, 3.0);
    }

    public void testObjectEquals() {
        Object v = new Object(), o = new Object();
        checkEquals(true, v, v);
        checkEquals(true, "", "");
        checkEquals(true, null, null);
        checkEquals(false, v, o);
        checkEquals(false, v, "");
        checkEquals(false, v, null);
    }

    public void testAssertFalse() {
        checkAssertFalse(true, false);
        checkAssertFalse(false, true);
    }

    public void testAssertTrue() {
        checkAssertTrue(false, false);
        checkAssertTrue(true, true);
    }


    public void testAssertNotNull() {
        checkAssertNotNull(false, null);
        checkAssertNotNull(true, "");
        checkAssertNotNull(true, new Object());
    }

    public void testAssertSame() {
        Double v = new Double(0.0), o = new Double(0.0);
        assertTrue(v.equals(o));
        checkAssertSame(true, v, v);
        checkAssertSame(true, null, null);
        checkAssertSame(true, "", "");
        checkAssertSame(false, v, "");
        checkAssertSame(false, v, o);
    }

    public void testAssertNotSame() {
        Double v = new Double(0.0), o = new Double(0.0);
        assertTrue(v.equals(o));
        checkAssertNotSame(false, v, v);
        checkAssertNotSame(false, null, null);
        checkAssertNotSame(false, "", "");
        checkAssertNotSame(true, v, "");
        checkAssertNotSame(true, v, o);
    }

    public void testFail() {
        check(false, new Checker() {
            void run(boolean withMessage) {
                if (withMessage) {
                    fail("");
                } else {
                    fail();
                }
            }
        });
    }
}
