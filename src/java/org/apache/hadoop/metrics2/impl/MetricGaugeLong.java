begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
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
name|AbstractMetric
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
name|MetricType
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
name|MetricsVisitor
import|;
end_import

begin_class
DECL|class|MetricGaugeLong
class|class
name|MetricGaugeLong
extends|extends
name|AbstractMetric
block|{
DECL|field|value
specifier|final
name|long
name|value
decl_stmt|;
DECL|method|MetricGaugeLong (MetricsInfo info, long value)
name|MetricGaugeLong
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
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
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value ()
specifier|public
name|Long
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|type ()
specifier|public
name|MetricType
name|type
parameter_list|()
block|{
return|return
name|MetricType
operator|.
name|GAUGE
return|;
block|}
annotation|@
name|Override
DECL|method|visit (MetricsVisitor visitor)
specifier|public
name|void
name|visit
parameter_list|(
name|MetricsVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|gauge
argument_list|(
name|this
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

