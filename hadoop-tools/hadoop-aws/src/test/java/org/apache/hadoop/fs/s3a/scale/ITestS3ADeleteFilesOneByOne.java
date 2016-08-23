begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Tests file deletion with multi-delete disabled.  */
end_comment

begin_class
DECL|class|ITestS3ADeleteFilesOneByOne
specifier|public
class|class
name|ITestS3ADeleteFilesOneByOne
extends|extends
name|ITestS3ADeleteManyFiles
block|{
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|configuration
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|Constants
operator|.
name|ENABLE_MULTI_DELETE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|configuration
return|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testOpenCreate ()
specifier|public
name|void
name|testOpenCreate
parameter_list|()
throws|throws
name|IOException
block|{    }
block|}
end_class

end_unit

