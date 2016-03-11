clc
clear

billAccount = BankAccount('Bill',100,.025);
bobAccount = BankAccount('Bob',100,.05);
withdraw(billAccount,500);
deposit(bobAccount,50);
billAccount
bobAccount
bobAccount.accumulateInterest
bobAccount