## DiskSim Simulator

DiskSim is a disk simulation software used for I/O analysis research. This module consists of a Java simulation of two of the scheduling algorithms used in DiskSim.

# Scheduling algorithms

DiskSim provides a number of scheduling algorithms, which perform accesses on an access queue in some order.

In this module, *CYCLE_CYL* is an implementation of a cyclic cyndrical access, where accesses are ordered by disk cylinders.

*SATF_OPT* is an implementation of a shortest access time first approach, which calculates the optimal access time and orders accesses optimally in order.

# Tests

The *tests* folder contains sample input and output. The input files contain the disk parameters and the accesses.

# References

- Bucy, John S., et al. "The DiskSim Simulation Environment Version 4.0 Reference Manual (cmu-pdl-08-101)." Parallel Data Laboratory (2008): 26.
APA
