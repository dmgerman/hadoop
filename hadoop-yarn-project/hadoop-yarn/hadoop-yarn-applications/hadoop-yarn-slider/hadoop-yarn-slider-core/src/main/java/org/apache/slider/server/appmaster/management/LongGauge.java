begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.management
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|management
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
import|;
end_import

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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * This is a {@link AtomicLong} which acts as a metrics gauge: its state can be exposed as  * a metrics.  * It also exposes some of the same method names as the Codahale Counter class, so that  * it's easy to swap in.  *  */
end_comment

begin_class
DECL|class|LongGauge
specifier|public
class|class
name|LongGauge
extends|extends
name|AtomicLong
implements|implements
name|Metric
implements|,
name|Gauge
argument_list|<
name|Long
argument_list|>
block|{
comment|/**    * Instantiate    * @param val current value    */
DECL|method|LongGauge (long val)
specifier|public
name|LongGauge
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|super
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate with value 0    */
DECL|method|LongGauge ()
specifier|public
name|LongGauge
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the value as a metric    * @return current value    */
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
return|return
name|get
argument_list|()
return|;
block|}
comment|/**    * Method from {@Code counter}; used here for drop-in replacement    * without any recompile    * @return current value    */
DECL|method|getCount ()
specifier|public
name|Long
name|getCount
parameter_list|()
block|{
return|return
name|get
argument_list|()
return|;
block|}
comment|/**    * {@code ++}    */
DECL|method|inc ()
specifier|public
name|void
name|inc
parameter_list|()
block|{
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@code --}    */
DECL|method|dec ()
specifier|public
name|void
name|dec
parameter_list|()
block|{
name|decrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Decrement to the floor of 0. Operations in parallel may cause confusion here,    * but it will still never go below zero    * @param delta delta    * @return the current value    */
DECL|method|decToFloor (long delta)
specifier|public
name|long
name|decToFloor
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|long
name|l
init|=
name|get
argument_list|()
decl_stmt|;
name|long
name|r
init|=
name|l
operator|-
name|delta
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
block|{
name|r
operator|=
literal|0
expr_stmt|;
block|}
comment|// if this fails, the decrement has been lost
name|compareAndSet
argument_list|(
name|l
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

