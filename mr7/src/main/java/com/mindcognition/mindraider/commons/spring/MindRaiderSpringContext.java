/*
 ===========================================================================
   Copyright 2002-2010 Martin Dvorak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ===========================================================================
*/
package com.mindcognition.mindraider.commons.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring application context for MindRaider.
 */
public class MindRaiderSpringContext {

    /**
     * Spring application context
     */
    private static ApplicationContext applicationContext 
        = new ClassPathXmlApplicationContext("spring/mr-context.xml"); 
      //= new FileSystemXmlApplicationContext("config/spring/spring-testcontext.xml"); 

    public static ApplicationContext getCtx(){ 
        return applicationContext; 
    } 
}
