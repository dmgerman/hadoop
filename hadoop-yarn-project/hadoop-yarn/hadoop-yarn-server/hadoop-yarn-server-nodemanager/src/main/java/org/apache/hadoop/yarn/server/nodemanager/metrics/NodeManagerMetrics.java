begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.metrics
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
name|nodemanager
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|MetricsSystem
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metric
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metrics
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableCounterInt
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableGaugeInt
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableRate
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for node manager"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|NodeManagerMetrics
specifier|public
class|class
name|NodeManagerMetrics
block|{
DECL|field|containersLaunched
annotation|@
name|Metric
name|MutableCounterInt
name|containersLaunched
decl_stmt|;
DECL|field|containersCompleted
annotation|@
name|Metric
name|MutableCounterInt
name|containersCompleted
decl_stmt|;
DECL|field|containersFailed
annotation|@
name|Metric
name|MutableCounterInt
name|containersFailed
decl_stmt|;
DECL|field|containersKilled
annotation|@
name|Metric
name|MutableCounterInt
name|containersKilled
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"# of initializing containers"
argument_list|)
DECL|field|containersIniting
name|MutableGaugeInt
name|containersIniting
decl_stmt|;
DECL|field|containersRunning
annotation|@
name|Metric
name|MutableGaugeInt
name|containersRunning
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Current allocated memory in GB"
argument_list|)
DECL|field|allocatedGB
name|MutableGaugeInt
name|allocatedGB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Current # of allocated containers"
argument_list|)
DECL|field|allocatedContainers
name|MutableGaugeInt
name|allocatedContainers
decl_stmt|;
DECL|field|availableGB
annotation|@
name|Metric
name|MutableGaugeInt
name|availableGB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Current allocated Virtual Cores"
argument_list|)
DECL|field|allocatedVCores
name|MutableGaugeInt
name|allocatedVCores
decl_stmt|;
DECL|field|availableVCores
annotation|@
name|Metric
name|MutableGaugeInt
name|availableVCores
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Container launch duration"
argument_list|)
DECL|field|containerLaunchDuration
name|MutableRate
name|containerLaunchDuration
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"# of bad local dirs"
argument_list|)
DECL|field|badLocalDirs
name|MutableGaugeInt
name|badLocalDirs
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"# of bad log dirs"
argument_list|)
DECL|field|badLogDirs
name|MutableGaugeInt
name|badLogDirs
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Disk utilization % on good local dirs"
argument_list|)
DECL|field|goodLocalDirsDiskUtilizationPerc
name|MutableGaugeInt
name|goodLocalDirsDiskUtilizationPerc
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Disk utilization % on good log dirs"
argument_list|)
DECL|field|goodLogDirsDiskUtilizationPerc
name|MutableGaugeInt
name|goodLogDirsDiskUtilizationPerc
decl_stmt|;
DECL|field|jvmMetrics
specifier|private
name|JvmMetrics
name|jvmMetrics
init|=
literal|null
decl_stmt|;
DECL|field|allocatedMB
specifier|private
name|long
name|allocatedMB
decl_stmt|;
DECL|field|availableMB
specifier|private
name|long
name|availableMB
decl_stmt|;
DECL|method|NodeManagerMetrics (JvmMetrics jvmMetrics)
specifier|public
name|NodeManagerMetrics
parameter_list|(
name|JvmMetrics
name|jvmMetrics
parameter_list|)
block|{
name|this
operator|.
name|jvmMetrics
operator|=
name|jvmMetrics
expr_stmt|;
block|}
DECL|method|create ()
specifier|public
specifier|static
name|NodeManagerMetrics
name|create
parameter_list|()
block|{
return|return
name|create
argument_list|(
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
argument_list|)
return|;
block|}
DECL|method|create (MetricsSystem ms)
specifier|static
name|NodeManagerMetrics
name|create
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
name|JvmMetrics
name|jm
init|=
name|JvmMetrics
operator|.
name|create
argument_list|(
literal|"NodeManager"
argument_list|,
literal|null
argument_list|,
name|ms
argument_list|)
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
operator|new
name|NodeManagerMetrics
argument_list|(
name|jm
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getJvmMetrics ()
specifier|public
name|JvmMetrics
name|getJvmMetrics
parameter_list|()
block|{
return|return
name|jvmMetrics
return|;
block|}
comment|// Potential instrumentation interface methods
DECL|method|launchedContainer ()
specifier|public
name|void
name|launchedContainer
parameter_list|()
block|{
name|containersLaunched
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|completedContainer ()
specifier|public
name|void
name|completedContainer
parameter_list|()
block|{
name|containersCompleted
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|failedContainer ()
specifier|public
name|void
name|failedContainer
parameter_list|()
block|{
name|containersFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|killedContainer ()
specifier|public
name|void
name|killedContainer
parameter_list|()
block|{
name|containersKilled
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|initingContainer ()
specifier|public
name|void
name|initingContainer
parameter_list|()
block|{
name|containersIniting
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endInitingContainer ()
specifier|public
name|void
name|endInitingContainer
parameter_list|()
block|{
name|containersIniting
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|runningContainer ()
specifier|public
name|void
name|runningContainer
parameter_list|()
block|{
name|containersRunning
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endRunningContainer ()
specifier|public
name|void
name|endRunningContainer
parameter_list|()
block|{
name|containersRunning
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|allocateContainer (Resource res)
specifier|public
name|void
name|allocateContainer
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocatedContainers
operator|.
name|incr
argument_list|()
expr_stmt|;
name|allocatedMB
operator|=
name|allocatedMB
operator|+
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|allocatedGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|allocatedMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|availableMB
operator|=
name|availableMB
operator|-
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|availableGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|availableMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|allocatedVCores
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|availableVCores
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|releaseContainer (Resource res)
specifier|public
name|void
name|releaseContainer
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocatedContainers
operator|.
name|decr
argument_list|()
expr_stmt|;
name|allocatedMB
operator|=
name|allocatedMB
operator|-
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|allocatedGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|allocatedMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|availableMB
operator|=
name|availableMB
operator|+
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|availableGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|availableMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|allocatedVCores
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
name|availableVCores
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|changeContainer (Resource before, Resource now)
specifier|public
name|void
name|changeContainer
parameter_list|(
name|Resource
name|before
parameter_list|,
name|Resource
name|now
parameter_list|)
block|{
name|long
name|deltaMB
init|=
name|now
operator|.
name|getMemorySize
argument_list|()
operator|-
name|before
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|int
name|deltaVCores
init|=
name|now
operator|.
name|getVirtualCores
argument_list|()
operator|-
name|before
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
name|allocatedMB
operator|=
name|allocatedMB
operator|+
name|deltaMB
expr_stmt|;
name|allocatedGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|allocatedMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|availableMB
operator|=
name|availableMB
operator|-
name|deltaMB
expr_stmt|;
name|availableGB
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|availableMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|allocatedVCores
operator|.
name|incr
argument_list|(
name|deltaVCores
argument_list|)
expr_stmt|;
name|availableVCores
operator|.
name|decr
argument_list|(
name|deltaVCores
argument_list|)
expr_stmt|;
block|}
DECL|method|addResource (Resource res)
specifier|public
name|void
name|addResource
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|availableMB
operator|=
name|availableMB
operator|+
name|res
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|availableGB
operator|.
name|incr
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|availableMB
operator|/
literal|1024d
argument_list|)
argument_list|)
expr_stmt|;
name|availableVCores
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addContainerLaunchDuration (long value)
specifier|public
name|void
name|addContainerLaunchDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|containerLaunchDuration
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setBadLocalDirs (int badLocalDirs)
specifier|public
name|void
name|setBadLocalDirs
parameter_list|(
name|int
name|badLocalDirs
parameter_list|)
block|{
name|this
operator|.
name|badLocalDirs
operator|.
name|set
argument_list|(
name|badLocalDirs
argument_list|)
expr_stmt|;
block|}
DECL|method|setBadLogDirs (int badLogDirs)
specifier|public
name|void
name|setBadLogDirs
parameter_list|(
name|int
name|badLogDirs
parameter_list|)
block|{
name|this
operator|.
name|badLogDirs
operator|.
name|set
argument_list|(
name|badLogDirs
argument_list|)
expr_stmt|;
block|}
DECL|method|setGoodLocalDirsDiskUtilizationPerc ( int goodLocalDirsDiskUtilizationPerc)
specifier|public
name|void
name|setGoodLocalDirsDiskUtilizationPerc
parameter_list|(
name|int
name|goodLocalDirsDiskUtilizationPerc
parameter_list|)
block|{
name|this
operator|.
name|goodLocalDirsDiskUtilizationPerc
operator|.
name|set
argument_list|(
name|goodLocalDirsDiskUtilizationPerc
argument_list|)
expr_stmt|;
block|}
DECL|method|setGoodLogDirsDiskUtilizationPerc ( int goodLogDirsDiskUtilizationPerc)
specifier|public
name|void
name|setGoodLogDirsDiskUtilizationPerc
parameter_list|(
name|int
name|goodLogDirsDiskUtilizationPerc
parameter_list|)
block|{
name|this
operator|.
name|goodLogDirsDiskUtilizationPerc
operator|.
name|set
argument_list|(
name|goodLogDirsDiskUtilizationPerc
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunningContainers ()
specifier|public
name|int
name|getRunningContainers
parameter_list|()
block|{
return|return
name|containersRunning
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getKilledContainers ()
specifier|public
name|int
name|getKilledContainers
parameter_list|()
block|{
return|return
name|containersKilled
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getFailedContainers ()
specifier|public
name|int
name|getFailedContainers
parameter_list|()
block|{
return|return
name|containersFailed
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCompletedContainers ()
specifier|public
name|int
name|getCompletedContainers
parameter_list|()
block|{
return|return
name|containersCompleted
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBadLogDirs ()
specifier|public
name|int
name|getBadLogDirs
parameter_list|()
block|{
return|return
name|badLogDirs
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBadLocalDirs ()
specifier|public
name|int
name|getBadLocalDirs
parameter_list|()
block|{
return|return
name|badLocalDirs
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGoodLogDirsDiskUtilizationPerc ()
specifier|public
name|int
name|getGoodLogDirsDiskUtilizationPerc
parameter_list|()
block|{
return|return
name|goodLogDirsDiskUtilizationPerc
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGoodLocalDirsDiskUtilizationPerc ()
specifier|public
name|int
name|getGoodLocalDirsDiskUtilizationPerc
parameter_list|()
block|{
return|return
name|goodLocalDirsDiskUtilizationPerc
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

