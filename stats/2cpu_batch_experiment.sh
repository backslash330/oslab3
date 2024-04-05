

2cpu_batch_experiment(){
    echo "Running experiment.sh"
    echo "Test comment"
    echo "Running configuration: 2 CPUs, all the processes created are batch"
    # create csv and add header row 
    echo "Ready Processes,Processes in CPU,Blocked processes,Completed processes,Accounted For, Number of CPUs,Exiting at simulation time,TurnaroundTime" > 2cpus_batch_raw.csv
    echo "Ready Processes, Batch Chance, Avg Total Processes" > 2cpus_batch_avg.csv
    ilength=100
    iskip=10
    jlength=10

    # loop through the configuration 1000 times for each possible rate of creation between 1 and 1000
    # for i in $(seq 1 $iskip $ilength)
    for i in $(seq 1 $ilength)
    do
        echo "Running Completion Rate 1 of $i"
        for j in $(seq 1 $jlength)
        do
            echo "Running Simulation $j of $jlength"
            java Simulator 100000 $i 10000 500 2 > temp.txt
            # create a var to hold the csv formatted results
            formatted_results=""
            line_start=""
            substring=""

            # avg variables
            avg_ready_processes=0
            avg_total_processes=0
            avg_throughput=0
            avg_turnaround_time=0
            st_dev_throughput=0
            st_dev_turnaround_time=0
            current_ready_processes=0
            current_total_processes=0
            current_throughput=0
            current_turnaround_time=0
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
                    formatted_results="${formatted_results},$(echo $line | cut -d ',' -f 4)"
                fi
                #  otherwise add the number after the : to the variable
                formatted_results="${formatted_results},${line#*:}"
            done < temp.txt
            # add the formatted results to the csv
            echo $formatted_results >> 2cpus_batch_raw.csv
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
        # echo ${line%%,*}

            # Get the total number of created processes (5th column in the csv file)

            # collect 5th column
            current_total_processes=$(echo $line | cut -d ',' -f 5)
            #echo "current_total_processes: $current_total_processes"
            avg_total_processes=$((avg_total_processes + current_total_processes))
        done < temp_j_loop.txt
        # divide by the number of runs to get the average
    # echo "avg_ready_processes: $avg_ready_processes"
        avg_ready_processes=$((avg_ready_processes / jlength))
        avg_total_processes=$((avg_total_processes / jlength))
        # add the average to the csv
        echo "$avg_ready_processes,$i,$avg_total_processes" >> 2cpus_batch_avg.csv
        # clear avg variables
        avg_ready_processes=0

        # clear the temp file
        > temp_j_loop.txt
    done

    # get the mean and standard deviation of the throughput and turnaround time
    # clear stats file
    > 2cpus_batch_stats.csv
    while IFS= read -r line
    do
        # skip the header row
        if [ "$line" = "Ready Processes,Processes in CPU,Blocked processes,Completed processes,Accounted For, Number of CPUs,Exiting at simulation time,TurnaroundTime" ]
        then
            continue
        fi
        # convert the line to an integer and add it to the avg variable
        current_total_processes=$(echo $line | cut -d ',' -f 4)
        current_turnaround_time=$(echo $line | cut -d ',' -f 8)
        echo "current_total_processes: $current_total_processes"
        avg_total_processes=$((avg_total_processes + current_total_processes))
        avg_turnaround_time=$((avg_turnaround_time + current_turnaround_time))
    
    done < 2cpus_batch_raw.csv

    # divide by the number of runs to get the average
    avg_total_processes=$((avg_total_processes / ilength))
    echo "Mean Total Processes: $avg_total_processes" >> 2cpus_batch_stats.csv
    echo "Mean Turnaround Time: $avg_turnaround_time" >> 2cpus_batch_stats.csv


    # get the standard deviations of the throughput
    while IFS= read -r line
    do
        # skip the header row
        if [ "$line" = "Ready Processes,Processes in CPU,Blocked processes,Completed processes,Accounted For, Number of CPUs,Exiting at simulation time,TurnaroundTime" ]
        then
            continue
        fi
        # convert the line to an integer and add it to the avg variable
        current_total_processes=$(echo $line | cut -d ',' -f 4)
        current_turnaround_time=$(echo $line | cut -d ',' -f 8)
        echo $line
        st_dev_throughput=$((st_dev_throughput + (current_total_processes - avg_total_processes) ** 2))
        st_dev_turnaround_time=$((st_dev_turnaround_time + (current_turnaround_time - avg_turnaround_time) ** 2))
    done < 2cpus_batch_raw.csv

    # divide by the number of runs to get the average
    st_dev_throughput=$((st_dev_throughput / (ilength-1)))
    st_dev_throughput=$(echo "sqrt($st_dev_throughput)" | bc -l)
    st_dev_turnaround_time=$((st_dev_turnaround_time / (ilength-1)))
    st_dev_turnaround_time=$(echo "sqrt($st_dev_turnaround_time)" | bc -l)
    echo "Standard Deviation of Total Processes: $st_dev_throughput" >> 2cpus_batch_stats.csv
    echo "Standard Deviation of Turnaround Time: $st_dev_turnaround_time" >> 2cpus_batch_stats.csv

    # turnaround time mean


}

2cpu_batch_experiment