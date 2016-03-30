function [ imageObject ] = addImageToAxes( imageFileName, axesHandle, axesWidth )
%ADDIMAGETOAXES Adds an image to an axes
%   Opens the image file name, adds it to the axes
%   Return the image object
%   if axesWidth = 0 then use image's default pixel size
    
%   Open the file to get the image data
    imageData = imread(imageFileName);

%   Create an image object and make the parent the axes
    imageObject = image(imageData, 'Parent', axesHandle);
    
%   make the unit of the axes 'pixels'
%   set visible to 'off'
    set(axesHandle, 'Units', 'Pixels', 'Visible', 'Off');

%   Get current 'position' of the axes in order to use the x and y
    currentPosition = get(axesHandle, 'Position');

%   Get the number of rows and columns of the image
    [rows, cols, depth] = size(imageData);
%   if axes width =0
    % axesWidth = num cols
    % axesHeight = num rows
%   else
    % use the axesWidth and aspect ration to calculate the height
    if axesWidth == 0
        axesWidth = cols;
        axesHeight = rows;
    else
        axesHeight = axesWidth*rows/cols;
    end
    
%   set the new 'Position' on the axes
    set(axesHandle, 'Position', [currentPosition(1) currentPosition(2) axesWidth axesHeight]);
    

end

