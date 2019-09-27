begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
package|;
end_package

begin_comment
comment|/**  * Configuration options for the CosN file system for testing.  */
end_comment

begin_class
DECL|class|CosNTestConfigKey
specifier|public
specifier|final
class|class
name|CosNTestConfigKey
block|{
DECL|method|CosNTestConfigKey ()
specifier|private
name|CosNTestConfigKey
parameter_list|()
block|{   }
DECL|field|TEST_COS_FILESYSTEM_CONF_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEST_COS_FILESYSTEM_CONF_KEY
init|=
literal|"test.fs.cosn.name"
decl_stmt|;
DECL|field|DEFAULT_TEST_COS_FILESYSTEM_CONF_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TEST_COS_FILESYSTEM_CONF_VALUE
init|=
literal|""
decl_stmt|;
DECL|field|TEST_UNIQUE_FORK_ID_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEST_UNIQUE_FORK_ID_KEY
init|=
literal|"test.unique.fork.id"
decl_stmt|;
block|}
end_class

end_unit

