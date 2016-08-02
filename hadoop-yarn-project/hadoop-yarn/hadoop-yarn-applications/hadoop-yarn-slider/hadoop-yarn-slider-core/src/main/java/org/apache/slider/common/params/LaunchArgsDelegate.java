begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
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
comment|/**  * Any launch-time args  */
end_comment

begin_class
DECL|class|LaunchArgsDelegate
specifier|public
class|class
name|LaunchArgsDelegate
extends|extends
name|WaitArgsDelegate
implements|implements
name|LaunchArgsAccessor
block|{
comment|//TODO: do we need this?
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
name|ARG_RESOURCE_MANAGER
argument_list|,
name|description
operator|=
literal|"Resource manager hostname:port "
argument_list|,
name|required
operator|=
literal|false
argument_list|)
DECL|field|rmAddress
specifier|private
name|String
name|rmAddress
decl_stmt|;
annotation|@
name|Override
DECL|method|getRmAddress ()
specifier|public
name|String
name|getRmAddress
parameter_list|()
block|{
return|return
name|rmAddress
return|;
block|}
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_OUTPUT
block|,
name|ARG_OUTPUT_SHORT
block|}
argument_list|,
name|description
operator|=
literal|"output file for any application report"
argument_list|)
DECL|field|outputFile
specifier|public
name|File
name|outputFile
decl_stmt|;
annotation|@
name|Override
DECL|method|getOutputFile ()
specifier|public
name|File
name|getOutputFile
parameter_list|()
block|{
return|return
name|outputFile
return|;
block|}
block|}
end_class

end_unit

