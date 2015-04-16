begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSXAttrBaseTest
import|;
end_import

begin_comment
comment|/**  * Tests XAttr APIs via WebHDFS.  */
end_comment

begin_class
DECL|class|TestWebHDFSXAttr
specifier|public
class|class
name|TestWebHDFSXAttr
extends|extends
name|FSXAttrBaseTest
block|{
comment|/**    * Overridden to provide a WebHdfsFileSystem wrapper for the super-user.    *    * @return WebHdfsFileSystem for super-user    * @throws Exception if creation fails    */
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|WebHdfsFileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
return|;
block|}
block|}
end_class

end_unit

