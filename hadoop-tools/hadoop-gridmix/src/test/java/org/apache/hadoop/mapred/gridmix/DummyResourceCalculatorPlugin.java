begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|classification
operator|.
name|InterfaceAudience
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
name|ResourceCalculatorPlugin
import|;
end_import

begin_comment
comment|/**  * Plugin class to test resource information reported by NM. Use configuration  * items {@link #MAXVMEM_TESTING_PROPERTY} and {@link #MAXPMEM_TESTING_PROPERTY}  * to tell NM the total vmem and the total pmem. Use configuration items  * {@link #NUM_PROCESSORS}, {@link #CPU_FREQUENCY}, {@link #CUMULATIVE_CPU_TIME}  * and {@link #CPU_USAGE} to tell TT the CPU information.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DummyResourceCalculatorPlugin
specifier|public
class|class
name|DummyResourceCalculatorPlugin
extends|extends
name|ResourceCalculatorPlugin
block|{
DECL|method|DummyResourceCalculatorPlugin ()
name|DummyResourceCalculatorPlugin
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** max vmem on the TT */
DECL|field|MAXVMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|MAXVMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.maxvmem.testing"
decl_stmt|;
comment|/** max pmem on the TT */
DECL|field|MAXPMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|MAXPMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.maxpmem.testing"
decl_stmt|;
comment|/** number of processors for testing */
DECL|field|NUM_PROCESSORS
specifier|public
specifier|static
specifier|final
name|String
name|NUM_PROCESSORS
init|=
literal|"mapred.tasktracker.numprocessors.testing"
decl_stmt|;
comment|/** CPU frequency for testing */
DECL|field|CPU_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|CPU_FREQUENCY
init|=
literal|"mapred.tasktracker.cpufrequency.testing"
decl_stmt|;
comment|/** cumulative CPU usage time for testing */
DECL|field|CUMULATIVE_CPU_TIME
specifier|public
specifier|static
specifier|final
name|String
name|CUMULATIVE_CPU_TIME
init|=
literal|"mapred.tasktracker.cumulativecputime.testing"
decl_stmt|;
comment|/** CPU usage percentage for testing */
DECL|field|CPU_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|CPU_USAGE
init|=
literal|"mapred.tasktracker.cpuusage.testing"
decl_stmt|;
comment|/** cumulative number of bytes read over the network */
DECL|field|NETWORK_BYTES_READ
specifier|public
specifier|static
specifier|final
name|String
name|NETWORK_BYTES_READ
init|=
literal|"mapred.tasktracker.networkread.testing"
decl_stmt|;
comment|/** cumulative number of bytes written over the network */
DECL|field|NETWORK_BYTES_WRITTEN
specifier|public
specifier|static
specifier|final
name|String
name|NETWORK_BYTES_WRITTEN
init|=
literal|"mapred.tasktracker.networkwritten.testing"
decl_stmt|;
comment|/** cumulative number of bytes read from disks */
DECL|field|STORAGE_BYTES_READ
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_BYTES_READ
init|=
literal|"mapred.tasktracker.storageread.testing"
decl_stmt|;
comment|/** cumulative number of bytes written to disks */
DECL|field|STORAGE_BYTES_WRITTEN
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_BYTES_WRITTEN
init|=
literal|"mapred.tasktracker.storagewritten.testing"
decl_stmt|;
comment|/** process cumulative CPU usage time for testing */
DECL|field|PROC_CUMULATIVE_CPU_TIME
specifier|public
specifier|static
specifier|final
name|String
name|PROC_CUMULATIVE_CPU_TIME
init|=
literal|"mapred.tasktracker.proccumulativecputime.testing"
decl_stmt|;
comment|/** process pmem for testing */
DECL|field|PROC_PMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|PROC_PMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.procpmem.testing"
decl_stmt|;
comment|/** process vmem for testing */
DECL|field|PROC_VMEM_TESTING_PROPERTY
specifier|public
specifier|static
specifier|final
name|String
name|PROC_VMEM_TESTING_PROPERTY
init|=
literal|"mapred.tasktracker.procvmem.testing"
decl_stmt|;
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXVMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXPMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXVMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|MAXPMEM_TESTING_PROPERTY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumProcessors ()
specifier|public
name|int
name|getNumProcessors
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|NUM_PROCESSORS
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
name|getNumProcessors
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCpuFrequency ()
specifier|public
name|long
name|getCpuFrequency
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|CPU_FREQUENCY
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|CUMULATIVE_CPU_TIME
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCpuUsagePercentage ()
specifier|public
name|float
name|getCpuUsagePercentage
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getFloat
argument_list|(
name|CPU_USAGE
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNetworkBytesRead ()
specifier|public
name|long
name|getNetworkBytesRead
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|NETWORK_BYTES_READ
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNetworkBytesWritten ()
specifier|public
name|long
name|getNetworkBytesWritten
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|NETWORK_BYTES_WRITTEN
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getStorageBytesRead ()
specifier|public
name|long
name|getStorageBytesRead
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|STORAGE_BYTES_READ
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getStorageBytesWritten ()
specifier|public
name|long
name|getStorageBytesWritten
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|STORAGE_BYTES_WRITTEN
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

