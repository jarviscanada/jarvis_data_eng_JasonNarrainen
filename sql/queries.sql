-- Q1
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
VALUES
    (9, 'Spa', 20, 30, 100000, 800);

-- Q2
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

-- Q3
UPDATE
    cd.facilities
SET
    initialoutlay = 10000
WHERE
        name = 'Tennis Court 2';

-- Q4
UPDATE
    cd.facilities
SET
    membercost = src.membercost * 1.1,
    guestcost = src.guestcost * 1.1
FROM (SELECT * from cd.facilities WHERE facid = 0) src
WHERE
    facilities.facid = 1;

-- Q5
DELETE FROM cd.bookings;

--Q6
DELETE FROM cd.members WHERE memid = 37;

--Q7
SELECT
    facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0
    AND membercost < facilities.monthlymaintenance / 50;

--Q8
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';

--Q9
SELECT  * FROM cd.facilities WHERE facid IN (1,5);

--Q10
SELECT
    memid,
    surname,
    firstname,
    joindate
FROM
    cd.members
WHERE
    joindate > '2012/09/01';

--Q11
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities;

--Q12
SELECT
    starttime
FROM
    cd.bookings bks
    INNER JOIN cd.members mems ON bks.memid = mems.memid
WHERE
    mems.firstname = 'David'
    AND mems.surname = 'Farrell';

--Q13
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

--Q14
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

--Q15
SELECT
    DISTINCT recs.firstname,
             recs.surname
FROM
    cd.members mems
        INNER JOIN cd.members recs ON recs.memid = mems.recommendedby
ORDER BY
    recs.surname,
    recs.firstname;

--Q16
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

--Q17
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

--Q18
SELECT
    facid,
    sum(slots) AS "Total Slots"
FROM
    cd.bookings
GROUP BY
    facid
ORDER BY
    facid;

--Q19
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

--Q20
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

--Q21
SELECT COUNT(DISTINCT memid) FROM cd.bookings;

--Q22
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

--Q23
SELECT
    count(memid) OVER (),
    firstname,
    surname
FROM cd.members
ORDER BY joindate;

--Q24
SELECT row_number() over (ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;

--Q25
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

--Q26
SELECT surname || ', ' || firstname AS name FROM cd.members;

--Q27
SELECT memid, telephone FROM cd.members WHERE telephone ~ '[()]';

--Q28
SELECT substring(surname, 1, 1) AS letter, count(*)
FROM cd.members mems
GROUP BY letter
ORDER BY letter;