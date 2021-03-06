package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Teleop")

public class Teleop extends OpMode
{
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

    private double CLAW_OPEN;
    private double CLAW_CLOSED;
    private double ELEVATOR_POWER;

    private final double SLIDE_CLOSED = 0.6;
    private final double SLIDE_DROP = 0.2;

    boolean aOldPressed = false;
    boolean oldclawState;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        armBase = hardwareMap.get(DcMotor.class, "arm_base");
        elevator = hardwareMap.get(DcMotor.class, "elevator");
        armExtend = hardwareMap.get(DcMotor.class, "arm_extend");
        clawL = hardwareMap.get(Servo.class, "clawL");
        clawR = hardwareMap.get(Servo.class, "clawR");
        slide = hardwareMap.get(Servo.class, "slide");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        armBase.setDirection(DcMotor.Direction.FORWARD);
        elevator.setDirection(DcMotor.Direction.FORWARD);

        clawL.setDirection(Servo.Direction.FORWARD);
        clawR.setDirection(Servo.Direction.REVERSE);
    
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
        
        CLAW_OPEN = 0.25;
        CLAW_CLOSED = 0.4;
        
        ELEVATOR_POWER = 1;
        
        // Init claw position
 //       clawL.setPosition(CLAW_OPEN);
 //       clawR.setPosition(CLAW_OPEN);
        
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
     
    @Override
    public void start() {
        runtime.reset();



    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;
        double armPower;
        double elevatorPower;
        double armExtendPower;

        if (gamepad2.a) {
            if (!aOldPressed) {

            oldclawState = !oldclawState;
            }

            aOldPressed = true;
        } else {
            aOldPressed = false;
        }

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double drive = -gamepad1.left_stick_y;
        double turn  = gamepad1.right_stick_x;
        
        if (gamepad1.right_bumper || gamepad1.left_bumper) {
            
            leftPower    = Math.pow(Range.clip(drive + turn, -1.0, 1.0), 3) * 0.5;
            rightPower   = Math.pow(Range.clip(drive - turn, -1.0, 1.0), 3) * 0.5;
            
        } else {
        
            leftPower    = Math.pow(Range.clip(drive + turn, -1.0, 1.0), 3);
            rightPower   = Math.pow(Range.clip(drive - turn, -1.0, 1.0), 3);
        }
        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y;
        // rightPower = -gamepad1.right_stick_y;

        armPower = Math.pow(gamepad2.left_stick_y, 3);
        armExtendPower = Math.pow(gamepad2.right_stick_x, 3);

        // Set claw position
        if (oldclawState) {
            
            clawL.setPosition(CLAW_OPEN);
            clawR.setPosition(CLAW_OPEN);             
        } else {
  
            clawL.setPosition(CLAW_CLOSED);
            clawR.setPosition(CLAW_CLOSED);
        }

        if (gamepad1.x) {

            slideClose();
        } else if (gamepad1.y) {

            slideDrop();
        }

        elevatorPower = 0;
        if (gamepad1.dpad_up) {
         
            elevatorPower = -ELEVATOR_POWER;
        } else if (gamepad1.dpad_down) {
            
            elevatorPower = ELEVATOR_POWER;
        }
     
        // Send calculated power to motors
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
        armBase.setPower(armPower);
        elevator.setPower(elevatorPower);
        armExtend.setPower(armExtendPower);
        
        // Show the elapsed game time and wheel power.
        telemetry.addData("STATUS", "Run Time: " + runtime.toString());
    }

    public void slideClose () {
        slide.setPosition(SLIDE_CLOSED);
        slide.setPosition(SLIDE_CLOSED);

    }
    public void slideDrop () {
        slide.setPosition(SLIDE_DROP);
        slide.setPosition(SLIDE_DROP);

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
