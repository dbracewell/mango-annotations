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
import com.davidbracewell.io.QuietIO;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
@SupportedAnnotationTypes("com.davidbracewell.annotation.Preload")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@MetaInfServices(Processor.class)
public class PreloadProcessor extends AbstractProcessor {

  private Set<String> classNames = new HashSet<>();

  public PreloadProcessor() {
    super();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      FileObject f;
      PrintWriter pw;
      OutputStreamWriter os = null;
      try {
        f = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/preload.ini");
        os = new OutputStreamWriter(f.openOutputStream(), StandardCharsets.UTF_8);
        pw = new PrintWriter(os);
        classNames.forEach(pw::println);
      } catch (IOException e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
      } finally {
        QuietIO.closeQuietly(os);
      }
      return false;
    }


    roundEnv
      .getElementsAnnotatedWith(Preload.class)
      .stream()
      .filter(e -> e.getKind() == ElementKind.CLASS || e.getKind() == ElementKind.INTERFACE)
      .forEach(e -> {
        TypeElement classElement = Cast.as(e);
        classNames.add(classElement.getQualifiedName().toString());
      });


    return false;
  }

}//END OF DynamicEnumProcessor
