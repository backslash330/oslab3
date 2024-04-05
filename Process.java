
/*
 * Process.java
 * Creates a process object with a type, burst time, I/O block time, CPU time, and creation time.
 */
public class Process {
    private static int pidCounter = 0;
    private String type;
    private int pid;
    private int burst;
    private int ioBlock;
    private int cpuTime;
    private int creationTime;
    private int remainingTime;
    private int timeToNextIoBlock;
    private int completedTime; // will be used to calculate turnaround time
    private int remainingQuantum;
    private int remainingIOBlock;
    
    // Constructor
    public Process(String type, int burst, int ioBlock, int cpuTime, int creationTime) {
        this.type = type;
        this.burst = burst;
        this.ioBlock = ioBlock;
        this.cpuTime = cpuTime;
        this.creationTime = creationTime;
        this.pid = pidCounter;
        pidCounter++;
        this.remainingTime = cpuTime;
        this.timeToNextIoBlock = burst;
    }
    
    // toString method
    public String toString() {
        return "Process:\nType: " + type + 
        "\nPID: " + pid + 
        "\nBurst: " + burst + 
        "\nIO Block Time: " + ioBlock + 
        "\nCPU Time: " + cpuTime + 
        "\nCreation Time Stamp: " + creationTime;
    }

    // get pid
    public int getPid() {
        return pid;
    }

    // run the process
    public void run(int quantum) {
       // System.out.println("Running process " + pid);
       // System.out.println("Time to next I/O block: " + timeToNextIoBlock);
        if (remainingQuantum <= 0) {
            remainingQuantum = quantum;
        }
        if (timeToNextIoBlock <= quantum) {
            remainingTime -= timeToNextIoBlock;
            remainingQuantum = remainingQuantum - timeToNextIoBlock;
            timeToNextIoBlock = 0; // we wil reset this in another method
        } else {
            remainingTime -= quantum;
            timeToNextIoBlock -= quantum;
        }

    }

    // get remaining time
    public int getRemainingTime() {
        return remainingTime;
    }

    // get time to next I/O block
    public int getTimeToNextIoBlock() {
        return timeToNextIoBlock;
    }

    // get remaining quantum
    public int getRemainingQuantum() {
        return remainingQuantum;
    }

    // increment the ioBlock time
    public void incrementIO() {
        remainingIOBlock++;
    }

    // reset the time to next I/O block
    public void resetTimeToNextIoBlock() {
        timeToNextIoBlock = ioBlock;
        remainingIOBlock = 0;
    }

    // set completed time
    public void setCompletedTime(int completedTime) {
        this.completedTime = completedTime;
    }

    // get completed time
    public int getCompletedTime() {
        return completedTime;
    }

    // get creation time
    public int getCreationTime() {
        return creationTime;
    }

    // check if ioBlock is complete
    public boolean IOBlockCleared() {
        return remainingIOBlock == ioBlock;
    }

}