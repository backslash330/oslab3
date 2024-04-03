public class Cpu {
    private int quantum;
    private int CpuNumber;
    private Process currentProcess;

    public Cpu(int quantum, int CpuNumber) {
        this.quantum = quantum;
        this.CpuNumber = CpuNumber;
    }   

    public void run() {
        if (currentProcess == null) {
            // System.out.println("CPU " + CpuNumber + " is idle.");
        } else {
            // System.out.println("CPU " + CpuNumber + " is running process " + currentProcess.getPid());
            currentProcess.run(quantum);
        }
    }

    public Process removecompletedProcess() {
        if (currentProcess != null && currentProcess.getRemainingTime() == 0) {
            Process temp = currentProcess;
            currentProcess = null;
            return temp;
        }
        return null;
    }

    public Process removeBlockedProcess() {
        if (currentProcess != null && currentProcess.getTimeToNextIoBlock() == 0) {
            Process temp = currentProcess;
            currentProcess = null;
            return temp;
        }
        return null;
    }

    public Process removeQuantumProcess() {
        if (currentProcess != null && currentProcess.getRemainingQuantum() == 0) {
            Process temp = currentProcess;
            currentProcess = null;
            return temp;
        }
        return null;
    }

    public void addProcess(Process process) {
        currentProcess = process;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process currentProcess) {
        this.currentProcess = currentProcess;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public int getCpuNumber() {
        return CpuNumber;
    }

    public void setCpuNumber(int CpuNumber) {
        this.CpuNumber = CpuNumber;
    }

    public void print() {
        System.out.println("CPU " + CpuNumber + " Quantum: " + quantum + " Current Process: " + currentProcess.getPid());
    }
}
