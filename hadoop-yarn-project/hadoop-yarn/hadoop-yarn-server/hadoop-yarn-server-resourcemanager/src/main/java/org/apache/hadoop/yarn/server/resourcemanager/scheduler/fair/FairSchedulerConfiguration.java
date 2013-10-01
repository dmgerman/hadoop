begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|conf
operator|.
name|Configuration
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
name|records
operator|.
name|Resource
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
name|server
operator|.
name|utils
operator|.
name|BuilderUtils
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|FairSchedulerConfiguration
specifier|public
class|class
name|FairSchedulerConfiguration
extends|extends
name|Configuration
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Increment request grant-able by the RM scheduler.     * These properties are looked up in the yarn-site.xml  */
DECL|field|RM_SCHEDULER_INCREMENT_ALLOCATION_MB
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER_INCREMENT_ALLOCATION_MB
init|=
name|YarnConfiguration
operator|.
name|YARN_PREFIX
operator|+
literal|"scheduler.increment-allocation-mb"
decl_stmt|;
DECL|field|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_MB
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_MB
init|=
literal|1024
decl_stmt|;
DECL|field|RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
init|=
name|YarnConfiguration
operator|.
name|YARN_PREFIX
operator|+
literal|"scheduler.increment-allocation-vcores"
decl_stmt|;
DECL|field|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
init|=
literal|1
decl_stmt|;
DECL|field|CONF_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONF_PREFIX
init|=
literal|"yarn.scheduler.fair."
decl_stmt|;
DECL|field|ALLOCATION_FILE
specifier|protected
specifier|static
specifier|final
name|String
name|ALLOCATION_FILE
init|=
name|CONF_PREFIX
operator|+
literal|"allocation.file"
decl_stmt|;
DECL|field|DEFAULT_ALLOCATION_FILE
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_ALLOCATION_FILE
init|=
literal|"fair-scheduler.xml"
decl_stmt|;
DECL|field|EVENT_LOG_DIR
specifier|protected
specifier|static
specifier|final
name|String
name|EVENT_LOG_DIR
init|=
literal|"eventlog.dir"
decl_stmt|;
comment|/** Whether to use the user name as the queue name (instead of "default") if    * the request does not specify a queue. */
DECL|field|USER_AS_DEFAULT_QUEUE
specifier|protected
specifier|static
specifier|final
name|String
name|USER_AS_DEFAULT_QUEUE
init|=
name|CONF_PREFIX
operator|+
literal|"user-as-default-queue"
decl_stmt|;
DECL|field|DEFAULT_USER_AS_DEFAULT_QUEUE
specifier|protected
specifier|static
specifier|final
name|boolean
name|DEFAULT_USER_AS_DEFAULT_QUEUE
init|=
literal|true
decl_stmt|;
DECL|field|DEFAULT_LOCALITY_THRESHOLD
specifier|protected
specifier|static
specifier|final
name|float
name|DEFAULT_LOCALITY_THRESHOLD
init|=
operator|-
literal|1.0f
decl_stmt|;
comment|/** Cluster threshold for node locality. */
DECL|field|LOCALITY_THRESHOLD_NODE
specifier|protected
specifier|static
specifier|final
name|String
name|LOCALITY_THRESHOLD_NODE
init|=
name|CONF_PREFIX
operator|+
literal|"locality.threshold.node"
decl_stmt|;
DECL|field|DEFAULT_LOCALITY_THRESHOLD_NODE
specifier|protected
specifier|static
specifier|final
name|float
name|DEFAULT_LOCALITY_THRESHOLD_NODE
init|=
name|DEFAULT_LOCALITY_THRESHOLD
decl_stmt|;
comment|/** Cluster threshold for rack locality. */
DECL|field|LOCALITY_THRESHOLD_RACK
specifier|protected
specifier|static
specifier|final
name|String
name|LOCALITY_THRESHOLD_RACK
init|=
name|CONF_PREFIX
operator|+
literal|"locality.threshold.rack"
decl_stmt|;
DECL|field|DEFAULT_LOCALITY_THRESHOLD_RACK
specifier|protected
specifier|static
specifier|final
name|float
name|DEFAULT_LOCALITY_THRESHOLD_RACK
init|=
name|DEFAULT_LOCALITY_THRESHOLD
decl_stmt|;
comment|/** Delay for node locality. */
DECL|field|LOCALITY_DELAY_NODE_MS
specifier|protected
specifier|static
specifier|final
name|String
name|LOCALITY_DELAY_NODE_MS
init|=
name|CONF_PREFIX
operator|+
literal|"locality-delay-node-ms"
decl_stmt|;
DECL|field|DEFAULT_LOCALITY_DELAY_NODE_MS
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_LOCALITY_DELAY_NODE_MS
init|=
operator|-
literal|1L
decl_stmt|;
comment|/** Delay for rack locality. */
DECL|field|LOCALITY_DELAY_RACK_MS
specifier|protected
specifier|static
specifier|final
name|String
name|LOCALITY_DELAY_RACK_MS
init|=
name|CONF_PREFIX
operator|+
literal|"locality-delay-rack-ms"
decl_stmt|;
DECL|field|DEFAULT_LOCALITY_DELAY_RACK_MS
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_LOCALITY_DELAY_RACK_MS
init|=
operator|-
literal|1L
decl_stmt|;
comment|/** Enable continuous scheduling or not. */
DECL|field|CONTINUOUS_SCHEDULING_ENABLED
specifier|protected
specifier|static
specifier|final
name|String
name|CONTINUOUS_SCHEDULING_ENABLED
init|=
name|CONF_PREFIX
operator|+
literal|"continuous-scheduling-enabled"
decl_stmt|;
DECL|field|DEFAULT_CONTINUOUS_SCHEDULING_ENABLED
specifier|protected
specifier|static
specifier|final
name|boolean
name|DEFAULT_CONTINUOUS_SCHEDULING_ENABLED
init|=
literal|false
decl_stmt|;
comment|/** Sleep time of each pass in continuous scheduling (5ms in default) */
DECL|field|CONTINUOUS_SCHEDULING_SLEEP_MS
specifier|protected
specifier|static
specifier|final
name|String
name|CONTINUOUS_SCHEDULING_SLEEP_MS
init|=
name|CONF_PREFIX
operator|+
literal|"continuous-scheduling-sleep-ms"
decl_stmt|;
DECL|field|DEFAULT_CONTINUOUS_SCHEDULING_SLEEP_MS
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_CONTINUOUS_SCHEDULING_SLEEP_MS
init|=
literal|5
decl_stmt|;
comment|/** Whether preemption is enabled. */
DECL|field|PREEMPTION
specifier|protected
specifier|static
specifier|final
name|String
name|PREEMPTION
init|=
name|CONF_PREFIX
operator|+
literal|"preemption"
decl_stmt|;
DECL|field|DEFAULT_PREEMPTION
specifier|protected
specifier|static
specifier|final
name|boolean
name|DEFAULT_PREEMPTION
init|=
literal|false
decl_stmt|;
DECL|field|PREEMPTION_INTERVAL
specifier|protected
specifier|static
specifier|final
name|String
name|PREEMPTION_INTERVAL
init|=
name|CONF_PREFIX
operator|+
literal|"preemptionInterval"
decl_stmt|;
DECL|field|DEFAULT_PREEMPTION_INTERVAL
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_PREEMPTION_INTERVAL
init|=
literal|5000
decl_stmt|;
DECL|field|WAIT_TIME_BEFORE_KILL
specifier|protected
specifier|static
specifier|final
name|String
name|WAIT_TIME_BEFORE_KILL
init|=
name|CONF_PREFIX
operator|+
literal|"waitTimeBeforeKill"
decl_stmt|;
DECL|field|DEFAULT_WAIT_TIME_BEFORE_KILL
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_WAIT_TIME_BEFORE_KILL
init|=
literal|15000
decl_stmt|;
comment|/** Whether to assign multiple containers in one check-in. */
DECL|field|ASSIGN_MULTIPLE
specifier|protected
specifier|static
specifier|final
name|String
name|ASSIGN_MULTIPLE
init|=
name|CONF_PREFIX
operator|+
literal|"assignmultiple"
decl_stmt|;
DECL|field|DEFAULT_ASSIGN_MULTIPLE
specifier|protected
specifier|static
specifier|final
name|boolean
name|DEFAULT_ASSIGN_MULTIPLE
init|=
literal|false
decl_stmt|;
comment|/** Whether to give more weight to apps requiring many resources. */
DECL|field|SIZE_BASED_WEIGHT
specifier|protected
specifier|static
specifier|final
name|String
name|SIZE_BASED_WEIGHT
init|=
name|CONF_PREFIX
operator|+
literal|"sizebasedweight"
decl_stmt|;
DECL|field|DEFAULT_SIZE_BASED_WEIGHT
specifier|protected
specifier|static
specifier|final
name|boolean
name|DEFAULT_SIZE_BASED_WEIGHT
init|=
literal|false
decl_stmt|;
comment|/** Maximum number of containers to assign on each check-in. */
DECL|field|MAX_ASSIGN
specifier|protected
specifier|static
specifier|final
name|String
name|MAX_ASSIGN
init|=
name|CONF_PREFIX
operator|+
literal|"max.assign"
decl_stmt|;
DECL|field|DEFAULT_MAX_ASSIGN
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_ASSIGN
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|FairSchedulerConfiguration (Configuration conf)
specifier|public
name|FairSchedulerConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getMinimumAllocation ()
specifier|public
name|Resource
name|getMinimumAllocation
parameter_list|()
block|{
name|int
name|mem
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|int
name|cpu
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|mem
argument_list|,
name|cpu
argument_list|)
return|;
block|}
DECL|method|getMaximumAllocation ()
specifier|public
name|Resource
name|getMaximumAllocation
parameter_list|()
block|{
name|int
name|mem
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|int
name|cpu
init|=
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|mem
argument_list|,
name|cpu
argument_list|)
return|;
block|}
DECL|method|getIncrementAllocation ()
specifier|public
name|Resource
name|getIncrementAllocation
parameter_list|()
block|{
name|int
name|incrementMemory
init|=
name|getInt
argument_list|(
name|RM_SCHEDULER_INCREMENT_ALLOCATION_MB
argument_list|,
name|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|int
name|incrementCores
init|=
name|getInt
argument_list|(
name|RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
argument_list|,
name|DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
name|incrementMemory
argument_list|,
name|incrementCores
argument_list|)
return|;
block|}
DECL|method|getUserAsDefaultQueue ()
specifier|public
name|boolean
name|getUserAsDefaultQueue
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|USER_AS_DEFAULT_QUEUE
argument_list|,
name|DEFAULT_USER_AS_DEFAULT_QUEUE
argument_list|)
return|;
block|}
DECL|method|getLocalityThresholdNode ()
specifier|public
name|float
name|getLocalityThresholdNode
parameter_list|()
block|{
return|return
name|getFloat
argument_list|(
name|LOCALITY_THRESHOLD_NODE
argument_list|,
name|DEFAULT_LOCALITY_THRESHOLD_NODE
argument_list|)
return|;
block|}
DECL|method|getLocalityThresholdRack ()
specifier|public
name|float
name|getLocalityThresholdRack
parameter_list|()
block|{
return|return
name|getFloat
argument_list|(
name|LOCALITY_THRESHOLD_RACK
argument_list|,
name|DEFAULT_LOCALITY_THRESHOLD_RACK
argument_list|)
return|;
block|}
DECL|method|isContinuousSchedulingEnabled ()
specifier|public
name|boolean
name|isContinuousSchedulingEnabled
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|CONTINUOUS_SCHEDULING_ENABLED
argument_list|,
name|DEFAULT_CONTINUOUS_SCHEDULING_ENABLED
argument_list|)
return|;
block|}
DECL|method|getContinuousSchedulingSleepMs ()
specifier|public
name|int
name|getContinuousSchedulingSleepMs
parameter_list|()
block|{
return|return
name|getInt
argument_list|(
name|CONTINUOUS_SCHEDULING_SLEEP_MS
argument_list|,
name|DEFAULT_CONTINUOUS_SCHEDULING_SLEEP_MS
argument_list|)
return|;
block|}
DECL|method|getLocalityDelayNodeMs ()
specifier|public
name|long
name|getLocalityDelayNodeMs
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|LOCALITY_DELAY_NODE_MS
argument_list|,
name|DEFAULT_LOCALITY_DELAY_NODE_MS
argument_list|)
return|;
block|}
DECL|method|getLocalityDelayRackMs ()
specifier|public
name|long
name|getLocalityDelayRackMs
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|LOCALITY_DELAY_RACK_MS
argument_list|,
name|DEFAULT_LOCALITY_DELAY_RACK_MS
argument_list|)
return|;
block|}
DECL|method|getPreemptionEnabled ()
specifier|public
name|boolean
name|getPreemptionEnabled
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|PREEMPTION
argument_list|,
name|DEFAULT_PREEMPTION
argument_list|)
return|;
block|}
DECL|method|getAssignMultiple ()
specifier|public
name|boolean
name|getAssignMultiple
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|ASSIGN_MULTIPLE
argument_list|,
name|DEFAULT_ASSIGN_MULTIPLE
argument_list|)
return|;
block|}
DECL|method|getMaxAssign ()
specifier|public
name|int
name|getMaxAssign
parameter_list|()
block|{
return|return
name|getInt
argument_list|(
name|MAX_ASSIGN
argument_list|,
name|DEFAULT_MAX_ASSIGN
argument_list|)
return|;
block|}
DECL|method|getSizeBasedWeight ()
specifier|public
name|boolean
name|getSizeBasedWeight
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|SIZE_BASED_WEIGHT
argument_list|,
name|DEFAULT_SIZE_BASED_WEIGHT
argument_list|)
return|;
block|}
comment|/**    * Path to XML file containing allocations. If the    * path is relative, it is searched for in the    * classpath, but loaded like a regular File.    */
DECL|method|getAllocationFile ()
specifier|public
name|File
name|getAllocationFile
parameter_list|()
block|{
name|String
name|allocFilePath
init|=
name|get
argument_list|(
name|ALLOCATION_FILE
argument_list|,
name|DEFAULT_ALLOCATION_FILE
argument_list|)
decl_stmt|;
name|File
name|allocFile
init|=
operator|new
name|File
argument_list|(
name|allocFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allocFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|allocFilePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|allocFilePath
operator|+
literal|" not found on the classpath."
argument_list|)
expr_stmt|;
name|allocFile
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Allocation file "
operator|+
name|url
operator|+
literal|" found on the classpath is not on the local filesystem."
argument_list|)
throw|;
block|}
else|else
block|{
name|allocFile
operator|=
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|allocFile
return|;
block|}
DECL|method|getEventlogDir ()
specifier|public
name|String
name|getEventlogDir
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|EVENT_LOG_DIR
argument_list|,
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
literal|"/tmp/"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"fairscheduler"
argument_list|)
return|;
block|}
DECL|method|getPreemptionInterval ()
specifier|public
name|int
name|getPreemptionInterval
parameter_list|()
block|{
return|return
name|getInt
argument_list|(
name|PREEMPTION_INTERVAL
argument_list|,
name|DEFAULT_PREEMPTION_INTERVAL
argument_list|)
return|;
block|}
DECL|method|getWaitTimeBeforeKill ()
specifier|public
name|int
name|getWaitTimeBeforeKill
parameter_list|()
block|{
return|return
name|getInt
argument_list|(
name|WAIT_TIME_BEFORE_KILL
argument_list|,
name|DEFAULT_WAIT_TIME_BEFORE_KILL
argument_list|)
return|;
block|}
DECL|method|getUsePortForNodeName ()
specifier|public
name|boolean
name|getUsePortForNodeName
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_INCLUDE_PORT_IN_NODE_NAME
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_USE_PORT_FOR_NODE_NAME
argument_list|)
return|;
block|}
comment|/**    * Parses a resource config value of a form like "1024", "1024 mb",    * or "1024 mb, 3 vcores". If no units are given, megabytes are assumed.    *     * @throws AllocationConfigurationException    */
DECL|method|parseResourceConfigValue (String val)
specifier|public
specifier|static
name|Resource
name|parseResourceConfigValue
parameter_list|(
name|String
name|val
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
try|try
block|{
name|int
name|memory
init|=
name|findResource
argument_list|(
name|val
argument_list|,
literal|"mb"
argument_list|)
decl_stmt|;
name|int
name|vcores
init|=
name|findResource
argument_list|(
name|val
argument_list|,
literal|"vcores"
argument_list|)
decl_stmt|;
return|return
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AllocationConfigurationException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"Error reading resource config"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|findResource (String val, String units)
specifier|private
specifier|static
name|int
name|findResource
parameter_list|(
name|String
name|val
parameter_list|,
name|String
name|units
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\d+) ?"
operator|+
name|units
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AllocationConfigurationException
argument_list|(
literal|"Missing resource: "
operator|+
name|units
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

