begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|MetricsException
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
name|impl
operator|.
name|MetricsSystemImpl
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

begin_comment
comment|/**  * The default metrics system singleton. This class is used by all the daemon  * processes(such as NameNode, DataNode, JobTracker etc.). During daemon process  * initialization the processes call {@link DefaultMetricsSystem#init(String)}  * to initialize the {@link MetricsSystem}.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|DefaultMetricsSystem
specifier|public
enum|enum
name|DefaultMetricsSystem
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
comment|// the singleton
DECL|field|impl
specifier|private
name|AtomicReference
argument_list|<
name|MetricsSystem
argument_list|>
name|impl
init|=
operator|new
name|AtomicReference
argument_list|<
name|MetricsSystem
argument_list|>
argument_list|(
operator|new
name|MetricsSystemImpl
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|miniClusterMode
specifier|volatile
name|boolean
name|miniClusterMode
init|=
literal|false
decl_stmt|;
DECL|field|mBeanNames
specifier|transient
specifier|final
name|UniqueNames
name|mBeanNames
init|=
operator|new
name|UniqueNames
argument_list|()
decl_stmt|;
DECL|field|sourceNames
specifier|transient
specifier|final
name|UniqueNames
name|sourceNames
init|=
operator|new
name|UniqueNames
argument_list|()
decl_stmt|;
comment|/**    * Convenience method to initialize the metrics system    * @param prefix  for the metrics system configuration    * @return the metrics system instance    */
DECL|method|initialize (String prefix)
specifier|public
specifier|static
name|MetricsSystem
name|initialize
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|INSTANCE
operator|.
name|init
argument_list|(
name|prefix
argument_list|)
return|;
block|}
DECL|method|init (String prefix)
name|MetricsSystem
name|init
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|impl
operator|.
name|get
argument_list|()
operator|.
name|init
argument_list|(
name|prefix
argument_list|)
return|;
block|}
comment|/**    * @return the metrics system object    */
DECL|method|instance ()
specifier|public
specifier|static
name|MetricsSystem
name|instance
parameter_list|()
block|{
return|return
name|INSTANCE
operator|.
name|getImpl
argument_list|()
return|;
block|}
comment|/**    * Shutdown the metrics system    */
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|INSTANCE
operator|.
name|shutdownInstance
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdownInstance ()
name|void
name|shutdownInstance
parameter_list|()
block|{
name|boolean
name|last
init|=
name|impl
operator|.
name|get
argument_list|()
operator|.
name|shutdown
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
condition|)
synchronized|synchronized
init|(
name|this
init|)
block|{
name|mBeanNames
operator|.
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|sourceNames
operator|.
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setInstance (MetricsSystem ms)
specifier|public
specifier|static
name|MetricsSystem
name|setInstance
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
return|return
name|INSTANCE
operator|.
name|setImpl
argument_list|(
name|ms
argument_list|)
return|;
block|}
DECL|method|setImpl (MetricsSystem ms)
name|MetricsSystem
name|setImpl
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
return|return
name|impl
operator|.
name|getAndSet
argument_list|(
name|ms
argument_list|)
return|;
block|}
DECL|method|getImpl ()
name|MetricsSystem
name|getImpl
parameter_list|()
block|{
return|return
name|impl
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setMiniClusterMode (boolean choice)
specifier|public
specifier|static
name|void
name|setMiniClusterMode
parameter_list|(
name|boolean
name|choice
parameter_list|)
block|{
name|INSTANCE
operator|.
name|miniClusterMode
operator|=
name|choice
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|inMiniClusterMode ()
specifier|public
specifier|static
name|boolean
name|inMiniClusterMode
parameter_list|()
block|{
return|return
name|INSTANCE
operator|.
name|miniClusterMode
return|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|newMBeanName (String name)
specifier|public
specifier|static
name|ObjectName
name|newMBeanName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|INSTANCE
operator|.
name|newObjectName
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|removeMBeanName (ObjectName name)
specifier|public
specifier|static
name|void
name|removeMBeanName
parameter_list|(
name|ObjectName
name|name
parameter_list|)
block|{
name|INSTANCE
operator|.
name|removeObjectName
argument_list|(
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|removeSourceName (String name)
specifier|public
specifier|static
name|void
name|removeSourceName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|INSTANCE
operator|.
name|removeSource
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|sourceName (String name, boolean dupOK)
specifier|public
specifier|static
name|String
name|sourceName
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|dupOK
parameter_list|)
block|{
return|return
name|INSTANCE
operator|.
name|newSourceName
argument_list|(
name|name
argument_list|,
name|dupOK
argument_list|)
return|;
block|}
DECL|method|newObjectName (String name)
specifier|synchronized
name|ObjectName
name|newObjectName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|mBeanNames
operator|.
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|miniClusterMode
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
name|name
operator|+
literal|" already exists!"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ObjectName
argument_list|(
name|mBeanNames
operator|.
name|uniqueName
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|removeObjectName (String name)
specifier|synchronized
name|void
name|removeObjectName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|mBeanNames
operator|.
name|map
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|removeSource (String name)
specifier|synchronized
name|void
name|removeSource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|sourceNames
operator|.
name|map
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|newSourceName (String name, boolean dupOK)
specifier|synchronized
name|String
name|newSourceName
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|dupOK
parameter_list|)
block|{
if|if
condition|(
name|sourceNames
operator|.
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|dupOK
condition|)
block|{
return|return
name|name
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|miniClusterMode
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Metrics source "
operator|+
name|name
operator|+
literal|" already exists!"
argument_list|)
throw|;
block|}
block|}
return|return
name|sourceNames
operator|.
name|uniqueName
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

