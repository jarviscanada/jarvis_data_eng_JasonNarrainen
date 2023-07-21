# Linux Cluster Monitoring Agent
# Introduction
This project is a Linux server monitoring agent which monitors the server 
hardware usage every minute and saves the data to a database. This project 
is geared toward Linux system administrators who want to get more insight
on the resource usage of their servers to better optimize them. The 
technologies used in this project are Git for the version control system, Bash 
as the scripting language, Docker as the platform for the containers with the 
dependencies, PostgreSQL for the database, and crontab for the automation of the
script.
# Quick Start
Follow the steps below to start quickly
```shell
# Create the docker container
./scripts/psql_docker.sh create "db_username" "db_password"

# Run docker container
./scripts/psql_docker.sh start

# Create tables in PostgreSQL
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

# Insert host hardware specs in the db
./scripts/host_info.sh localhost 5432 host_agent postgres password

# Insert host hardware usage data in the db
./scripts/host_usage.sh localhost 5432 host_agent postgres password

# Setup crontab for running host_usage.sh every minute
crontab -e
```
Your text editor will open, and you can add the following command and then save and quit.
```shell
# Add the cron job with the absolute path to the file with your text editor
* * * * * \
  bash /path/to/folder/jarvis_data_eng_JasonNarrainen/linux_sql/scripts/host_usage.sh \
  localhost 5432 host_agent postgres password > /tmp/host_usage.log
```
# Implementation
Each node has the host_info script run once to send host hardware information 
to the central database and then they each run the host_usage script every 
minute through a cron job to insert the usage information for each node.
## Architecture
![Two servers are connected to a router and a server and the router are
connected to the database. Every server is connected to a script](assets/architecture_diagram.png)
## Scripts
- ### psql_docker.sh
This script is used for setting up the docker container and running it to 
execute psql commands to interact with the database.
```shell
# creates the docker container
bash psql_docker.sh create username password

# runs the docker container
bash psql_docker.sh start

# stops the docker container
bash psql_docker.sh stop
```
- ### host_info.sh
This script is used for reading the node's hardware specifications such as 
hostname, cpu core count, cpu architecture, cpu model name, cpu clock speed, 
L2 cache, timestamp, and total RAM. This information is then inserted into 
the database.
```shell
bash host_info.sh psql_host psql_port db_name psql_user psql_password
```
- ### host_usage.sh
This script is used for reading the node's current hardware resource usage such as
free RAM, the idle cpu percentage used, the kernel cpu percentage used, 
current disk io reads or writes, the disk storage space available, and a 
timestamp. This information is then inserted into the database.
```shell
bash host_info.sh psql_host psql_port db_name psql_user psql_password
```
- ### ddl.sql
This file is used to create the two tables for the host_info script and the 
host_usage script.
## Database Modeling
- ### host_info
| Field            | Type      | Null | Key | Default | Extra  |
|------------------|-----------|------|-----|---------|--------|
| id               | serial    | no   | PK  | NULL    |        |
| hostname         | varchar   | no   |     | NULL    | unique |
| cpu_number       | int2      | no   |     | NULL    |        |
| cpu_architecture | varchar   | no   |     | NULL    |        |
| cpu_model        | varchar   | no   |     | NULL    |        |
| cpu_mhz          | float8    | no   |     | NULL    |        |
| l2_cache         | int4      | no   |     | NULL    |        |
| timestamp        | timestamp | yes  |     | NULL    |        |
| total_mem        | int4      | yes  |     | NULL    |        |
- ### host_usage
| Field          | Type      | Null | Key | Default | Extra |
|----------------|-----------|------|-----|---------|-------|
| timestamp      | timestamp | no   |     | NULL    |       |
| host_id        | serial    | no   | FK  | NULL    |       |
| memory_free    | int4      | no   |     | NULL    |       |
| cpu_idle       | int2      | no   |     | NULL    |       |
| cpu_kernel     | int2      | no   |     | NULL    |       |
| disk_io        | int4      | no   |     | NULL    |       |
| disk_available | int4      | no   |     | NULL    |       |
# Test
The scripts were tested manually by executing the scripts and validating the 
table entries and all scripts work as intended.
# Deployment
The codebase is available on GitHub and the project needs to be manually 
installed on each node in the Linux cluster. Once the docker container is 
running, the tables are created and the host_info script has been run once, 
then the host_usage script needs to be automated with a cron job.
# Improvements
- Add the ability to detect system failures by verifying if there are any time 
differences greater than one minute between entries.
- Add the ability to check for hardware updates.
- Add a command to create a report of the most used and least used node.