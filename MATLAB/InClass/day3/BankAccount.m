classdef BankAccount < handle
    properties
        name,
        balance,
        interestRate
    end
    methods
        function obj = BankAccount(name,balance,interestRate)
            obj.name = name;
            obj.balance = balance;
            obj.interestRate = interestRate;
        end
        function deposit(obj,amount)
            obj.balance = obj.balance + amount;
        end
        function withdraw(obj,amount)
            obj.balance = obj.balance - amount;
        end
        function accumulateInterest(obj)
            obj.balance = obj.balance + (obj.balance * obj.interestRate);
        end
        function disp(obj)
            fprintf('Name: %s Balance: $%.2f Interest Rate: %.2f%%\n\n',obj.name,obj.balance,obj.interestRate*100);
        end
    end
end
