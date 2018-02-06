begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.allocationfile
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
operator|.
name|allocationfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * This class is capable of serializing allocation file data to a file  * in XML format.  * See {@link #writeToFile(String)} method for the implementation.  */
end_comment

begin_class
DECL|class|AllocationFileWriter
specifier|public
specifier|final
class|class
name|AllocationFileWriter
block|{
DECL|field|queueMaxAppsDefault
specifier|private
name|Integer
name|queueMaxAppsDefault
decl_stmt|;
DECL|field|queueMaxResourcesDefault
specifier|private
name|String
name|queueMaxResourcesDefault
decl_stmt|;
DECL|field|userMaxAppsDefault
specifier|private
name|Integer
name|userMaxAppsDefault
decl_stmt|;
DECL|field|queueMaxAMShareDefault
specifier|private
name|Double
name|queueMaxAMShareDefault
decl_stmt|;
DECL|field|defaultMinSharePreemptionTimeout
specifier|private
name|Integer
name|defaultMinSharePreemptionTimeout
decl_stmt|;
DECL|field|defaultFairSharePreemptionTimeout
specifier|private
name|Integer
name|defaultFairSharePreemptionTimeout
decl_stmt|;
DECL|field|defaultFairSharePreemptionThreshold
specifier|private
name|Double
name|defaultFairSharePreemptionThreshold
decl_stmt|;
DECL|field|defaultQueueSchedulingPolicy
specifier|private
name|String
name|defaultQueueSchedulingPolicy
decl_stmt|;
DECL|field|queues
specifier|private
name|List
argument_list|<
name|AllocationFileQueue
argument_list|>
name|queues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|userSettings
specifier|private
name|UserSettings
name|userSettings
decl_stmt|;
DECL|method|AllocationFileWriter ()
specifier|private
name|AllocationFileWriter
parameter_list|()
block|{   }
DECL|method|create ()
specifier|public
specifier|static
name|AllocationFileWriter
name|create
parameter_list|()
block|{
return|return
operator|new
name|AllocationFileWriter
argument_list|()
return|;
block|}
DECL|method|queue (String queueName)
specifier|public
name|AllocationFileSimpleQueueBuilder
name|queue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
return|return
operator|new
name|AllocationFileSimpleQueueBuilder
argument_list|(
name|this
argument_list|,
name|queueName
argument_list|)
return|;
block|}
DECL|method|queueMaxAppsDefault (int value)
specifier|public
name|AllocationFileWriter
name|queueMaxAppsDefault
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueMaxAppsDefault
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueMaxResourcesDefault (String value)
specifier|public
name|AllocationFileWriter
name|queueMaxResourcesDefault
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueMaxResourcesDefault
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|userMaxAppsDefault (int value)
specifier|public
name|AllocationFileWriter
name|userMaxAppsDefault
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|userMaxAppsDefault
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|queueMaxAMShareDefault (double value)
specifier|public
name|AllocationFileWriter
name|queueMaxAMShareDefault
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|queueMaxAMShareDefault
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|defaultMinSharePreemptionTimeout (int value)
specifier|public
name|AllocationFileWriter
name|defaultMinSharePreemptionTimeout
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|defaultMinSharePreemptionTimeout
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|defaultFairSharePreemptionTimeout (int value)
specifier|public
name|AllocationFileWriter
name|defaultFairSharePreemptionTimeout
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|defaultFairSharePreemptionTimeout
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|defaultFairSharePreemptionThreshold ( double value)
specifier|public
name|AllocationFileWriter
name|defaultFairSharePreemptionThreshold
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|defaultFairSharePreemptionThreshold
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|defaultQueueSchedulingPolicy (String value)
specifier|public
name|AllocationFileWriter
name|defaultQueueSchedulingPolicy
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|defaultQueueSchedulingPolicy
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|userSettings (String username)
specifier|public
name|UserSettings
operator|.
name|Builder
name|userSettings
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
operator|new
name|UserSettings
operator|.
name|Builder
argument_list|(
name|this
argument_list|,
name|username
argument_list|)
return|;
block|}
DECL|method|addQueue (AllocationFileQueue queue)
name|void
name|addQueue
parameter_list|(
name|AllocationFileQueue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queues
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
DECL|method|setUserSettings (UserSettings userSettings)
name|void
name|setUserSettings
parameter_list|(
name|UserSettings
name|userSettings
parameter_list|)
block|{
name|this
operator|.
name|userSettings
operator|=
name|userSettings
expr_stmt|;
block|}
DECL|method|printQueues (PrintWriter pw, List<AllocationFileQueue> queues)
specifier|static
name|void
name|printQueues
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|List
argument_list|<
name|AllocationFileQueue
argument_list|>
name|queues
parameter_list|)
block|{
for|for
control|(
name|AllocationFileQueue
name|queue
range|:
name|queues
control|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|queue
operator|.
name|render
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printUserSettings (PrintWriter pw)
specifier|private
name|void
name|printUserSettings
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|userSettings
operator|.
name|render
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addIfPresent (PrintWriter pw, String tag, Supplier<String> supplier)
specifier|static
name|void
name|addIfPresent
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|tag
parameter_list|,
name|Supplier
argument_list|<
name|String
argument_list|>
name|supplier
parameter_list|)
block|{
if|if
condition|(
name|supplier
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"<"
operator|+
name|tag
operator|+
literal|">"
operator|+
name|supplier
operator|.
name|get
argument_list|()
operator|+
literal|"</"
operator|+
name|tag
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createNumberSupplier (Object number)
specifier|static
name|String
name|createNumberSupplier
parameter_list|(
name|Object
name|number
parameter_list|)
block|{
if|if
condition|(
name|number
operator|!=
literal|null
condition|)
block|{
return|return
name|number
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|writeHeader (PrintWriter pw)
specifier|private
name|void
name|writeHeader
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFooter (PrintWriter pw)
specifier|private
name|void
name|writeFooter
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
block|}
DECL|method|writeToFile (String filename)
specifier|public
name|void
name|writeToFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|PrintWriter
name|pw
decl_stmt|;
try|try
block|{
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|filename
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|writeHeader
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|queues
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|printQueues
argument_list|(
name|pw
argument_list|,
name|queues
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userSettings
operator|!=
literal|null
condition|)
block|{
name|printUserSettings
argument_list|(
name|pw
argument_list|)
expr_stmt|;
block|}
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"queueMaxAppsDefault"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|queueMaxAppsDefault
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"queueMaxResourcesDefault"
argument_list|,
parameter_list|()
lambda|->
name|queueMaxResourcesDefault
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"userMaxAppsDefault"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|userMaxAppsDefault
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"queueMaxAMShareDefault"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|queueMaxAMShareDefault
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"defaultMinSharePreemptionTimeout"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|defaultMinSharePreemptionTimeout
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"defaultFairSharePreemptionTimeout"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|defaultFairSharePreemptionTimeout
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"defaultFairSharePreemptionThreshold"
argument_list|,
parameter_list|()
lambda|->
name|createNumberSupplier
argument_list|(
name|defaultFairSharePreemptionThreshold
argument_list|)
argument_list|)
expr_stmt|;
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"defaultQueueSchedulingPolicy"
argument_list|,
parameter_list|()
lambda|->
name|defaultQueueSchedulingPolicy
argument_list|)
expr_stmt|;
name|writeFooter
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

