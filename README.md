# Cloud Computing and Virtualization - 2018/19, 2nd Semester
Group 9 - Alameda

[83531 - Miguel Belém](mailto:miguelbelem@tecnico.ulisboa.pt)

[83567 - Tiago Gonçalves](mailto:tiago.miguel.c.g@tecnico.ulisboa.pt)

[83576 - Vítor Nunes](mailto:vitor.sobrinho.nunes@tecnico.ulisboa.pt)

## How to build and run
In order to simplify the process of compiling and instrumenting the source code
we created a script to automate these tasks. To load the script simply run
the following command on the project's root folder:

	source script.sh

From now on it is possible to perform the following tasks easily:
**Cleaning the class files**

	clean

**Compiling the source code**

	compile

**Instrumenting the solvers classes**

	inst

**Start the WebServer**

	wsvc

You should see a green OK message after every of the above commands if the
task were successfully.

## Metrics
All metrics are saved on ***Logs/*** folder in binary format and on Amazon DynamoDB
It is possible to display the content of a metric invoking the following command...

	readLog 0.bin

... for instance, to display the first metric saved by the WebServer.

## Load Balancer
The following table displays the basic configuration of the load balancer:
# Basic Properties

| Property / Attribute      | Value          |
|---------------------------|----------------|
| Type                      | Classic        |
| Idle timeout              | 600 seconds    |
| Access logs               | Disabled       |
| Cross-Zone Load Balancing | Enabled        |

# Health check
|                           |                  |
|---------------------------|------------------|
| Ping Target               | HTTP:8000/climb  |
| Timeout                   | 5 seconds        |
| Interval                  | 30 seconds       |
| Unhealthy threshold       | 2                |
| Healthy threshold         | 10               |

## Auto Scaler
|                           |                  |
|---------------------------|------------------|
| Desired Capacity          | 1                |
| Min # of instances        | 1                |
| Max # of instances        | 3                |
| Health Check Grace Period | 300              |
| Default Cooldown          | 300              |

#Scaling Policies
**Decrease group size**
|                           |                  |
|---------------------------|------------------|
| Policy type               | Step scaling     |
| Min # of instances        | awsec2-cnv-project-a09-High-CPU-Utilization breaches the alarm threshold: CPUUtilization <= 40 for 300 seconds |
| Take the action           | Remove 1 instance when 40 >= CPU Utilization > -infinity  |

**Increase group size**
|                           |                  |
|---------------------------|------------------|
| Policy type               | Step scaling     |
| Min # of instances        | awsec2-cnv-project-a09-High-CPU-Utilization breaches the alarm threshold: CPUUtilization >= 60 for 60 seconds |
| Take the action           | Add 1 instance when 60 <= CPU Utilization < +infinity  |
| Instances need            | 60 seconds to warm up after each step |
