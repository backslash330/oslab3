#open the 2cpus_interactive_avg.csv file
# and the 2cpus_interactive_raw.csv file

import csv


#open the avg file, then for each ten rows in the raw file (starting 2-11, 12-21, 22-31, etc)
# get the 5th column (the total processes) and then find the average
# in a new file, write the two rows from the raw file and the average total processes
# header: Ready Processes, Interactive Chance, Avg Total Processes

with open('2cpus_interactive_avg.csv', 'r') as avg_file:
    avg_reader = csv.reader(avg_file)
    avg_data = list(avg_reader)
    with open('2cpus_interactive_raw.csv', 'r') as raw_file:
        raw_reader = csv.reader(raw_file)
        raw_data = list(raw_reader)
        with open('2cpus_interactive_correction.csv', 'w', newline='') as correction_file:
            correction_writer = csv.writer(correction_file)
            correction_writer.writerow(['Ready Processes', 'Interactive Chance', 'Avg Total Processes'])
            for i in range(0, len(avg_data)):
                avg = 0
                for j in range(0, 10):
                    avg += int(raw_data[i*10+j][4])
                avg /= 10
                correction_writer.writerow([raw_data[i*10][0], raw_data[i*10][1], avg])

print('done')
    
