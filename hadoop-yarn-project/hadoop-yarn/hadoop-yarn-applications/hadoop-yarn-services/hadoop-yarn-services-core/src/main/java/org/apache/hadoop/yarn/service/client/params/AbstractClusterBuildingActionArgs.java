begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client.params
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
operator|.
name|client
operator|.
name|params
package|;
end_package

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Abstract Action to build things; shares args across build and  * list  */
end_comment

begin_class
DECL|class|AbstractClusterBuildingActionArgs
specifier|public
specifier|abstract
class|class
name|AbstractClusterBuildingActionArgs
extends|extends
name|AbstractActionArgs
block|{
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_FILE
block|,
name|ARG_FILE_SHORT
block|}
argument_list|,
name|description
operator|=
literal|"The path to the service definition file in JSON format."
argument_list|)
DECL|field|file
specifier|public
name|File
name|file
decl_stmt|;
DECL|method|getFile ()
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_QUEUE
block|,
name|ARG_SHORT_QUEUE
block|}
argument_list|,
name|description
operator|=
literal|"Queue to submit the service"
argument_list|)
DECL|field|queue
specifier|public
name|String
name|queue
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_LIFETIME
block|}
argument_list|,
name|description
operator|=
literal|"Lifetime of the service from the time of request"
argument_list|)
DECL|field|lifetime
specifier|public
name|long
name|lifetime
decl_stmt|;
block|}
end_class

end_unit

