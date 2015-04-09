begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|web
operator|.
name|AuthFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_class
DECL|class|TestDFSConfigKeys
specifier|public
class|class
name|TestDFSConfigKeys
block|{
comment|/**    * Make sure we keep the String literal up to date with what we'd get by calling    * class.getName.    */
annotation|@
name|Test
DECL|method|testStringLiteralDefaultWebFilter ()
specifier|public
name|void
name|testStringLiteralDefaultWebFilter
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The default webhdfs auth filter should make the FQCN of AuthFilter."
argument_list|,
name|AuthFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_AUTHENTICATION_FILTER_DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

