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
package edu.csun.ecs.cs.multitouchj.ui.graphic.image;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import edu.csun.ecs.cs.multitouchj.utility.FileUtility;

/**
 * @author Atsuya Takagi
 * 
 * $Id: ImageHandler.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public abstract class ImageHandler {
    public ImageHandler() {
    }

    /**
     * Decode file with given name and returns Texture from the file.
     * 
     * @param String
     *            fileName File name of a file to be decoded.
     * @return Texture Texture.
     */
    public Image decode(String path) {
        File file = new File(path);
        URL url = null;
        try {
            url = file.toURL();
        } catch(Exception exception) {
            exception.printStackTrace();
            return null;
        }

        return decode(url);
    }

    public Image decode(URL url) {
        File file = new File(url.getPath());
        String fileExtension = FileUtility.getExtension(file.getName());
        if(!isSupported(fileExtension)) {
            return null;
        }

        Image image = null;
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            image = decode(inputStream, fileExtension);
        } catch(Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch(Exception exception) {
            }
        }

        return image;
    }

    public abstract Image decode(InputStream inputStream, String fileExtension);

    /**
     * Get supported image formats.
     */
    public abstract String[] getSupportedFormats();

    public abstract boolean isSupported(String fileExtension);
}
