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
name|MetricsRecordBuilder
import|;
end_import

begin_comment
comment|/**  * The mutable metric interface  */
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
DECL|class|MutableMetric
specifier|public
specifier|abstract
class|class
name|MutableMetric
block|{
DECL|field|changed
specifier|private
specifier|volatile
name|boolean
name|changed
init|=
literal|true
decl_stmt|;
comment|/**    * Get a snapshot of the metric    * @param builder the metrics record builder    * @param all if true, snapshot unchanged metrics as well    */
DECL|method|snapshot (MetricsRecordBuilder builder, boolean all)
specifier|public
specifier|abstract
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|,
name|boolean
name|all
parameter_list|)
function_decl|;
comment|/**    * Get a snapshot of metric if changed    * @param builder the metrics record builder    */
DECL|method|snapshot (MetricsRecordBuilder builder)
specifier|public
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|builder
parameter_list|)
block|{
name|snapshot
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the changed flag in mutable operations    */
DECL|method|setChanged ()
specifier|protected
name|void
name|setChanged
parameter_list|()
block|{
name|changed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Clear the changed flag in the snapshot operations    */
DECL|method|clearChanged ()
specifier|protected
name|void
name|clearChanged
parameter_list|()
block|{
name|changed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * @return  true if metric is changed since last snapshot/snapshot    */
DECL|method|changed ()
specifier|public
name|boolean
name|changed
parameter_list|()
block|{
return|return
name|changed
return|;
block|}
block|}
end_class

end_unit

