function varargout = DogsGUI(varargin)
% DOGSGUI MATLAB code for DogsGUI.fig
%      DOGSGUI, by itself, creates a new DOGSGUI or raises the existing
%      singleton*.
%
%      H = DOGSGUI returns the handle to a new DOGSGUI or the handle to
%      the existing singleton*.
%
%      DOGSGUI('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in DOGSGUI.M with the given input arguments.
%
%      DOGSGUI('Property','Value',...) creates a new DOGSGUI or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before DogsGUI_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to DogsGUI_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help DogsGUI

% Last Modified by GUIDE v2.5 15-Mar-2016 00:39:52

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @DogsGUI_OpeningFcn, ...
                   'gui_OutputFcn',  @DogsGUI_OutputFcn, ...
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


% --- Executes just before DogsGUI is made visible.
function DogsGUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to DogsGUI (see VARARGIN)

% Choose default command line output for DogsGUI
handles.output = hObject;

addImageToAxes('DogImages/puppyKingsley.jpg', handles.axes_kingsley, 150);
addImageToAxes('DogImages/Keely.jpg', handles.axes_keely, 150);
handles.user.moveAmount = 25;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes DogsGUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = DogsGUI_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in checkbox_kingsleyVisble.
function checkbox_kingsleyVisble_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_kingsleyVisble (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_kingsleyVisble
if get(hObject,'Value')
    set(get(handles.axes_kingsley,'Children'),'Visible','On');
    set(handles.uipanel_kingsleyMove,'Visible','On');
    set(handles.uibuttongroup_kingsleyImage,'Visible','On');
else
    % set invisible
    set(get(handles.axes_kingsley,'Children'),'Visible','Off');
    set(handles.uipanel_kingsleyMove,'Visible','Off');
    set(handles.uibuttongroup_kingsleyImage,'Visible','Off');
end


% --- Executes on button press in checkbox_keelyVisible.
function checkbox_keelyVisible_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_keelyVisible (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_keelyVisible
if get(hObject,'Value')
    set(get(handles.axes_keely,'Children'),'Visible','On');
    set(handles.uipanel_keelyMove,'Visible','On');
else
    % set invisible
    set(get(handles.axes_keely,'Children'),'Visible','Off');
    set(handles.uipanel_keelyMove,'Visible','Off');
end

% --- Executes on button press in pushbutton_keelyLeft.
function pushbutton_keelyLeft_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_keelyLeft (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_keely,'Position',get(handles.axes_keely,'Position')+[-handles.user.moveAmount 0 0 0]);


% --- Executes on button press in pushbutton_keelyDown.
function pushbutton_keelyDown_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_keelyDown (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_keely,'Position',get(handles.axes_keely,'Position')+[0 -handles.user.moveAmount 0 0]);


% --- Executes on button press in pushbutton_keelyRight.
function pushbutton_keelyRight_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_keelyRight (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_keely,'Position',get(handles.axes_keely,'Position')+[handles.user.moveAmount 0 0 0]);


% --- Executes on button press in pushbutton_keelyUp.
function pushbutton_keelyUp_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_keelyUp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_keely,'Position',get(handles.axes_keely,'Position')+[0 handles.user.moveAmount 0 0]);


% --- Executes on button press in pushbutton_kingsleyLeft.
function pushbutton_kingsleyLeft_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_kingsleyLeft (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_kingsley,'Position',get(handles.axes_kingsley,'Position')+[-handles.user.moveAmount 0 0 0]);


% --- Executes on button press in pushbutton_kingsleyDown.
function pushbutton_kingsleyDown_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_kingsleyDown (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_kingsley,'Position',get(handles.axes_kingsley,'Position')+[0 -handles.user.moveAmount 0 0]);



% --- Executes on button press in pushbutton_kingsleyRight.
function pushbutton_kingsleyRight_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_kingsleyRight (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_kingsley,'Position',get(handles.axes_kingsley,'Position')+[handles.user.moveAmount 0 0 0]);


% --- Executes on button press in pushbutton_kingsleyUp.
function pushbutton_kingsleyUp_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_kingsleyUp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.axes_kingsley,'Position',get(handles.axes_kingsley,'Position')+[0 handles.user.moveAmount 0 0]);


% --- Executes on button press in radiobutton_puppyButton.
function radiobutton_puppyButton_Callback(hObject, eventdata, handles)
% hObject    handle to radiobutton_puppyButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of radiobutton_puppyButton
addImageToAxes('DogImages/puppyKingsley.jpg', handles.axes_kingsley, 150);


% --- Executes on button press in radiobutton_babyButton.
function radiobutton_babyButton_Callback(hObject, eventdata, handles)
% hObject    handle to radiobutton_babyButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of radiobutton_babyButton
addImageToAxes('DogImages/babyKingsley.jpg', handles.axes_kingsley, 150);


% --- Executes on button press in radiobutton_bigButton.
function radiobutton_bigButton_Callback(hObject, eventdata, handles)
% hObject    handle to radiobutton_bigButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of radiobutton_bigButton
addImageToAxes('DogImages/bigKingsley.jpg', handles.axes_kingsley, 150);


% --- Executes during object creation, after setting all properties.
function uipanel_kingsleyMove_CreateFcn(hObject, eventdata, handles)
% hObject    handle to uipanel_kingsleyMove (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called
