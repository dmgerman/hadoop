begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|metrics2
operator|.
name|MetricsSource
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
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
name|TestQueueMetrics
operator|.
name|userSource
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * This class holds queue and user metrics for a particular queue,  * used for testing metrics.  * Reference for the parent queue is also stored for every queue,  * except if the queue is root.  */
end_comment

begin_class
DECL|class|QueueInfo
specifier|public
specifier|final
class|class
name|QueueInfo
block|{
DECL|field|parentQueueInfo
specifier|private
specifier|final
name|QueueInfo
name|parentQueueInfo
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|Queue
name|queue
decl_stmt|;
DECL|field|queueMetrics
specifier|final
name|QueueMetrics
name|queueMetrics
decl_stmt|;
DECL|field|queueSource
specifier|final
name|MetricsSource
name|queueSource
decl_stmt|;
DECL|field|userSource
specifier|final
name|MetricsSource
name|userSource
decl_stmt|;
DECL|method|QueueInfo (QueueInfo parent, String queueName, MetricsSystem ms, Configuration conf, String user)
specifier|public
name|QueueInfo
parameter_list|(
name|QueueInfo
name|parent
parameter_list|,
name|String
name|queueName
parameter_list|,
name|MetricsSystem
name|ms
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|Queue
name|parentQueue
init|=
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
operator|.
name|queue
decl_stmt|;
name|parentQueueInfo
operator|=
name|parent
expr_stmt|;
name|queueMetrics
operator|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|parentQueue
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|queue
operator|=
name|mock
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|queue
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueMetrics
argument_list|)
expr_stmt|;
name|queueSource
operator|=
name|ms
operator|.
name|getSource
argument_list|(
name|QueueMetrics
operator|.
name|sourceName
argument_list|(
name|queueName
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// need to call getUserMetrics so that a non-null userSource is returned
comment|// with the call to userSource(..)
name|queueMetrics
operator|.
name|getUserMetrics
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|userSource
operator|=
name|userSource
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
DECL|method|getRoot ()
specifier|public
name|QueueInfo
name|getRoot
parameter_list|()
block|{
name|QueueInfo
name|root
init|=
name|this
decl_stmt|;
while|while
condition|(
name|root
operator|.
name|parentQueueInfo
operator|!=
literal|null
condition|)
block|{
name|root
operator|=
name|root
operator|.
name|parentQueueInfo
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
DECL|method|checkAllQueueSources (Consumer<MetricsSource> consumer)
specifier|public
name|void
name|checkAllQueueSources
parameter_list|(
name|Consumer
argument_list|<
name|MetricsSource
argument_list|>
name|consumer
parameter_list|)
block|{
name|checkQueueSourcesRecursive
argument_list|(
name|this
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQueueSourcesRecursive (QueueInfo queueInfo, Consumer<MetricsSource> consumer)
specifier|private
name|void
name|checkQueueSourcesRecursive
parameter_list|(
name|QueueInfo
name|queueInfo
parameter_list|,
name|Consumer
argument_list|<
name|MetricsSource
argument_list|>
name|consumer
parameter_list|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|queueInfo
operator|.
name|queueSource
argument_list|)
expr_stmt|;
if|if
condition|(
name|queueInfo
operator|.
name|parentQueueInfo
operator|!=
literal|null
condition|)
block|{
name|checkQueueSourcesRecursive
argument_list|(
name|queueInfo
operator|.
name|parentQueueInfo
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkAllQueueMetrics (Consumer<QueueMetrics> consumer)
specifier|public
name|void
name|checkAllQueueMetrics
parameter_list|(
name|Consumer
argument_list|<
name|QueueMetrics
argument_list|>
name|consumer
parameter_list|)
block|{
name|checkAllQueueMetricsRecursive
argument_list|(
name|this
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
DECL|method|checkAllQueueMetricsRecursive (QueueInfo queueInfo, Consumer <QueueMetrics> consumer)
specifier|private
name|void
name|checkAllQueueMetricsRecursive
parameter_list|(
name|QueueInfo
name|queueInfo
parameter_list|,
name|Consumer
argument_list|<
name|QueueMetrics
argument_list|>
name|consumer
parameter_list|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|queueInfo
operator|.
name|queueMetrics
argument_list|)
expr_stmt|;
if|if
condition|(
name|queueInfo
operator|.
name|parentQueueInfo
operator|!=
literal|null
condition|)
block|{
name|checkAllQueueMetricsRecursive
argument_list|(
name|queueInfo
operator|.
name|parentQueueInfo
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

