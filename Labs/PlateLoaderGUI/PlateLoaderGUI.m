function varargout = PlateLoaderGUI(varargin)
% PLATELOADERGUI MATLAB code for PlateLoaderGUI.fig
%      PLATELOADERGUI, by itself, creates a new PLATELOADERGUI or raises the existing
%      singleton*.
%
%      H = PLATELOADERGUI returns the handle to a new PLATELOADERGUI or the handle to
%      the existing singleton*.
%
%      PLATELOADERGUI('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in PLATELOADERGUI.M with the given input arguments.
%
%      PLATELOADERGUI('Property','Value',...) creates a new PLATELOADERGUI or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before PlateLoaderGUI_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to PlateLoaderGUI_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help PlateLoaderGUI

% Last Modified by GUIDE v2.5 18-Mar-2016 11:00:08

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @PlateLoaderGUI_OpeningFcn, ...
                   'gui_OutputFcn',  @PlateLoaderGUI_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before PlateLoaderGUI is made visible.
function PlateLoaderGUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to PlateLoaderGUI (see VARARGIN)

% Choose default command line output for PlateLoaderGUI
handles.output = hObject;

%todo: adjust position of these to be in the correct locations.
handles.user.backgroundImage = addImageToAxes('robot_background.jpg', handles.axes_background, 550);
handles.user.gripperImage = addImageToAxes('gripper_closed_no_plate.jpg', handles.axes_gripper, 100);
handles.user.plateImage = addImageToAxes('plate_only.jpg', handles.axes_plate, 100);
handles.user.extendImage = addImageToAxes('extended_bars.jpg', handles.axes_extended_bars,25);

handles.pushbutton_pos_1.Enable = 'off';
handles.pushbutton_pos_2.Enable = 'off';
handles.pushbutton_pos_3.Enable = 'off';
handles.pushbutton_pos_4.Enable = 'off';
handles.pushbutton_pos_5.Enable = 'off';
handles.pushbutton_gripper_extend.Enable = 'off';
handles.pushbutton_gripper_retract.Enable = 'off';
handles.pushbutton_gripper_open.Enable = 'off';
handles.pushbutton_gripper_close.Enable = 'off';
% Update handles structure
guidata(hObject, handles);

% UIWAIT makes PlateLoaderGUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = PlateLoaderGUI_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;

function updateDisplay(handles)
%Todo: using handles.user.robot.xAxisPosition, ".isZAxisExtended,
%".isGripperClosed, ".isPlatePresent, update/move the axes around to show
%iths. 

% --- Executes on button press in pushbutton_pos_1.
function pushbutton_pos_1_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.x(1);
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_pos2.
function pushbutton_pos2_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.x(2);
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_pos_3.
function pushbutton_pos_3_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.x(3);
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_pos_4.
function pushbutton_pos_4_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.x(4);
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_pos_5.
function pushbutton_pos_5_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.x(5);
updateDisplay(handles);

guidata(hObject, handles);


% --- Executes on button press in pushbutton_timing_default.
function pushbutton_timing_default_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.resetDefaultTimes;
% TODO: clear that timing table on the GUI
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_timing_current.
function pushbutton_timing_current_Callback(hObject, eventdata, handles)
%TODO: Grab data from table and pass into
%handles.user.robot.setTimeValues(values)

% --- Executes on button press in pushbutton_gripper_extend.
function pushbutton_gripper_extend_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.extend;
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_gripper_retract.
function pushbutton_gripper_retract_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.retract;
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_gripper_open.
function pushbutton_gripper_open_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.open;
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in pushbutton_gripper_close.
function pushbutton_gripper_close_Callback(hObject, eventdata, handles)
handles.text_status.String = handles.user.robot.close;
updateDisplay(handles);

guidata(hObject, handles);


function edit_comNumber_Callback(hObject, eventdata, handles)
% hObject    handle to edit_comNumber (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_comNumber as text
%        str2double(get(hObject,'String')) returns contents of edit_comNumber as a double

%TODO: Make sure only numbers are in there

% --- Executes during object creation, after setting all properties.
function edit_comNumber_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_comNumber (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton_connect.
function pushbutton_connect_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_connect (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
num = str2double(handles.edit_comNumber.String);
if num > 0 && num < 50
    handles.user.robot = PlateLoader(str2double(handles.edit_comNumber.String));
    handles.text_status.String = handles.user.robot.getStatus;
    hObject.Enable = 'off';
    handles.pushbutton_disconnect.Enable = 'on';
    handles.pushbutton_reset.Enable = 'on';
    
    handles.pushbutton_pos_1.Enable = 'on';
    handles.pushbutton_pos_2.Enable = 'on';
    handles.pushbutton_pos_3.Enable = 'on';
    handles.pushbutton_pos_4.Enable = 'on';
    handles.pushbutton_pos_5.Enable = 'on';
    handles.pushbutton_gripper_extend.Enable = 'on';
    handles.pushbutton_gripper_retract.Enable = 'on';
    handles.pushbutton_gripper_open.Enable = 'on';
    handles.pushbutton_gripper_close.Enable = 'on';
end
guidata(hObject, handles);


% --- Executes on button press in pushbutton_disconnect.
function pushbutton_disconnect_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_disconnect (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
handles.text_status.String = handles.user.robot.shutdown;
handles.pushbutton_disconnect.Enable = 'off';
handles.pushbutton_reset.Enable = 'off';
guidata(hObject, handles);


% --- Executes on button press in pushbutton_reset.
function pushbutton_reset_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_reset (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
handles.text_status.String = handles.user.robot.reset;
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on button press in checkbox_simulator.
function checkbox_simulator_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_simulator (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_simulator


% --- Executes on button press in pushbutton_special_a.
function pushbutton_special_a_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_special_a (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton_special_B.
function pushbutton_special_B_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_special_B (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton_getStatus.
function pushbutton_getStatus_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_getStatus (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
handles.text_status.String = handles.user.robot.getStatus;
updateDisplay(handles);

guidata(hObject, handles);

% --- Executes on selection change in popupmenu_fromPos.
function popupmenu_fromPos_Callback(hObject, eventdata, handles)
% hObject    handle to popupmenu_fromPos (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu_fromPos contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu_fromPos


% --- Executes during object creation, after setting all properties.
function popupmenu_fromPos_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popupmenu_fromPos (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in popupmenu_toPos.
function popupmenu_toPos_Callback(hObject, eventdata, handles)
% hObject    handle to popupmenu_toPos (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popupmenu_toPos contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popupmenu_toPos


% --- Executes during object creation, after setting all properties.
function popupmenu_toPos_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popupmenu_toPos (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton_movePlate.
function pushbutton_movePlate_Callback(hObject, eventdata, handles)
n1 = str2double(handles.popupmenu_fromPos);
n2 = str2double(handles.popupmenu_toPos);
if n1 == n2
    handles.text_status.String = 'Please make a different position selection';
else
    handles.text_status.String = handles.user.robot.movePlate(n1, n2);
end
updateDisplay(handles);
guidata(hObject, handles);
