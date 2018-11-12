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
name|MetricsRecordBuilder
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * A mutable int gauge  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MutableGaugeInt
specifier|public
class|class
name|MutableGaugeInt
extends|extends
name|MutableGauge
block|{
DECL|field|value
specifier|private
name|AtomicInteger
name|value
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|MutableGaugeInt (MetricsInfo info, int initValue)
name|MutableGaugeInt
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|initValue
parameter_list|)
block|{
name|super
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|.
name|set
argument_list|(
name|initValue
argument_list|)
expr_stmt|;
block|}
DECL|method|value ()
specifier|public
name|int
name|value
parameter_list|()
block|{
return|return
name|value
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|incr ()
specifier|public
name|void
name|incr
parameter_list|()
block|{
name|incr
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment by delta    * @param delta of the increment    */
DECL|method|incr (int delta)
specifier|public
name|void
name|incr
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|value
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decr ()
specifier|public
name|void
name|decr
parameter_list|()
block|{
name|decr
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * decrement by delta    * @param delta of the decrement    */
DECL|method|decr (int delta)
specifier|public
name|void
name|decr
parameter_list|(
name|int
name|delta
parameter_list|)
block|{
name|value
operator|.
name|addAndGet
argument_list|(
operator|-
name|delta
argument_list|)
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the value of the metric    * @param value to set    */
DECL|method|set (int value)
specifier|public
name|void
name|set
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|snapshot (MetricsRecordBuilder builder, boolean all)
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
if|if
condition|(
name|all
operator|||
name|changed
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addGauge
argument_list|(
name|info
argument_list|()
argument_list|,
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|clearChanged
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return  the value of the metric    */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

