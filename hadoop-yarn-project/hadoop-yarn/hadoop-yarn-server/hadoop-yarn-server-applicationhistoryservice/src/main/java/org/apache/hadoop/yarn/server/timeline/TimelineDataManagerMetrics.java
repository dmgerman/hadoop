begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
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
name|MutableCounterLong
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

begin_comment
comment|/** This class tracks metrics for the TimelineDataManager. */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for TimelineDataManager"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|TimelineDataManagerMetrics
specifier|public
class|class
name|TimelineDataManagerMetrics
block|{
annotation|@
name|Metric
argument_list|(
literal|"getEntities calls"
argument_list|)
DECL|field|getEntitiesOps
name|MutableCounterLong
name|getEntitiesOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Entities returned via getEntities"
argument_list|)
DECL|field|getEntitiesTotal
name|MutableCounterLong
name|getEntitiesTotal
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEntities processing time"
argument_list|)
DECL|field|getEntitiesTime
name|MutableRate
name|getEntitiesTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEntity calls"
argument_list|)
DECL|field|getEntityOps
name|MutableCounterLong
name|getEntityOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEntity processing time"
argument_list|)
DECL|field|getEntityTime
name|MutableRate
name|getEntityTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEvents calls"
argument_list|)
DECL|field|getEventsOps
name|MutableCounterLong
name|getEventsOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Events returned via getEvents"
argument_list|)
DECL|field|getEventsTotal
name|MutableCounterLong
name|getEventsTotal
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getEvents processing time"
argument_list|)
DECL|field|getEventsTime
name|MutableRate
name|getEventsTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"postEntities calls"
argument_list|)
DECL|field|postEntitiesOps
name|MutableCounterLong
name|postEntitiesOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Entities posted via postEntities"
argument_list|)
DECL|field|postEntitiesTotal
name|MutableCounterLong
name|postEntitiesTotal
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"postEntities processing time"
argument_list|)
DECL|field|postEntitiesTime
name|MutableRate
name|postEntitiesTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"putDomain calls"
argument_list|)
DECL|field|putDomainOps
name|MutableCounterLong
name|putDomainOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"putDomain processing time"
argument_list|)
DECL|field|putDomainTime
name|MutableRate
name|putDomainTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getDomain calls"
argument_list|)
DECL|field|getDomainOps
name|MutableCounterLong
name|getDomainOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getDomain processing time"
argument_list|)
DECL|field|getDomainTime
name|MutableRate
name|getDomainTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getDomains calls"
argument_list|)
DECL|field|getDomainsOps
name|MutableCounterLong
name|getDomainsOps
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Domains returned via getDomains"
argument_list|)
DECL|field|getDomainsTotal
name|MutableCounterLong
name|getDomainsTotal
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"getDomains processing time"
argument_list|)
DECL|field|getDomainsTime
name|MutableRate
name|getDomainsTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Total calls"
argument_list|)
DECL|method|totalOps ()
specifier|public
name|long
name|totalOps
parameter_list|()
block|{
return|return
name|getEntitiesOps
operator|.
name|value
argument_list|()
operator|+
name|getEntityOps
operator|.
name|value
argument_list|()
operator|+
name|getEventsOps
operator|.
name|value
argument_list|()
operator|+
name|postEntitiesOps
operator|.
name|value
argument_list|()
operator|+
name|putDomainOps
operator|.
name|value
argument_list|()
operator|+
name|getDomainOps
operator|.
name|value
argument_list|()
operator|+
name|getDomainsOps
operator|.
name|value
argument_list|()
return|;
block|}
DECL|field|instance
specifier|private
specifier|static
name|TimelineDataManagerMetrics
name|instance
init|=
literal|null
decl_stmt|;
DECL|method|TimelineDataManagerMetrics ()
name|TimelineDataManagerMetrics
parameter_list|()
block|{   }
DECL|method|create ()
specifier|public
specifier|static
specifier|synchronized
name|TimelineDataManagerMetrics
name|create
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|instance
operator|=
name|ms
operator|.
name|register
argument_list|(
operator|new
name|TimelineDataManagerMetrics
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|incrGetEntitiesOps ()
specifier|public
name|void
name|incrGetEntitiesOps
parameter_list|()
block|{
name|getEntitiesOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetEntitiesTotal (long delta)
specifier|public
name|void
name|incrGetEntitiesTotal
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|getEntitiesTotal
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|addGetEntitiesTime (long msec)
specifier|public
name|void
name|addGetEntitiesTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|getEntitiesTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrGetEntityOps ()
specifier|public
name|void
name|incrGetEntityOps
parameter_list|()
block|{
name|getEntityOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addGetEntityTime (long msec)
specifier|public
name|void
name|addGetEntityTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|getEntityTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrGetEventsOps ()
specifier|public
name|void
name|incrGetEventsOps
parameter_list|()
block|{
name|getEventsOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetEventsTotal (long delta)
specifier|public
name|void
name|incrGetEventsTotal
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|getEventsTotal
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|addGetEventsTime (long msec)
specifier|public
name|void
name|addGetEventsTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|getEventsTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrPostEntitiesOps ()
specifier|public
name|void
name|incrPostEntitiesOps
parameter_list|()
block|{
name|postEntitiesOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrPostEntitiesTotal (long delta)
specifier|public
name|void
name|incrPostEntitiesTotal
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|postEntitiesTotal
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|addPostEntitiesTime (long msec)
specifier|public
name|void
name|addPostEntitiesTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|postEntitiesTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrPutDomainOps ()
specifier|public
name|void
name|incrPutDomainOps
parameter_list|()
block|{
name|putDomainOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addPutDomainTime (long msec)
specifier|public
name|void
name|addPutDomainTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|putDomainTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrGetDomainOps ()
specifier|public
name|void
name|incrGetDomainOps
parameter_list|()
block|{
name|getDomainOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|addGetDomainTime (long msec)
specifier|public
name|void
name|addGetDomainTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|getDomainTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
DECL|method|incrGetDomainsOps ()
specifier|public
name|void
name|incrGetDomainsOps
parameter_list|()
block|{
name|getDomainsOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrGetDomainsTotal (long delta)
specifier|public
name|void
name|incrGetDomainsTotal
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|getDomainsTotal
operator|.
name|incr
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|addGetDomainsTime (long msec)
specifier|public
name|void
name|addGetDomainsTime
parameter_list|(
name|long
name|msec
parameter_list|)
block|{
name|getDomainsTime
operator|.
name|add
argument_list|(
name|msec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

