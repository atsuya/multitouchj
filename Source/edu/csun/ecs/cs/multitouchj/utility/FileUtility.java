/**
 * Copyright 2009 Atsuya Takagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package edu.csun.ecs.cs.multitouchj.utility;

/**
 * @author Atsuya Takagi
 * 
 * $Id: FileUtility.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public class FileUtility {
    protected FileUtility() {
    }

    public static String getExtension(String fileName) {
        int index;

        // get index of where '.' is located from the end.
        index = fileName.lastIndexOf(".");
        if(index == -1) {
            return "";
        }

        // check if fileName ends with '.'.
        if(fileName.endsWith(".")) {
            return "";
        }

        return fileName.substring((index + 1));
    }
}
