begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|DELETED
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|DELETING
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|QUASI_CLOSED
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
name|metrics2
operator|.
name|MetricsCollector
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
name|Interns
import|;
end_import

begin_comment
comment|/**  * Metrics source to report number of containers in different states.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"SCM Container Manager Metrics"
argument_list|,
name|context
operator|=
literal|"ozone"
argument_list|)
DECL|class|SCMContainerMetrics
specifier|public
class|class
name|SCMContainerMetrics
implements|implements
name|MetricsSource
block|{
DECL|field|scmmxBean
specifier|private
specifier|final
name|SCMMXBean
name|scmmxBean
decl_stmt|;
DECL|field|SOURCE
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE
init|=
name|SCMContainerMetrics
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|method|SCMContainerMetrics (SCMMXBean scmmxBean)
specifier|public
name|SCMContainerMetrics
parameter_list|(
name|SCMMXBean
name|scmmxBean
parameter_list|)
block|{
name|this
operator|.
name|scmmxBean
operator|=
name|scmmxBean
expr_stmt|;
block|}
DECL|method|create (SCMMXBean scmmxBean)
specifier|public
specifier|static
name|SCMContainerMetrics
name|create
parameter_list|(
name|SCMMXBean
name|scmmxBean
parameter_list|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
name|SOURCE
argument_list|,
literal|"Storage "
operator|+
literal|"Container Manager Metrics"
argument_list|,
operator|new
name|SCMContainerMetrics
argument_list|(
name|scmmxBean
argument_list|)
argument_list|)
return|;
block|}
DECL|method|unRegister ()
specifier|public
name|void
name|unRegister
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|ms
operator|.
name|unregisterSource
argument_list|(
name|SOURCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SuspiciousMethodCalls"
argument_list|)
DECL|method|getMetrics (MetricsCollector collector, boolean all)
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|stateCount
init|=
name|scmmxBean
operator|.
name|getContainerStateCount
argument_list|()
decl_stmt|;
name|collector
operator|.
name|addRecord
argument_list|(
name|SOURCE
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"OpenContainers"
argument_list|,
literal|"Number of open containers"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|OPEN
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"ClosingContainers"
argument_list|,
literal|"Number of containers in closing state"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|CLOSING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"QuasiClosedContainers"
argument_list|,
literal|"Number of containers in quasi closed state"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|QUASI_CLOSED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"ClosedContainers"
argument_list|,
literal|"Number of containers in closed state"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|CLOSED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"DeletingContainers"
argument_list|,
literal|"Number of containers in deleting state"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|DELETING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|Interns
operator|.
name|info
argument_list|(
literal|"DeletedContainers"
argument_list|,
literal|"Number of containers in deleted state"
argument_list|)
argument_list|,
name|stateCount
operator|.
name|get
argument_list|(
name|DELETED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

