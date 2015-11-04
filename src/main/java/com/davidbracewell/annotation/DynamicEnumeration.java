/*
 * (c) 2005 David B. Bracewell
 *
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

package com.davidbracewell.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Dynamic enumeration.
 *
 * @author David B. Bracewell
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface DynamicEnumeration {

  /**
   * The package that the enum will be in
   *
   * @return the package (default is same package of type with the annotation)
   */
  String packageName() default "";

  /**
   * The class name of the enum
   *
   * @return the class name (default will be name of type + Enum, e.g. if annotated on Tags the enum will be TagsEnum)
   */
  String className() default "";

  /**
   * Is this enum hierarchical?
   *
   * @return true if hierarchical, false otherwise
   */
  boolean hierarchical() default false;

  /**
   * The prefix to use when accessing config values
   *
   * @return the config prefix (default will fully qualified class name)
   */
  String configPrefix() default "";

  /**
   * Implements class class.
   *
   * @return the class
   */
  String[] implementsClass() default "";

  /**
   * The Javadoc associated with the class
   *
   * @return the javadoc
   */
  String javadoc() default "";


  /**
   * The name of the root element. (Only used if hierarchical is true)
   *
   * @return The name of the root element
   */
  String rootName() default "";


}//END OF DynamicEnumeration
