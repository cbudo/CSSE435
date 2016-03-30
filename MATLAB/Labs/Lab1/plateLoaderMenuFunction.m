function plateLoaderMenuFunction( num )
%UNTITLED6 Summary of this function goes here
%   Detailed explanation goes here
    s = open(num);
    while(1)
        choice = menu('Choose command','Reset','X-Axis','Z-Axis','Gripper','Move','Status','Special Moves','Exit');
        switch choice
            case 1
                reset(s)
            case 2
                x_axis(s)
            case 3
                z_axis(s)
            case 4
                gripper(s)
            case 5
                move(s)
            case 6
                status(s)
            case 7
                special(s)
            otherwise
                break;
        end
    end
end

function s = open(num)
    %close all open conns
    open_ports = instrfind('Type', 'serial', 'Status', 'open');

    if ~isempty(open_ports)
        fclose(open_ports);
    end
    com = sprintf('COM%d',num);
    fprintf('Connecting to robot\n');
    s = serial(com, 'BaudRate', 19200, 'Terminator', 10, 'Timeout', 5);
    fprintf('Status is %s\n', s.Status);
    fprintf('Opening..\n');
    fopen(s);
    fprintf('Status is %s\n', s.Status);
    fprintf(s, 'INITIALIZE');
    fprintf('READY\n');
end

function reset(s)
    fprintf(s,'RESET');
    fprintf(getResponse(s));
end

function x_axis(s)
    choice = menu('Choose location to move to','1','2','3','4','5');
    switch choice
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
    fprintf(getResponse(s));
end

function z_axis(s)
    choice = menu('Z-AXIS:','EXTEND','RETRACT');
    if choice == 1
        fprintf(s,'Z-AXIS EXTEND');
    else
        fprintf(s,'Z-AXIS RETRACT');
    end
    fprintf(getResponse(s));
end

function gripper(s)
    choice = menu('Gripper:','Open','Close');
    if choice == 1
        fprintf(s, 'GRIPPER OPEN');
    else
        fprintf(s, 'GRIPPER CLOSE');
    end
    fprintf(getResponse(s));
end

function move(s)
    prompt = {'Location to pick up from:','Location to move to:'};
    dlg_title = 'Move menu';
    num_lines = 1;
    def = {'1','2'};
    answer = inputdlg(prompt,dlg_title,num_lines,def);
    moveCmd = sprintf('MOVE %c %c\n',char(answer(1)), char(answer(2)));
    fprintf(s, moveCmd);
    fprintf(getResponse(s));
end
%% Prints the loader status. 
function status(s)
    fprintf(s,'LOADER_STATUS');
    fprintf(getResponse(s));
end
%% Simply moves from Loc 1 to 5 and back
function special(s)
    fprintf(s,'GRIPPER OPEN\n');
    fprintf(getResponse(s));
    fprintf(s,'X-AXIS 1\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS EXTEND\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER CLOSE\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS RETRACT\n');
    fprintf(getResponse(s));
    fprintf(s,'X-AXIS 5\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS EXTEND\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER OPEN\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS RETRACT\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER CLOSE\n');
    fprintf(getResponse(s));
    fprintf(s,'X-AXIS 4\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER OPEN\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS EXTEND\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER CLOSE\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS RETRACT\n');
    fprintf(getResponse(s));
    fprintf(s,'X-AXIS 2\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS EXTEND\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER OPEN\n');
    fprintf(getResponse(s));
    fprintf(s,'Z-AXIS RETRACT\n');
    fprintf(getResponse(s));
    fprintf(s,'GRIPPER CLOSE\n');
end
