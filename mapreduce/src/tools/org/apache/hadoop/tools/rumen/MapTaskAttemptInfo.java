begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapred
operator|.
name|TaskStatus
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * {@link MapTaskAttemptInfo} represents the information with regard to a  * map task attempt.  */
end_comment

begin_class
DECL|class|MapTaskAttemptInfo
specifier|public
class|class
name|MapTaskAttemptInfo
extends|extends
name|TaskAttemptInfo
block|{
DECL|field|runtime
specifier|private
name|long
name|runtime
decl_stmt|;
DECL|method|MapTaskAttemptInfo (State state, TaskInfo taskInfo, long runtime)
specifier|public
name|MapTaskAttemptInfo
parameter_list|(
name|State
name|state
parameter_list|,
name|TaskInfo
name|taskInfo
parameter_list|,
name|long
name|runtime
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
name|taskInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|runtime
operator|=
name|runtime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRuntime ()
specifier|public
name|long
name|getRuntime
parameter_list|()
block|{
return|return
name|getMapRuntime
argument_list|()
return|;
block|}
comment|/**    * Get the runtime for the<b>map</b> phase of the map-task attempt.    *     * @return the runtime for the<b>map</b> phase of the map-task attempt    */
DECL|method|getMapRuntime ()
specifier|public
name|long
name|getMapRuntime
parameter_list|()
block|{
return|return
name|runtime
return|;
block|}
block|}
end_class

end_unit

