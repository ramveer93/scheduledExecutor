# scheduledExecutor
The code will execute two apis parallelly in the interval of 5 minutes. <br>
Change the api url and token in application.properties file and start the application as spring boot application, 
it will write the result to a file in filesystem, Please change the location of output file as well in HydroPerfService.java
file at line 100.<br>
To change the run configurations go to HydroPerfController.java at line 56 , r is the runnable task , 1 is the first occurance of the task and 5 is the time interval to run r .



