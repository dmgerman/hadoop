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
comment|/**  * Indicates a particular phase of the namenode startup sequence.  The phases  * are listed here in their execution order.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|Phase
specifier|public
enum|enum
name|Phase
block|{
comment|/**    * The namenode is loading the fsimage file into memory.    */
DECL|enumConstant|LOADING_FSIMAGE
name|LOADING_FSIMAGE
argument_list|(
literal|"LoadingFsImage"
argument_list|,
literal|"Loading fsimage"
argument_list|)
block|,
comment|/**    * The namenode is loading the edits file and applying its operations to the    * in-memory metadata.    */
DECL|enumConstant|LOADING_EDITS
name|LOADING_EDITS
argument_list|(
literal|"LoadingEdits"
argument_list|,
literal|"Loading edits"
argument_list|)
block|,
comment|/**    * The namenode is saving a new checkpoint.    */
DECL|enumConstant|SAVING_CHECKPOINT
name|SAVING_CHECKPOINT
argument_list|(
literal|"SavingCheckpoint"
argument_list|,
literal|"Saving checkpoint"
argument_list|)
block|,
comment|/**    * The namenode has entered safemode, awaiting block reports from data nodes.    */
DECL|enumConstant|SAFEMODE
name|SAFEMODE
argument_list|(
literal|"SafeMode"
argument_list|,
literal|"Safe mode"
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
comment|/**    * Returns phase description.    *     * @return String description    */
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
comment|/**    * Returns phase name.    *     * @return String phase name    */
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
comment|/**    * Private constructor of enum.    *     * @param name String phase name    * @param description String phase description    */
DECL|method|Phase (String name, String description)
specifier|private
name|Phase
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
block|}
end_enum

end_unit

