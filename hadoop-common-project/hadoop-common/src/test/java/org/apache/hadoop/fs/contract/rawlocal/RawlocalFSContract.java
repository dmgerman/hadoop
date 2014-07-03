begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.rawlocal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|rawlocal
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
name|FileSystem
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
name|contract
operator|.
name|localfs
operator|.
name|LocalFSContract
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
comment|/**  * Raw local filesystem. This is the inner OS-layer FS  * before checksumming is added around it.  */
end_comment

begin_class
DECL|class|RawlocalFSContract
specifier|public
class|class
name|RawlocalFSContract
extends|extends
name|LocalFSContract
block|{
DECL|method|RawlocalFSContract (Configuration conf)
specifier|public
name|RawlocalFSContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|field|RAW_CONTRACT_XML
specifier|public
specifier|static
specifier|final
name|String
name|RAW_CONTRACT_XML
init|=
literal|"contract/localfs.xml"
decl_stmt|;
annotation|@
name|Override
DECL|method|getContractXml ()
specifier|protected
name|String
name|getContractXml
parameter_list|()
block|{
return|return
name|RAW_CONTRACT_XML
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalFS ()
specifier|protected
name|FileSystem
name|getLocalFS
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|getConf
argument_list|()
argument_list|)
operator|.
name|getRawFileSystem
argument_list|()
return|;
block|}
DECL|method|getTestDirectory ()
specifier|public
name|File
name|getTestDirectory
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|getTestDataDir
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

