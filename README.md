# Akka to many timer

Finding resource usage of akka framework
when there are too many timers on system

## program
Assuming that we have to many jobs thats wants to be executed in different times.

The job Logic actor is trying to simulate the job role

![story](img/story.png)


## Test Env
* Akka version: 2.5.21
* Java: openjdk version "11.0.1"

# Result

## Run With 2 job

```
app {
  worker-count = 5
  job-count = 2
}
```

The App Take 75Mb of heap before GC and 1% of cpu

![2-job-thread](img/2-JobTop.png)
![2-job-heap](img/2-JobMemory.png)

I don't know why heap usage are **increasing** !

The program starts new thread called scheduler and its sleeping.

![2-job-thread](img/2-JobThreads.png)

