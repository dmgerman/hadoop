begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskLog
operator|.
name|LogName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|ID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|Apps
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|MapReduceChildJVM
specifier|public
class|class
name|MapReduceChildJVM
block|{
DECL|method|getTaskLogFile (LogName filter)
specifier|private
specifier|static
name|String
name|getTaskLogFile
parameter_list|(
name|LogName
name|filter
parameter_list|)
block|{
return|return
name|ApplicationConstants
operator|.
name|LOG_DIR_EXPANSION_VAR
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|filter
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getChildEnv (JobConf jobConf, boolean isMap)
specifier|private
specifier|static
name|String
name|getChildEnv
parameter_list|(
name|JobConf
name|jobConf
parameter_list|,
name|boolean
name|isMap
parameter_list|)
block|{
if|if
condition|(
name|isMap
condition|)
block|{
return|return
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_ENV
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_ENV
argument_list|)
argument_list|)
return|;
block|}
return|return
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_ENV
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_ENV
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getChildLogLevel (JobConf conf, boolean isMap)
specifier|private
specifier|static
name|String
name|getChildLogLevel
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|boolean
name|isMap
parameter_list|)
block|{
if|if
condition|(
name|isMap
condition|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MAP_LOG_LEVEL
argument_list|,
name|JobConf
operator|.
name|DEFAULT_LOG_LEVEL
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_LOG_LEVEL
argument_list|,
name|JobConf
operator|.
name|DEFAULT_LOG_LEVEL
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|setVMEnv (Map<String, String> environment, Task task)
specifier|public
specifier|static
name|void
name|setVMEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|,
name|Task
name|task
parameter_list|)
block|{
name|JobConf
name|conf
init|=
name|task
operator|.
name|conf
decl_stmt|;
comment|// Add the env variables passed by the user
name|String
name|mapredChildEnv
init|=
name|getChildEnv
argument_list|(
name|conf
argument_list|,
name|task
operator|.
name|isMapTask
argument_list|()
argument_list|)
decl_stmt|;
name|Apps
operator|.
name|setEnvFromInputString
argument_list|(
name|environment
argument_list|,
name|mapredChildEnv
argument_list|)
expr_stmt|;
comment|// Set logging level in the environment.
comment|// This is so that, if the child forks another "bin/hadoop" (common in
comment|// streaming) it will have the correct loglevel.
name|environment
operator|.
name|put
argument_list|(
literal|"HADOOP_ROOT_LOGGER"
argument_list|,
name|getChildLogLevel
argument_list|(
name|conf
argument_list|,
name|task
operator|.
name|isMapTask
argument_list|()
argument_list|)
operator|+
literal|",CLA"
argument_list|)
expr_stmt|;
comment|// TODO: The following is useful for instance in streaming tasks. Should be
comment|// set in ApplicationMaster's env by the RM.
name|String
name|hadoopClientOpts
init|=
name|System
operator|.
name|getenv
argument_list|(
literal|"HADOOP_CLIENT_OPTS"
argument_list|)
decl_stmt|;
if|if
condition|(
name|hadoopClientOpts
operator|==
literal|null
condition|)
block|{
name|hadoopClientOpts
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|hadoopClientOpts
operator|=
name|hadoopClientOpts
operator|+
literal|" "
expr_stmt|;
block|}
comment|// FIXME: don't think this is also needed given we already set java
comment|// properties.
name|long
name|logSize
init|=
name|TaskLog
operator|.
name|getTaskLogLength
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Vector
argument_list|<
name|String
argument_list|>
name|logProps
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|setupLog4jProperties
argument_list|(
name|task
argument_list|,
name|logProps
argument_list|,
name|logSize
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|logProps
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|hadoopClientOpts
operator|=
name|hadoopClientOpts
operator|+
name|buffer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"HADOOP_CLIENT_OPTS"
argument_list|,
name|hadoopClientOpts
argument_list|)
expr_stmt|;
comment|// Add stdout/stderr env
name|environment
operator|.
name|put
argument_list|(
name|MRJobConfig
operator|.
name|STDOUT_LOGFILE_ENV
argument_list|,
name|getTaskLogFile
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|STDOUT
argument_list|)
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|MRJobConfig
operator|.
name|STDERR_LOGFILE_ENV
argument_list|,
name|getTaskLogFile
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|STDERR
argument_list|)
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID_ENV
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildJavaOpts (JobConf jobConf, boolean isMapTask)
specifier|private
specifier|static
name|String
name|getChildJavaOpts
parameter_list|(
name|JobConf
name|jobConf
parameter_list|,
name|boolean
name|isMapTask
parameter_list|)
block|{
name|String
name|userClasspath
init|=
literal|""
decl_stmt|;
name|String
name|adminClasspath
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|isMapTask
condition|)
block|{
name|userClasspath
operator|=
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|,
name|JobConf
operator|.
name|DEFAULT_MAPRED_TASK_JAVA_OPTS
argument_list|)
argument_list|)
expr_stmt|;
name|adminClasspath
operator|=
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MAPRED_MAP_ADMIN_JAVA_OPTS
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MAPRED_ADMIN_JAVA_OPTS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userClasspath
operator|=
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|,
name|JobConf
operator|.
name|DEFAULT_MAPRED_TASK_JAVA_OPTS
argument_list|)
argument_list|)
expr_stmt|;
name|adminClasspath
operator|=
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MAPRED_REDUCE_ADMIN_JAVA_OPTS
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MAPRED_ADMIN_JAVA_OPTS
argument_list|)
expr_stmt|;
block|}
comment|// Add admin classpath first so it can be overridden by user.
return|return
name|adminClasspath
operator|+
literal|" "
operator|+
name|userClasspath
return|;
block|}
DECL|method|setupLog4jProperties (Task task, Vector<String> vargs, long logSize)
specifier|private
specifier|static
name|void
name|setupLog4jProperties
parameter_list|(
name|Task
name|task
parameter_list|,
name|Vector
argument_list|<
name|String
argument_list|>
name|vargs
parameter_list|,
name|long
name|logSize
parameter_list|)
block|{
name|String
name|logLevel
init|=
name|getChildLogLevel
argument_list|(
name|task
operator|.
name|conf
argument_list|,
name|task
operator|.
name|isMapTask
argument_list|()
argument_list|)
decl_stmt|;
name|MRApps
operator|.
name|addLog4jSystemProperties
argument_list|(
name|logLevel
argument_list|,
name|logSize
argument_list|,
name|vargs
argument_list|)
expr_stmt|;
block|}
DECL|method|getVMCommand ( InetSocketAddress taskAttemptListenerAddr, Task task, ID jvmID)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getVMCommand
parameter_list|(
name|InetSocketAddress
name|taskAttemptListenerAddr
parameter_list|,
name|Task
name|task
parameter_list|,
name|ID
name|jvmID
parameter_list|)
block|{
name|TaskAttemptID
name|attemptID
init|=
name|task
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
name|JobConf
name|conf
init|=
name|task
operator|.
name|conf
decl_stmt|;
name|Vector
argument_list|<
name|String
argument_list|>
name|vargs
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|vargs
operator|.
name|add
argument_list|(
name|Environment
operator|.
name|JAVA_HOME
operator|.
name|$
argument_list|()
operator|+
literal|"/bin/java"
argument_list|)
expr_stmt|;
comment|// Add child (task) java-vm options.
comment|//
comment|// The following symbols if present in mapred.{map|reduce}.child.java.opts
comment|// value are replaced:
comment|// + @taskid@ is interpolated with value of TaskID.
comment|// Other occurrences of @ will not be altered.
comment|//
comment|// Example with multiple arguments and substitutions, showing
comment|// jvm GC logging, and start of a passwordless JVM JMX agent so can
comment|// connect with jconsole and the likes to watch child memory, threads
comment|// and get thread dumps.
comment|//
comment|//<property>
comment|//<name>mapred.map.child.java.opts</name>
comment|//<value>-Xmx 512M -verbose:gc -Xloggc:/tmp/@taskid@.gc \
comment|//           -Dcom.sun.management.jmxremote.authenticate=false \
comment|//           -Dcom.sun.management.jmxremote.ssl=false \
comment|//</value>
comment|//</property>
comment|//
comment|//<property>
comment|//<name>mapred.reduce.child.java.opts</name>
comment|//<value>-Xmx 1024M -verbose:gc -Xloggc:/tmp/@taskid@.gc \
comment|//           -Dcom.sun.management.jmxremote.authenticate=false \
comment|//           -Dcom.sun.management.jmxremote.ssl=false \
comment|//</value>
comment|//</property>
comment|//
name|String
name|javaOpts
init|=
name|getChildJavaOpts
argument_list|(
name|conf
argument_list|,
name|task
operator|.
name|isMapTask
argument_list|()
argument_list|)
decl_stmt|;
name|javaOpts
operator|=
name|javaOpts
operator|.
name|replace
argument_list|(
literal|"@taskid@"
argument_list|,
name|attemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|javaOptsSplit
init|=
name|javaOpts
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|javaOptsSplit
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vargs
operator|.
name|add
argument_list|(
name|javaOptsSplit
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|Path
name|childTmpDir
init|=
operator|new
name|Path
argument_list|(
name|Environment
operator|.
name|PWD
operator|.
name|$
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_CONTAINER_TEMP_DIR
argument_list|)
decl_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"-Djava.io.tmpdir="
operator|+
name|childTmpDir
argument_list|)
expr_stmt|;
comment|// Setup the log4j prop
name|long
name|logSize
init|=
name|TaskLog
operator|.
name|getTaskLogLength
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|setupLog4jProperties
argument_list|(
name|task
argument_list|,
name|vargs
argument_list|,
name|logSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|getProfileEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|conf
operator|.
name|getProfileTaskRange
argument_list|(
name|task
operator|.
name|isMapTask
argument_list|()
argument_list|)
operator|.
name|isIncluded
argument_list|(
name|task
operator|.
name|getPartition
argument_list|()
argument_list|)
condition|)
block|{
name|vargs
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|conf
operator|.
name|getProfileParams
argument_list|()
argument_list|,
name|getTaskLogFile
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|PROFILE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|task
operator|.
name|isMapTask
argument_list|()
condition|)
block|{
name|vargs
operator|.
name|add
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|TASK_MAP_PROFILE_PARAMS
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vargs
operator|.
name|add
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|TASK_REDUCE_PROFILE_PARAMS
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Add main class and its arguments
name|vargs
operator|.
name|add
argument_list|(
name|YarnChild
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// main of Child
comment|// pass TaskAttemptListener's address
name|vargs
operator|.
name|add
argument_list|(
name|taskAttemptListenerAddr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|taskAttemptListenerAddr
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
name|attemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// pass task identifier
comment|// Finally add the jvmID
name|vargs
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jvmID
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"1>"
operator|+
name|getTaskLogFile
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|STDOUT
argument_list|)
argument_list|)
expr_stmt|;
name|vargs
operator|.
name|add
argument_list|(
literal|"2>"
operator|+
name|getTaskLogFile
argument_list|(
name|TaskLog
operator|.
name|LogName
operator|.
name|STDERR
argument_list|)
argument_list|)
expr_stmt|;
comment|// Final commmand
name|StringBuilder
name|mergedCommand
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|CharSequence
name|str
range|:
name|vargs
control|)
block|{
name|mergedCommand
operator|.
name|append
argument_list|(
name|str
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|Vector
argument_list|<
name|String
argument_list|>
name|vargsFinal
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|vargsFinal
operator|.
name|add
argument_list|(
name|mergedCommand
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|vargsFinal
return|;
block|}
block|}
end_class

end_unit

