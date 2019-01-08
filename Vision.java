package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guinea on 10/5/17.
 * -------------------------------------------------------------------------------------
 * Copyright (c) 2018 FTC Team 5484 Enderbots
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * 
 * By downloading, copying, installing or using the software you agree to this license.
 * If you do not agree to this license, do not download, install,
 * copy or use the software.
 * -------------------------------------------------------------------------------------
 * This is a sample opmode that demonstrates the use of an OpenCVPipeline with FTC code.
 * When the x button is pressed on controller one, the camera is set to show areas of the image
 * where a certain color is, in this case, blue.
 *
 * Additionally, the centers of the bounding rectangles of the contours are sent to telemetry.
 */
@Disabled
public class Vision extends OpMode {

    private GLaDOS GLaDOS;
    public String pos;


    @Override
    public void init() {
        GLaDOS = new GLaDOS();
        // can replace with ActivityViewDisplay.getInstance() for fullscreen
        GLaDOS.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), 1);


        GLaDOS.setShowContours(true);
        // start the vision system
        GLaDOS.enable();

    }

    @Override
    public void loop() {

        List<MatOfPoint> goldContours = GLaDOS.getContoursGold();
        List<MatOfPoint> silverContours = GLaDOS.getContoursSilver();


        telemetry.addData("Gold Contours:", goldContours.size());
        telemetry.addData("Silver Contours:", silverContours.size());

        pos = "UNKNOWN";

        for (int i = 0; i < goldContours.size(); i++) {

            Rect rect = Imgproc.boundingRect(goldContours.get(i));
            telemetry.addData("Gold:", rect.y);

            if (rect.y < 150) {
                pos = "LEFT";
            } else if (rect.y < 450) {
                pos = "CENTER";
            } else {
                pos = "RIGHT";
            }
        }
        telemetry.addData("Position:", pos);
    }

    public void stop() {
        // stop the vision system
        GLaDOS.disable();
    }

    MatOfPoint lowestContour(List<MatOfPoint> contours) {

        if (contours.size() > 0) {

            double low = 999999;
            int lowIndex = 0;

            for (int i = 0; i < contours.size(); i++) {

                Rect boundingRect = Imgproc.boundingRect(contours.get(i));
                if (boundingRect.x < low) {

                    lowIndex = i;
                    low = boundingRect.x;
                }

            }
            return contours.get(lowIndex);
        } else {

            telemetry.addLine("RETURNED NULL");
            return null;
        }
    }

    private List<MatOfPoint> lowestContours(List<MatOfPoint> contours) {

        if (contours.size() > 1) {

            List<MatOfPoint> output = new ArrayList<MatOfPoint>();

            output.add(lowestContour(contours));
            contours.remove(lowestContour(contours));
            output.add(lowestContour(contours));

            return output;
        } else if (contours.size() == 1) {

            return contours;
        } else {

            return null;
        }
    }
}