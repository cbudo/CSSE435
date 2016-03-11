clear
clc
mckinley = Baby('McKinley');
keegan = Baby('Keegan');

for i = 1:2
    keegan.hourPasses()
    mckinley.feedBaby()
    for j = 1:4
        mckinley.hourPasses()
    end
end