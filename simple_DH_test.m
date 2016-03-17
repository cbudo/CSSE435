clc
clear

A1 = homogeneousTransformation(2,3,0,0);
A2 = homogeneousTransformation(3,0,0,0);

pointAOnLink1 = [0;0;2;1];
pointAOnBase = A1 * pointAOnLink1

pointBOnLink2 = [1;0;1;1];
pointBOnBase = A1 * A2 * pointBOnLink2