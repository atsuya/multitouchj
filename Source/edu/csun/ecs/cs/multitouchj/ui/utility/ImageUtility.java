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
package edu.csun.ecs.cs.multitouchj.ui.utility;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

/**
 * @author Atsuya Takagi
 * 
 * $Id: ImageUtility.java 57 2009-02-10 08:02:38Z Atsuya Takagi $
 */
public class ImageUtility {
    protected ImageUtility() {
    }

    public static void saveImage(BufferedImage bufferedImage) {
        saveImage(bufferedImage, "");
    }

    public static void saveImage(BufferedImage bufferedImage, String fileName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String file = fileName + "-" + sdf.format(new Date()) + ".png";

        saveImage(bufferedImage, "png", new File(file));
    }

    public static void saveImage(BufferedImage bufferedImage, String format,
            File file) {
        try {
            ImageIO.write(bufferedImage, format, file);
        } catch(Exception exception) {
            exception.toString();
        }
    }

    public static BufferedImage rotateBufferedImage(
            BufferedImage bufferedImage, double angle) {
        BufferedImage bufferedImageFixed = new BufferedImage(bufferedImage
                .getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        AffineTransform at = new AffineTransform();
        at.rotate(angle, (bufferedImage.getWidth() / 2.0), (bufferedImage
                .getHeight() / 2.0));

        AffineTransformOp ato = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BILINEAR);
        ato.filter(bufferedImage, bufferedImageFixed);

        return bufferedImageFixed;
    }

    public static BufferedImage flipBufferedImageHorizontally(
            BufferedImage bufferedImage) {
        BufferedImage bufferedImageFixed = new BufferedImage(bufferedImage
                .getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
        at.translate(-bufferedImage.getWidth(null), 0);

        AffineTransformOp ato = new AffineTransformOp(at,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        ato.filter(bufferedImage, bufferedImageFixed);

        return bufferedImageFixed;
    }

    public static BufferedImage flipBufferedImageVertically(
            BufferedImage bufferedImage) {
        BufferedImage bufferedImageFixed = new BufferedImage(bufferedImage
                .getWidth(), bufferedImage.getHeight(), bufferedImage.getType());

        AffineTransform at = AffineTransform.getScaleInstance(1, -1);
        at.translate(0, -bufferedImage.getHeight(null));

        AffineTransformOp ato = new AffineTransformOp(at,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        ato.filter(bufferedImage, bufferedImageFixed);

        return bufferedImageFixed;
    }
}
