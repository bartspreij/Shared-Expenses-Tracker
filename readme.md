Part of Java Backend Developer track by **[hyperskill](https://hyperskill.org/tracks/12)**.

# Program Description

Commands Presently Supported:

**[date] borrow PersonOne PersonTwo amount**
PersonTwo lent some amount to PersonOne on the specified date.

Example:  
writeOff  
borrow Bob Chuck 100  
borrow Chuck Diana 100  
borrow Chuck Bob 30  
borrow Diana Bob 100  
balance close  
Bob owes Chuck 70.00  
Chuck owes Diana 100.00  
Diana owes Bob 100.00  

**[date] repay PersonOne PersonTwo amount**
PersonOne repaid an amount to PersonTwo on the date.

**[date] balance [open|close] [(+/- list of names and groups)]**
Calculate and display the list of repayments with names and amounts to be repaid in the natural sorted order for the balance date. Here, open refers to the opening balance on the first day of the month, and close refers to the closing balance on that specific date. The list of names and groups is resolved, and the balance filters (i.e., displays only) those who owe among the resolved names.

 TODO
**[date] balancePerfect [open|close] [(+/- list of names and groups)]**
Calculate and display the list of repayments as in the balance command above, but do an optimization of repayments, e.g., net owing sums in a way that as few transactions, repayments as possible are needed to resettle all debts. (cf: [Stack Overflow Reference](https://stackoverflow.com/questions/1163116/algorithm-to-determine-minimum-payments-amongst-a-group))

**group create|show|add|remove GROUPNAME [(+/- list of names and groups)]**
Create or display groups of persons used in the purchase command.

**[date] purchase Person itemName amount (+/- list of names and groups)**
A person purchases an item for an amount, which must be spread in the resolved group. A possible cents remainder of N cents from splitting division is spread between the first N persons in the list ordered by name; each person pays an extra 0.01.

**secretSanta GROUPNAME**
Random gift assignment in a group with rules: No-one should be assigned to get a gift for themselves in groups larger than 1 person; Gift pairs cannot be reciprocal in groups larger than 2 people: in other words, you can't get a gift from a person and give a gift to the same person.

Example:  
group create SOMESANTAGROUP (Gordon,Bob,Ann,Chuck,Elon,Diana,Foxy)  
secretSanta SOMESANTAGROUP  
Ann gift to Elon  
Bob gift to Gordon  
Chuck gift to Bob  
Diana gift to Chuck  
Elon gift to Diana  
Foxy gift to Ann  
Gordon gift to Foxy  

**[date] cashback Person itemName amount [(list of [+|-] persons | GROUPS)]**
A commitment to refund some expense to a group, splitting it exactly between members with the same logic as in purchase.

**[date] writeOff**
Clear all transactions (in the database) before and including the given limit date (default today).

**help**
Print the list of commands in natural sorted order.

**exit**
Exit the program.

# Bigger example

writeOff  
group create TEAM (Ann, Bob, Chuck, Diana, Elon, Frank)  
group create CAR (Diana, Elon)  
group create BUS (Ann, Bob, Chuck, Frank)  
purchase Chuck busTickets 5.25 (BUS, -Frank)  
purchase Elon fuel 25 (CAR, Frank)  
purchase Ann chocolate 2.99 (BUS, -Bob, CAR)  
purchase Diana soda 5.45 (TEAM, -Ann, -Chuck)  
purchase Frank bbq 29.90 (TEAM, CAR, BUS, -Frank, -Bob)  
cashBack YourCompany party 12 (TEAM, BUS)  
cashBack YourCompany tickets 3.50 (BUS)  
borrow Frank Bob 10  
repay Chuck Diana 20  
balance close  
Ann owes Chuck 1.15  
Ann owes Frank 6.89  
Bob owes Chuck 1.75  
Bob owes Diana 1.37  
Chuck owes Frank 7.48  
Diana owes Ann 0.60  
Diana owes Chuck 20.00  
Diana owes Elon 6.98  
Diana owes Frank 6.11  
Elon owes Ann 0.60  
Frank owes Bob 10.00  
Frank owes Elon 0.86  
YourCompany owes Ann 2.88  
YourCompany owes Bob 2.88  
YourCompany owes Chuck 2.87  
YourCompany owes Diana 2.00  
YourCompany owes Elon 2.00  
YourCompany owes Frank 2.87  
