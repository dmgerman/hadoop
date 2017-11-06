begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|MetricsInfo
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
name|MetricsRegistry
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
import|import static
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
operator|.
name|info
import|;
end_import

begin_class
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"yarn-native-service"
argument_list|)
DECL|class|ServiceMetrics
specifier|public
class|class
name|ServiceMetrics
implements|implements
name|MetricsSource
block|{
annotation|@
name|Metric
argument_list|(
literal|"containers requested"
argument_list|)
DECL|field|containersRequested
specifier|public
name|MutableGaugeInt
name|containersRequested
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers running"
argument_list|)
DECL|field|containersRunning
specifier|public
name|MutableGaugeInt
name|containersRunning
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers ready"
argument_list|)
DECL|field|containersReady
specifier|public
name|MutableGaugeInt
name|containersReady
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers desired"
argument_list|)
DECL|field|containersDesired
specifier|public
name|MutableGaugeInt
name|containersDesired
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers succeeded"
argument_list|)
DECL|field|containersSucceeded
specifier|public
name|MutableGaugeInt
name|containersSucceeded
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers failed"
argument_list|)
DECL|field|containersFailed
specifier|public
name|MutableGaugeInt
name|containersFailed
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers preempted"
argument_list|)
DECL|field|containersPreempted
specifier|public
name|MutableGaugeInt
name|containersPreempted
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers surplus"
argument_list|)
DECL|field|surplusContainers
specifier|public
name|MutableGaugeInt
name|surplusContainers
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"containers failed due to disk failure"
argument_list|)
DECL|field|containersDiskFailure
specifier|public
name|MutableGaugeInt
name|containersDiskFailure
decl_stmt|;
DECL|field|registry
specifier|protected
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|method|ServiceMetrics (MetricsInfo metricsInfo)
specifier|public
name|ServiceMetrics
parameter_list|(
name|MetricsInfo
name|metricsInfo
parameter_list|)
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|metricsInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|registry
operator|.
name|snapshot
argument_list|(
name|collector
operator|.
name|addRecord
argument_list|(
name|registry
operator|.
name|info
argument_list|()
argument_list|)
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|register (String name, String description)
specifier|public
specifier|static
name|ServiceMetrics
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|ServiceMetrics
name|metrics
init|=
operator|new
name|ServiceMetrics
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
name|description
argument_list|)
argument_list|)
decl_stmt|;
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
DECL|method|tag (String name, String description, String value)
specifier|public
name|void
name|tag
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|registry
operator|.
name|tag
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ServiceMetrics{"
operator|+
literal|"containersRequested="
operator|+
name|containersRequested
operator|.
name|value
argument_list|()
operator|+
literal|", containersRunning="
operator|+
name|containersRunning
operator|.
name|value
argument_list|()
operator|+
literal|", containersDesired="
operator|+
name|containersDesired
operator|.
name|value
argument_list|()
operator|+
literal|", containersSucceeded="
operator|+
name|containersSucceeded
operator|.
name|value
argument_list|()
operator|+
literal|", containersFailed="
operator|+
name|containersFailed
operator|.
name|value
argument_list|()
operator|+
literal|", containersPreempted="
operator|+
name|containersPreempted
operator|.
name|value
argument_list|()
operator|+
literal|", surplusContainers="
operator|+
name|surplusContainers
operator|.
name|value
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

