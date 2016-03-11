function varargout = gripperControl(varargin)
% GRIPPERCONTROL MATLAB code for gripperControl.fig
%      GRIPPERCONTROL, by itself, creates a new GRIPPERCONTROL or raises the existing
%      singleton*.
%
%      H = GRIPPERCONTROL returns the handle to a new GRIPPERCONTROL or the handle to
%      the existing singleton*.
%
%      GRIPPERCONTROL('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in GRIPPERCONTROL.M with the given input arguments.
%
%      GRIPPERCONTROL('Property','Value',...) creates a new GRIPPERCONTROL or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before gripperControl_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to gripperControl_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help gripperControl

% Last Modified by GUIDE v2.5 11-Mar-2016 00:22:16

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @gripperControl_OpeningFcn, ...
                   'gui_OutputFcn',  @gripperControl_OutputFcn, ...
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


% --- Executes just before gripperControl is made visible.
function gripperControl_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to gripperControl (see VARARGIN)

% Choose default command line output for gripperControl
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes gripperControl wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = gripperControl_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;



function edit_comport_Callback(hObject, eventdata, handles)
% hObject    handle to edit_comport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_comport as text
%        str2double(get(hObject,'String')) returns contents of edit_comport as a double


% --- Executes during object creation, after setting all properties.
function edit_comport_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_comport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton_opencom.
function pushbutton_opencom_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_opencom (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
open_ports = instrfind('Type','serial','Status','open');
if ~isempty(open_ports)
    fclose(open_ports);
end

com_port = get(handles.edit_comport,'String');

fprintf('TODO: open a serial connection to the robot arm on: COM%s.\n',com_port);
guidata(hObject,handles);


% --- Executes on button press in pushbutton_closecom.
function pushbutton_closecom_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_closecom (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
open_ports = instrfind('Type','serial','Status','open');
if ~isempty(open_ports)
    fclose(open_ports);
end


% --- Executes on slider movement.
function slider_jointChange_Callback(hObject, eventdata, handles)
% hObject    handle to slider_joint1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
jointAngles(1) = round(get(handles.slider_joint1,'Value'));
jointAngles(2) = round(get(handles.slider_joint2,'Value'));
jointAngles(3) = round(get(handles.slider_joint3,'Value'));
jointAngles(4) = round(get(handles.slider_joint4,'Value'));
jointAngles(5) = round(get(handles.slider_joint5,'Value'));

jointAnglesStr = sprintf('%d %d %d %d %d',jointAngles);
set(handles.text_anglevalues, 'String', jointAnglesStr);



% --- Executes during object creation, after setting all properties.
function slider_joint1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end



% --- Executes during object creation, after setting all properties.
function slider_joint2_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end



% --- Executes during object creation, after setting all properties.
function slider_joint3_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end


% --- Executes during object creation, after setting all properties.
function slider_joint4_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end

% --- Executes during object creation, after setting all properties.
function slider_joint5_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_joint5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end


% --- Executes on slider movement.
function slider_gripper_Callback(hObject, eventdata, handles)
% hObject    handle to slider_gripper (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'Value') returns position of slider
%        get(hObject,'Min') and get(hObject,'Max') to determine range of slider
fprintf('TODO: Send a gripper command to the serial robot arm if open.\n');


% --- Executes during object creation, after setting all properties.
function slider_gripper_CreateFcn(hObject, eventdata, handles)
% hObject    handle to slider_gripper (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: slider controls usually have a light gray background.
if isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor',[.9 .9 .9]);
end
