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
package org.apache.tamaya.functions;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Accessor that provides useful functions along with configuration.
 */
public final class PropertySourceFunctions {
    /**
     * Private singleton constructor.
     */
    private PropertySourceFunctions() {
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (non recursive). Hereby
     * the section key is stripped away fromMap the resulting key.
     * <p>
     * Metadata is added only for keys that are present on the original configuration.
     * They are added in the following format:
     * <pre>
     *     Given are
     *       1) a configuration with two entries: entry1, entry 2
     *       2) metadata as metaKey1=metaValue1, metaKey2=metaValue2
     *
     * The final configuration will look like:
     *
     *     entry1=entry1Value;
     *     entry2=entry2Value;
     *     [meta:metaKey1]entry1=metaValue1
     *     [meta:metaKey2]entry1=metaValue2
     *     [meta:metaKey1]entry2=metaValue1
     *     [meta:metaKey2]entry2=metaValue2
     * </pre>
     *
     * This mechanism allows to add meta information such as origin, sensitivity, to all keys of a current
     * PropertySource or Configuration. If done on multiple PropertySources that are combined the corresponding
     * values are visible in synch with the values visible.
     *
     * @param propertySource the base propertySource, not null.
     * @param metaData the metaData to be added, not null
     * @return the metadata enriched configuration, not null.
     */
    public static PropertySource addMetaData(PropertySource propertySource, Map<String,String> metaData) {
        return new MetaEnrichedPropertySource(propertySource, metaData);
    }

    /**
     * Calculates the current section key and compares it with the given key.
     *
     * @param key     the fully qualified entry key, not null
     * @param sectionKey the section key, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSection(String key, String sectionKey) {
        int lastIndex = key.lastIndexOf('.');
        String curAreaKey = lastIndex > 0 ? key.substring(0, lastIndex) : "";
        return curAreaKey.equals(sectionKey);
    }

    /**
     * Calculates the current section key and compares it with the given section keys.
     *
     * @param key     the fully qualified entry key, not null
     * @param sectionKeys the section keys, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSections(String key, String... sectionKeys) {
        for(String areaKey:sectionKeys){
            if(isKeyInSection(key, areaKey)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return a query to evaluate the set with all fully qualifies section names. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @return s set with all sections, never {@code null}.
     */
    public static Set<String> sections(Map<String, String> properties) {
            final Set<String> areas = new HashSet<>();
            for(String s: properties.keySet()) {
                int index = s.lastIndexOf('.');
                if (index > 0) {
                    areas.add(s.substring(0, index));
                } else {
                    areas.add("<root>");
                }
            }
            return areas;
    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the sections as accurate
     * as possible, but may not provide a complete set of sections that are finally accessible, especially when the
     * underlying storage does not support key iteration.
     *
     * @return s set with all transitive sections, never {@code null}.
     */
    public static Set<String> transitiveSections(Map<String, String> properties) {
            final Set<String> transitiveAreas = new HashSet<>();
            for(String s: sections(properties)) {
                int index = s.lastIndexOf('.');
                if (index < 0) {
                    transitiveAreas.add("<root>");
                } else {
                    while (index > 0) {
                        s = s.substring(0, index);
                        transitiveAreas.add(s);
                        index = s.lastIndexOf('.');
                    }
                }
            }
            return transitiveAreas;
    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing only the
     * sections that match the predicate and have properties attached. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which sections should be returned, not {@code null}.
     * @return s set with all sections, never {@code null}.
     */
    public static Set<String> sections(Map<String, String> properties, final Predicate<String> predicate) {
        Set<String> treeSet = new TreeSet<>();
        for(String area: sections(properties)){
            if(predicate.test(area)){
                treeSet.add(area);
            }
        }
        return treeSet;
    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which sections should be returned, not {@code null}.
     * @return s set with all transitive sections, never {@code null}.
     */
    public static Set<String> transitiveSections(Map<String, String> properties, Predicate<String> predicate) {
        Set<String> treeSet = new TreeSet<>();
        for(String area: transitiveSections(properties)){
            if(predicate.test(area)){
                treeSet.add(area);
            }
        }
        return treeSet;
    }


    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (recursive). Hereby
     * the section key is stripped away fromMap the resulting key.
     *
     * @param sectionKeys the section keys, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static Map<String,String> sectionsRecursive(Map<String, String> properties, String... sectionKeys) {
        return sectionRecursive(properties, true, sectionKeys);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (recursive).
     *
     * @param sectionKeys   the section keys, not null
     * @param stripKeys if set to true, the section key is stripped away fromMap the resulting key.
     * @return the section configuration, with the areaKey stripped away.
     */
    public static Map<String,String> sectionRecursive(Map<String, String> properties, boolean stripKeys, String... sectionKeys) {
        Map<String,String> result = new HashMap<>(properties.size());
        if(stripKeys) {
            for(Map.Entry<String,String> en: properties.entrySet()){
                if(isKeyInSections(en.getKey(), sectionKeys)){
                    result.put(en.getKey(), en.getValue());
                }
            }
        } else {
            for(Map.Entry<String,String> en: properties.entrySet()){
                if(isKeyInSections(en.getKey(), sectionKeys)){
                    result.put(stripSectionKeys(en.getKey(), sectionKeys), en.getValue());
                }
            }
        }
        return result;
    }

    /**
     * Strips the section key of the given absolute key, if it is one of the areaKeys passed.
     * @param key the current key, not null.
     * @param areaKeys the areaKeys, not null.
     * @return the stripped key, or the original key (if no section was matching).
     */
    static String stripSectionKeys(String key, String... areaKeys) {
        for(String areaKey:areaKeys) {
            if(key.startsWith(areaKey+'.')) {
                return key.substring(areaKey.length() + 1);
            }
        }
        return key;
    }


}
