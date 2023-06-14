# Concurrent-MBTA-Simulator

Given a code skeleton, created simulation of train, where passengers will ride trains between stations, boarding and deboarding to complete their journey. Simulation will generate a log showing the movements of passengers and trains. Simulation is multi-threaded, with a thread for each passenger and each train. 

Also built a verifier that checks that the simulation result is sensible, e.g., passengers can only deboard trains at the stations the trains are at, trains must move along their lines in sequence, etc.

# What I Gained

* Practice with concurrency
* Experience dealing with data races, mutual exclusion with locks, deadlocks, threading Nuances, and the synchronized Keyword
* Practice with threading patterns, the producer/consumer pattern, and factory design pattern
* Practice with JSON and JSON library
