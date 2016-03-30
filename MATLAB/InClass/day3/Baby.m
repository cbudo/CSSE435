classdef Baby < handles
    properties
        name,
        hours
    end
    methods
        function obj = Baby(name)
            obj.name = name;
            obj.hours = 0;
            fprintf('Hello baby, %s\n',name);
        end
        function feedBaby(obj)
            fprintf('Thank you for feeding baby, %s\n', obj.name);
        end
        function hourPasses(obj)
            if obj.hours == 0
                fprintf('Baby %s is sleeping\n',obj.name);
            elseif obj.hours == 1
                fprintf('Baby %s is awake. Time for food.\n', obj.name);
            else
                fprintf('Baby %s is CRYING uncontrollably! Feed the Baby!\n',obj.name);
            end
            obj.hours = obj.hours + 1;
        end
    end
end