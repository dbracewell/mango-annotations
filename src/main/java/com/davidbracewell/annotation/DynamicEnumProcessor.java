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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.Resources;
import com.davidbracewell.string.StringPredicates;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Throwables;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
@SupportedAnnotationTypes("com.davidbracewell.annotation.DynamicEnumeration")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@MetaInfServices(Processor.class)
public class DynamicEnumProcessor extends AbstractProcessor {

  public DynamicEnumProcessor() {
    super();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element e : roundEnv.getElementsAnnotatedWith(DynamicEnumeration.class)) {
      if (e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE) {
        TypeElement classElement = Cast.as(e);


        Element parent = classElement.getEnclosingElement();
        while (parent != null && !(parent instanceof PackageElement)) {
          parent = parent.getEnclosingElement();
        }
        if (parent == null) {
          continue;
        }

        PackageElement packageElement = Cast.as(parent);

        try {

          DynamicEnumeration de = e.getAnnotation(DynamicEnumeration.class);

          String cName = classElement.getSimpleName() + "Enum";
          String pName = packageElement.getQualifiedName().toString();
          String implementsClass = StringUtils.EMPTY;
          String javaDoc = StringUtils.EMPTY;
          String rootName = classElement.getSimpleName() + "_ROOT";
          String configPrefix = cName;
          boolean isHierarchical = false;

          if (de != null) {
            if (!StringUtils.isNullOrBlank(de.className())) {
              cName = de.className();
            }

            if (!StringUtils.isNullOrBlank(de.packageName())) {
              pName = de.packageName();
            }

            isHierarchical = de.hierarchical();

            if (!StringUtils.isNullOrBlank(de.configPrefix())) {
              configPrefix = de.configPrefix();
            } else {
              configPrefix = cName;
            }


            if (de.implementsClass() != null) {
              String s = Stream.of(de.implementsClass())
                .filter(StringPredicates.IS_NULL_OR_BLANK.negate())
                .collect(Collectors.joining(","));
              if (!StringUtils.isNullOrBlank(s)) {
                implementsClass = " implements " + s;
              }
            }

            if (!StringUtils.isNullOrBlank(de.javadoc())) {
              javaDoc = de.javadoc();
            }

            if (!StringUtils.isNullOrBlank(de.rootName())) {
              rootName = de.rootName();
            }

          }

          JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(pName + "." + cName);

          try (BufferedWriter bw = new BufferedWriter(fileObject.openWriter())) {
            String classFile;

            if (isHierarchical) {
              classFile = Resources.fromClasspath("com/davidbracewell/hierarchical_enum_value.template").readToString();
            } else {
              classFile = Resources.fromClasspath("com/davidbracewell/enum_value.template").readToString();
            }

            classFile = classFile.replace("$TYPE_NAME", cName)
              .replace("$CONFIG_PREFIX", configPrefix)
              .replace("$IMPLEMENTS", implementsClass)
              .replace("$ROOT_NAME", rootName)
              .replace("$JAVADOC", javaDoc);
            bw.append("package ");
            bw.append(pName);
            bw.append(";");
            bw.newLine();
            bw.newLine();
            bw.append(classFile);
            bw.newLine();
          }

        } catch (IOException ioe) {
          throw Throwables.propagate(ioe);
        }
      }
    }
    return true;
  }

}//END OF DynamicEnumProcessor
