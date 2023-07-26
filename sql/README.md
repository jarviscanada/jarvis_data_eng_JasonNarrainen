# Introduction

# SQL Queries

##### Table Setup (DDL)
- cd.facilities

  | Field              | Type         | Null | Key | Default | Extra |
  |--------------------|--------------|------|-----|---------|-------|
  | facid              | integer      | no   | PK  |         |       |
  | name               | varchar(100) | no   |     |         |       |
  | membercost         | numeric      | no   |     |         |       |
  | guestcost          | numeric      | no   |     |         |       |
  | initialoutlay      | numeric      | no   |     |         |       |
  | monthlymaintenance | numeric      | no   |     |         |       |
- cd.members

  | Field         | Type         | Null | Key | Default | Extra |
  |---------------|--------------|------|-----|---------|-------|
  | memid         | integer      | no   | PK  |         |       |
  | surname       | varchar(200) | no   |     |         |       |
  | firstname     | varchar(200) | no   |     |         |       |
  | address       | varchar(300) | no   |     |         |       |
  | zipcode       | integer      | no   |     |         |       |
  | telephone     | varchar(20)  | no   |     |         |       |
  | recommendedby | integer      | yes  | FK  |         |       |
  | joindate      | timestamp    | no   |     |         |       |
- cd.bookings

  | Field     | Type      | Null | Key | Default | Extra |
  |-----------|-----------|------|-----|---------|-------|
  | bookid    | integer   | no   | PK  |         |       |
  | facid     | integer   | no   | FK  |         |       |
  | memid     | integer   | no   | FK  |         |       |
  | starttime | timestamp | no   |     |         |       |
  | slots     | integer   | no   |     |         |       |
##### Question 1: Insert some data into a table
The club is adding a new facility - a spa. We need to add it into the 
facilities table. Use the following values:

- facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.

```sql
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
VALUES
    (9, 'Spa', 20, 30, 100000, 800);
```
##### Question 2: Insert calculated data into a table
Let's try adding the spa to the facilities table again. This time, though, we 
want to automatically generate the value for the next facid, rather than 
specifying it as a constant. Use the following values for everything else:
- Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, 
monthlymaintenance: 800.

```sql
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
VALUES
    (
            (
                SELECT
                    max(facid)
                from
                    cd.facilities
            ) + 1, 'Spa',
            20,
            30,
            100000,
            800
    );
```
##### Question 3: Update some existing data
We made a mistake when entering the data for the second tennis court. The 
initial outlay was 10000 rather than 8000: you need to alter the data to fix 
the error.
```sql
UPDATE 
  cd.facilities 
SET 
  initialoutlay = 10000 
WHERE 
  name = 'Tennis Court 2';
```
##### Question 4: Update a row based on the contents of another row
We want to alter the price of the second tennis court so that it costs 10% 
more than the first one. Try to do this without using constant values for the 
prices, so that we can reuse the statement if we want to.
```sql
UPDATE
    cd.facilities
SET
    membercost = src.membercost * 1.1,
    guestcost = src.guestcost * 1.1
FROM cd.facilities src
WHERE
    facilities.facid = src.facid
  AND facilities.name = 'Tennis Court 2';
```
##### Question 5: Delete all bookings
As part of a clearout of our database, we want to delete all bookings from the 
cd.bookings table. How can we accomplish this?
```sql
DELETE FROM cd.bookings;
```
##### Question 6: Delete a member from the cd.members table
We want to remove member 37, who has never made a booking, from our database. 
How can we achieve that?
```sql
DELETE FROM cd.members WHERE memid = 37;
```
##### Question 7: Control which rows are retrieved - part 2
How can you produce a list of facilities that charge a fee to members, and 
that fee is less than 1/50th of the monthly maintenance cost? Return the 
facid, facility name, member cost, and monthly maintenance of the facilities 
in question.
```sql
SELECT 
    facid, name, membercost, monthlymaintenance 
FROM cd.facilities
WHERE membercost > 0
    AND membercost < facilities.monthlymaintenance / 50;
```
##### Question 8: Basic string searches
How can you produce a list of all facilities with the word 'Tennis' in their 
name?
```sql
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';
```
##### Question 9: Matching against multiple possible values
How can you retrieve the details of facilities with ID 1 and 5? Try to do it 
without using the OR operator.
```sql
SELECT  * FROM cd.facilities WHERE facid IN (1,5);
```
##### Question 10: Working with dates
How can you produce a list of members who joined after the start of September 
2012? Return the memid, surname, firstname, and joindate of the members in 
question.
```sql
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate > '2012/09/01';
```
##### Question 11: Combining results from multiple queries
You, for some reason, want a combined list of all surnames and all facility 
names. Yes, this is a contrived example :-). Produce that list!
```sql
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities;
```
##### Question 12: Retrieve the start times of members' bookings
How can you produce a list of the start times for bookings by members named 
'David Farrell'?
```sql
SELECT 
  starttime 
FROM 
  cd.bookings bks 
  INNER JOIN cd.members mems ON bks.memid = mems.memid 
WHERE 
  mems.firstname = 'David' 
  AND mems.surname = 'Farrell';
```
##### Question 13: Work out the start times of bookings for tennis courts
How can you produce a list of the start times for bookings for tennis courts, 
for the date '2012-09-21'? Return a list of start time and facility name 
pairings, ordered by the time.
```sql
SELECT 
  bks.starttime, 
  fac.name 
FROM 
  cd.bookings bks 
  INNER JOIN cd.facilities fac ON bks.facid = fac.facid 
WHERE 
  fac.name LIKE '%Tennis Court%' 
  AND bks.starttime :: date = '2012/09/21' 
ORDER BY 
  bks.starttime;
```
##### Question 14: Produce a list of all members, along with their recommender
How can you output a list of all members, including the individual who 
recommended them (if any)? Ensure that results are ordered by 
(surname, firstname).
```sql
SELECT
    mems.firstname AS memfname,
    mems.surname AS memsname,
    recs.firstname AS recfname,
    recs.surname AS recsname
FROM
    cd.members mems
    LEFT OUTER JOIN cd.members recs ON mems.recommendedby = recs.memid
ORDER BY
    mems.surname,
    mems.firstname;
```
##### Question 15: Produce a list of all members who have recommended another member
How can you output a list of all members who have recommended another member? 
Ensure that there are no duplicates in the list, and that results are ordered 
by (surname, firstname).
```sql
SELECT 
  DISTINCT recs.firstname, 
  recs.surname 
FROM 
  cd.members mems 
  INNER JOIN cd.members recs ON recs.memid = mems.recommendedby 
ORDER BY 
  recs.surname, 
  recs.firstname;
```
##### Question 16: Produce a list of all members, along with their recommender, using no joins.
How can you output a list of all members, including the individual who 
recommended them (if any), without using any joins? Ensure that there are no 
duplicates in the list, and that each firstname + surname pairing is formatted 
as a column and ordered.
```sql
SELECT
    DISTINCT mems.firstname || ' ' || mems.surname AS member,
    (
         SELECT
             recs.firstname || ' ' || recs.surname AS recommender
         FROM
             cd.members recs
         WHERE
             recs.memid = mems.recommendedby
     )
FROM
    cd.members mems
ORDER BY
    member;
```
##### Question 17: Count the number of recommendations each member makes.
Produce a count of the number of recommendations each member has made. Order 
by member ID.
```sql
SELECT 
  recommendedby, 
  count(*) 
FROM 
  cd.members 
WHERE 
  recommendedby is not null 
GROUP BY 
  recommendedby 
ORDER BY 
  recommendedby;
```
##### Question 18: List the total slots booked per facility
Produce a list of the total number of slots booked per facility. For now, just 
produce an output table consisting of facility id and slots, sorted by 
facility id.
```sql
SELECT
    facid,
    sum(slots) AS "Total Slots"
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY
    facid;
```
##### Question 19: List the total slots booked per facility in a given month
Produce a list of the total number of slots booked per facility in the month 
of September 2012. Produce an output table consisting of facility id and 
slots, sorted by the number of slots.
```sql
SELECT 
  facid, 
  sum(slots) AS "Total Slots" 
FROM 
  cd.bookings 
WHERE 
  starttime >= '2012/09/01' 
  AND starttime < '2012/10/01' 
GROUP BY 
  facid 
ORDER BY 
  "Total Slots";
```
##### Question 20: List the total slots booked per facility per month
Produce a list of the total number of slots booked per facility per month in 
the year of 2012. Produce an output table consisting of facility id and slots, 
sorted by the id and month.
```sql
SELECT
    facid,
    EXTRACT(
        month
        from
            starttime
    ) AS month,
SUM(slots) AS "Total Slots"
FROM
    cd.bookings
WHERE
    EXTRACT(
        year
        FROM
            starttime
    ) = 2012
GROUP BY
    facid,
    month
ORDER BY
    facid,
    month;
```
##### Question 21: Find the count of members who have made at least one booking
Find the total number of members (including guests) who have made at least one booking.
```sql
SELECT COUNT(DISTINCT memid) FROM cd.bookings;
```
##### Question 22: List each member's first booking after September 1st 2012
Produce a list of each member name, id, and their first booking after 
September 1st 2012. Order by member ID.
```sql
SELECT
    mems.surname,
    mems.firstname,
    mems.memid,
    min(starttime)
FROM
    cd.members mems
    INNER JOIN cd.bookings bks ON mems.memid = bks.memid
WHERE
    starttime >= '2012-09-01'
GROUP BY
    mems.memid
ORDER BY
    memid;
```
##### Question 23: Produce a list of member names, with each row containing the total member count
Produce a list of member names, with each row containing the total member 
count. Order by join date, and include guest members.
```sql
SELECT
    count(memid) OVER (),
    firstname,
    surname
FROM cd.members
ORDER BY joindate;
```
##### Question 24: Produce a numbered list of members
Produce a monotonically increasing numbered list of members (including 
guests), ordered by their date of joining. Remember that member IDs are not 
guaranteed to be sequential.
```sql
SELECT row_number() over (ORDER BY joindate), firstname, surname 
FROM cd.members 
ORDER BY joindate;
```
##### Question 25: Output the facility id that has the highest number of slots booked, again
Output the facility id that has the highest number of slots booked. Ensure 
that in the event of a tie, all tieing results get output.
```sql
SELECT
    facid,
    total
FROM
    (
        SELECT
            facid,
            sum(slots) total,
            rank() OVER (
                ORDER BY
                    sum(slots) DESC
                ) rank
        FROM
            cd.bookings
        GROUP BY
            facid
    ) AS ranked
WHERE
    rank = 1;
```
##### Question 26: Format the names of members
Output the names of all members, formatted as 'Surname, Firstname'
```sql
SELECT surname || ', ' || firstname AS name FROM cd.members;
```
##### Question 27: Find telephone numbers with parentheses
You've noticed that the club's member table has telephone numbers with very 
inconsistent formatting. You'd like to find all the telephone numbers that 
contain parentheses, returning the member ID and telephone number sorted by 
member ID.
```sql
SELECT memid, telephone FROM cd.members WHERE telephone ~ '[()]';
```
##### Question 28: Count the number of members whose surname starts with each letter of the alphabet
You'd like to produce a count of how many members you have whose surname 
starts with each letter of the alphabet. Sort by the letter, and don't worry 
about printing out a letter if the count is 0.
```sql
SELECT substring(surname, 1, 1) AS letter, count(*)
FROM cd.members mems
GROUP BY letter
ORDER BY letter;
```