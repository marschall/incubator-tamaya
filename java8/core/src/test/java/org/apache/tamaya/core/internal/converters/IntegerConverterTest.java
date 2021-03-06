/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the default converter for Integers.
 */
public class IntegerConverterTest {

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Decimal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.decimal", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().intValue(), 101);
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Octal() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.octal", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().intValue(), Integer.decode("02").intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_Hex() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.hex.lowerX", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().intValue(), Integer.decode("0x2F").intValue());
        valueRead = config.getOptional("tests.converter.integer.hex.upperX", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(valueRead.get().intValue(), Integer.decode("0X3F").intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_NotPresent() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.foo", Integer.class);
        assertFalse(valueRead.isPresent());
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_MinValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.min", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(Integer.MIN_VALUE, valueRead.get().intValue());
    }

    /**
     * Test conversion. The value are provided by
     * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
     * @throws Exception
     */
    @Test
    public void testConvert_Integer_MaxValue() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        Optional<Integer> valueRead = config.getOptional("tests.converter.integer.max", Integer.class);
        assertTrue(valueRead.isPresent());
        assertEquals(Integer.MAX_VALUE, valueRead.get().intValue());
    }
}