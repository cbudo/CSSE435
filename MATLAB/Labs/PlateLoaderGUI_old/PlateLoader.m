classdef PlateLoader < handle
    %PLATELOADER class is a wrapper object for interacting with the
    %Robotics Lab Plate loader. 
    
    properties
        serialRobot
        xAxisPosition
        isZAxisExtended
        isGripperClosed
        isPlatePresent 
    end
    
    methods
        %% Constructs the PlateLoader on a port, and sets standard flags to their defaults. 
        function robot = PlateLoader(port)
            robot.connect(port);
            robot.xAxisPosition = 3;
            robot.isZAxisExtended = 0;
            robot.isGripperClosed = 1;
            robot.isPlatePresent = 0;
        end
        %% Connects to the specified port after closing all open connections.
        function connect(robot, num)
            %close all open conns
            open_ports = instrfind('Type', 'serial', 'Status', 'open');

            if ~isempty(open_ports)
                fclose(open_ports);
            end
            com = sprintf('COM%d',num);
            fprintf('Connecting to robot\n');
            robot.serialRobot = serial(com, 'BaudRate', 19200, 'Terminator', 10, 'Timeout', 5);
            fprintf(robot.getResponse());
            fprintf('Opening..\n');
            fopen(robot.serialRobot);
            fprintf(robot.getResponse());
            fprintf(robot.serialRobot, 'INITIALIZE');
            fprintf('READY\n');
        end
        %% Clean way of retrieving Serial data from the buffer
        function response = getResponse(robot)
            s = robot.serialRobot;
            warning off all
            response = char(fread(s, 1));
            while((isempty(response))||(~strcmp(response(end), char(get(s,'Terminator')))))
                response = [response, char,(fread(s,1))];
            end
            warning on all
        end
        %% Resets/Initializes the plate loader, and returns a response
        function response = reset(robot)
            fprintf(robot.serialRobot,'RESET');
            response = robot.getResponse();
        end
        %% Moves to x-position, returns a response
        function response = x(robot, pos)
            s = robot.serialRobot;
            switch pos
                case 1
                    fprintf(s,'X-AXIS 1\n');
                case 2
                    fprintf(s,'X-AXIS 2\n');
                case 3
                    fprintf(s,'X-AXIS 3\n');
                case 4
                    fprintf(s,'X-AXIS 4\n');
                otherwise
                    fprintf(s,'X-AXIS 5\n');
            end
            response = robot.getResponse();
        end
        %% Extends the arm, and returns a response
        function response = extend(robot)            
            fprintf(robot.serialRobot,'Z-AXIS EXTEND');
            response = robot.getResponse();
        end
        %% Retracts the arm, and returns a response
        function response = retract(robot)
            fprintf(robot.serialRobot,'Z-AXIS RETRACT');
            response = robot.getResponse();
        end
        %% Opens the gripper, and returns a response
        function response = open(robot)
            fprintf(robot.serialRobot,'GRIPPER OPEN');
            response = robot.getResponse();
        end
        %% Close the gripper, and returns a response
        function response = close(robot)
            fprintf(robot.serialRobot,'GRIPPER CLOSE');
            response = robot.getResponse();
        end
        %% Moves the plate and returns the response. 
        function response = movePlate(robot, startPos, endPos)
            moveCmd = sprintf('MOVE %c %c\n',char(startPos), char(endPos));
            fprintf(robot.serialRobot, moveCmd);
            response = robot.getResponse();
        end
        %% TODO: Sets all the time values, prints individual responses to the screen, and returns last response
        function response = setTimeValues(robot, timeDelays)
        end
        
        %% TODO: Resets all time values to the default time values
        function resetDefaultTimes(robot)
        end
        %% Calls the LOADER_STATUS and prints values to the screen
        function getStatus(robot)
            fprintf(robot.serialRobot,'LOADER_STATUS');
            fprintf(robot.getResponse());
        end
        %% TODO: Returns the status properties of the robot for display purposes
        function getProperties(robot)
        end
        %% Closes the COM port
        function shutdown(robot)
            robot.reset();
            fclose(robot.serialRobot);
        end
                
    end
    
end

