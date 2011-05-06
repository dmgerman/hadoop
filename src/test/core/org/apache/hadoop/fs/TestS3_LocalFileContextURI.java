begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_DEFAULT
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_class
DECL|class|TestS3_LocalFileContextURI
specifier|public
class|class
name|TestS3_LocalFileContextURI
extends|extends
name|FileContextURIBase
block|{
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|localConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|fc2
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|localConf
argument_list|)
expr_stmt|;
name|Configuration
name|s3conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|s3conf
operator|.
name|set
argument_list|(
name|FS_DEFAULT_NAME_DEFAULT
argument_list|,
name|s3conf
operator|.
name|get
argument_list|(
literal|"test.fs.s3.name"
argument_list|)
argument_list|)
expr_stmt|;
name|fc1
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|s3conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

