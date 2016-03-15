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
            comstr = sprintf('COM%d',port);
            robot.serialRobot = serial(comstr, 'BaudRate', 19200, 'Terminator', 10, 'Timeout', 5);
            robot.xAxisPosition = 3;
            robot.isZAxisExtended = 0;
            robot.isGripperClosed = 1;
            robot.isPlatePresent = 0;
        end
        
        function reset(robot)
        end
        
        function x(robot, pos)
        end
        
        function extend(robot)
        end
        
        function retract(robot)
        end
        
        function open(robot)
        end
        
        function close(robot)
        end
        
        function movePlate(robot, startPos, endPos)
        end
        
        function setTimeValues(robot, timeDelays)
        end
        
        function resetDefaultTimes(robot)
        end
        
        function getStatus(robot)
        end
        
        function getProperties(robot)
        end
        
        function shutdown(robot)
        end
                
    end
    
end

