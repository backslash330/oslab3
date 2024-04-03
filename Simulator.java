
import java.util.Random;
import java.util.LinkedList;

/*
 * Simulator.java
 * Main class that creates processes and adds them to a ready queue.
 * Prints the ready queue when both types of processes are created simultaneously.
 */
public class Simulator {
    // static variables to keep track of whether both types of processes have been
    // created
    // and a queue to hold the processes
    static boolean interactiveCreated = false;
    static boolean batchCreated = false;
    static LinkedList<Process> readyQueue = new LinkedList<Process>();
    // we need an array of cpu objects
    static LinkedList<Cpu> cpuList = new LinkedList<Cpu>();
    static LinkedList<Process> IOQueue = new LinkedList<Process>();
    static LinkedList<Process> IOQueueRemoval = new LinkedList<Process>();
    static int clock = 0;
    static int completedProcesses = 0;
    static int readyProcesses = 0;
    static int processesInCPU = 0;
    static int blockedProcesses = 0;
    static int accountedFor = 0;
    static int createdProcesses = 0;
    static int sumTurnaroundTime = 0;




    // useageMessage method
    public static void useageMessage() {
        System.out.println("This program takes 5 paramenetes \nAll  parameters  are  positive  integers  (greater  than zero).\nIn order, they are as follows:\n1. Interactive Chance: specifies x for a 1 in x chance of creating an interactive process at each time step\n2. Batch Chance: specifies x for a 1 in x chance of creating a batch process at each time step\n3. Simulation Time: The number of time steps before the simulation terminates\n4. Quantum: The number of time steps a process may spend in a CPU before it is preempted\n5. Number of CPUs: The number of CPUs in the simulation\n");
    }
    // main method
    // creates processes until both types of processes are created simultaneously
    public static void main(String[] args) {
        // ensure there are only five args if not provide usage message
        if (args.length != 5) {
            useageMessage();
            System.exit(1);
        }

        // confirm all args are positive integers
        for (int i = 0; i < args.length; i++) {
            try {
                int num = Integer.parseInt(args[i]);
                if (num <= 0) {
                    useageMessage();
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                useageMessage();
                System.exit(1);
            }
        }
        // System.out.println("Interactive Chance: " + args[0]);
        // System.out.println("Batch Chance: " + args[1]);
        // System.out.println("Simulation Time: " + args[2]);
        // System.out.println("Quantum: " + args[3]);
        // System.out.println("Number of CPUs: " + args[4]);

        // System.out.println("Starting simulation.");

        // set parameters
        int interactiveChance = Integer.parseInt(args[0]);
        int batchChance = Integer.parseInt(args[1]);
        int simulationTime = Integer.parseInt(args[2]);
        int quantum = Integer.parseInt(args[3]);
        int numCPUs = Integer.parseInt(args[4]);

        // create variables
        Random rand = new Random();
        // create the CPUs
        for (int i = 0; i < numCPUs; i++) {
            Cpu cpu = new Cpu(quantum, i);
            cpuList.add(cpu);
        }
        // System.out.println("Number of CPUs: " + cpuList.size());

        // Loop until the simulation time has expired, at each time step:
        while (clock < simulationTime) {
            createProcesses(rand, interactiveChance, batchChance);

            // b. Process the CPUs
            //     • Increment the processes in the CPU (clock tick)
            //     • Remove processes that have completed
            //     • Move processes that have blocked to the I/O queue
            //     • Move the processes that have reached their quantum to the ready queue
            
            clock++;
            // System.out.println("Clock: " + clock);
            for (Cpu cpu : cpuList) {
                cpu.run();
                Process currentProcess = cpu.removecompletedProcess();
                if (currentProcess != null) {
                    sumTurnaroundTime += clock - currentProcess.getCreationTime();
                    currentProcess.setCompletedTime(clock);
                    completedProcesses++;
                    //System.out.println("Process " + currentProcess.getPid() + " has completed.");
                }
                Process blockedProcess = cpu.removeBlockedProcess();
                if (blockedProcess != null) {
                    IOQueue.add(blockedProcess);
                    //System.out.println("Process " + blockedProcess.getPid() + " has been blocked.");
                }
                Process quantumProcess = cpu.removeQuantumProcess();
                if (quantumProcess != null) {
                    readyQueue.add(quantumProcess);
                    //System.out.println("Process " + quantumProcess.getPid() + " has reached its quantum.");
                }
            }

            // c. Process the Ready Queue
            //     • Move processes into the CPUs if there are available CPUs

            for (Cpu cpu : cpuList) {
                if (cpu.getCurrentProcess() == null && !readyQueue.isEmpty()) {
                    Process nextProcess = readyQueue.remove();
                    cpu.setCurrentProcess(nextProcess);
                    //System.out.println("Process " + nextProcess.getPid() + " has been added to CPU " + cpu.getCpuNumber());
                }
            }
            // d. Process the I/O Queue
            //     • Increment the processes in the I/O Queue (clock tick)
            //     • Move unblocked processes into the ready queue
            for (Process p : IOQueue) {
                p.incrementIO();
                if (p.getTimeToNextIoBlock() <= 0) {
                    p.resetTimeToNextIoBlock();
                    readyQueue.add(p);
                    IOQueueRemoval.add(p);
                    //System.out.println("Process " + p.getPid() + " has been unblocked.");
                }
            }

            for (Process p : IOQueueRemoval) {
                IOQueue.remove(p);
            }

            // clear the removal list
            IOQueueRemoval.clear();


        }
        // 4. Report statistics – use the following format (including the words specified at the beginning of the 
        // line)
        //     • Ready Processes: number of processes in the ready queue when the simulation ends
        //     • Processes in CPU: number of processes in the CPU array when the simulation ends
        //     • Blocked processes: number of processes in the I/O queue when the simulation ends
        //     • Completed processes: number of processes that have completed when the simulation 
        //     ends
        //     • Accounted for (ready+CPU+blocked+completed) of created processes
        //     • Number of CPUs: number of CPUs used in the simulation
        //     • Exiting at simulation time: number of simulation steps
        //     • “Simulation Result”, CPU quantum, number of completed processes, number of time 
        //     steps in the simulation, sum of the turnaround time for all completed processes
        for (Process p : readyQueue) {
            readyProcesses++;
        }
        for (Cpu cpu : cpuList) {
            if (cpu.getCurrentProcess() != null) {
                processesInCPU++;
            }
        }
        for (Process p : IOQueue) {
            blockedProcesses++;
        }
        // completedProcesses is already counted
        accountedFor = readyProcesses + processesInCPU + blockedProcesses + completedProcesses;
        // createdProcesses is already counted
        // numCPUs is already counted
        // simulationTime is already counted

        // Print the statistics
        System.out.println("Ready Processes: " + readyProcesses);
        System.out.println("Processes in CPU: " + processesInCPU);
        System.out.println("Blocked Processes: " + blockedProcesses);
        System.out.println("Completed Processes: " + completedProcesses);
        System.out.println("Accounted For: " + accountedFor + " of " + createdProcesses + " created processes");
        System.out.println("Number of CPUs: " + numCPUs);
        System.out.println("Exiting at simulation time: " + simulationTime);
        System.out.println("Simulation Result:" + quantum + "," + completedProcesses + "," + simulationTime + "," + sumTurnaroundTime);
        

        System.exit(0);
    }

    // createProcesses method
    // creates processes with random burst, I/O block, and CPU times
    public static void createProcesses(Random rand, int interactiveChance, int batchChance) {
        interactiveCreated = false;
        batchCreated = false;
        int burst = 0;
        int ioBlock = 0;
        int cpuTime = 0;
        String type = "";
        // for example Jansen used times through the loop as the 'Creation Time Stamp'
        if (rand.nextInt(interactiveChance) < 1) {
            createdProcesses++;
            interactiveCreated = true;
            type = "Interactive";
            // multiply target by .8 to 1.2 to get 20% range
            // lowerBound + (Upper-Lower) * random
            burst = (int) (10 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            ioBlock = (int) (5 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            cpuTime = (int) (20 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            Process p = new Process(type, burst, ioBlock, cpuTime, clock);
            readyQueue.add(p);

        }
        // delay to prevent both processes from being created at the same time to
        // confirm that the program works
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (rand.nextInt(batchChance) < 1) {
            createdProcesses++;
            batchCreated = true;
            type = "Batch";
            burst = (int) (250 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            ioBlock = (int) (10 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            cpuTime = (int) (500 * (0.8 + (1.2 - 0.8) * rand.nextDouble()));
            Process p = new Process(type, burst, ioBlock, cpuTime, clock);
            readyQueue.add(p);

        }
        // // debug: print out block time of all processes
        // for (Process p : readyQueue) {
        //     System.out.println("Process " + p.getPid() + " has a block time of " + p.getTimeToNextIoBlock());
        // } 
        // for (Process p : IOQueue) {
        //     System.out.println("Process " + p.getPid() + " has a block time of " + p.getTimeToNextIoBlock());
        // }
         
        return;
    }
}