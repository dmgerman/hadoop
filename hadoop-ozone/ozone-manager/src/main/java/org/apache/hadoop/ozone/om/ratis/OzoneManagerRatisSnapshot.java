begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
package|;
end_package

begin_comment
comment|/**  * Functional interface for OM RatisSnapshot.  */
end_comment

begin_interface
DECL|interface|OzoneManagerRatisSnapshot
specifier|public
interface|interface
name|OzoneManagerRatisSnapshot
block|{
comment|/**    * Update lastAppliedIndex with the specified value in OzoneManager    * StateMachine.    * @param lastAppliedIndex    */
DECL|method|updateLastAppliedIndex (long lastAppliedIndex)
name|void
name|updateLastAppliedIndex
parameter_list|(
name|long
name|lastAppliedIndex
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

