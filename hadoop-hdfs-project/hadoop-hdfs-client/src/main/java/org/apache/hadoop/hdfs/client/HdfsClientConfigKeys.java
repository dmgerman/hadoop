begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
package|;
end_package

begin_comment
comment|/** Client configuration properties */
end_comment

begin_interface
DECL|interface|HdfsClientConfigKeys
specifier|public
interface|interface
name|HdfsClientConfigKeys
block|{
DECL|field|PREFIX
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"dfs.client."
decl_stmt|;
comment|/** Client retry configuration properties */
DECL|interface|Retry
specifier|public
interface|interface
name|Retry
block|{
DECL|field|PREFIX
specifier|static
specifier|final
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"retry."
decl_stmt|;
DECL|field|POLICY_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|POLICY_ENABLED_KEY
init|=
name|PREFIX
operator|+
literal|"policy.enabled"
decl_stmt|;
DECL|field|POLICY_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|POLICY_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|POLICY_SPEC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|POLICY_SPEC_KEY
init|=
name|PREFIX
operator|+
literal|"policy.spec"
decl_stmt|;
DECL|field|POLICY_SPEC_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|POLICY_SPEC_DEFAULT
init|=
literal|"10000,6,60000,10"
decl_stmt|;
comment|//t1,n1,t2,n2,...
DECL|field|TIMES_GET_LAST_BLOCK_LENGTH_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TIMES_GET_LAST_BLOCK_LENGTH_KEY
init|=
name|PREFIX
operator|+
literal|"times.get-last-block-length"
decl_stmt|;
DECL|field|TIMES_GET_LAST_BLOCK_LENGTH_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|TIMES_GET_LAST_BLOCK_LENGTH_DEFAULT
init|=
literal|3
decl_stmt|;
DECL|field|INTERVAL_GET_LAST_BLOCK_LENGTH_KEY
specifier|public
specifier|static
specifier|final
name|String
name|INTERVAL_GET_LAST_BLOCK_LENGTH_KEY
init|=
name|PREFIX
operator|+
literal|"interval-ms.get-last-block-length"
decl_stmt|;
DECL|field|INTERVAL_GET_LAST_BLOCK_LENGTH_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|INTERVAL_GET_LAST_BLOCK_LENGTH_DEFAULT
init|=
literal|4000
decl_stmt|;
DECL|field|MAX_ATTEMPTS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_ATTEMPTS_KEY
init|=
name|PREFIX
operator|+
literal|"max.attempts"
decl_stmt|;
DECL|field|MAX_ATTEMPTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_ATTEMPTS_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|WINDOW_BASE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|WINDOW_BASE_KEY
init|=
name|PREFIX
operator|+
literal|"window.base"
decl_stmt|;
DECL|field|WINDOW_BASE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|WINDOW_BASE_DEFAULT
init|=
literal|3000
decl_stmt|;
block|}
block|}
end_interface

end_unit

