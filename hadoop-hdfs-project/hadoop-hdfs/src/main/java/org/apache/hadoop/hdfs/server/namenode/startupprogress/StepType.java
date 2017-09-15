begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.startupprogress
package|package
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
name|startupprogress
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Indicates a particular type of {@link Step}.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|StepType
specifier|public
enum|enum
name|StepType
block|{
comment|/**    * The namenode has entered safemode and is awaiting block reports from    * datanodes.    */
DECL|enumConstant|AWAITING_REPORTED_BLOCKS
name|AWAITING_REPORTED_BLOCKS
argument_list|(
literal|"AwaitingReportedBlocks"
argument_list|,
literal|"awaiting reported blocks"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to delegation keys.    */
DECL|enumConstant|DELEGATION_KEYS
name|DELEGATION_KEYS
argument_list|(
literal|"DelegationKeys"
argument_list|,
literal|"delegation keys"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to delegation tokens.    */
DECL|enumConstant|DELEGATION_TOKENS
name|DELEGATION_TOKENS
argument_list|(
literal|"DelegationTokens"
argument_list|,
literal|"delegation tokens"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to inodes.    */
DECL|enumConstant|INODES
name|INODES
argument_list|(
literal|"Inodes"
argument_list|,
literal|"inodes"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to cache pools.    */
DECL|enumConstant|CACHE_POOLS
name|CACHE_POOLS
argument_list|(
literal|"CachePools"
argument_list|,
literal|"cache pools"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to cache entries.    */
DECL|enumConstant|CACHE_ENTRIES
name|CACHE_ENTRIES
argument_list|(
literal|"CacheEntries"
argument_list|,
literal|"cache entries"
argument_list|)
block|,
comment|/**    * The namenode is performing an operation related to erasure coding policies.    */
DECL|enumConstant|ERASURE_CODING_POLICIES
name|ERASURE_CODING_POLICIES
argument_list|(
literal|"ErasureCodingPolicies"
argument_list|,
literal|"erasure coding policies"
argument_list|)
block|;
DECL|field|name
DECL|field|description
specifier|private
specifier|final
name|String
name|name
block|,
name|description
block|;
comment|/**    * Private constructor of enum.    *     * @param name String step type name    * @param description String step type description    */
DECL|method|StepType (String name, String description)
specifier|private
name|StepType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * Returns step type description.    *     * @return String step type description    */
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**    * Returns step type name.    *     * @return String step type name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_enum

end_unit

