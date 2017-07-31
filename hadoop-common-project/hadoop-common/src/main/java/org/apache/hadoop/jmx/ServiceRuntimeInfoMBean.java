begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.jmx
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|jmx
package|;
end_package

begin_comment
comment|/**  * Common runtime information for any service components.  */
end_comment

begin_interface
DECL|interface|ServiceRuntimeInfoMBean
specifier|public
interface|interface
name|ServiceRuntimeInfoMBean
block|{
comment|/**    * Gets the version of Hadoop.    *    * @return the version    */
DECL|method|getVersion ()
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Get the version of software running on the Namenode    *    * @return a string representing the version    */
DECL|method|getSoftwareVersion ()
name|String
name|getSoftwareVersion
parameter_list|()
function_decl|;
comment|/**    * Get the compilation information which contains date, user and branch    *    * @return the compilation information, as a JSON string.    */
DECL|method|getCompileInfo ()
name|String
name|getCompileInfo
parameter_list|()
function_decl|;
comment|/**    * Gets the NN start time in milliseconds.    *    * @return the NN start time in msec    */
DECL|method|getStartedTimeInMillis ()
name|long
name|getStartedTimeInMillis
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

