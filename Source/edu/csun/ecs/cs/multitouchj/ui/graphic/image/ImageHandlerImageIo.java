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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import edu.csun.ecs.cs.multitouchj.ui.utility.ImageUtility;

/**
 * @author Atsuya Takagi
 * 
 * $Id: ImageHandlerImageIo.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public class ImageHandlerImageIo extends ImageHandler {
    public ImageHandlerImageIo() {
        super();
    }

    public Image decode(InputStream inputStream, String fileExtension) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch(Exception exception) {
            exception.printStackTrace();
        }

        if(bufferedImage == null) {
            return null;
        }

        // rotate
        BufferedImage alphaBufferedImage = bufferedImage;
        if(alphaBufferedImage.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
            alphaBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = alphaBufferedImage.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, null);
            graphics.dispose();
        }

        BufferedImage resultingBufferedImage = ImageUtility
                .flipBufferedImageVertically(alphaBufferedImage);

        // check types
        /*
         * i don't think type needs to be checked. boolean imageTypeFound =
         * false; int imageTypes[] = { BufferedImage.TYPE_3BYTE_BGR,
         * BufferedImage.TYPE_4BYTE_ABGR, }; for(int imageType : imageTypes) {
         * if(imageType == bufferedImage.getType()) { imageTypeFound = true;
         * break; } }
         * 
         * if(!imageTypeFound) {
         * System.out.println("FIXME: imageType="+bufferedImage.getType());
         * return null; }
         */

        // get data
        /*
         * System.out.println("FIXME: imageType="+resultingBufferedImage.getType(
         * )+", extension="+fileExtension);
         * switch(resultingBufferedImage.getType()) { case
         * BufferedImage.TYPE_3BYTE_BGR: System.out.println("FIXME: 3BYTE_BGR");
         * break; case BufferedImage.TYPE_INT_RGB:
         * System.out.println("FIXME: INT_RGB"); break; case
         * BufferedImage.TYPE_CUSTOM: System.out.println("FIXME: CUSTOM");
         * break; }
         */

        int width = resultingBufferedImage.getWidth();
        int height = resultingBufferedImage.getHeight();
        // byte bytes[] =
        // ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
        byte[] bytes = (byte[]) resultingBufferedImage.getRaster()
                .getDataElements(0, 0, width, height, null);

        /*
         * boolean alpha = false; switch(resultingBufferedImage.getType()) {
         * case BufferedImage.TYPE_4BYTE_ABGR: case
         * BufferedImage.TYPE_4BYTE_ABGR_PRE: case BufferedImage.TYPE_INT_ARGB:
         * case BufferedImage.TYPE_INT_ARGB_PRE: case BufferedImage.TYPE_CUSTOM:
         * System.out.println("FIXME: it is alpha."); alpha = true; break; }
         */

        return new Image(width, height, bytes, true);
    }

    /**
     * Get supported image formats.
     */
    public String[] getSupportedFormats() {
        return ImageIO.getReaderFormatNames();
    }

    public boolean isSupported(String fileExtension) {
        boolean supported = false;
        String[] supportedFormats = ImageIO.getReaderFormatNames();

        System.out.println("Checking format if supported: " + fileExtension);
        System.out.println("Supported format:");

        for(int i = 0; i < supportedFormats.length; i++) {
            if(supportedFormats[i].compareToIgnoreCase(fileExtension) == 0) {
                supported = true;
                break;
            }
        }

        return supported;
    }
}
