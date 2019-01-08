package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.corningrobotics.enderbots.endercv.CameraViewDisplay;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;

@Autonomous
public class Auto_Depot extends LinearOpMode {
    
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor armBase = null;
    private DcMotor elevator = null;
    private DcMotor armExtend = null;

    private Servo clawL = null;
    private Servo clawR = null;
    private Servo slide = null;


    // Encoder variables
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 6.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     WHEEL_BASE_DISTANCE     = 16.225;
    static final double     DRIVE_SPEED             = 0.3;
    static final double     TURN_SPEED              = 0.2;
    static final double     ELEVATOR_SPEED          = 0.4;
    static final double     ARM_SPEED          = 0.4;

    private final double CLAW_OPEN = 0.25;
    private final double CLAW_CLOSED = 0.4;

    private final double SLIDE_CLOSED = 0.55;
    private final double SLIDE_DROP = 0.3;

    private GLaDOS GLaDOS;
    private String pos;

    @Override
    public void runOpMode() {

        GLaDOS = new GLaDOS();
        // can replace with ActivityViewDisplay.getInstance() for fullscreen
        GLaDOS.init(hardwareMap.appContext, CameraViewDisplay.getInstance());


        GLaDOS.setShowContours(true);
        // start the vision system
        GLaDOS.enable();

        // Initialize the hardware variables
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        
        armBase = hardwareMap.get(DcMotor.class, "arm_base");
        elevator = hardwareMap.get(DcMotor.class, "elevator");
        armExtend = hardwareMap.get(DcMotor.class, "arm_extend");


        clawL = hardwareMap.get(Servo.class, "clawL");
        clawR = hardwareMap.get(Servo.class, "clawR");
        slide = hardwareMap.get(Servo.class, "slide");


        clawL.setDirection(Servo.Direction.FORWARD);
        clawR.setDirection(Servo.Direction.REVERSE);
        slide.setDirection(Servo.Direction.FORWARD);
        
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        armBase.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
         // Initialize motor direction
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        armBase.setDirection(DcMotor.Direction.FORWARD);
        elevator.setDirection(DcMotor.Direction.FORWARD);
        
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        armBase.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        // Initialize servo position
        // claw.setPosition(0.0);
        
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        elevator.setPower(0.2);
        
        closeClaw();
        slideClose();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        
        elevator.setPower(0.0);
        
        elevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        raiseElevator();

        drive(DRIVE_SPEED, -6.0, -6.0, 3.0);

        double angle = 30;
        double dist = 27;

        /*rotate(-95 + angle);
        if (!findGold().equals("UNKNOWN")) {

            //drive(DRIVE_SPEED, -dist, -dist, 3.0);
            //drive(DRIVE_SPEED, dist, dist, 3.0);
            //rotate(-angle);

            drive(DRIVE_SPEED, -42, -42, 10);
            rotate(-60);
            drive(DRIVE_SPEED, -16, -16, 10);
            rotate(-15);
            drive(DRIVE_SPEED, 58, 58, 15);
            rotate(-20);

            armDrive(ARM_SPEED, -15, 20);
            armBase.setPower(-0.015);
            extendDrive(ARM_SPEED, 8, 8);

        } else {

            rotate(-angle);
            if (!findGold().equals("UNKNOWN")) {

                drive(DRIVE_SPEED, -dist, -dist, 3.0);
                drive(DRIVE_SPEED, dist, dist, 3.0);
            } else {

                rotate(-angle);
                if (!findGold().equals("UNKNOWN")) {

                    drive(DRIVE_SPEED, -dist, -dist, 3.0);
                    drive(DRIVE_SPEED, dist, dist, 3.0);
                }
                rotate(angle);
            }
        }

        GLaDOS.disable();

        drive(DRIVE_SPEED, -13.5, -13.5, 3.0);
        rotate(-95);
        drive(DRIVE_SPEED, 47, 47, 20.0);
        rotate(20);
        drive(DRIVE_SPEED, 6, 6, 5.0);

        armDrive(ARM_SPEED, -15, 20);
        armBase.setPower(-0.015);
        extendDrive(ARM_SPEED, 8, 8);
        */

        rotate(-90);
        drive(DRIVE_SPEED, -46, -46, 10.0);
        slideDrop();

        drive(DRIVE_SPEED, 36, 36, 10.0);


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }

    public void extendDrive(double speed, double inches, double timeoutS) {

        int newTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newTarget = armExtend.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
            armExtend.setTargetPosition(newTarget);

            // Turn On RUN_TO_POSITION
            armExtend.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            armExtend.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (armExtend.isBusy())) {

            }

            // Stop all motion;
            armExtend.setPower(0);

            // Turn off RUN_TO_POSITION
            armExtend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move
        }
    }


    public void drive(double speed, double leftInches, double rightInches, double timeoutS) {
        
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            leftDrive.setTargetPosition(newLeftTarget);
            rightDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftDrive.setPower(Math.abs(speed));
            rightDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (leftDrive.isBusy() && rightDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                                            leftDrive.getCurrentPosition(),
                                            rightDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftDrive.setPower(0);
            rightDrive.setPower(0);

            // Turn off RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //sleep(250);   // optional pause after each move
        }
    }
   
    public void elevatorDrive(double speed, double inches, double timeoutS) {
        
        int newTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newTarget = elevator.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
            elevator.setTargetPosition(newTarget);

            // Turn On RUN_TO_POSITION
            elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            elevator.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (elevator.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d", newTarget);
                telemetry.addData("Path2",  "Running at %7d",
                                            elevator.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            elevator.setPower(0);

            // Turn off RUN_TO_POSITION
            elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move
        }
    }

    public void armDrive(double speed, double inches, double timeoutS) {
        
        int newTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newTarget = armBase.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH);
            armBase.setTargetPosition(newTarget);

            // Turn On RUN_TO_POSITION
            armBase.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            armBase.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (armBase.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d", newTarget);
                telemetry.addData("Path2",  "Running at %7d",
                                            armBase.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            armBase.setPower(0);

            // Turn off RUN_TO_POSITION
            armBase.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move
        }
    }
 
    void lowerElevator() {
        
        elevatorDrive(ELEVATOR_SPEED, 20, 5.0);
    }
    
    void raiseElevator () {
        
        elevatorDrive(ELEVATOR_SPEED, -20, 5.0);
    }

    void openClaw () {
        clawL.setPosition(CLAW_OPEN);
        clawR.setPosition(CLAW_OPEN);
        
    }
    void closeClaw () {
        clawL.setPosition(CLAW_CLOSED);
        clawR.setPosition(CLAW_CLOSED);
        
    }
    
    void rotate (double angle) {
        
        double circ = WHEEL_BASE_DISTANCE * 3.1415;
        double dist = (angle / 365) * circ;
        
        drive(TURN_SPEED, dist, -dist, 9999.0);
    }

    public void slideClose () {
        slide.setPosition(SLIDE_CLOSED);
        slide.setPosition(SLIDE_CLOSED);

    }
    public void slideDrop () {
        slide.setPosition(SLIDE_DROP);
        slide.setPosition(SLIDE_DROP);

    }
    private String findGold() {
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

        return pos;
    }
}

// #beingProductive

