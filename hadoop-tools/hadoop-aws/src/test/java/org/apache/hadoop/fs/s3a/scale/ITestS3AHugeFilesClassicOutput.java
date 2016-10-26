begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|scale
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
import|;
end_import

begin_comment
comment|/**  * Use classic output for writing things; tweaks the configuration to do  * this after it has been set up in the superclass.  * The generator test has been copied and re  */
end_comment

begin_class
DECL|class|ITestS3AHugeFilesClassicOutput
specifier|public
class|class
name|ITestS3AHugeFilesClassicOutput
extends|extends
name|AbstractSTestS3AHugeFiles
block|{
annotation|@
name|Override
DECL|method|createScaleConfiguration ()
specifier|protected
name|Configuration
name|createScaleConfiguration
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
name|super
operator|.
name|createScaleConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|Constants
operator|.
name|FAST_UPLOAD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|getBlockOutputBufferName ()
specifier|protected
name|String
name|getBlockOutputBufferName
parameter_list|()
block|{
return|return
literal|"classic"
return|;
block|}
block|}
end_class

end_unit

