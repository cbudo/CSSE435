classdef PlateLoader < handle
    %PLATELOADER Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        serialRobot
        xAxisPosition
        isZAxisExtended
        isGripperClosed
        isPlatePresent 
    end
    
    methods
        function robot = PlateLoader(port)
            robot.connect(port);
            robot.xAxisPosition = 3;
            robot.isZAxisExtended = 0;
            robot.isGripperClosed = 1;
            robot.isPlatePresent = 0;
        end

        function connect(robot, num)
            %close all open conns
            open_ports = instrfind('Type', 'serial', 'Status', 'open');

            if ~isempty(open_ports)
                fclose(open_ports);
            end
            com = sprintf('COM%d',num);
            fprintf('Connecting to robot\n');
            robot.serialRobot = serial(com, 'BaudRate', 19200, 'Terminator', 10, 'Timeout', 5);
            fprintf('Status is %s\n', s.Status);
            fprintf('Opening..\n');
            fopen(robot.serialRobot);
            fprintf('Status is %s\n', s.Status);
            fprintf(robot.serialRobot, 'INITIALIZE');
            fprintf('READY\n');
        end
        
        function response = getResponse(robot)
            s = robot.serialRobot;
            warning off all
            response = char(fread(s, 1));
            while((isempty(response))||(~strcmp(response(end), char(get(s,'Terminator')))))
                response = [response, char,(fread(s,1))];
            end
            warning on all
        end
        
        function reset(robot)
            fprintf(robot.serialRobot,'RESET');
            fprintf(robot.getResponse());
        end
        
        function x(robot, pos)
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
            fprintf(robot.getResponse());
        end
        
        function extend(robot)            
            fprintf(robot.serialRobot,'Z-AXIS EXTEND');
            fprintf(robot.getResponse());
        end
        
        function retract(robot)
            fprintf(robot.serialRobot,'Z-AXIS RETRACT');
            fprintf(robot.getResponse());
        end
        
        function open(robot)
            fprintf(robot.serialRobot,'GRIPPER OPEN');
            fprintf(robot.getResponse());
        end
        
        function close(robot)
            fprintf(robot.serialRobot,'GRIPPER CLOSE');
            fprintf(robot.getResponse());
        end
        
        function movePlate(robot, startPos, endPos)
            moveCmd = sprintf('MOVE %c %c\n',char(startPos), char(endPos));
            fprintf(robot.serialRobot, moveCmd);
            fprintf(robot.getResponse());
        end
        
        function setTimeValues(robot, timeDelays)
        end
        
        function resetDefaultTimes(robot)
        end
        
        function getStatus(robot)
            fprintf(robot.serialRobot,'LOADER_STATUS');
            fprintf(robot.getResponse());
        end
        
        function getProperties(robot)
        end
        
        function shutdown(robot)
            robot.reset();
            fclose(robot.serialRobot);
        end
                
    end
    
end

