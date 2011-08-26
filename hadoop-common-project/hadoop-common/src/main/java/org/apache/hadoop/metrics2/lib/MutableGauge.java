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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|*
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
name|MetricsInfo
import|;
end_import

begin_comment
comment|/**  * The mutable gauge metric interface  */
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
DECL|class|MutableGauge
specifier|public
specifier|abstract
class|class
name|MutableGauge
extends|extends
name|MutableMetric
block|{
DECL|field|info
specifier|private
specifier|final
name|MetricsInfo
name|info
decl_stmt|;
DECL|method|MutableGauge (MetricsInfo info)
specifier|protected
name|MutableGauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|checkNotNull
argument_list|(
name|info
argument_list|,
literal|"metric info"
argument_list|)
expr_stmt|;
block|}
DECL|method|info ()
specifier|protected
name|MetricsInfo
name|info
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|/**    * Increment the value of the metric by 1    */
DECL|method|incr ()
specifier|public
specifier|abstract
name|void
name|incr
parameter_list|()
function_decl|;
comment|/**    * Decrement the value of the metric by 1    */
DECL|method|decr ()
specifier|public
specifier|abstract
name|void
name|decr
parameter_list|()
function_decl|;
block|}
end_class

end_unit

