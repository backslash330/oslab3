# We need to determine the steady state for the given configurations. 
# To do this we need to create a csv for a number of runs for each configuration.
# Then we need to see whether the ready queue approaches zero, infinte or a steady state.
# We can do this by plotting the ready queue length over time for each configuration.
# We can then determine the steady state by looking at the graph.
# The variable for each configuration is the rate at which processes are created
# We also know that the following is the output format:
# Ready Processes: 128 
# Processes in CPU: 2 
# Blocked processes: 1 
# Completed processes: 944 
# Accounted for 1075 of 1075 processes 
# Number of CPUs: 2 
# Exiting at simulation time: 10000 
# Simulation Result,100,944,10000,654741
# Configurations:
# • 2 CPUs, all the processes created are interactive 
# • 4 CPUs, all the processes created are interactive 
# • 2 CPUs, all the processes created are batch 
# • 4 CPUs, all the processes created are batch 
# • 2 CPUs, half of the processes are interactive, and half are batch 
# • 4 CPUs, half of the processes are interactive, and half are batch  
# parameters for simulator
# 1. Interactive Chance: specifies x for a 1 in x chance of creating an interactive process at each time step 
# 2. Batch Chance: specifies x for a 1 in x chance of creating a batch process at each time step 
# 3. Simulation Time: The number of time steps before the simulation terminates 
# 4. Quantum: The number of time steps a process may spend in a CPU before it is preempted 
# 5. Number of CPUs: The number of CPUs in the simulation 
#!/bin/bash


echo "Running experiment.sh"
echo "Running configuration: 2 CPUs, all the processes created are interactive"
# create csv and add header row 
echo "Ready Processes,Processes in CPU,Blocked processes,Completed processes,Accounted For, Number of CPUs,Exiting at simulation time" > 2cpus_interactive_raw.csv
echo "Ready Processes, Interactive Chance" > 2cpus_interactive_avg.csv
ilength=1000
iskip=10
jlength=10

# loop through the configuration 1000 times for each possible rate of creation between 1 and 1000
# for i in $(seq 1 $iskip $ilength)
for i in $(seq 1 $ilength)
do
    echo "Running Completion Rate 1 of $i"
    for j in $(seq 1 $jlength)
    do
        echo "Running Simulation $j of 100"
        java Simulator $i 100000 10000 500 2 > temp.txt
        # create a var to hold the csv formatted results
        formatted_results=""
        line_start=""
        substring=""

        # avg variables
        avg_ready_processes=0
        current_ready_processes=0
        avg_processes_in_cpu=0
        avg_blocked_processes=0
        avg_completed_processes=0
        avg_accounted_for=0
        avg_number_of_cpus=0
        avg_exiting_at_simulation_time=0
        # loop through the lines of the output file and add the results to the csv
        while IFS= read -r line
        do
            # collect the beginning of the line before the colon
            line_start=${line%%:*}
           # echo $line_start
            # if the line is ready process then we do not want a leading ,
            if [ "$line_start" = "Ready Processes" ]
            then
                formatted_results="${line#*:}"
                continue
            fi
            # if the line starts with "Accounted for" then we only want the first number in the string
            #for example "Accounted For 1074 of 1075 processes" we only want the 1074
            if [ "$line_start" = "Accounted For" ]
            then
                substring=${line#*: }
                formatted_results="${formatted_results},${substring%% *}"
                continue
            fi
            # skip sim result line
            if [ "$line_start" = "Simulation Result" ]
            then
                continue
            fi
            #  otherwise add the number after the : to the variable
            formatted_results="${formatted_results},${line#*:}"
        done < temp.txt
        # add the formatted results to the csv
        echo $formatted_results >> 2cpus_interactive_raw.csv
        echo $formatted_results >> temp_j_loop.txt
        # clear the temp file
        > temp.txt
    done
    # next, we need to calculate the average ready processes for the given rate of creation
    # loop through the temp file and add the ready processes to the avg variable
    while IFS= read -r line
    do
        # convert the line to an integer and add it to the avg variable
        current_ready_processes=${line%%,*}
        avg_ready_processes=$((avg_ready_processes + current_ready_processes))
        echo ${line%%,*}
    done < temp_j_loop.txt
    # divide by the number of runs to get the average
    echo "avg_ready_processes: $avg_ready_processes"
    avg_ready_processes=$((avg_ready_processes / jlength))
    # add the average to the csv
    echo "$avg_ready_processes,$i" >> 2cpus_interactive_avg.csv
    # clear avg variables
    avg_ready_processes=0

    # clear the temp file
    > temp_j_loop.txt
done
