function [ imageObject ] = addImageToAxes( imageFilename, axesHandle, axesWidth )

[imageData, map, alphaChannel] = imread(imageFilename);
imageObject = image(imageData, 'Parent', axesHandle);
%imageObject = image(imageData, 'Parent', axesHandle, 'AlphaData', alphaChannel);

set(axesHandle,'Units','Pixels','Visible','Off');
currentPosition = get(axesHandle,'Position');

% Get the number of rows and columns of the image
[rows_height, cols_width, depth] = size(imageData);

if axesWidth == 0
    axesWidth = cols_width;
   axesHeight = rows_height;
else
   axesHeight = axesWidth * rows_height/cols_width;
end

set(axesHandle,'Position',[currentPosition(1) currentPosition(2) axesWidth axesHeight]);
end
